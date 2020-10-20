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

import forms.behaviours.OptionFieldBehaviours
import play.api.data.FormError

class SelectAddressFormProviderSpec extends OptionFieldBehaviours {

  val form = new SelectAddressFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "selectAddress.error.required"
    val addresses = Seq("Address 1", "Address 2", "Address 3")

    "must bind all valid values" in {
      for(value <- addresses) {

        val result = form.bind(Map(fieldName -> value)).apply(fieldName)
        result.value.value shouldEqual value
      }
    }

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
