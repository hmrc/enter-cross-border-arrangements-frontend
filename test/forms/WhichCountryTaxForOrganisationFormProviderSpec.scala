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

package forms

import forms.behaviours.StringFieldBehaviours
import models.Country
import org.scalacheck.Gen
import play.api.data.FormError

class WhichCountryTaxForOrganisationFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "whichCountryTaxForOrganisation.error.required"
  val countriesSeq: Seq[Country] = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))

  val form = new WhichCountryTaxForOrganisationFormProvider()(countriesSeq)

  ".country" - {

    val fieldName = "country"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      Gen.oneOf(Seq("GB", "FR"))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
