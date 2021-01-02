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

sealed trait HallmarkC

object HallmarkC extends Enumerable.Implicits {

  case object C1 extends WithName("C1") with HallmarkC
  case object C2 extends WithName("C2") with HallmarkC
  case object C3 extends WithName("C3") with HallmarkC
  case object C4 extends WithName("C4") with HallmarkC

  val values: Seq[HallmarkC] = Seq(
    C1,
    C2,
    C3,
    C4
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkC.c1", C1.toString),
      Checkboxes.Checkbox(msg"hallmarkC.c2", C2.toString),
      Checkboxes.Checkbox(msg"hallmarkC.c3", C3.toString),
      Checkboxes.Checkbox(msg"hallmarkC.c4", C4.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkC] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
