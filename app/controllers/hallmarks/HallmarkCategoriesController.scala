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

package controllers.hallmarks

import controllers.actions._
import forms.HallmarkCategoriesFormProvider
import javax.inject.Inject
import models.hallmarks.HallmarkCategories
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.hallmarks.HallmarkCategoriesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class HallmarkCategoriesController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    formProvider: HallmarkCategoriesFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(HallmarkCategoriesPage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"       -> preparedForm,
        "mode"       -> mode,
        "checkboxes" -> HallmarkCategories.checkboxes(preparedForm)
      )

      renderer.render("hallmarks/hallmarkCategories.njk",json).map(Ok(_))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "mode"       -> mode,
            "checkboxes" -> HallmarkCategories.checkboxes(formWithErrors)
          )

          renderer.render("hallmarks/hallmarkCategories.njk",json).map(BadRequest(_))
        },
        value => {
          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)

          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(HallmarkCategoriesPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(HallmarkCategoriesPage, mode, updatedAnswers))

        }
      )
  }
}
