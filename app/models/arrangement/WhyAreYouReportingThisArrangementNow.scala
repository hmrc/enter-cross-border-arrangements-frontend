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

package models.arrangement

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait WhyAreYouReportingThisArrangementNow

object WhyAreYouReportingThisArrangementNow extends Enumerable.Implicits {

  case object Dac6701 extends WithName("dAC6701") with WhyAreYouReportingThisArrangementNow
  case object Dac6702 extends WithName("dAC6702") with WhyAreYouReportingThisArrangementNow
  case object Dac6703 extends WithName("dAC6703") with WhyAreYouReportingThisArrangementNow
  case object Dac6704 extends WithName("dAC6704") with WhyAreYouReportingThisArrangementNow

  val values: Seq[WhyAreYouReportingThisArrangementNow] = Seq(
    Dac6701,
    Dac6702,
    Dac6703,
    Dac6704
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"whyAreYouReportingThisArrangementNow.dAC6701", Dac6701.toString),
      Radios.Radio(msg"whyAreYouReportingThisArrangementNow.dAC6702", Dac6702.toString),
      Radios.Radio(msg"whyAreYouReportingThisArrangementNow.dAC6703", Dac6703.toString),
      Radios.Radio(msg"whyAreYouReportingThisArrangementNow.dAC6704", Dac6704.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[WhyAreYouReportingThisArrangementNow] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
