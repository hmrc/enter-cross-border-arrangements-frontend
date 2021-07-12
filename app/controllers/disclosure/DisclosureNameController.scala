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

package controllers.disclosure

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.disclosure.DisclosureNameFormProvider
import javax.inject.Inject
import models.hallmarks.JourneyStatus
import models.{Mode, UserAnswers}
import navigation.NavigatorForDisclosure
import pages.disclosure.{DisclosureNamePage, DisclosureStatusPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class DisclosureNameController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForDisclosure,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DisclosureNameFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData.apply()).async {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(_.getBase(DisclosureNamePage)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode
      )

      renderer.render("disclosure/disclosureName.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[String]): Call =
    navigator.routeMap(DisclosureNamePage)(checkRoute)(None)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData.apply()).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form" -> formWithErrors,
              "mode" -> mode
            )

            renderer.render("disclosure/disclosureName.njk", json).map(BadRequest(_))
          },
          value => {
            val initialUserAnswers = UserAnswers(request.internalId)
            val userAnswers = request.userAnswers.fold(initialUserAnswers)(
              ua => ua
            )

            for {
              updatedAnswers           <- Future.fromTry(userAnswers.setBase(DisclosureNamePage, value))
              updatedAnswersWithStatus <- Future.fromTry(updatedAnswers.setBase(DisclosureStatusPage, JourneyStatus.InProgress))
              _                        <- sessionRepository.set(updatedAnswersWithStatus)
              checkRoute = toCheckRoute(mode, updatedAnswers)
            } yield Redirect(redirect(checkRoute, Some(value)))

          }
        )
  }
}
