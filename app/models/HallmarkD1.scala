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

sealed trait HallmarkD1

object HallmarkD1 extends Enumerable.Implicits {

  case object D1a extends WithName("D1a") with HallmarkD1
  case object D1b extends WithName("D1b") with HallmarkD1
  case object D1c extends WithName("D1c") with HallmarkD1
  case object D1d extends WithName("D1d") with HallmarkD1
  case object D1e extends WithName("D1e") with HallmarkD1
  case object D1f extends WithName("D1f") with HallmarkD1
  case object D1other extends WithName("D1other") with HallmarkD1

  val values: Seq[HallmarkD1] = Seq(
    D1a,
    D1b,
    D1c,
    D1d,
    D1e,
    D1f,
    D1other
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkD1.d1a", D1a.toString),
      Checkboxes.Checkbox(msg"hallmarkD1.d1b", D1b.toString),
      Checkboxes.Checkbox(msg"hallmarkD1.d1c", D1c.toString),
      Checkboxes.Checkbox(msg"hallmarkD1.d1d", D1d.toString),
      Checkboxes.Checkbox(msg"hallmarkD1.d1e", D1e.toString),
      Checkboxes.Checkbox(msg"hallmarkD1.d1f", D1f.toString),
      Checkboxes.Checkbox(msg"hallmarkD1.d1other", D1other.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkD1] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
