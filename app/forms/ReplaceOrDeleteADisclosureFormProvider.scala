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

package forms

import connectors.CrossBorderArrangementsConnector
import forms.mappings.Mappings
import models.requests.DataRequest
import models.{Country, ReplaceOrDeleteADisclosure}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class ReplaceOrDeleteADisclosureFormProvider @Inject() extends Mappings {

  lazy val startOfUKIDRegex = "^[GB]{2}.*"
  lazy val arrangementIDRegex = "[A-Z]{2}[A]([2]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))([A-Z0-9]{6})"
  lazy val disclosureIDRegex = "[A-Z]{2}[D]([2]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))([A-Z0-9]{6})"

  //GBA20210122IRN44A
  //GBD20210122DM9RTA

  //TODO Rename validatedArrangementIDText

   def apply(countryList: Seq[Country],
             crossBorderArrangementsConnector: CrossBorderArrangementsConnector)
            (implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Form[ReplaceOrDeleteADisclosure] =
     Form(
       mapping(
         "arrangementID" -> validatedArrangementIDText(
           "replaceOrDeleteADisclosure.error.arrangementID.required",
           "replaceOrDeleteADisclosure.error.arrangementID.invalid",
           countryList,
           arrangementIDRegex)
           .verifying("replaceOrDeleteADisclosure.error.arrangementID.notFound",
             id => {
               //TODO Validate non-UK format is correct and GB id exists
               if (id.toUpperCase.matches(startOfUKIDRegex)) {
                 val verifyID = crossBorderArrangementsConnector.verifyArrangementId(id.toUpperCase)

                 Await.result(verifyID, 5 seconds)
               } else {
                 true
               }
             }
           ),
         "disclosureID" -> validatedArrangementIDText(
           "replaceOrDeleteADisclosure.error.disclosureID.required",
           "replaceOrDeleteADisclosure.error.disclosureID.invalid",
           countryList,
           disclosureIDRegex)
           .verifying("replaceOrDeleteADisclosure.error.disclosureID.notFound",
             id => {
               //TODO Validate ID exists and was submitted by user
               if (id.toUpperCase.matches(startOfUKIDRegex)) {
                 val verifyID = crossBorderArrangementsConnector.verifyDisclosureId(id.toUpperCase, request.enrolmentID)

                 Await.result(verifyID, 5 seconds)
               } else {
                 true
               }
             }
           )
       )(ReplaceOrDeleteADisclosure.apply)(ReplaceOrDeleteADisclosure.unapply)
     )
 }
