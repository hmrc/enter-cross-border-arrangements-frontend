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

package controllers.organisation

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.PostcodeFormProvider
import helpers.JourneyHelpers.getOrganisationName
import models.Mode
import navigation.NavigatorForOrganisation
import pages.organisation.PostcodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OrganisationPostcodeController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForOrganisation,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: PostcodeFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  private def manualAddressURL(mode: Mode): String = routes.OrganisationAddressController.onPageLoad(mode).url

  private def actionUrl(mode: Mode) = routes.OrganisationPostcodeController.onSubmit(mode).url

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>


      val preparedForm = request.userAnswers.get(PostcodePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      val json = Json.obj(
        "form" -> preparedForm,
        "displayName" -> getOrganisationName(request.userAnswers),
        "manualAddressURL" -> manualAddressURL(mode),
        "actionUrl" -> actionUrl(mode),
        "individual" -> false,
        "mode" -> mode
      )

      renderer.render("postcode.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[String], index: Int = 0): Call =
    navigator.routeMap(PostcodePage)(checkRoute)(value)(index)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {
          val json = Json.obj(
            "form" -> formWithErrors,
            "displayName" -> getOrganisationName(request.userAnswers),
            "manualAddressURL" -> manualAddressURL(mode),
            "actionUrl" -> actionUrl(mode),
            "individual" -> false,
            "mode" -> mode
          )

          renderer.render("postcode.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PostcodePage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }
}
