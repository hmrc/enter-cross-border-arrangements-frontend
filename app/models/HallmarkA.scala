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

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait HallmarkA

object HallmarkA extends Enumerable.Implicits {

  case object HallmarkA1 extends WithName("A1") with HallmarkA
  case object HallmarkA2a extends WithName("A2a") with HallmarkA
  case object HallmarkA2b extends WithName("A2b") with HallmarkA
  case object HallmarkA3 extends WithName("A3") with HallmarkA

  val values: Seq[HallmarkA] = Seq(
    HallmarkA1,
    HallmarkA2a,
    HallmarkA2b,
    HallmarkA3
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkA.option1", HallmarkA1.toString),
      Checkboxes.Checkbox(msg"hallmarkA.option2", HallmarkA2a.toString),
      Checkboxes.Checkbox(msg"hallmarkA.option3", HallmarkA2b.toString),
      Checkboxes.Checkbox(msg"hallmarkA.option4", HallmarkA3.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkA] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
