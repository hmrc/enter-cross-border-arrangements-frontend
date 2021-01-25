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

package forms.disclosure

import forms.mappings.Mappings
import models.Country
import models.disclosure.ReplaceOrDeleteADisclosure
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class ReplaceOrDeleteADisclosureFormProvider @Inject() extends Mappings {

  lazy val startOfUKIDRegex = "^[GB]{2}.*"
  lazy val arrangementIDRegex = "[A-Z]{2}[A]([2]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))([A-Z0-9]{6})"
  lazy val disclosureIDRegex = "[A-Z]{2}[D]([2]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))([A-Z0-9]{6})"

  //GBA20210122IRN44A
  //GBD20210122DM9RTA

  //TODO Rename validatedArrangementIDText

   def apply(countryList: Seq[Country]): Form[ReplaceOrDeleteADisclosure] =
     Form(
       mapping(
         "arrangementID" -> validatedArrangementIDText(
           "replaceOrDeleteADisclosure.error.arrangementID.required",
           "replaceOrDeleteADisclosure.error.arrangementID.invalid",
           countryList,
           arrangementIDRegex),

         "disclosureID" -> validatedArrangementIDText(
           "replaceOrDeleteADisclosure.error.disclosureID.required",
           "replaceOrDeleteADisclosure.error.disclosureID.invalid",
           countryList,
           disclosureIDRegex)
       )(ReplaceOrDeleteADisclosure.apply)(ReplaceOrDeleteADisclosure.unapply)
     )
 }
