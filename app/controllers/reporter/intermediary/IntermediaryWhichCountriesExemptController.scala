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

package controllers.reporter.intermediary

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.IntermediaryWhichCountriesExemptFormProvider
import javax.inject.Inject
import models.{CountryList, Mode}
import navigation.NavigatorForReporter
import pages.reporter.intermediary.IntermediaryWhichCountriesExemptPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class IntermediaryWhichCountriesExemptController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForReporter,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: IntermediaryWhichCountriesExemptFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IntermediaryWhichCountriesExemptPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"       -> preparedForm,
        "id" -> id,
        "mode"       -> mode,
        "checkboxes" -> CountryList.nonGBCheckboxes(preparedForm)
      )

      renderer.render("reporter/intermediary/intermediaryWhichCountriesExempt.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[Set[CountryList]]): Call =
    navigator.routeMap(IntermediaryWhichCountriesExemptPage)(checkRoute)(id)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "id" -> id,
            "mode"       -> mode,
            "checkboxes" -> CountryList.nonGBCheckboxes(formWithErrors)
          )

          renderer.render("reporter/intermediary/intermediaryWhichCountriesExempt.njk", json).map(BadRequest(_))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IntermediaryWhichCountriesExemptPage, id, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers, id)
          } yield Redirect(redirect(id, checkRoute, Some(value)))
        }
      )
  }
}
