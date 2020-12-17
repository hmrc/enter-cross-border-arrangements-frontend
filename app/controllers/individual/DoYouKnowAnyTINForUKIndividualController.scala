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
import forms.individual.DoYouKnowAnyTINForUKIndividualFormProvider
import helpers.JourneyHelpers.currentIndexInsideLoop
import models.{LoopDetails, Mode, UserAnswers}
import navigation.NavigatorForIndividual
import pages.individual.{DoYouKnowAnyTINForUKIndividualPage, IndividualLoopPage, IndividualNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DoYouKnowAnyTINForUKIndividualController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: DoYouKnowAnyTINForUKIndividualFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IndividualLoopPage) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val doYouKnowUTR = value.lift(index).get.doYouKnowUTR
          if (doYouKnowUTR.isDefined) {
            form.fill(doYouKnowUTR.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios" -> Radios.yesNo(preparedForm("confirm")),
        "name" -> getIndividualName(request.userAnswers),
        "index" -> index
      )

      renderer.render("individual/doYouKnowAnyTINForUKIndividual.njk", json).map(Ok(_))
  }

  def redirect(mode: Mode, value: Option[Boolean], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForIndividual.nextPage(DoYouKnowAnyTINForUKIndividualPage, mode, value, index, alternative)

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> Radios.yesNo(formWithErrors("confirm")),
            "name" -> getIndividualName(request.userAnswers),
            "index" -> index
          )

          renderer.render("individual/doYouKnowAnyTINForUKIndividual.njk", json).map(BadRequest(_))
        },
        value => {

          val individualLoopList = request.userAnswers.get(IndividualLoopPage) match {
            case None =>
              val newIndividualLoop = LoopDetails(None, None, None, None, doYouKnowUTR = Some(value), None)
              IndexedSeq[LoopDetails](newIndividualLoop)
            case Some(list) =>
              if (list.lift(index).isDefined) {
                val updatedLoop = list.lift(index).get.copy(doYouKnowUTR = Some(value))
                list.updated(index, updatedLoop)
              } else {
                list
              }
          }

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DoYouKnowAnyTINForUKIndividualPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(IndividualLoopPage, individualLoopList))
            _ <- sessionRepository.set(updatedAnswersWithLoopDetails)
          } yield Redirect(redirect(mode, Some(value), currentIndexInsideLoop(request)))
        }
      )

  }

  private def getIndividualName(userAnswers: UserAnswers): String = {
    userAnswers.get(IndividualNamePage) match {
      case Some(name) => s"${name.firstName + " " + name.secondName + "’s"}"
      case None => "their"
    }
  }
}
