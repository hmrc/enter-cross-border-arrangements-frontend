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
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.reporter.ReporterEmailAddressFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NavigatorForReporter
import pages.reporter.individual.ReporterIndividualEmailAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class ReporterIndividualEmailAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForReporter,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReporterEmailAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private def actionUrl(id: Int, mode: Mode): String = routes.ReporterIndividualEmailAddressController.onSubmit(id, mode).url

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(ReporterIndividualEmailAddressPage, id) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"        -> preparedForm,
        "mode"        -> mode,
        "pageTitle"   -> "reporterIndividualEmailAddress.title",
        "actionUrl"   -> actionUrl(id, mode),
        "pageHeading" -> "reporterIndividualEmailAddress.heading"
      )

      renderer.render("reporter/reporterEmailAddress.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[String], index: Int = 0): Call =
    navigator.routeMap(ReporterIndividualEmailAddressPage)(checkRoute)(id)(value)(index)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"        -> formWithErrors,
              "mode"        -> mode,
              "pageTitle"   -> "reporterIndividualEmailAddress.title",
              "actionUrl"   -> actionUrl(id, mode),
              "pageHeading" -> "reporterIndividualEmailAddress.heading"
            )

            renderer.render("reporter/reporterEmailAddress.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterIndividualEmailAddressPage, id, value))
              _              <- sessionRepository.set(updatedAnswers)
              checkRoute = toCheckRoute(mode, updatedAnswers, id)
            } yield Redirect(redirect(id, checkRoute, Some(value)))
        )
  }
}
