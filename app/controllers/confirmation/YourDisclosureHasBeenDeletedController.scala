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
import handlers.ErrorHandler
import helpers.JourneyHelpers.{linkToHomePageText, surveyLinkText}
import models.GeneratedIDs
import pages.disclosure.DisclosureDeleteCheckYourAnswersPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class YourDisclosureHasBeenDeletedController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    appConfig: FrontendAppConfig,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    contactRetrievalAction: ContactRetrievalAction,
    val controllerComponents: MessagesControllerComponents,
    errorHandler: ErrorHandler,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData andThen contactRetrievalAction).async {
    implicit request =>

      request.userAnswers.getBase(DisclosureDeleteCheckYourAnswersPage) match {
        case Some(GeneratedIDs(Some(arrangementID), Some(disclosureID), Some(messageRefID))) =>

          val emailMessage = request.contacts.map(contacts => (contacts.secondEmail, contacts.contactEmail)) match {
            case Some((Some(secondary), Some(primary))) => primary + " and " + secondary
            case Some((None, Some(primary))) => primary
            case _ => throw new RuntimeException("Contact email details are missing")
          }

          val json = Json.obj (
            "disclosureID" -> disclosureID,
            "arrangementID" -> arrangementID,
            "messageRefid" -> messageRefID,
            "homePageLink" -> linkToHomePageText (appConfig.discloseArrangeLink),
            "betaFeedbackSurvey" -> surveyLinkText (appConfig.betaFeedbackUrl),
            "emailToggle" -> appConfig.sendEmailToggle,
            "emailMessage" -> emailMessage
          )
          renderer.render ("confirmation/yourDisclosureHasBeenDeleted.njk", json).map (Ok (_) )

       case _ => errorHandler.onServerError(request, new RuntimeException("Cannot retrieve arrangement details from session store"))
    }
  }
}
