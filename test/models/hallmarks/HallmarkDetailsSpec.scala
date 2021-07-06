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

package models.hallmarks

import base.ModelSpecBase
import generators.ModelGenerators
import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.unsubmitted.UnsubmittedDisclosurePage

class HallmarkDetailsSpec extends ModelSpecBase with ModelGenerators {

  "HallmarkDetails" - {

    "buildHallmarkDetails" - {

      val hallmarkD1parts = Set(
        HallmarkD1.enumerable.withName("DAC6D1a").get,
        HallmarkD1.enumerable.withName("DAC6D1b").get,
        HallmarkD1.enumerable.withName("DAC6D1c").get,
        HallmarkD1.enumerable.withName("DAC6D1d").get,
        HallmarkD1.enumerable.withName("DAC6D1e").get,
        HallmarkD1.enumerable.withName("DAC6D1f").get
      )

      "must create a HallmarkDetails when user selects D2 then submits" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D2").toSet)
            .success
            .value

        val expected = HallmarkDetails(
          hallmarkType = List("DAC6D2"),
          hallmarkContent = None
        )

        val hallmarkDetails = HallmarkDetails.buildHallmarkDetails(userAnswers, 0)

        hallmarkDetails mustBe expected
      }

      "must create a HallmarkDetails when user selects D1, DAC6D1a, DAC6D1b, DAC6D1c, DAC6D1d, DAC6D1e ,DAC6D1f" +
        " then submits" in {

          val userAnswers =
            UserAnswers("id")
              .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
              .success
              .value
              .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
              .success
              .value
              .set(HallmarkD1Page, 0, hallmarkD1parts)
              .success
              .value

          val expected = HallmarkDetails(
            hallmarkType = List("DAC6D1a", "DAC6D1b", "DAC6D1c", "DAC6D1d", "DAC6D1e", "DAC6D1f"),
            hallmarkContent = None
          )

          val hallmarkDetails = HallmarkDetails.buildHallmarkDetails(userAnswers, 0)

          hallmarkDetails mustBe expected
        }

      "must create a HallmarkDetails when user selects D1, D1other & provides info then submits" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
            .success
            .value
            .set(HallmarkD1Page, 0, HallmarkD1.enumerable.withName("DAC6D1Other").toSet)
            .success
            .value
            .set(HallmarkD1OtherPage, 0, "test")
            .success
            .value

        val expected = HallmarkDetails(
          hallmarkType = List("DAC6D1Other"),
          hallmarkContent = Some("test")
        )

        val hallmarkDetails = HallmarkDetails.buildHallmarkDetails(userAnswers, 0)

        hallmarkDetails mustBe expected
      }

      "must create a HallmarkDetails when user selects all options then submits" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(HallmarkDPage, 0, HallmarkD.values.toSet)
            .success
            .value
            .set(HallmarkD1Page, 0, hallmarkD1parts ++ HallmarkD1.enumerable.withName("DAC6D1Other").toSet)
            .success
            .value
            .set(HallmarkD1OtherPage, 0, "test")
            .success
            .value

        val expected = HallmarkDetails(
          hallmarkType = List("DAC6D1Other", "DAC6D1a", "DAC6D1b", "DAC6D1c", "DAC6D1d", "DAC6D1e", "DAC6D1f", "DAC6D2"),
          hallmarkContent = Some("test")
        )

        val hallmarkDetails = HallmarkDetails.buildHallmarkDetails(userAnswers, 0)

        hallmarkDetails mustBe expected
      }

      "must throw an Exception when user D1, D1other but does NOT provide hallmark info then submits" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
            .success
            .value
            .set(HallmarkD1Page, 0, HallmarkD1.enumerable.withName("DAC6D1Other").toSet)
            .success
            .value

        val ex = intercept[Exception] {
          HallmarkDetails.buildHallmarkDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "DAC6D1other information must be provided if DAC6D1other is selected"
      }

      "must throw an Exception when user has not provided all mandatory answers then submits" in {

        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
            .success
            .value

        val ex = intercept[Exception] {
          HallmarkDetails.buildHallmarkDetails(userAnswers, 0)
        }

        ex.getMessage mustEqual "Unable to build hallmark details as missing mandatory answers"
      }
    }
  }
}
