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

package forms.taxpayer

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError

class WhatIsTaxpayersStartDateForImplementingArrangementFormProviderSpec extends DateBehaviours {

  val form = new WhatIsTaxpayersStartDateForImplementingArrangementFormProvider()()

  ".value" - {

    val fieldName = "value"
    val futureDate = LocalDate.of(3000,1, 1)
    val minDate = LocalDate.of(2018, 6, 26)

    val validData = datesBetween(
      min = minDate,
      max = futureDate
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "whatIsTaxpayersStartDateForImplementingArrangement.error.required.all")

    behave like dateFieldWithMax(
      form = form,
      key = fieldName,
      max = futureDate,
      formError = FormError(
        fieldName, "whatIsTaxpayersStartDateForImplementingArrangement.error.futureDate")
    )

    behave like dateFieldWithMin(
      form = form,
      key = fieldName,
      min = minDate,
      formError = FormError(
        fieldName, "whatIsTaxpayersStartDateForImplementingArrangement.error.pastDate"
      )
    )
  }
}
