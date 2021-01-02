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

package models.hallmarks

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait HallmarkE

object HallmarkE extends Enumerable.Implicits {

  case object HallmarkE1 extends WithName("E1") with HallmarkE
  case object HallmarkE2 extends WithName("E2") with HallmarkE
  case object HallmarkE3 extends WithName("E3") with HallmarkE

  val values: Seq[HallmarkE] = Seq(
    HallmarkE1,
    HallmarkE2,
    HallmarkE3
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkE.hallmarkE1", HallmarkE1.toString),
      Checkboxes.Checkbox(msg"hallmarkE.hallmarkE2", HallmarkE2.toString),
      Checkboxes.Checkbox(msg"hallmarkE.hallmarkE3", HallmarkE3.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkE] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
