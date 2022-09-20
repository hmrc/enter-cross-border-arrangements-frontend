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

package controllers.disclosure

import connectors.HistoryConnector
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.disclosure.DisclosureTypeFormProvider
import handlers.ErrorHandler
import models.Mode
import models.disclosure.DisclosureType
import navigation.NavigatorForDisclosure
import pages.disclosure.DisclosureTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DisclosureTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForDisclosure,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DisclosureTypeFormProvider,
  connector: HistoryConnector,
  errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.getBase(DisclosureTypePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      {
        for {
          hasHistory <- connector.getSubmissionDetails(request.enrolmentID)
          radios = if (hasHistory) DisclosureType.radiosComplete(preparedForm) else DisclosureType.radios(preparedForm)
          json = Json.obj(
            "form"   -> preparedForm,
            "mode"   -> mode,
            "radios" -> radios
          )
          renderedForm <- renderer.render("disclosure/disclosureType.njk", json)
        } yield Ok(renderedForm)
      }.recoverWith {
        case ex: Exception => errorHandler.onServerError(request, ex)
      }
  }

  def redirect(checkRoute: CheckRoute, value: Option[DisclosureType]): Call =
    navigator.routeMap(DisclosureTypePage)(checkRoute)(None)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            {
              for {
                hasHistory <- connector.getSubmissionDetails(request.enrolmentID)
                radios = if (hasHistory) DisclosureType.radiosComplete(formWithErrors) else DisclosureType.radios(formWithErrors)
                json = Json.obj(
                  "form"   -> formWithErrors,
                  "mode"   -> mode,
                  "radios" -> radios
                )
                renderedForm <- renderer.render("disclosure/disclosureType.njk", json)
              } yield BadRequest(renderedForm)

            }.recoverWith {
              case ex: Exception => errorHandler.onServerError(request, ex)
            },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.setBase(DisclosureTypePage, value))
              _              <- sessionRepository.set(updatedAnswers)
              checkRoute = toCheckRoute(mode, updatedAnswers)
            } yield Redirect(redirect(checkRoute, Some(value)))
        )
  }
}
