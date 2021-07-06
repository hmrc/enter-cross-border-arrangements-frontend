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

import forms.behaviours.StringFieldBehaviours
import models.Country
import play.api.data.FormError

class AddressFormProviderSpec extends StringFieldBehaviours {
  val countries = Seq(Country("valid", "AD", "Andorra"))
  val form      = new AddressFormProvider()(countries)

  val addressLineMaxLength = 400
  val postcodeMaxLength    = 10

  ".addressLine1" - {

    val fieldName = "addressLine1"
    val lengthKey = "address.error.addressLine1.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )
  }

  ".addressLine2" - {

    val fieldName = "addressLine2"
    val lengthKey = "address.error.addressLine2.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )
  }

  ".addressLine3" - {

    val fieldName = "addressLine3"
    val lengthKey = "address.error.addressLine3.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )
  }

  ".city" - {

    val fieldName   = "city"
    val lengthKey   = "address.error.city.length"
    val requiredKey = "address.error.city.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".postCode" - {

    val fieldName = "postCode"
    val lengthKey = "address.error.postcode.optional.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(postcodeMaxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = postcodeMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(postcodeMaxLength))
    )
  }

  ".country" - {

    val fieldName   = "country"
    val requiredKey = "address.error.country.required"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
