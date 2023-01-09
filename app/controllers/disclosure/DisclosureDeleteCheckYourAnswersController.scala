/*
 * Copyright 2023 HM Revenue & Customs
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
import config.FrontendAppConfig
import controllers.actions.{ContactRetrievalAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.exceptions.DisclosureInformationIsMissingException
import controllers.mixins.{DefaultRouting, RoutingSupport}
import helpers.JourneyHelpers.linkToHomePageText
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.requests.DataRequestWithContacts
import models.{GeneratedIDs, NormalMode, Submission}
import navigation.NavigatorForDisclosure
import pages.disclosure._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.{EmailService, XMLGenerationService}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class DisclosureDeleteCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  navigator: NavigatorForDisclosure,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactRetrievalAction: ContactRetrievalAction,
  frontendAppConfig: FrontendAppConfig,
  xmlGenerationService: XMLGenerationService,
  sessionRepository: SessionRepository,
  emailService: EmailService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData.apply() andThen requireData andThen contactRetrievalAction.apply).async {
    implicit request =>
      request.userAnswers
        .getBase(DisclosureTypePage)
        .fold {
          Future.successful(Redirect(controllers.routes.DisclosureAlreadySentController.onDeleted()))
        } {
          _ =>
            val helper = new CheckYourAnswersHelper(request.userAnswers)

            val disclosureSummary: Seq[SummaryList.Row] =
              helper.disclosureNamePage.toSeq ++
                helper.buildDisclosureSummaryDetails

            renderer
              .render(
                "disclosure/check-your-answers-delete-disclosure.njk",
                Json.obj(
                  "disclosureSummary" -> disclosureSummary,
                  "homePageLink"      -> linkToHomePageText(frontendAppConfig.discloseArrangeLink, "site.homePageLink.text")
                )
              )
              .map(Ok(_))
        }
  }

  def onContinue(): Action[AnyContent] = (identify andThen getData.apply() andThen requireData andThen contactRetrievalAction.apply).async {
    implicit request =>
      val submission: Submission = request.userAnswers.getBase(ReplaceOrDeleteADisclosurePage) match {
        case Some(ids) =>
          val disclosureDetails = DisclosureDetails
            .build(request.userAnswers)
            .withDisclosureType(DisclosureType.Dac6del)
            .withIds(ids.arrangementID, ids.disclosureID)
          Submission(request.enrolmentID, disclosureDetails)
        case _ => throw new DisclosureInformationIsMissingException("Cannot retrieve Disclosure Information")
      }

      xmlGenerationService.createAndValidateXmlSubmission(submission).flatMap {
        _.fold(
          _ => throw new IllegalStateException(s"Unable to delete submission: $submission"),
          ids =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.setBase(DisclosureDeleteCheckYourAnswersPage, submission.updateIds(ids)))
              _              <- sessionRepository.set(updatedAnswers)
              _              <- sendDeleteMail(ids)
            } yield Redirect(navigator.routeMap(DisclosureDeleteCheckYourAnswersPage)(DefaultRouting(NormalMode))(None)(None)(0))
        )
      }
  }

  private def sendDeleteMail(ids: GeneratedIDs)(implicit request: DataRequestWithContacts[_]): Future[Option[HttpResponse]] =
    request.userAnswers.getBase(ReplaceOrDeleteADisclosurePage) match {
      case Some(detail) =>
        emailService.sendEmail(request.contacts, GeneratedIDs(Some(detail.arrangementID), Some(detail.disclosureID)), "dac6del", ids.messageRefID.get)
      case _ => throw new DisclosureInformationIsMissingException("MessageRef, DisclosureID or ArrangementID can't be found for email.")
    }
}
