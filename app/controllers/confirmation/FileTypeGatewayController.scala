/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{ContactRetrievalAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.DefaultRouting
import models.disclosure.DisclosureDetails
import models.requests.DataRequestWithContacts
import models.{GeneratedIDs, NormalMode, UserAnswers}
import navigation.NavigatorForConfirmation
import org.slf4j.LoggerFactory
import pages.GeneratedIDPage
import pages.disclosure.DisclosureDetailsPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.EmailService
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class FileTypeGatewayController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactRetrievalAction: ContactRetrievalAction,
  navigator: NavigatorForConfirmation,
  frontendAppConfig: FrontendAppConfig,
  emailService: EmailService,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController {

  private val logger = LoggerFactory.getLogger(getClass)

  def onRouting(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData andThen contactRetrievalAction.apply).async {
    implicit request =>
      val disclosureDetails = request.userAnswers.get(DisclosureDetailsPage, id)

      sendMail(id, disclosureDetails.get.disclosureType.toString)

      Future.successful(disclosureDetails.map(_.disclosureType)).map {
        disclosureType =>
          Redirect(navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(id)(disclosureType)(0))
      }
  }

  def sendMail(id: Int, importInstruction: String)(implicit request: DataRequestWithContacts[_]): Future[Option[HttpResponse]] =
    request.userAnswers.get(GeneratedIDPage, id) match {

      //new
      case Some(GeneratedIDs(Some(arrangementID), Some(disclosureID), Some(messageRefID), _)) =>
        emailService.sendEmail(request.contacts, GeneratedIDs(Some(arrangementID), Some(disclosureID)), importInstruction, messageRefID)

      // add
      case Some(GeneratedIDs(None, Some(disclosureID), Some(messageRefID), _)) =>
        val details = retrieveDisclosureId(request.userAnswers, id)
        emailService.sendEmail(request.contacts, GeneratedIDs(details.arrangementID, Some(disclosureID)), importInstruction, messageRefID)

      //rep
      case Some(GeneratedIDs(None, None, Some(messageRefID), _)) =>
        val details = retrieveDisclosureId(request.userAnswers, id)
        emailService.sendEmail(request.contacts, GeneratedIDs(details.arrangementID, details.disclosureID), importInstruction, messageRefID)

      case Some(GeneratedIDs(_, _, None, _)) =>
        logger.warn(s"FileGateWayController: Generated Ids does not contain the MessageRefID id: $id")
        throw new IllegalStateException("DisclosureID or ArrangementID or MessageRefID cannot be found for email.")
      case Some(_) =>
        logger.warn(s"FileGateWayController: Generated Ids does not contain the DisclosureID or MessageRefID id: $id")
        throw new IllegalStateException("DisclosureID or ArrangementID or MessageRefID cannot be found for email.")
      case None =>
        logger.warn(s"FileGateWayController: GeneratedIds cannot retreived from user answers  id: $id")
        throw new IllegalStateException("GeneratedIDs cannot be found for the email.")
    }

  private def retrieveDisclosureId(userAnswers: UserAnswers, id: Int): DisclosureDetails = userAnswers.get(DisclosureDetailsPage, id) match {
    case Some(disclosureId) => disclosureId
    case None =>
      logger.warn(s"FileGateWayController: Cannot retrieve disclosureID from UserAnswers id: $id")
      throw new IllegalStateException("DisclosureID or ArrangementID or MessageRefID cannot be found for email.")
  }
}
