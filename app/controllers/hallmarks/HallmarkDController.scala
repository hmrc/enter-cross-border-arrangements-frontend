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

package controllers.hallmarks

import controllers.actions._
import forms.hallmarks.HallmarkDFormProvider

import javax.inject.Inject
import models.hallmarks.HallmarkD
import models.hallmarks.HallmarkD.D1
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.hallmarks.{HallmarkD1Page, HallmarkDPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class HallmarkDController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: HallmarkDFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData ).async {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(HallmarkDPage, id)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"       -> preparedForm,
        "id" -> id,
        "mode"       -> mode,
        "checkboxes" -> HallmarkD.checkboxes(preparedForm)
      )

      renderer.render("hallmarks/hallmarkD.njk",json).map(Ok(_))
  }

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "id" -> id,
            "mode"       -> mode,
            "checkboxes" -> HallmarkD.checkboxes(formWithErrors)
          )

          renderer.render("hallmarks/hallmarkD.njk", json).map(BadRequest(_))
        },
        value => {
          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)

          for {
            userAnswers <- Future.fromTry(removeD1Parts(userAnswers, id, value))
            updatedAnswers <- Future.fromTry(userAnswers.set(HallmarkDPage, id, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(HallmarkDPage, id, mode, updatedAnswers))
        }
      )
  }

  private def removeD1Parts(userAnswers: UserAnswers, id:Int, values: Set[HallmarkD]): Try[UserAnswers] =
    userAnswers.get(HallmarkD1Page, id) match {
      case Some(_) if !values.contains(D1) => userAnswers.remove(HallmarkD1Page, id)
      case _ => Success(userAnswers)
    }

}
