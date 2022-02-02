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

package controllers.individual

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.individual.IndividualDateOfBirthFormProvider
import models.{Mode, UserAnswers}
import navigation.NavigatorForIndividual
import pages.individual.{IndividualDateOfBirthPage, IndividualNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import utils.DateInput

class IndividualDateOfBirthController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForIndividual,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IndividualDateOfBirthFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(IndividualDateOfBirthPage, id) match {
        case Some(value) => form.fill(value)
        case None        => form
      }

      val viewModel = DateInput.localDate(preparedForm("dob"))

      val json = Json.obj(
        "form" -> preparedForm,
        "id"   -> id,
        "mode" -> mode,
        "date" -> viewModel,
        "name" -> getIndividualName(request.userAnswers, id)
      )

      renderer.render("individual/individualDateOfBirth.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[LocalDate]): Call =
    navigator.routeMap(IndividualDateOfBirthPage)(checkRoute)(id)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[LocalDate]) => {

            val viewModel: DateInput.ViewModel = DateInput.localDate(field = formWithErrors("dob"))

            val json = Json.obj(
              "form" -> formWithErrors,
              "id"   -> id,
              "mode" -> mode,
              "date" -> viewModel,
              "name" -> getIndividualName(request.userAnswers, id)
            )

            renderer.render("individual/individualDateOfBirth.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualDateOfBirthPage, id, value))
              _              <- sessionRepository.set(updatedAnswers)
              checkRoute = toCheckRoute(mode, updatedAnswers, id)
            } yield Redirect(redirect(id, checkRoute, Some(value)))
        )
  }

  private def getIndividualName(userAnswers: UserAnswers, id: Int): String =
    userAnswers.get(IndividualNamePage, id) match {
      case Some(name) => s"${name.displayName}’s"
      case None       => "their"
    }
}
