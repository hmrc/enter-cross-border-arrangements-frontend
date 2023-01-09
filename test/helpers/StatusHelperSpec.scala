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

package helpers

import base.SpecBase
import generators.Generators
import helpers.data.ValidUserAnswersForSubmission.validTaxpayers
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.hallmarks.JourneyStatus
import models.reporter.RoleInArrangement
import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.disclosure.{DisclosureDetailsPage, DisclosureTypePage}
import pages.reporter.RoleInArrangementPage
import pages.taxpayer.TaxpayerLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage

class StatusHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val index                     = 0
  val mockDisclosure            = DisclosureDetails("name", DisclosureType.Dac6new, Some("123"), Some("321"), initialDisclosureMA = true, Some("messageRefID"))
  val mockUnsubmittedDisclosure = UnsubmittedDisclosure("12", "name")

  "StatusHelper" - {

    "checkTaxpayerStatusConditions" - {

      "must return COMPLETE when user is submitting a new arrangement with initial disclosure as marketable" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(DisclosureDetailsPage, index, mockDisclosure)
          .success
          .value
          .set(DisclosureTypePage, index, DisclosureType.Dac6new)
          .success
          .value

        StatusHelper.checkTaxpayerStatusConditions(userAnswers, index) mustBe JourneyStatus.Completed
      }

      "must return COMPLETE when user is submitting a new arrangement with initial disclosure as NOT marketable" +
        "but reporting as a Taxpayer" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureDetailsPage, index, mockDisclosure)
            .success
            .value
            .set(DisclosureTypePage, index, DisclosureType.Dac6new)
            .success
            .value
            .set(RoleInArrangementPage, index, RoleInArrangement.Taxpayer)
            .success
            .value

          StatusHelper.checkTaxpayerStatusConditions(userAnswers, index) mustBe JourneyStatus.Completed
        }

      "must return COMPLETE when user is submitting any arrangement as an Intermediary & has added" +
        "at least 1 relevant taxpayer" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureDetailsPage, index, mockDisclosure.copy(initialDisclosureMA = false))
            .success
            .value
            .set(DisclosureTypePage, index, DisclosureType.Dac6add)
            .success
            .value
            .set(RoleInArrangementPage, index, RoleInArrangement.Intermediary)
            .success
            .value
            .set(TaxpayerLoopPage, index, validTaxpayers)
            .success
            .value

          StatusHelper.checkTaxpayerStatusConditions(userAnswers, index) mustBe JourneyStatus.Completed
        }

      "must return COMPLETE when user is submitting a replacement arrangement with initial disclosure as marketable " +
        "& user is reporting as intermediary & user has not added any taxpayers" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureDetailsPage, index, mockDisclosure)
            .success
            .value
            .set(DisclosureTypePage, index, DisclosureType.Dac6rep)
            .success
            .value
            .set(RoleInArrangementPage, index, RoleInArrangement.Intermediary)
            .success
            .value
            .set(TaxpayerLoopPage, index, IndexedSeq.empty)
            .success
            .value

          StatusHelper.checkTaxpayerStatusConditions(userAnswers, index) mustBe JourneyStatus.Completed
        }

      "must return NOT STARTED for all other conditions" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(DisclosureDetailsPage, index, mockDisclosure.copy(initialDisclosureMA = false))
          .success
          .value
          .set(DisclosureTypePage, index, DisclosureType.Dac6rep)
          .success
          .value
          .set(RoleInArrangementPage, index, RoleInArrangement.Intermediary)
          .success
          .value
          .set(TaxpayerLoopPage, index, IndexedSeq.empty)
          .success
          .value

        StatusHelper.checkTaxpayerStatusConditions(userAnswers, index) mustBe JourneyStatus.NotStarted
      }
    }
  }
}
