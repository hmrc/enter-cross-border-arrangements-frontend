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

package controllers.individual

import controllers.actions._
import forms.individual.IndividualNameFormProvider
import models.{Mode, Name, UserAnswers}
import navigation.NavigatorForIndividual
import pages.individual.IndividualNamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualNameController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    formProvider: IndividualNameFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(IndividualNamePage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode
      )

      renderer.render("individual/individualName.njk", json).map(Ok(_))
  }

  def redirect(mode: Mode, value: Option[Name], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForIndividual.nextPage(IndividualNamePage, mode, value, index, alternative)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode
          )

          renderer.render("individual/individualName.njk", json).map(BadRequest(_))
        },

        value => {
          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)

          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(IndividualNamePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(redirect(mode, Some(value)))

        }
      )
  }
}
