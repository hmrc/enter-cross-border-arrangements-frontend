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

package models.arrangement

import base.ModelSpecBase
import generators.ModelGenerators
import models.CountryList.UnitedKingdom
import models.arrangement.WhyAreYouReportingThisArrangementNow.Dac6701
import models.{CountryList, UnsubmittedDisclosure, UserAnswers}
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import pages.arrangement.{GiveDetailsOfThisArrangementPage, _}
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate

class ArrangementDetailsSpec extends ModelSpecBase with ModelGenerators {

  "ArrangementDetails" - {

    "buildArrangementDetails" - {

      "must create an ArrangementDetails if mandatory details are available" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value
            .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
            .success
            .value
            .set(WhyAreYouReportingThisArrangementNowPage, 0, Dac6701)
            .success
            .value
            .set(WhichExpectedInvolvedCountriesArrangementPage, 0, CountryList.enumerable.withName("GB").toSet)
            .success
            .value
            .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 100))
            .success
            .value
            .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, "testNationalProvision")
            .success
            .value
            .set(GiveDetailsOfThisArrangementPage, 0, "testArrangementDetails")
            .success
            .value

        val expected = ArrangementDetails(
          arrangementName = "testArrangement",
          implementationDate = LocalDate.now(),
          reportingReason = Some("DAC6701"),
          countriesInvolved = List(UnitedKingdom),
          expectedValue = ExpectedArrangementValue("GBP", 100),
          nationalProvisionDetails = "testNationalProvision",
          arrangementDetails = "testArrangementDetails"
        )

        val arrangementDetails = ArrangementDetails.buildArrangementDetails(userAnswers, 0)

        arrangementDetails mustBe expected
      }

      "must create an ArrangementDetails if all details are available" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value
            .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
            .success
            .value
            .set(WhyAreYouReportingThisArrangementNowPage, 0, Dac6701)
            .success
            .value
            .set(WhyAreYouReportingThisArrangementNowPage, 0, WhyAreYouReportingThisArrangementNow.Dac6701)
            .success
            .value
            .set(WhichExpectedInvolvedCountriesArrangementPage, 0, CountryList.enumerable.withName("GB").toSet)
            .success
            .value
            .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 100))
            .success
            .value
            .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, "testNationalProvision")
            .success
            .value
            .set(GiveDetailsOfThisArrangementPage, 0, "testArrangementDetails")
            .success
            .value

        val expected = ArrangementDetails(
          arrangementName = "testArrangement",
          implementationDate = LocalDate.now(),
          reportingReason = Some("DAC6701"),
          countriesInvolved = List(UnitedKingdom),
          expectedValue = ExpectedArrangementValue("GBP", 100),
          nationalProvisionDetails = "testNationalProvision",
          arrangementDetails = "testArrangementDetails"
        )

        val arrangementDetails = ArrangementDetails.buildArrangementDetails(userAnswers, 0)

        arrangementDetails mustBe expected
      }

      "must throw an Exception if Arrangement name is missing" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value

        val ex = intercept[Exception] {
          ArrangementDetails.buildArrangementDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Arrangement details must contain a name for Disclosure Information"
      }

      "must throw an Exception if expected arrangement implementing date is missing" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value

        val ex = intercept[Exception] {
          ArrangementDetails.buildArrangementDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Arrangement details must contain an expected implementation date for Disclosure Information"
      }

      "must throw an Exception if reporting reason is missing & do you know reason to report is 'yes'" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value
            .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
            .success
            .value

        val ex = intercept[Exception] {
          ArrangementDetails.buildArrangementDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Arrangement details must contain a reporting reason when 'yes' to 'do you know reporting reason' is selected"
      }

      "must throw an Exception if expected involved countries details is missing" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value
            .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
            .success
            .value
            .set(WhyAreYouReportingThisArrangementNowPage, 0, Dac6701)
            .success
            .value

        val ex = intercept[Exception] {
          ArrangementDetails.buildArrangementDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Arrangement details must contain expected involved countries details for Disclosure Information"
      }

      "must throw an Exception if national provision details is missing" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value
            .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
            .success
            .value
            .set(WhyAreYouReportingThisArrangementNowPage, 0, Dac6701)
            .success
            .value
            .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 100))
            .success
            .value
            .set(WhichExpectedInvolvedCountriesArrangementPage, 0, CountryList.enumerable.withName("GB").toSet)
            .success
            .value

        val ex = intercept[Exception] {
          ArrangementDetails.buildArrangementDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Arrangement details must contain national provision details for Disclosure Information"
      }

      "must throw an Exception if details of arrangement are missing" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(WhatIsThisArrangementCalledPage, 0, "testArrangement")
            .success
            .value
            .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
            .success
            .value
            .set(WhyAreYouReportingThisArrangementNowPage, 0, Dac6701)
            .success
            .value
            .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 100))
            .success
            .value
            .set(WhichExpectedInvolvedCountriesArrangementPage, 0, CountryList.enumerable.withName("GB").toSet)
            .success
            .value
            .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, "testNationalProvision")
            .success
            .value

        val ex = intercept[Exception] {
          ArrangementDetails.buildArrangementDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Arrangement details must contain details of arrangement for Disclosure Information"
      }
    }
  }
}
