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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{ContactRetrievalAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.DefaultRouting
import models.requests.DataRequestWithContacts
import models.{GeneratedIDs, NormalMode}
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
        emailService.sendEmail(request.contacts, GeneratedIDs(Some(disclosureID), Some(arrangementID)), importInstruction, messageRefID)

      // add
      case Some(GeneratedIDs(None, Some(disclosureID), Some(messageRefID), _)) =>
        val details = request.userAnswers
          .get(DisclosureDetailsPage, id)
          .getOrElse(throw new IllegalStateException("DisclosureID or ArrangementID can't be found for email."))
        emailService.sendEmail(request.contacts, GeneratedIDs(Some(disclosureID), details.arrangementID), importInstruction, messageRefID)

      //rep
      case Some(GeneratedIDs(None, None, Some(messageRefID), _)) =>
        val details = request.userAnswers
          .get(DisclosureDetailsPage, id)
          .getOrElse(throw new IllegalStateException("DisclosureID or ArrangementID can't be found for email."))
        emailService.sendEmail(request.contacts, GeneratedIDs(details.disclosureID, details.arrangementID), importInstruction, messageRefID)

      case _ => throw new IllegalStateException("DisclosureID or ArrangementID can't be found for email.")
    }
}
