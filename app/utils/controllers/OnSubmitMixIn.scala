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
import models.{Mode, UserAnswers}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Try

trait OnSubmitMixIn[A] extends OnPageLoadMixIn[A] {

  val setPage: UserAnswers => A => Try[UserAnswers]

  def redirect(mode: Mode, value: Option[A], index: Int = 0, alternative: Boolean = false): Call

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
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
            value => updateAnswers(userAnswers, value) { updatedAnswers =>

              success(mode, updatedAnswers, value, getIndex(request))
            }
          )
        case _ => Future.successful(Redirect(failOnSubmit()))
      }
  }

  def success(mode: Mode, userAnswers: UserAnswers, value: A, index: Int = 0): Result = {
    val previousValue: Option[A] = getPage(userAnswers)
    val alternative = previousValue.exists(_ != value)
    Redirect(redirect(mode, Some(value), index, alternative))
  }

  def failOnSubmit(): Call = routes.SessionExpiredController.onPageLoad()

  def updateAnswers(userAnswers: UserAnswers, value: A)(f: UserAnswers => Result): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(setPage(userAnswers)(value))
      _ <- sessionRepository.set(updatedAnswers)
    } yield f(updatedAnswers)

  def getIndex(request: Request[AnyContent]): Int =
    Try {
      val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
      val uriPattern(_, index) = request.uri

      index.toInt
    }.getOrElse(0)

}
