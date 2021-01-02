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

package forms.arrangement

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class WhatIsTheImplementationDateFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "whatIsTheImplementationDate.error.invalid",
        allRequiredKey = "whatIsTheImplementationDate.error.required.all",
        twoRequiredKey = "whatIsTheImplementationDate.error.required.two",
        requiredKey    = "whatIsTheImplementationDate.error.required",
        nonNumericKey =  "whatIsTheImplementationDate.error.nonNumericKey"
      ).verifying(minDate(LocalDate.of(2018,6,25),"whatIsTheImplementationDate.error.pastDate"))
    )
}
