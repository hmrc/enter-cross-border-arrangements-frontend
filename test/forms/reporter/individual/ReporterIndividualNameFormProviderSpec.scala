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

package forms.reporter.individual

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ReporterIndividualNameFormProviderSpec extends StringFieldBehaviours {

  val form = new ReporterIndividualNameFormProvider()()

  ".firstName" - {

    val fieldName = "firstName"
    val requiredKey = "reporterIndividualName.error.firstName.required"
    val lengthKey = "reporterIndividualName.error.firstName.length"
    val maxLength = 200

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".lastName" - {

    val fieldName = "lastName"
    val requiredKey = "reporterIndividualName.error.lastName.required"
    val lengthKey = "reporterIndividualName.error.lastName.length"
    val maxLength = 200

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
