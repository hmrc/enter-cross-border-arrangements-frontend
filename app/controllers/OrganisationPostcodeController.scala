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

package controllers

import controllers.actions._
import forms.PostcodeFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.{DisplayNamePage, PostcodePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import utils.ViewHelpers._

import scala.concurrent.{ExecutionContext, Future}

class OrganisationPostcodeController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: PostcodeFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  implicit val alternativeText: String = "organisation's name"

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
        "usersName" -> getUsersName(request.userAnswers),
        "manualAddressURL" -> manualAddressURL(mode),
        "actionUrl" -> actionUrl(mode),
        "individual" -> false,
        "mode" -> mode
      )

      renderer.render("postcode.njk", json).map(Ok(_))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {
          val json = Json.obj(
            "form" -> formWithErrors,
            "usersName" -> getUsersName(request.userAnswers),
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
          } yield Redirect(navigator.nextPage(PostcodePage, mode, updatedAnswers))
      )
  }
}
