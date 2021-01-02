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

sealed trait HallmarkB

object HallmarkB extends Enumerable.Implicits {

  case object HallmarkB1 extends WithName("B1") with HallmarkB
  case object HallmarkB2 extends WithName("B2") with HallmarkB
  case object HallmarkB3 extends WithName("B3") with HallmarkB

  val values: Seq[HallmarkB] = Seq(
    HallmarkB1,
    HallmarkB2,
    HallmarkB3
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkB.b1", HallmarkB1.toString),
      Checkboxes.Checkbox(msg"hallmarkB.b2", HallmarkB2.toString),
      Checkboxes.Checkbox(msg"hallmarkB.b3", HallmarkB3.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkB] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
