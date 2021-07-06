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

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import utils.RegexConstants

class EmailAddressForIndividualFormProvider @Inject() extends Mappings with RegexConstants {

  private val maxLength = 400

  def apply(): Form[String] =
    Form(
      "email" -> validatedText("emailAddressForIndividual.error.required",
                               "emailAddressForIndividual.error.invalid",
                               "emailAddressForIndividual.error.length",
                               emailRegex,
                               maxLength
      )
    )
}
