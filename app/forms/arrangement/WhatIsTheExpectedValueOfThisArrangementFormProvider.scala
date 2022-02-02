/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.mappings.Mappings
import javax.inject.Inject
import models.Currency
import models.arrangement.ExpectedArrangementValue
import play.api.data.Form
import play.api.data.Forms._

class WhatIsTheExpectedValueOfThisArrangementFormProvider @Inject() extends Mappings {

  def apply(currencyList: Seq[Currency]): Form[ExpectedArrangementValue] = Form(
    mapping(
      "currency" -> text("whatIsTheExpectedValueOfThisArrangement.error.currency.required")
        .verifying("whatIsTheExpectedValueOfThisArrangement.error.currency.required", value => currencyList.exists(_.code == value)),
      "amount" -> int(
        "whatIsTheExpectedValueOfThisArrangement.error.amount.required",
        "whatIsTheExpectedValueOfThisArrangement.error.amount.wholeNumber",
        "whatIsTheExpectedValueOfThisArrangement.error.amount.nonNumeric"
      )
        .verifying(inRange(0, Int.MaxValue, "whatIsTheExpectedValueOfThisArrangement.error.amount.outOfRange"))
    )(ExpectedArrangementValue.apply)(ExpectedArrangementValue.unapply)
  )
}
