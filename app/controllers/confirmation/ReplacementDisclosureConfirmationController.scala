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
import pages.MessageRefIDPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ReplacementDisclosureConfirmationController @Inject()(
    override val messagesApi: MessagesApi,
    appConfig: FrontendAppConfig,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    contactRetrievalAction: ContactRetrievalAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData andThen contactRetrievalAction).async {
    implicit request =>

      val messageRefID =  request.userAnswers.get(MessageRefIDPage, id) match {
        case Some(id) => id
        case None => throw new RuntimeException("messageRefID cannot be found")
      }

      val emailMessage = request.contacts.map(contacts => (contacts.secondEmail, contacts.contactEmail)) match {
        case Some((Some(secondary), Some(primary))) => primary + " and " + secondary
        case Some((None, Some(primary))) => primary
        case _ => throw new RuntimeException("Contact email details are missing")
      }

      val json = Json.obj(
        "messageRefID" -> messageRefID,
        "homePageLink" -> linkToHomePageText(appConfig.discloseArrangeLink),
        "betaFeedbackSurvey" -> surveyLinkText(appConfig.betaFeedbackUrl),
        "emailMessage" -> emailMessage,
        "emailToggle" -> appConfig.sendEmailToggle
      )

      renderer.render("confirmation/replacementDisclosureConfirmation.njk", json).map(Ok(_))
  }
}
