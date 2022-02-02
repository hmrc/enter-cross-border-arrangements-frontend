/*
 * Copyright 2022 HM Revenue & Customs
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

package pages.enterprises

import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.listOf
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

class AssociatedEnterpriseLoopPageSpec extends PageBehaviours {

  "AssociatedEnterpriseLoopPage" - {

    "must be able to removed all related pages from UserAnswers" in {

      val gen = for {
        userAnswersGen     <- arbitrary[UserAnswers]
        youHaveNotAddedGen <- arbitraryYouHaveNotAddedAnyAssociatedEnterprises.arbitrary
        taxpayersGen       <- listOf(arbitrary[String])
        selectTypeGen      <- arbitrarySelectType.arbitrary
        isAffectedGen      <- arbitrary[Boolean]

      } yield (userAnswersGen, youHaveNotAddedGen, taxpayersGen, selectTypeGen, isAffectedGen)

      forAll(gen) {
        case (userAnswers, youHaveNotAdded, taxpayers, selectType, isAffected) =>
          val answersBeforeCleanUp = userAnswers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, 0, youHaveNotAdded)
            .success
            .value
            .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, taxpayers)
            .success
            .value
            .set(AssociatedEnterpriseTypePage, 0, selectType)
            .success
            .value
            .set(IsAssociatedEnterpriseAffectedPage, 0, isAffected)
            .success
            .value
          val answersAfterCleanUp = AssociatedEnterpriseLoopPage.cleanup(Some(IndexedSeq.empty), answersBeforeCleanUp, 0)
          answersAfterCleanUp.foreach {
            ua =>
              ua.get(YouHaveNotAddedAnyAssociatedEnterprisesPage, 0) mustBe None
              ua.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0) mustBe None
              ua.get(AssociatedEnterpriseTypePage, 0) mustBe None
              ua.get(IsAssociatedEnterpriseAffectedPage, 0) mustBe None
          }
      }
    }

  }
}
