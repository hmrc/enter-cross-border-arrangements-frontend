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

package controllers.reporter.intermediary

import controllers.actions._
import forms.reporter.intermediary.IntermediaryRoleFormProvider
import javax.inject.Inject
import models.Mode
import models.reporter.intermediary.IntermediaryRole
import navigation.NavigatorForReporter
import pages.reporter.intermediary.IntermediaryRolePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class IntermediaryRoleController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: IntermediaryRoleFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IntermediaryRolePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios"  -> IntermediaryRole.radios(preparedForm)
      )

      renderer.render("reporter/intermediary/intermediaryRole.njk", json).map(Ok(_))
  }

  def redirect(mode:Mode, value: Option[IntermediaryRole], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForReporter.nextPage(IntermediaryRolePage, mode, value, index, alternative)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> IntermediaryRole.radios(formWithErrors)
          )

          renderer.render("reporter/intermediary/intermediaryRole.njk", json).map(BadRequest(_))
        },
        value => {


          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IntermediaryRolePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(redirect(mode, Some(value), 0))

        }
      )
  }
}
