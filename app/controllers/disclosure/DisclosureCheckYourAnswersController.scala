/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.disclosure

import com.google.inject.Inject
import connectors.CrossBorderArrangementsConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.{DefaultRouting, RoutingSupport}
import helpers.IDHelper
import models.disclosure.DisclosureType.{Dac6add, Dac6rep}
import models.hallmarks.JourneyStatus
import models.{NormalMode, UnsubmittedDisclosure}
import navigation.NavigatorForDisclosure
import pages.disclosure._
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class DisclosureCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForDisclosure,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    crossBorderArrangementsConnector: CrossBorderArrangementsConnector,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val disclosureSummary: Seq[SummaryList.Row] =
        helper.disclosureNamePage.toSeq ++
        helper.disclosureTypePage.toSeq ++
        helper.buildDisclosureSummaryDetails

      renderer.render(
        "disclosure/check-your-answers-disclosure.njk",
        Json.obj("disclosureSummary" -> disclosureSummary
        )
      ).map(Ok(_))
    }

  def onContinue: Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      val openDisclosures: Seq[UnsubmittedDisclosure] = request.userAnswers.getBase(UnsubmittedDisclosurePage).getOrElse(Seq.empty)

      //generate an id for the disclosure submission
      val submissionID = IDHelper.generateID(openDisclosures.map(_.id), suffixLength = 6)

      //generate model for disclosure name and id and shove at the end of the list
      val disclosureName = request.userAnswers.getBase(DisclosureNamePage)
      val updatedUnsubmittedDisclosures = openDisclosures :+ UnsubmittedDisclosure(submissionID, disclosureName.get)
      val index = updatedUnsubmittedDisclosures.zipWithIndex.last._2

      def isMarketable: Future[Boolean] = request.userAnswers.getBase(DisclosureTypePage) match {
        case Some(Dac6add) =>
          request.userAnswers.getBase(DisclosureIdentifyArrangementPage).fold(
            throw new Exception("Unable to retrieve isMarketableArrangement from disclosure backend"))(
            arrangementId => crossBorderArrangementsConnector.isMarketableArrangement(arrangementId))
        case Some(Dac6rep) =>
          request.userAnswers.getBase(ReplaceOrDeleteADisclosurePage).fold(
            throw new Exception("Unable to retrieve ReplaceOrDeleteADisclosure IDs"))(
            ids => crossBorderArrangementsConnector.isMarketableArrangement(ids.arrangementID))
        case _ =>
          request.userAnswers.getBase(DisclosureMarketablePage).fold(
            throw new Exception("Unable to retrieve user answer marketable arrangement"))(bool =>
            Future.successful(bool))
      }

      //build the disclosure details model from pages and store under id
      for {
        isMarketableResult <- isMarketable
        updateAnswers <- Future.fromTry(request.userAnswers.setBase(DisclosureMarketablePage, isMarketableResult))
        disclosureDetails = DisclosureDetailsPage.build(updateAnswers)
        updatedAnswers <- Future.fromTry(updateAnswers.setBase(UnsubmittedDisclosurePage, updatedUnsubmittedDisclosures))
        newAnswers <- Future.fromTry(updatedAnswers.set(DisclosureDetailsPage, index, disclosureDetails))
        updateNewAnswersWithStatus <- Future.fromTry(newAnswers.set(DisclosureStatusPage, index, JourneyStatus.Completed))
        _  <- sessionRepository.set(updateNewAnswersWithStatus)
      } yield Redirect(navigator.routeMap(DisclosureCheckYourAnswersPage)(DefaultRouting(NormalMode))(Some(index))(None)(0))
  }
}
