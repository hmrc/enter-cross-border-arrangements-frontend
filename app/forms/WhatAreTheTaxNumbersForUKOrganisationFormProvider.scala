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

import forms.mappings.Mappings
import javax.inject.Inject
import models.TaxReferenceNumbers
import play.api.data.Form
import play.api.data.Forms.mapping

class WhatAreTheTaxNumbersForUKOrganisationFormProvider @Inject() extends Mappings {

  val maxLength: Int = 200
  val acceptAllRegex = "^.*"

  def apply(): Form[TaxReferenceNumbers] =
    Form(
      mapping(
      "firstTaxNumber" -> validatedText(
        "whatAreTheTaxNumbersForUKOrganisation.error.required",
        "",
        "whatAreTheTaxNumbersForUKOrganisation.label1.error.length",
        acceptAllRegex,
        maxLength),
      "secondTaxNumber" -> validatedOptionalText(
        "",
        "whatAreTheTaxNumbersForUKOrganisation.label2.error.length",
        acceptAllRegex,
        maxLength),
      "thirdTaxNumber" -> validatedOptionalText(
        "",
        "whatAreTheTaxNumbersForUKOrganisation.label3.error.length",
        acceptAllRegex,
        maxLength)
      )(TaxReferenceNumbers.apply)(TaxReferenceNumbers.unapply)
    )
}
