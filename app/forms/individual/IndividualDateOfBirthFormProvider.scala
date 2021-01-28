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

package forms.individual

import java.time.LocalDate

import forms.mappings.Mappings
import helpers.DateHelper._
import javax.inject.Inject
import play.api.data.Form

class IndividualDateOfBirthFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "individualDateOfBirth.error.invalid",
        allRequiredKey = "individualDateOfBirth.error.required.all",
        twoRequiredKey = "individualDateOfBirth.error.required.two",
        requiredKey    = "individualDateOfBirth.error.required"
      ).verifying(maxDate(today, "individualDateOfBirth.error.futureDate", formatDateToString(today)))
        .verifying(minDate(LocalDate.of(1903,1,1),"individualDateOfBirth.error.pastDate"))
    )

}
