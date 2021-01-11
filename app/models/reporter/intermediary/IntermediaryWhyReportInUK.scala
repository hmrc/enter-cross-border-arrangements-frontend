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

package models.reporter.intermediary

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait IntermediaryWhyReportInUK

object IntermediaryWhyReportInUK extends Enumerable.Implicits {

  case object TaxResidentUK extends WithName("taxResidentUK") with IntermediaryWhyReportInUK
  case object PermanentEstablishment extends WithName("permanentEstablishment") with IntermediaryWhyReportInUK
  case object GovernedByLaw extends WithName("governedByLaw") with IntermediaryWhyReportInUK
  case object RegisteredWithAssociated extends WithName("registeredWithAssociated") with IntermediaryWhyReportInUK
  case object DoNotKnow extends WithName("doNotKnow") with IntermediaryWhyReportInUK

  val values: Seq[IntermediaryWhyReportInUK] = Seq(
    TaxResidentUK,
    PermanentEstablishment,
    GovernedByLaw,
    RegisteredWithAssociated,
    DoNotKnow
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"whyReportInUK.taxResidentUK", TaxResidentUK.toString),
      Radios.Radio(msg"whyReportInUK.permanentEstablishment", PermanentEstablishment.toString),
      Radios.Radio(msg"whyReportInUK.governedByLaw", GovernedByLaw.toString),
      Radios.Radio(msg"whyReportInUK.registeredWithAssociated", RegisteredWithAssociated.toString),
      Radios.Radio(msg"whyReportInUK.doNotKnow", DoNotKnow.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[IntermediaryWhyReportInUK] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
