/*
 * Copyright 2022 HM Revenue & Customs
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

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.affected.YouHaveNotAddedAnyAffectedFormProvider
import models.affected.YouHaveNotAddedAnyAffected
import models.hallmarks.JourneyStatus
import models.{ItemList, Mode, UserAnswers}
import navigation.NavigatorForAffected
import pages.affected.{AffectedLoopPage, AffectedStatusPage, YouHaveNotAddedAnyAffectedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class YouHaveNotAddedAnyAffectedController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForAffected,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: YouHaveNotAddedAnyAffectedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  requireData: DataRequiredAction,
  renderer: Renderer,
  frontendAppConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(YouHaveNotAddedAnyAffectedPage, id) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"         -> preparedForm,
        "id"           -> id,
        "mode"         -> mode,
        "affectedList" -> Json.toJson(toItemList(request.userAnswers, id)),
        "radios"       -> YouHaveNotAddedAnyAffected.radios(preparedForm)
      )

      renderer.render("affected/youHaveNotAddedAnyAffected.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[YouHaveNotAddedAnyAffected]): Call =
    navigator.routeMap(YouHaveNotAddedAnyAffectedPage)(checkRoute)(id)(value)(0)

  private[affected] def toItemList(userAnswers: UserAnswers, id: Int): IndexedSeq[ItemList] = userAnswers.get(AffectedLoopPage, id) match {
    case Some(list) =>
      for {
        affected <- list
      } yield {
        val changeUrl = routes.AffectedCheckYourAnswersController.onPageLoad(id, Option(affected.affectedId)).url
        val removeUrl = routes.AreYouSureYouWantToRemoveAffectedController.onPageLoad(id, affected.affectedId).url
        ItemList(affected.nameAsString, changeUrl, removeUrl)
      }
    case None => IndexedSeq.empty
  }

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"         -> formWithErrors,
              "id"           -> id,
              "mode"         -> mode,
              "affectedList" -> Json.toJson(toItemList(request.userAnswers, id)),
              "radios"       -> YouHaveNotAddedAnyAffected.radios(formWithErrors)
            )

            renderer.render("affected/youHaveNotAddedAnyAffected.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers           <- Future.fromTry(request.userAnswers.set(YouHaveNotAddedAnyAffectedPage, id, value))
              updatedAnswersWithStatus <- Future.fromTry(updatedAnswers.set(AffectedStatusPage, id, setStatus(value, updatedAnswers)))
              _                        <- sessionRepository.set(updatedAnswersWithStatus)
              checkRoute = toCheckRoute(mode, updatedAnswers)
            } yield Redirect(redirect(id, checkRoute, Some(value)))
        )
  }

  private def setStatus(selectedAnswer: YouHaveNotAddedAnyAffected, ua: UserAnswers): JourneyStatus =
    selectedAnswer match {
      case YouHaveNotAddedAnyAffected.YesAddLater => JourneyStatus.InProgress
      case YouHaveNotAddedAnyAffected.No          => JourneyStatus.Completed
      case _                                      => JourneyStatus.NotStarted
    }
}
