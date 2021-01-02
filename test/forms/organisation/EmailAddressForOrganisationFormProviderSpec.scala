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

package forms.organisation

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class EmailAddressForOrganisationFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "emailAddressForOrganisation.error.required"
  val invalidKey = "emailAddressForOrganisation.error.invalid"
  val lengthKey = "emailAddressForOrganisation.error.length"
  val maxLength = 254

  val form = new EmailAddressForOrganisationFormProvider()()

  ".email" - {

    val fieldName = "email"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validEmailAddress
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      invalidString = "not a valid email",
      error = FormError(fieldName, invalidKey)
    )

    behave like fieldWithMaxLengthEmail(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
