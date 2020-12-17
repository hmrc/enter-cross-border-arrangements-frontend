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

package utils.mixins

import controllers.Assets.Redirect
import models.{Mode, UserAnswers}
import play.api.mvc.{AnyContent, Request, Result}

import scala.concurrent.Future
import scala.util.Try

trait OnSubmitSuccessMixIn[A] extends PageSubmitMixIn[A] {

  def success(mode: Mode, userAnswers: UserAnswers, value: A, index: Int = 0, alternative: Boolean = false): Result = {
    Redirect(redirect(mode, Some(value), index, alternative))
  }

  def updateAnswers(userAnswers: UserAnswers, value: A)(f: UserAnswers => Result): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(setValue(userAnswers)(value))
      _ <- sessionRepository.set(updatedAnswers)
    } yield f(updatedAnswers)

  def getIndex(request: Request[AnyContent]): Int =
    Try {
      val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
      val uriPattern(_, index) = request.uri

      index.toInt
    }.getOrElse(0)

}
