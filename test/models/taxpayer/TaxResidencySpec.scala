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

package models.taxpayer

import models.{Country, LoopDetails, TaxReferenceNumbers}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TaxResidencySpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "TaxResidency" - {

    "must be created with taxNumbersUK when Country is 'GB'" in {

      val loopDetailsWithUK = IndexedSeq(LoopDetails(Some(true),
        Some(Country("valid", "GB", "United Kingdom")),
        Some(true),
        None,
        Some(false),
        Some(TaxReferenceNumbers("UTR12345", Some("UTR12345"), Some("UTR12345")))))

      TaxResidency.buildFromLoopDetails(loopDetailsWithUK) mustEqual
      IndexedSeq(TaxResidency(Some(Country("valid", "GB", "United Kingdom")),
        Some(TaxReferenceNumbers("UTR12345", Some("UTR12345"), Some("UTR12345")))))

    }

    "must be created with taxNumbersNonUK when Country is not'GB'" in {

      val loopDetailsWithNonUK = IndexedSeq(LoopDetails(Some(true),
        Some(Country("valid", "FR", "France")),
        Some(true),
        Some(TaxReferenceNumbers("TIN12345678", Some("TIN12345678"), Some("TIN12345678"))),
        Some(false),
        None)
      )

      TaxResidency.buildFromLoopDetails(loopDetailsWithNonUK) mustEqual
      IndexedSeq(TaxResidency(Some(Country("valid", "FR", "France")),
        Some(TaxReferenceNumbers("TIN12345678", Some("TIN12345678"), Some("TIN12345678")))))

    }
  }
}
