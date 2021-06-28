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
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class LoopDetailsSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "LoopDetails" - {

    val loopUK = LoopDetails(
      taxResidentOtherCountries = Some(false),
      whichCountry = Some(Country.UK),
      doYouKnowTIN = None,
      taxNumbersNonUK = None,
      doYouKnowUTR = Some(true),
      taxNumbersUK = Some(TaxReferenceNumbers("UTR1234", None, None))
    )

    val loopFR = LoopDetails(
      taxResidentOtherCountries = Some(false),
      whichCountry = Some(Country("", "FR", "France")),
      doYouKnowTIN = Some(true),
      taxNumbersNonUK = Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)),
      doYouKnowUTR = None,
      taxNumbersUK = None
    )

    val loopCH = LoopDetails(
      taxResidentOtherCountries = Some(false),
      whichCountry = Some(Country("", "CH", "Switzerland")),
      doYouKnowTIN = Some(true),
      taxNumbersNonUK = Some(TaxReferenceNumbers("CS700100B", Some("UTR6789"), None)),
      doYouKnowUTR = None,
      taxNumbersUK = None
    )

    "must read from tax residency" in {

      LoopDetails(validTaxResidencies(0)) mustBe loopUK
      LoopDetails(validTaxResidencies(1)) mustBe loopFR
    }

    "must order starting with UK and then alphabetically" in {
      val listOfCountries = List(loopFR, loopUK, loopCH).sorted
      listOfCountries.head must be(loopUK)
      listOfCountries(1) must be(loopFR)
      listOfCountries(2) must be(loopCH)
    }
  }
}
