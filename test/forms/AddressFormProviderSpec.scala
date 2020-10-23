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
import play.api.data.FormError

class AddressFormProviderSpec extends StringFieldBehaviours {
  val countries = Seq(Country("valid", "AD", "Andorra"))
  val form = new AddressFormProvider()(countries)

  val addressLineMaxLength = 35

  ".addressLine1" - {

    val fieldName = "addressLine1"
    val invalidKey = "address.error.addressLine1.invalid"
    val lengthKey = "address.error.addressLine1.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validAddressLine
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".addressLine2" - {

    val fieldName = "addressLine2"
    val invalidKey = "address.error.addressLine2.invalid"
    val lengthKey = "address.error.addressLine2.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validAddressLine
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".addressLine3" - {

    val fieldName = "addressLine3"
    val invalidKey = "address.error.addressLine3.invalid"
    val lengthKey = "address.error.addressLine3.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validAddressLine
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".city" - {

    val fieldName = "city"
    val invalidKey = "address.error.city.invalid"
    val lengthKey = "address.error.city.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validAddressLine
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }
}
