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

package utils

import base.SpecBase
import models.{Country, OrganisationLoopDetails, TaxReferenceNumbers}
import org.scalatestplus.mockito.MockitoSugar

class CheckYourAnswersOrganisationHelperSpec extends SpecBase with MockitoSugar {

  "Check Your Answers Organisation Helper" - {

    val mockCountryLoop = IndexedSeq(
      OrganisationLoopDetails(
        Some(true),
        Some(Country("", "GB", "United Kingdom")),
        Some(true),
        Some(TaxReferenceNumbers("TIN12345678", None, None)))
    )

    "buildTaxResidencySummary" - {


    }
  }
}
