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
import pages.disclosure.{DisclosureDetailsPage, DisclosureIdentifyArrangementPage}
import pages.{GeneratedIDPage, MessageRefIDPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.EmailService
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class FileTypeGatewayController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactRetrievalAction: ContactRetrievalAction,
  navigator: NavigatorForConfirmation,
  frontendAppConfig: FrontendAppConfig,
  emailService: EmailService,
  val controllerComponents: MessagesControllerComponents
  )(implicit ec: ExecutionContext) extends FrontendBaseController {

  private val logger = LoggerFactory.getLogger(getClass)

  def onRouting(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData andThen contactRetrievalAction).async {
    implicit request =>

      val disclosureDetails = request.userAnswers.get(DisclosureDetailsPage, id)

      if (frontendAppConfig.sendEmailToggle) {
        sendMail(id, disclosureDetails.get.disclosureType.toString)
      }

      else {
          logger.warn("Email not sent - toggle set to false")
          Future.successful(None)
        }

      Future.successful(disclosureDetails.map(_.disclosureType)).map { disclosureType =>
        Redirect(navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(id)(disclosureType)(0))
      }
  }

    def sendMail(id: Int, importInstruction: String)(implicit request: DataRequestWithContacts[_]): Future[Option[HttpResponse]] = {

      request.userAnswers.get(GeneratedIDPage, id)  match {


         //new
        case Some(GeneratedIDs(Some(arrangementID), Some(disclosureID), Some(messageRefID), _)) =>


          println("new email *****" + request.contacts, GeneratedIDs(Some(disclosureID), Some(arrangementID)), importInstruction,messageRefID)
          emailService.sendEmail(request.contacts, GeneratedIDs(Some(disclosureID), Some(arrangementID)), importInstruction, messageRefID)

         // add
        case Some(GeneratedIDs(None, Some(disclosureID), Some(messageRefID), _)) =>
          val details = request.userAnswers.get(DisclosureDetailsPage, id)
          println("rep or add email *****" + details.get.arrangementID + disclosureID + messageRefID + importInstruction)
          emailService.sendEmail(request.contacts, GeneratedIDs(Some(disclosureID), details.get.arrangementID), importInstruction, messageRefID)

          //rep
        case Some(GeneratedIDs(None, None, Some(messageRefID), _)) =>
          val details = request.userAnswers.get(DisclosureDetailsPage, id)
          emailService.sendEmail(request.contacts, GeneratedIDs(details.get.disclosureID, details.get.arrangementID), importInstruction, messageRefID)


        case _ => throw new IllegalStateException("DisclosureID or ArrangementID can't be found for email.")
      }
    }
}
