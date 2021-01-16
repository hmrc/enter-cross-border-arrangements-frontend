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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.DefaultRouting
import models.NormalMode
import navigation.NavigatorForConfirmation
import pages.disclosure.{DisclosureDetailsPage, DisclosureTypePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class FileTypeGatewayController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  navigator: NavigatorForConfirmation,
  val controllerComponents: MessagesControllerComponents
  )(implicit ec: ExecutionContext) extends FrontendBaseController {

  def onRouting(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      Future.successful(request.userAnswers.get(DisclosureTypePage, id)).map { disclosureType =>
        Redirect(navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(id)(disclosureType)(0))
      }
  }
}
