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
import models.NormalMode
import models.disclosure.DisclosureType.Dac6add
import navigation.NavigatorForDisclosure
import pages.disclosure._
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

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
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

  def onContinue(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

//    TODO build the disclosure details model from pages
//    val disclosureDetails: DisclosureDetails = DisclosureDetailsPage.build(request.userAnswers)
//
//    for {
//      updatedAnswers <- Future.fromTry(request.userAnswers.set(DisclosureDetailsPage, disclosureDetails))
//    } yield sessionRepository.set(updatedAnswers)

      val isMarketable: Boolean = request.userAnswers.get(DisclosureTypePage) match {
        case Some(Dac6add) =>
          request.userAnswers.get(DisclosureIdentifyArrangementPage).fold(false)(
            arrangementId => crossBorderArrangementsConnector.isMarketableArrangement(arrangementId).isCompleted)
        case _ =>
          request.userAnswers.get(DisclosureMarketablePage).fold(
            throw new Exception("Unable to retrieve user answer marketable arrangement"))(bool =>
            bool)
      }

      for {
        updateAnswers <- Future.fromTry(request.userAnswers.set(DisclosureMarketablePage, isMarketable))
        _ <- sessionRepository.set(updateAnswers)
      } yield Redirect(navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(None)(0))
  }
}

