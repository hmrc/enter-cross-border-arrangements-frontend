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

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class WhatIsTaxpayersStartDateForImplementingArrangementFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "whatIsTaxpayersStartDateForImplementingArrangement.error.invalid",
        allRequiredKey = "whatIsTaxpayersStartDateForImplementingArrangement.error.required.all",
        twoRequiredKey = "whatIsTaxpayersStartDateForImplementingArrangement.error.required.two",
        requiredKey    = "whatIsTaxpayersStartDateForImplementingArrangement.error.required"
      ).verifying(maxDate(LocalDate.of(3000, 1,1), "whatIsTaxpayersStartDateForImplementingArrangement.error.futureDate"))
        .verifying(minDate(LocalDate.of(2018,6,25),"whatIsTaxpayersStartDateForImplementingArrangement.error.pastDate"))
    )
}
