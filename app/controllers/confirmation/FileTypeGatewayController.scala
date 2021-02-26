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
import models.disclosure.DisclosureDetails
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
      } else {
          logger.warn("Email not sent - toggle set to false")
          Future.successful(None)
      }

      Future.successful(disclosureDetails.map(_.disclosureType)).map { disclosureType =>
        Redirect(navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(id)(disclosureType)(0))
      }
  }

    def sendMail(id: Int, importInstruction: String)(implicit request: DataRequestWithContacts[_]): Future[Option[HttpResponse]] = {

      val disclosureID = request.userAnswers.get(GeneratedIDPage, id) match {
        case Some(id) => id.disclosureID
        case None => throw new RuntimeException("DisclosureID cannot be found")
      }

      val arrangementID = request.userAnswers.get(GeneratedIDPage, id) match {
        case Some(id) => id.arrangementID
        case _ =>  request.userAnswers.get(DisclosureIdentifyArrangementPage, id)
      }

      emailService.sendEmail(request.contacts, GeneratedIDs(disclosureID, arrangementID), importInstruction, request.userAnswers.get(MessageRefIDPage, id).get)
    }
}
