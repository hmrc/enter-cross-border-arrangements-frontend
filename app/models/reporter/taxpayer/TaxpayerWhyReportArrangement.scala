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

package models.reporter.taxpayer

import models.{Enumerable, WithName}
import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait TaxpayerWhyReportArrangement

object TaxpayerWhyReportArrangement extends Enumerable.Implicits {

  case object NoIntermediaries extends WithName("DAC61106") with TaxpayerWhyReportArrangement
  case object ProfessionalPrivilege extends WithName("DAC61104") with TaxpayerWhyReportArrangement
  case object OutsideUKOrEU extends WithName("DAC61105") with TaxpayerWhyReportArrangement
  case object DoNotKnow extends WithName("doNotKnow") with TaxpayerWhyReportArrangement

  val values: Seq[TaxpayerWhyReportArrangement] = Seq(
    NoIntermediaries,
    ProfessionalPrivilege,
    OutsideUKOrEU,
    DoNotKnow
  )

  def fromString(name: String): Option[TaxpayerWhyReportArrangement] = TaxpayerWhyReportArrangement.values.find(_.toString == name)

  def radios(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"taxpayerWhyReportArrangement.DAC61106", NoIntermediaries.toString),
      Radios.Radio(msg"taxpayerWhyReportArrangement.DAC61104", ProfessionalPrivilege.toString),
      Radios.Radio(msg"taxpayerWhyReportArrangement.DAC61105", OutsideUKOrEU.toString),
      Radios.Radio(msg"taxpayerWhyReportArrangement.doNotKnow", DoNotKnow.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[TaxpayerWhyReportArrangement] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
