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

import base.ModelSpecBase
import models.{Country, LoopDetails, TaxReferenceNumbers}

class TaxResidencySpec extends ModelSpecBase {

  "TaxResidency" - {

    "must be created with taxNumbersUK when Country is 'GB'" in {

      val loopDetailsWithUK = IndexedSeq(LoopDetails(
        taxResidentOtherCountries = Some(true),
        whichCountry              = Some(Country("valid", "GB", "United Kingdom")),
        doYouKnowTIN              = Some(false), // non uk
        taxNumbersNonUK           = None,
        doYouKnowUTR              = Some(true), // uk
        taxNumbersUK              = Some(TaxReferenceNumbers("UTR12345", Some("UTR12345"), Some("UTR12345")))))

      TaxResidency.buildFromLoopDetails(loopDetailsWithUK) mustEqual
      IndexedSeq(TaxResidency(Some(Country("valid", "GB", "United Kingdom")),
        Some(TaxReferenceNumbers("UTR12345", Some("UTR12345"), Some("UTR12345")))))

    }

    "must be created with taxNumbersNonUK when Country is not'GB'" in {

      val loopDetailsWithNonUK = IndexedSeq(LoopDetails(
        taxResidentOtherCountries = Some(true),
        whichCountry              = Some(Country("valid", "FR", "France")),
        doYouKnowTIN              = Some(true), // non uk
        taxNumbersNonUK           = Some(TaxReferenceNumbers("TIN12345678", Some("TIN12345678"), Some("TIN12345678"))),
        doYouKnowUTR              = Some(false), // uk
        taxNumbersUK              = None)
      )

      TaxResidency.buildFromLoopDetails(loopDetailsWithNonUK) mustEqual
      IndexedSeq(TaxResidency(Some(Country("valid", "FR", "France")),
        Some(TaxReferenceNumbers("TIN12345678", Some("TIN12345678"), Some("TIN12345678")))))

    }
  }
}
