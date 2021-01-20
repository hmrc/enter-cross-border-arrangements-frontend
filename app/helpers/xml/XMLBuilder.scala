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

package helpers.xml

import models.{CompletionState, InProgress, UserAnswers}

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

trait XMLBuilder {

  def toXml(userAnswers: UserAnswers): Either[Throwable, Elem]

  def build(from: Either[CompletionState, NodeSeq])(as: NodeSeq => Elem): Either[CompletionState, Elem] =
    from.fold(
      error => Left(error),
      nodes => Try { as(nodes) }.toEither.left.map(_ => InProgress)
    )

}
