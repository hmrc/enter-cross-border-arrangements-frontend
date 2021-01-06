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

package controllers.reporter.individual

import controllers.actions._
import forms.reporter.individual.ReporterIndividualDateOfBirthFormProvider
import models.Mode
import navigation.NavigatorForReporter
import pages.reporter.individual.ReporterIndividualDateOfBirthPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}
import java.time.LocalDate

import controllers.mixins.{CheckRoute, RoutingSupport}
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class ReporterIndividualDateOfBirthController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    navigator: NavigatorForReporter,
    formProvider: ReporterIndividualDateOfBirthFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(ReporterIndividualDateOfBirthPage) match {
        case Some(value) => form.fill(value)
        case None        => form
      }

      val viewModel = DateInput.localDate(preparedForm("dob"))

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "date" -> viewModel
      )

      renderer.render("reporter/individual/reporterIndividualDateOfBirth.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[LocalDate], index: Int = 0): Call =
    navigator.routeMap(ReporterIndividualDateOfBirthPage)(checkRoute)(value)(index)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>  {

          val viewModel = DateInput.localDate(formWithErrors("dob"))

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "date" -> viewModel
          )

          renderer.render("reporter/individual/reporterIndividualDateOfBirth.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterIndividualDateOfBirthPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }
}