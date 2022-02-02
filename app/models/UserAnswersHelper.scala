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

package models

import pages.QuestionPage
import play.api.libs.json.{Reads, Writes}

import scala.concurrent.Future

object UserAnswersHelper {

  def updateUserAnswers[A](userAnswers: UserAnswers, id: Int, page: QuestionPage[A], value: A)(implicit rds: Reads[A], wrs: Writes[A]): Future[UserAnswers] =
    userAnswers.get(page, id) match {
      case Some(oldValue) if oldValue == value => Future.successful(userAnswers)
      case _                                   => Future.fromTry(userAnswers.set(page, id, value))
    }
}
