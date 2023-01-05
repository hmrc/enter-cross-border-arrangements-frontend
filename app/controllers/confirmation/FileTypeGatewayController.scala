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

package controllers.confirmation

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{ContactRetrievalAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.DefaultRouting
import models.NormalMode
import navigation.NavigatorForConfirmation
import org.slf4j.LoggerFactory
import pages.disclosure.DisclosureDetailsPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.EmailService
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

      Future.successful(disclosureDetails.map(_.disclosureType)).map {
        disclosureType =>
          Redirect(navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(id)(disclosureType)(0))
      }
  }

}
