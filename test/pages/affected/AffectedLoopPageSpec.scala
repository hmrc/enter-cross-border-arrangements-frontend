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

package pages.affected

import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

class AffectedLoopPageSpec extends PageBehaviours {

  "AffectedLoopPage" - {

    "must be able to removed all related pages from UserAnswers" in {

      val gen = for {
        userAnswersGen <- arbitrary[UserAnswers]
        youHaveNotAddedGen <- arbitraryYouHaveNotAddedAnyAffected.arbitrary
        selectTypeGen <- arbitrarySelectType.arbitrary

      } yield (userAnswersGen, youHaveNotAddedGen, selectTypeGen)

      forAll(gen) { case (userAnswers, youHaveNotAdded, selectType) =>
        val answersBeforeCleanUp = userAnswers
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(YouHaveNotAddedAnyAffectedPage, 0,  youHaveNotAdded).success.value
          .set(AffectedTypePage, 0,  selectType).success.value
        val answersAfterCleanUp = AffectedLoopPage.cleanup(Some(IndexedSeq.empty), answersBeforeCleanUp, 0)
        answersAfterCleanUp.foreach { ua =>
          ua.get(YouHaveNotAddedAnyAffectedPage, 0) mustBe (None)
          ua.get(AffectedTypePage, 0) mustBe (None)
        }
      }
    }

  }
}