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

package utils.controllers

import controllers.routes
import models.{CheckMode, Mode, UserAnswers}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Try

trait OnSubmitIndexMixIn[A, D] extends OnPageLoadIndexMixIn[A, D] {

  val setPage: UserAnswers => A => Try[UserAnswers]

  val setLoopPage: UserAnswers => IndexedSeq[D] => Try[UserAnswers]

  def redirect(mode: Mode, value: Option[A], index: Int = 0, alternative: Boolean = false): Call

  val toDetail: A => D

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getUserAnswers(request) match {

        case Some(userAnswers) =>

          form.bindFromRequest().fold(
            formWithErrors => {

              val json = Json.obj(
                "form" -> formWithErrors,
                "mode" -> mode
              )
              renderer.render(template, pageData(formWithErrors, Some(userAnswers)) ++ json).map(BadRequest(_))
            },
            value => {

              val loopList = getLoopList(mode, userAnswers, value, index)
              updateAnswers(userAnswers, value, loopList) { updatedAnswers =>

                success(mode, updatedAnswers, value, index)
              }
            }
          )
        case _ => Future.successful(Redirect(failOnSubmit()))
      }
  }

  def getLoopList(mode: Mode, userAnswers: UserAnswers, value: A, index: Int): IndexedSeq[D] =
    (getLoopPage(userAnswers), mode, value) match {
      case (None, _, _)                    => IndexedSeq[D](toDetail(value))
      case (Some(list), CheckMode, false)  => list.slice(0, index) // Remove from loop in CheckMode
      case (Some(list), _, true)           => list :+ toDetail(value)
      case (Some(list), _, _)              => list.lift(index).fold(list) { loopDetail =>
        list.updated(index, updatedLoop(loopDetail, value))
      }

    }

  def updatedLoop(ix: D, value: A): D

  def success(mode: Mode, userAnswers: UserAnswers, value: A, index: Int): Result = {
    Redirect(redirect(mode, Some(value), index, false))
  }

  def failOnSubmit(): Call = routes.SessionExpiredController.onPageLoad()

  def updateAnswers(userAnswers: UserAnswers, value: A, loopList: IndexedSeq[D])(f: UserAnswers => Result): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(setPage(userAnswers)(value))
      updatedAnswersWithLoopDetails <- Future.fromTry(setLoopPage(updatedAnswers)(loopList))
      _ <- sessionRepository.set(updatedAnswersWithLoopDetails)
    } yield f(updatedAnswersWithLoopDetails)

}
