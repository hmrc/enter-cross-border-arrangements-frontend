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

package models.hallmarks

import models.hallmarks.JourneyStatus.Completed
import models.{Enumerable, WithName}

sealed trait JourneyStatus {

  val isCompleted: Boolean = this == Completed
}

object JourneyStatus extends Enumerable.Implicits {

  case object Completed extends WithName("Completed") with JourneyStatus

  case object InProgress extends WithName("In Progress") with JourneyStatus

  case object NotStarted extends WithName("Not Started") with JourneyStatus

  case object Restricted extends WithName("Cannot start") with JourneyStatus

  val values: Set[JourneyStatus] =
    Set(Completed, InProgress, NotStarted, Restricted)

  implicit val enumerable: Enumerable[JourneyStatus] =
    Enumerable(
      values.toSeq.map(
        v => v.toString -> v
      ): _*
    )
}
