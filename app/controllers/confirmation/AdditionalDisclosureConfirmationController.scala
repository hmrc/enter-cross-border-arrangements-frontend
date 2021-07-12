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

package controllers.confirmation

import config.FrontendAppConfig
import controllers.actions._
import helpers.JourneyHelpers.{linkToHomePageText, surveyLinkText}
import models.GeneratedIDs
import pages.GeneratedIDPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.Html

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AdditionalDisclosureConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactRetrievalAction: ContactRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  sessionRepository: SessionRepository,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with UpdateDisclosureDetailsAsSent {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData andThen contactRetrievalAction.apply).async {
    implicit request =>
      val generatedIDs: Option[GeneratedIDs] = request.userAnswers.get(GeneratedIDPage, id)
      val (disclosureID, messageRefID) = generatedIDs
        .map {
          generatedIDs =>
            (generatedIDs.disclosureID, generatedIDs.messageRefID) match {
              case (Some(disclosureID), Some(messageRefId)) => (disclosureID, messageRefId)
              case _                                        => throw new IllegalStateException("At least one of disclosureID or messageRefID cannot be found.")
            }
        }
        .getOrElse(throw new IllegalStateException("At least one of arrangementID, disclosureID or messageRefID cannot be found."))

      val emailMessage = request.contacts.flatMap(_.emailMessage).getOrElse(throw new RuntimeException("Contact email details are missing"))

      val json = Json.obj(
        "panelTitle"         -> confirmationPanelTitle,
        "panelText"          -> confirmationPanelText(disclosureID),
        "emailMessage"       -> emailMessage,
        "messageRefID"       -> messageRefID,
        "homePageLink"       -> linkToHomePageText(appConfig.discloseArrangeLink),
        "betaFeedbackSurvey" -> surveyLinkText(appConfig.betaFeedbackUrl)
      )

      updateDisclosureDetailsAsSent(request.userAnswers, id).map(sessionRepository.set)

      renderer.render("confirmation/disclosureConfirmation.njk", json).map(Ok(_))
  }

  private def confirmationPanelTitle(implicit messages: Messages): String =
    messages("disclosureConfirmation.panel.add")

  private def confirmationPanelText(id: String)(implicit messages: Messages): Html =
    Html(s"${messages("disclosureConfirmation.panel.add")}<br><strong>$id</strong>")
}
