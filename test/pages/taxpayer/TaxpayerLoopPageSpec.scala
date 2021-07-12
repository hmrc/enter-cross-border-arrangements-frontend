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

package pages.taxpayer

import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate

class TaxpayerLoopPageSpec extends PageBehaviours {

  "TaxpayerLoopPage" - {

    "must be able to removed all related pages from UserAnswers" in {

      implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
        datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
      }

      val gen = for {
        userAnswersGen     <- arbitrary[UserAnswers]
        youHaveNotAddedGen <- arbitraryUpdateTaxpayer.arbitrary
        selectTypeGen      <- arbitrarySelectType.arbitrary
        startDateGen       <- arbitraryLocalDate.arbitrary

      } yield (userAnswersGen, youHaveNotAddedGen, selectTypeGen, startDateGen)

      forAll(gen) {
        case (userAnswers, youHaveNotAdded, selectType, startDate) =>
          val answersBeforeCleanUp = userAnswers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(UpdateTaxpayerPage, 0, youHaveNotAdded)
            .success
            .value
            .set(TaxpayerSelectTypePage, 0, selectType)
            .success
            .value
            .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, startDate)
            .success
            .value
          val answersAfterCleanUp = TaxpayerLoopPage.cleanup(Some(IndexedSeq.empty), answersBeforeCleanUp, 0)
          answersAfterCleanUp.foreach {
            ua =>
              ua.get(UpdateTaxpayerPage, 0) mustBe None
              ua.get(TaxpayerSelectTypePage, 0) mustBe None
              ua.get(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0) mustBe None
          }
      }
    }

  }
}
