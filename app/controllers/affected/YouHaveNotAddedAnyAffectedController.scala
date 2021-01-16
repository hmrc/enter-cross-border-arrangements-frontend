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

package controllers.affected

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.affected.YouHaveNotAddedAnyAffectedFormProvider
import models.affected.YouHaveNotAddedAnyAffected
import models.{Mode, UserAnswers}
import navigation.NavigatorForAffected
import pages.affected.{AffectedLoopPage, YouHaveNotAddedAnyAffectedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class YouHaveNotAddedAnyAffectedController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForAffected,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    formProvider: YouHaveNotAddedAnyAffectedFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val preparedForm = request.userAnswers.flatMap(_.get(YouHaveNotAddedAnyAffectedPage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val namesOfAffected: IndexedSeq[String] = request.userAnswers.flatMap(_.get(AffectedLoopPage)) match {
        case Some(list) =>
          for {
            affected <- list
          } yield {
            affected.nameAsString
          }
        case None => IndexedSeq.empty
      }

      val json = Json.obj(
        "form"       -> preparedForm,
        "mode"       -> mode,
        "affectedList" -> namesOfAffected,
        "radios" -> YouHaveNotAddedAnyAffected.radios(preparedForm)
      )

      renderer.render("affected/youHaveNotAddedAnyAffected.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[YouHaveNotAddedAnyAffected]): Call =
    navigator.routeMap(YouHaveNotAddedAnyAffectedPage)(checkRoute)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "mode"       -> mode,
            "radios" -> YouHaveNotAddedAnyAffected.radios(formWithErrors)
          )

          renderer.render("affected/youHaveNotAddedAnyAffected.njk", json).map(BadRequest(_))
        },
        value => {


          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)

          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(YouHaveNotAddedAnyAffectedPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
        }
      )
  }
}
