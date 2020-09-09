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

sealed trait HallmarkCategories

object HallmarkCategories extends Enumerable.Implicits {

  case object Option1 extends WithName("option1") with HallmarkCategories
  case object Option2 extends WithName("option2") with HallmarkCategories
  case object Option3 extends WithName("option3") with HallmarkCategories
  case object Option4 extends WithName("option4") with HallmarkCategories
  case object Option5 extends WithName("option5") with HallmarkCategories

  val values: Seq[HallmarkCategories] = Seq(
    Option1,
    Option2,
    Option3,
    Option4,
    Option5
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"hallmarkCategories.option1", Option1.toString),
      Checkboxes.Checkbox(msg"hallmarkCategories.option2", Option2.toString),
      Checkboxes.Checkbox(msg"hallmarkCategories.option3", Option3.toString),
      Checkboxes.Checkbox(msg"hallmarkCategories.option4", Option4.toString),
      Checkboxes.Checkbox(msg"hallmarkCategories.option5", Option5.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[HallmarkCategories] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
