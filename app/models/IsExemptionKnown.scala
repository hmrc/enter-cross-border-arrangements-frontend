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

package models

import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.viewmodels._

sealed trait IsExemptionKnown

object IsExemptionKnown extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with IsExemptionKnown
  case object No extends WithName("no") with IsExemptionKnown
  case object Unknown extends WithName("unknown") with IsExemptionKnown

  val values: Seq[IsExemptionKnown] = Seq(
    Yes,
    No,
    Unknown
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"isExemptionKnown.yes", Yes.toString),
      Radios.Radio(msg"isExemptionKnown.no", No.toString),
      Radios.Radio(msg"isExemptionKnown.unknown", Unknown.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[IsExemptionKnown] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
