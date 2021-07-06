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

package pages.intermediaries

import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.listOf
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

class IntermediariesLoopPageSpec extends PageBehaviours {

  "IntermediariesLoopPage" - {

    "must be able to removed all related pages from UserAnswers" in {

      val gen = for {
        userAnswersGen             <- arbitrary[UserAnswers]
        youHaveNotAddedGen         <- arbitraryYouHaveNotAddedAnyIntermediaries.arbitrary
        selectTypeGen              <- arbitrarySelectType.arbitrary
        isExemptionKnownGen        <- arbitraryIsExemptionKnown.arbitrary
        isExemptionCountryKnownGen <- arbitrary[Boolean]
        exemptCountriesGen         <- listOf(arbitraryCountryList.arbitrary)

      } yield (userAnswersGen, youHaveNotAddedGen, selectTypeGen, isExemptionKnownGen, isExemptionCountryKnownGen, exemptCountriesGen)

      forAll(gen) {
        case (userAnswers, youHaveNotAdded, selectType, isExemptionKnown, isExemptionCountryKnown, exemptCountries) =>
          val answersBeforeCleanUp = userAnswers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(YouHaveNotAddedAnyIntermediariesPage, 0, youHaveNotAdded)
            .success
            .value
            .set(IntermediariesTypePage, 0, selectType)
            .success
            .value
            .set(IsExemptionKnownPage, 0, isExemptionKnown)
            .success
            .value
            .set(IsExemptionCountryKnownPage, 0, isExemptionCountryKnown)
            .success
            .value
            .set(ExemptCountriesPage, 0, exemptCountries.toSet)
            .success
            .value
          val answersAfterCleanUp = IntermediaryLoopPage.cleanup(Some(IndexedSeq.empty), answersBeforeCleanUp, 0)
          answersAfterCleanUp.foreach {
            ua =>
              ua.get(YouHaveNotAddedAnyIntermediariesPage, 0) mustBe None
              ua.get(IntermediariesTypePage, 0) mustBe None
              ua.get(IsExemptionKnownPage, 0) mustBe None
              ua.get(IsExemptionCountryKnownPage, 0) mustBe None
              ua.get(ExemptCountriesPage, 0) mustBe None
          }
      }
    }

  }
}
