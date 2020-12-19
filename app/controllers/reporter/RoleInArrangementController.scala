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

package controllers.reporter

import controllers.actions._
import forms.reporter.RoleInArrangementFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import models.reporter.RoleInArrangement
import helpers.JourneyHelpers.hasValueChanged
import navigation.NavigatorForReporter
import pages.reporter.RoleInArrangementPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class RoleInArrangementController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    formProvider: RoleInArrangementFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      //TODO - Change back to below method & add requireData when full journey is built

      val preparedForm = request.userAnswers.flatMap(_.get(RoleInArrangementPage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios"  -> RoleInArrangement.radios(preparedForm)
      )

      renderer.render("reporter/roleInArrangement.njk", json).map(Ok(_))
  }

//  def redirect(mode: Mode, value: Option[Boolean], index: Int = 0, alternative: Boolean = false): Call =
//    NavigatorForIndividual.nextPage(DoYouKnowAnyTINForUKIndividualPage, mode, value, index, alternative)

  def redirect(mode:Mode, value: Option[RoleInArrangement], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForReporter.nextPage(RoleInArrangementPage, mode, value, index, alternative)


  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      //TODO - add requireData back to when full journey is built

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> RoleInArrangement.radios(formWithErrors)
          )

          renderer.render("reporter/roleInArrangement.njk", json).map(BadRequest(_))
        },
        value => {
          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)
          val redirectUsers = hasValueChanged(value, RoleInArrangementPage, mode, userAnswers)

          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(RoleInArrangementPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(redirect(mode, Some(value), 0, redirectUsers))

        }
      )
  }
}
