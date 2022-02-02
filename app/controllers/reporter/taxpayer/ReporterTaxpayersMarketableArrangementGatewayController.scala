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

package controllers.reporter.taxpayer

import com.google.inject.Inject
import connectors.CrossBorderArrangementsConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.DefaultRouting
import handlers.ErrorHandler
import models.Mode
import models.disclosure.DisclosureType
import navigation.NavigatorForReporter
import pages.disclosure.{DisclosureDetailsPage, DisclosureMarketablePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class ReporterTaxpayersMarketableArrangementGatewayController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  navigator: NavigatorForReporter,
  errorHandler: ErrorHandler,
  crossBorderArrangementsConnector: CrossBorderArrangementsConnector,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController {

  def onRouting(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      (request.userAnswers.get(DisclosureDetailsPage, id).map(_.disclosureType) match {
        case Some(DisclosureType.Dac6new) =>
          Future.successful(request.userAnswers.get(DisclosureDetailsPage, id).exists(_.initialDisclosureMA))
        case Some(DisclosureType.Dac6add | DisclosureType.Dac6rep) =>
          request.userAnswers.get(DisclosureDetailsPage, id).flatMap(_.arrangementID) match {
            case Some(arrangementId) =>
              crossBorderArrangementsConnector.isMarketableArrangement(arrangementId)
          }
        case _ => throw new UnsupportedOperationException("A disclosure must contain either a new or added arrangement")

      }) map {
        isMarketableArrangement =>
          Redirect(navigator.routeMap(DisclosureMarketablePage)(DefaultRouting(mode))(id)(Some(isMarketableArrangement))(0))
      } recoverWith {
        case ex: Exception => errorHandler.onServerError(request, ex)
      }
  }
}
