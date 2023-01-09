/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.actions._
import forms.intermediaries.AreYouSureYouWantToRemoveIntermediaryFormProvider
import models.UserAnswers
import pages.intermediaries.IntermediaryLoopPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AreYouSureYouWantToRemoveIntermediaryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AreYouSureYouWantToRemoveIntermediaryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, itemId: String): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = form

      val json = Json.obj(
        "form"   -> preparedForm,
        "id"     -> id,
        "itemId" -> itemId,
        "name"   -> getIntermediaryName(request.userAnswers, id, itemId),
        "radios" -> Radios.yesNo(preparedForm("value"))
      )

      renderer.render("intermediaries/areYouSureYouWantToRemoveIntermediary.njk", json).map(Ok(_))
  }

  def onSubmit(id: Int, itemId: String): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"   -> formWithErrors,
              "id"     -> id,
              "itemId" -> itemId,
              "name"   -> getIntermediaryName(request.userAnswers, id, itemId),
              "radios" -> Radios.yesNo(formWithErrors("value"))
            )

            renderer.render("intermediaries/areYouSureYouWantToRemoveIntermediary.njk", json).map(BadRequest(_))
          },
          value => {
            if (value) {
              val updatedLoop = request.userAnswers.get(IntermediaryLoopPage, id).map(_.filterNot(_.intermediaryId == itemId)).getOrElse(IndexedSeq.empty)
              request.userAnswers.set(IntermediaryLoopPage, id, updatedLoop).foreach(sessionRepository.set)
            }
            Future.successful(Redirect(routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(id)))
          }
        )
  }

  def getIntermediaryName(userAnswers: UserAnswers, id: Int, itemId: String): String =
    userAnswers.get(IntermediaryLoopPage, id).flatMap(_.find(_.intermediaryId == itemId)).map(_.nameAsString).getOrElse("")
}
