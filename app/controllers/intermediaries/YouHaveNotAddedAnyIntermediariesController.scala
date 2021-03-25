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

package controllers.intermediaries

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.intermediaries.YouHaveNotAddedAnyIntermediariesFormProvider
import models.hallmarks.JourneyStatus
import models.intermediaries.YouHaveNotAddedAnyIntermediaries
import models.{ItemList, Mode, NormalMode, UserAnswers}
import navigation.NavigatorForIntermediaries
import pages.intermediaries.{IntermediariesStatusPage, IntermediaryLoopPage, YouHaveNotAddedAnyIntermediariesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class YouHaveNotAddedAnyIntermediariesController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForIntermediaries,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: YouHaveNotAddedAnyIntermediariesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  frontendAppConfig: FrontendAppConfig
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(YouHaveNotAddedAnyIntermediariesPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"       -> preparedForm,
        "id" -> id,
        "mode"       -> mode,
        "intermediaryList" -> Json.toJson(toItemList(request.userAnswers, id)),
        "radios" -> YouHaveNotAddedAnyIntermediaries.radios(preparedForm)
      )

      renderer.render("intermediaries/youHaveNotAddedAnyIntermediaries.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[YouHaveNotAddedAnyIntermediaries]): Call =
    navigator.routeMap(YouHaveNotAddedAnyIntermediariesPage)(checkRoute)(id)(value)(0)

  private[intermediaries] def toItemList(userAnswers: UserAnswers, id: Int): IndexedSeq[ItemList] = userAnswers.get(IntermediaryLoopPage, id) match {
    case Some(list) =>
      for {
        intermediary <- list
      } yield {
        val changeUrl = if (frontendAppConfig.changeLinkToggle) "#" else routes.IntermediariesTypeController.onPageLoad(id, NormalMode).url
        val removeUrl = routes.AreYouSureYouWantToRemoveIntermediaryController.onPageLoad(id, intermediary.intermediaryId).url
        ItemList(intermediary.nameAsString, changeUrl, removeUrl)
      }
    case None => IndexedSeq.empty
  }

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "id" -> id,
            "mode"       -> mode,
            "intermediaryList" -> Json.toJson(toItemList(request.userAnswers, id)),
            "radios" -> YouHaveNotAddedAnyIntermediaries.radios(formWithErrors)
          )

          renderer.render("intermediaries/youHaveNotAddedAnyIntermediaries.njk", json).map(BadRequest(_))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(YouHaveNotAddedAnyIntermediariesPage, id, value))
            updatedAnswersWithStatus <- Future.fromTry(updatedAnswers.set(IntermediariesStatusPage, id, setStatus(value, updatedAnswers)))
            _              <- sessionRepository.set(updatedAnswersWithStatus)
            checkRoute     =  toCheckRoute(mode, updatedAnswers, id)
          } yield Redirect(redirect(id, checkRoute, Some(value)))
        }
      )
  }

  private def setStatus(selectedAnswer: YouHaveNotAddedAnyIntermediaries, ua: UserAnswers): JourneyStatus = {
    selectedAnswer match {
      case YouHaveNotAddedAnyIntermediaries.YesAddLater => JourneyStatus.InProgress
      case YouHaveNotAddedAnyIntermediaries.No => JourneyStatus.Completed
      case _ => JourneyStatus.NotStarted
    }
  }
}
