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

package models.intermediaries

import play.api.data.{Field, Form}
import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.viewmodels._
import models._

sealed trait WhatTypeofIntermediary

object WhatTypeofIntermediary extends Enumerable.Implicits {

  case object Promoter extends WithName("promoter") with WhatTypeofIntermediary
  case object Serviceprovider extends WithName("serviceProvider") with WhatTypeofIntermediary
  case object IDoNotKnow extends WithName("iDoNotKnow") with WhatTypeofIntermediary

  val values: Seq[WhatTypeofIntermediary] = Seq(
    Promoter,
    Serviceprovider,
    IDoNotKnow
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[RadiosHint.Item] = {

    val field = form("value")
    val items = Seq(
      RadiosHint.Radio(msg"whatTypeofIntermediary.promoter", Promoter.toString,
        Some(RadiosHint.Hint(msg"whatTypeofIntermediary.promoter.hint"))),
      RadiosHint.Radio(msg"whatTypeofIntermediary.serviceProvider", Serviceprovider.toString,
        Some(RadiosHint.Hint(msg"whatTypeofIntermediary.serviceProvider.hint"))),
      RadiosHint.Radio(msg"whatTypeofIntermediary.iDoNotKnow", Serviceprovider.toString, None)
    )
    RadiosHint(field, items)
  }

  implicit val enumerable: Enumerable[WhatTypeofIntermediary] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

object RadiosHint {

  final case class Hint(text: Text, classes: String = "govuk-hint govuk-radios__hint")
  final case class Radio(label: Text, value: String, hint: Option[Hint])
  final case class Item(id: String, text: Text, hint: Option[Hint], value: String, checked: Boolean)

  object Item {
    implicit def writes(implicit messages: Messages): OWrites[Item] =
      Json.writes[Item]
  }

  object Hint {
    implicit def writes(implicit messages: Messages): OWrites[Hint] =
      Json.writes[Hint]
  }

  def apply(field: Field, items: Seq[Radio]): Seq[Item] = {

    val head = items.headOption.map {
      item =>
        Item(
          id      = field.id,
          text    = item.label,
          value   = item.value,
          hint = item.hint,
          checked = field.values.contains(item.value)
        )
    }

    val tail = items.zipWithIndex.tail.map {
      case (item, i) =>
        Item(
          id      = s"${field.id}_$i",
          text    = item.label,
          value   = item.value,
          hint = item.hint,
          checked = field.values.contains(item.value)
        )
    }

    head.toSeq ++ tail
  }
}
