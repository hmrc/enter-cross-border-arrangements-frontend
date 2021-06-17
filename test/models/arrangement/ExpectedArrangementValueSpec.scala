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
import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import pages.WhatIsTheExpectedValueOfThisArrangementPage
import pages.unsubmitted.UnsubmittedDisclosurePage

class ExpectedArrangementValueSpec extends ModelSpecBase with ModelGenerators {

  "ExpectedArrangementValue" - {

    "buildExpectedArrangementValue" - {

      "must create an ExpectedArrangementValue if country & value are provided" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success.value
            .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 100))
            .success.value

        val expected = ExpectedArrangementValue(
          currency = "GBP",
          amount = 100
        )

        val expectedValue = ExpectedArrangementValue.buildExpectedArrangementValue(userAnswers, 0)

        expectedValue mustBe expected
      }

      "must throw an Exception if expected arrangement country & value are not provided" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success.value

        val ex = intercept[Exception] {
          ExpectedArrangementValue.buildExpectedArrangementValue(userAnswers, 0)
        }

        ex.getMessage mustEqual "Unable to build ExpectedArrangementValue as missing mandatory answers"
      }
    }
  }
}
