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

sealed trait HallmarkC1

object HallmarkC1 extends Enumerable.Implicits {

  case object C1a extends WithName("C1a") with HallmarkC1
  case object C1bi extends WithName("C1bi") with HallmarkC1
  case object C1bii extends WithName("C1bii") with HallmarkC1
  case object C1c extends WithName("C1c") with HallmarkC1
  case object C1d extends WithName("C1d") with HallmarkC1

  val values: Seq[HallmarkC1] = Seq(
    C1a,
    C1bi,
    C1bii,
    C1c,
    C1d
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkC1.c1a", C1a.toString),
      Checkboxes.Checkbox(msg"hallmarkC1.c1bi", C1bi.toString),
      Checkboxes.Checkbox(msg"hallmarkC1.c1bii", C1bii.toString),
      Checkboxes.Checkbox(msg"hallmarkC1.c1c", C1c.toString),
      Checkboxes.Checkbox(msg"hallmarkC1.c1d", C1d.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkC1] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
