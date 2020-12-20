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

package models.reporter.intermediary

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait IntermediaryExemptionInEU

object IntermediaryExemptionInEU extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with IntermediaryExemptionInEU
  case object No extends WithName("no") with IntermediaryExemptionInEU

  val values: Seq[IntermediaryExemptionInEU] = Seq(
    Yes,
    No
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"intermediaryExemptionInEU.yes", Yes.toString),
      Radios.Radio(msg"intermediaryExemptionInEU.no", No.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[IntermediaryExemptionInEU] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
