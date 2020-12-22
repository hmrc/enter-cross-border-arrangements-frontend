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

package forms.disclosure

import connectors.CrossBorderArrangementsConnector
import forms.mappings.Mappings
import models.Country
import play.api.data.Form
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
import scala.util.matching.Regex

class DisclosureIdentifyArrangementFormProvider @Inject() extends Mappings {

  val startOfUKIDRegex = "^[GB]{2}.*"
  val arrangementIDRegex = "[A-Z]{2}[A]([2]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))([A-Z0-9]{6})"

  def apply(countryList: Seq[Country],
            crossBorderArrangementsConnector: CrossBorderArrangementsConnector
           )(implicit hc: HeaderCarrier, ec: ExecutionContext): Form[String] =
    Form(
      "arrangementID" -> text("disclosureIdentifyArrangement.error.required")
        .verifying("disclosureIdentifyArrangement.error.notFound",
          id => {
            if (id.matches(startOfUKIDRegex)) {
              val verifyID = crossBorderArrangementsConnector.verifyArrangementId(id)

              Await.result(verifyID, 5 seconds)
            } else {
              true //If value is true, data is valid
            }
          }
        )
        .verifying("disclosureIdentifyArrangement.error.invalid", id => {
          if (id.matches(arrangementIDRegex) && !id.matches(startOfUKIDRegex)) {
            val splitID: Regex = "(^[A-Za-z]{2})([A-Za-z0-9]+)".r
            val splitID(countryCode, _) = id

            countryList.exists(_.code == countryCode)
          } else {
            true //If value is true, data is valid
          }
        })
    )
}
