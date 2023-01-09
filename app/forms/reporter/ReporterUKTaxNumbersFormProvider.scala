/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.reporter

import forms.mappings.Mappings

import javax.inject.Inject
import models.TaxReferenceNumbers
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

class ReporterUKTaxNumbersFormProvider @Inject() extends Mappings {

  val maxLength: Int = 200

  def apply(reporterType: String)(implicit messages: Messages): Form[TaxReferenceNumbers] =
    Form(
      mapping(
        "firstTaxNumber" -> validatedTextMaxLength(messages(s"${reporterType}UKTaxNumbers.error.required"),
                                                   "reporterUKTaxNumbers.error.length.label1",
                                                   maxLength
        ),
        "secondTaxNumber" -> validatedOptionalTextMaxLength("reporterUKTaxNumbers.error.length.label2", maxLength),
        "thirdTaxNumber"  -> validatedOptionalTextMaxLength("reporterUKTaxNumbers.error.length.label3", maxLength)
      )(TaxReferenceNumbers.apply)(TaxReferenceNumbers.unapply)
    )
}
