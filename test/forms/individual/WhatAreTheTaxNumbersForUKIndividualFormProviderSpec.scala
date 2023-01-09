/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.individual

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhatAreTheTaxNumbersForUKIndividualFormProviderSpec extends StringFieldBehaviours {

  val requiredKey     = "whatAreTheTaxNumbersForUKIndividual.error.required"
  val lengthKeyLabel1 = "whatAreTheTaxNumbersForUKIndividual.label1.error.length"
  val lengthKeyLabel2 = "whatAreTheTaxNumbersForUKIndividual.label2.error.length"
  val lengthKeyLabel3 = "whatAreTheTaxNumbersForUKIndividual.label3.error.length"
  val maxLength       = 200

  val form = new WhatAreTheTaxNumbersForUKIndividualFormProvider()()

  ".firstTaxNumber" - {

    val fieldName = "firstTaxNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKeyLabel1)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".secondTaxNumber" - {

    val fieldName = "secondTaxNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKeyLabel2)
    )
  }

  ".thirdTaxNumber" - {

    val fieldName = "thirdTaxNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKeyLabel3)
    )
  }
}
