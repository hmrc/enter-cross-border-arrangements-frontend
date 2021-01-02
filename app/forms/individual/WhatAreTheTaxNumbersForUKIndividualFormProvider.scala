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
import models.TaxReferenceNumbers
import play.api.data.Form
import play.api.data.Forms.mapping
import utils.RegexConstants

class WhatAreTheTaxNumbersForUKIndividualFormProvider @Inject() extends Mappings with RegexConstants {

  val maxLength: Int = 200

  def apply(): Form[TaxReferenceNumbers] =
    Form(
      mapping(
        "firstTaxNumber" -> validatedTextMaxLength(
          "whatAreTheTaxNumbersForUKIndividual.error.required",
          "whatAreTheTaxNumbersForUKIndividual.label1.error.length",
          maxLength),
        "secondTaxNumber" -> validatedOptionalTextMaxLength(
          "whatAreTheTaxNumbersForUKIndividual.label2.error.length",
          maxLength),
        "thirdTaxNumber" -> validatedOptionalTextMaxLength(
          "whatAreTheTaxNumbersForUKIndividual.label3.error.length",
          maxLength)
      )(TaxReferenceNumbers.apply)(TaxReferenceNumbers.unapply)
    )
}
