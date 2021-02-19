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

package pages.disclosure

import models.disclosure.{DisclosureType, ReplaceOrDeleteADisclosure}
import models.{GeneratedIDs, UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

class DisclosureDeleteCheckYourAnswersPageSpec extends PageBehaviours {

  "DisclosureDeleteCheckYourAnswersPage" - {

    beRetrievable[GeneratedIDs](DisclosureDeleteCheckYourAnswersPage)

    beSettable[GeneratedIDs](DisclosureDeleteCheckYourAnswersPage)

    beRemovable[GeneratedIDs](DisclosureDeleteCheckYourAnswersPage)

    "must remove replaceOrDelete Information" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .setBase(DisclosureNamePage, "Disclosure").success.value
            .setBase(DisclosureTypePage, DisclosureType.Dac6del).success.value
            .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA20210101ABC123","GBD20210101ABC123"))
            .success
            .value
            .setBase(DisclosureDeleteCheckYourAnswersPage, GeneratedIDs(Some("GBA20210101ABC123"), Some("GBD20210101ABC123")))
            .success
            .value

          result.getBase(ReplaceOrDeleteADisclosurePage) mustBe None
          result.getBase(DisclosureTypePage) mustBe None
          result.getBase(DisclosureNamePage) mustBe None
      }
    }
  }
}
