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

package models.disclosure

import models.{Enumerable, WithName}
import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait DisclosureType

object DisclosureType extends Enumerable.Implicits {

  case object Dac6new extends WithName("dac6new") with DisclosureType
  case object Dac6add extends WithName("dac6add") with DisclosureType
  case object Dac6rep extends WithName("dac6rep") with DisclosureType
  case object Dac6del extends WithName("dac6del") with DisclosureType

  val values: Seq[DisclosureType] = Seq(
    Dac6new,
    Dac6add,
    Dac6rep,
    Dac6del
  )

  def radiosComplete(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"disclosureType.dac6new", Dac6new.toString),
      Radios.Radio(msg"disclosureType.dac6add", Dac6add.toString),
      Radios.Radio(msg"disclosureType.dac6rep", Dac6rep.toString),
      Radios.Radio(msg"disclosureType.dac6del", Dac6del.toString)
    )

    Radios(field, items)
  }

  def radios(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"disclosureType.dac6new", Dac6new.toString),
      Radios.Radio(msg"disclosureType.dac6add", Dac6add.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[DisclosureType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
