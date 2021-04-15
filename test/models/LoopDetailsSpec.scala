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

package models

import generators.ModelGenerators
import helpers.data.ValidUserAnswersForSubmission.validTaxResidencies
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class LoopDetailsSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "LoopDetails" - {

    "must read from tax residency" in {

      val loopUK = LoopDetails(
        taxResidentOtherCountries = Some(false),
        whichCountry = Some(Country.UK),
        doYouKnowTIN = None,
        taxNumbersNonUK = None,
        doYouKnowUTR = Some(true),
        taxNumbersUK = Some(TaxReferenceNumbers("UTR1234", None, None))
      )

      val loopNonUK = LoopDetails(
        taxResidentOtherCountries = Some(false),
        whichCountry = Some(Country("", "FR", "France")),
        doYouKnowTIN = Some(true),
        taxNumbersNonUK = Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)),
        doYouKnowUTR = None,
        taxNumbersUK = None
      )

      LoopDetails(validTaxResidencies(0)) mustBe loopUK
      LoopDetails(validTaxResidencies(1)) mustBe loopNonUK
    }
  }
}
