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

import javax.inject.Inject
import forms.mappings.Mappings
import models.TaxReferenceNumbers
import play.api.data.Form
import play.api.data.Forms.mapping
import utils.RegexConstants

class WhatAreTheTaxNumbersForUKOrganisationFormProvider @Inject() extends Mappings with RegexConstants {

  //TODO Error for text fields

  val taxNumberLength = 13

  def apply(): Form[TaxReferenceNumbers] =
    Form(
      mapping(
      "firstTaxNumber" -> validatedText(
        "whatAreTheTaxNumbersForUKOrganisation.error.required",
        "whatAreTheTaxNumbersForUKOrganisation.error.invalid",
        "whatAreTheTaxNumbersForUKOrganisation.error.length",
        utrRegex,
        taxNumberLength),
      "secondTaxNumber" -> validatedOptionalText(
        "whatAreTheTaxNumbersForUKOrganisation.error.invalid",
        "whatAreTheTaxNumbersForUKOrganisation.error.length",
        utrRegex,
        taxNumberLength),
      "thirdTaxNumber" -> validatedOptionalText(
        "whatAreTheTaxNumbersForUKOrganisation.error.invalid",
        "whatAreTheTaxNumbersForUKOrganisation.error.length",
        utrRegex,
        taxNumberLength)
      )(TaxReferenceNumbers.apply)(TaxReferenceNumbers.unapply)
    )
}
