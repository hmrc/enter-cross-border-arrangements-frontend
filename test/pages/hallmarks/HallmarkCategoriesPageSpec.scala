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

package pages.hallmarks

import models.hallmarks.{HallmarkA, HallmarkB, HallmarkCategories}
import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

class HallmarkCategoriesPageSpec extends PageBehaviours {

  "HallmarkCategoriesPage" - {

    beRetrievable[Set[HallmarkCategories]](HallmarkCategoriesPage)

    beSettable[Set[HallmarkCategories]](HallmarkCategoriesPage)

    beRemovable[Set[HallmarkCategories]](HallmarkCategoriesPage)

    "must remove HallmarkBPage if user only selects HallmarkA" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .set(HallmarkBPage, 0, HallmarkB.values.toSet)
            .success.value
            .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("A").toSet)
            .success.value

          result.get(HallmarkBPage, 0) must not be defined
      }
    }

    "must remove HallmarkAPage if user only selects HallmarkB" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .set(HallmarkAPage, 0, HallmarkA.values.toSet)
            .success.value
            .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("B").toSet)
            .success.value

          result.get(HallmarkAPage, 0) must not be defined
      }
    }

    "must remove HallmarkAPage and MainBenefitTestPage if user changes from HallmarkA to HallmarkB" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .set(HallmarkAPage, 0, HallmarkA.values.toSet)
            .success.value
            .set(MainBenefitTestPage, 0, true)
            .success.value
            .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("B").toSet)
            .success.value

          result.get(HallmarkAPage, 0) must not be defined
          result.get(MainBenefitTestPage, 0) must not be defined
      }
    }

    "must remove HallmarkBPage and MainBenefitTestPage if user changes from HallmarkB to HallmarkA" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .set(HallmarkBPage, 0, HallmarkB.values.toSet)
            .success.value
            .set(MainBenefitTestPage, 0, true)
            .success.value
            .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("A").toSet)
            .success.value

          result.get(HallmarkBPage, 0) must not be defined
          result.get(MainBenefitTestPage, 0) must not be defined
      }
    }
  }
}
