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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.{DefaultRouting, RoutingSupport}
import handlers.ErrorHandler
import models.disclosure.DisclosureType
import models.{NormalMode, Submission}
import navigation.NavigatorForDisclosure
import pages.disclosure._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.XMLGenerationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class DisclosureDeleteCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    navigator: NavigatorForDisclosure,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    xmlGenerationService: XMLGenerationService,
    sessionRepository: SessionRepository,
    errorHandler: ErrorHandler,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val disclosureSummary: Seq[SummaryList.Row] =
        helper.disclosureNamePage.toSeq ++
          helper.buildDisclosureSummaryDetails

      renderer.render(
        "disclosure/check-your-answers-delete-disclosure.njk",
        Json.obj("disclosureSummary" -> disclosureSummary
        )
      ).map(Ok(_))
    }

  def onContinue(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>


      val submission: Submission = request.userAnswers.getBase(ReplaceOrDeleteADisclosurePage) match {
        case Some(ids) =>
          val disclosureDetails = DisclosureDetailsPage.build(request.userAnswers)
            .withDisclosureType(DisclosureType.Dac6del)
            .withIds(ids.arrangementID, ids.disclosureID)
          Submission(request.enrolmentID, disclosureDetails)
        case _ => throw new RuntimeException("Cannot retrieve Disclosure Information")
      }

      xmlGenerationService.createAndValidateXmlSubmission(submission).flatMap {
        _.fold (
          _ => throw new IllegalStateException(s"Unable to delete submission: $submission")
          ,
          ids =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.setBase(DisclosureDeleteCheckYourAnswersPage, submission.updateIds(ids)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.routeMap(DisclosureDeleteCheckYourAnswersPage)(DefaultRouting(NormalMode))(None)(None)(0))
        )
      }
  }

}

