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

package models.reporter.taxpayer

import models.{Enumerable, WithName}
import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait TaxpayerWhyReportInUK

object TaxpayerWhyReportInUK extends Enumerable.Implicits {

  case object UkTaxResident extends WithName("ukTaxResident") with TaxpayerWhyReportInUK
  case object UkPermanentEstablishment extends WithName("ukPermanentEstablishment") with TaxpayerWhyReportInUK
  case object IncomeOrProfit extends WithName("incomeOrProfit") with TaxpayerWhyReportInUK
  case object UkActivity extends WithName("ukActivity") with TaxpayerWhyReportInUK
  case object DoNotKnow extends WithName("doNotKnow") with TaxpayerWhyReportInUK

  val values: Seq[TaxpayerWhyReportInUK] = Seq(
    UkTaxResident,
    UkPermanentEstablishment,
    IncomeOrProfit,
    UkActivity,
    DoNotKnow
  )

  def radios(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"taxpayerWhyReportInUK.ukTaxResident", UkTaxResident.toString),
      Radios.Radio(msg"taxpayerWhyReportInUK.ukPermanentEstablishment", UkPermanentEstablishment.toString),
      Radios.Radio(msg"taxpayerWhyReportInUK.incomeOrProfit", IncomeOrProfit.toString),
      Radios.Radio(msg"taxpayerWhyReportInUK.ukActivity", UkActivity.toString),
      Radios.Radio(msg"taxpayerWhyReportInUK.doNotKnow", DoNotKnow.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[TaxpayerWhyReportInUK] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
