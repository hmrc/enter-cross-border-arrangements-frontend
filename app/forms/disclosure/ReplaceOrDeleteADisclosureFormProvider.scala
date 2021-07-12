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
import utils.RegexConstants

import javax.inject.Inject

class ReplaceOrDeleteADisclosureFormProvider @Inject() extends Mappings with RegexConstants {

  def apply(countryList: Seq[Country]): Form[ReplaceOrDeleteADisclosure] =
    Form(
      mapping(
        "arrangementID" -> validatedDisclosureIDsText("replaceOrDeleteADisclosure.error.arrangementID.required",
                                                      "replaceOrDeleteADisclosure.error.arrangementID.invalid",
                                                      countryList,
                                                      arrangementIDRegex
        ),
        "disclosureID" -> validatedDisclosureIDsText("replaceOrDeleteADisclosure.error.disclosureID.required",
                                                     "replaceOrDeleteADisclosure.error.disclosureID.invalid",
                                                     countryList,
                                                     disclosureIDRegex
        )
      )(ReplaceOrDeleteADisclosure.apply)(ReplaceOrDeleteADisclosure.unapply)
    )
}
