/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.reporter.organisation

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.reporter.organisation.ReporterOrganisationNameFormProvider
import helpers.JourneyHelpers.hasValueChanged
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.NavigatorForOrganisation
import pages.reporter.organisation.ReporterOrganisationNamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class ReporterOrganisationNameController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForOrganisation,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    formProvider: ReporterOrganisationNameFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val preparedForm =  request.userAnswers.flatMap(_.get(ReporterOrganisationNamePage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode
      )

      renderer.render("reporter/organisation/reporterOrganisationName.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[String], isAlt: Boolean): Call =
    if (isAlt) {
      navigator.routeAltMap(ReporterOrganisationNamePage)(checkRoute)(value)(0)
    }
    else {
      navigator.routeMap(ReporterOrganisationNamePage)(checkRoute)(value)(0)
    }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode
          )

          renderer.render("reporter/organisation/reporterOrganisationName.njk", json).map(BadRequest(_))
        },
        value => {

          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)
          val redirectUsers = hasValueChanged(value, ReporterOrganisationNamePage, mode, userAnswers)

          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(ReporterOrganisationNamePage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value), redirectUsers))
        }
     )
  }
}
