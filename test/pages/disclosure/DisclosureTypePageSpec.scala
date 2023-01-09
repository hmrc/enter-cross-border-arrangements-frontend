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

package pages.disclosure

import models.disclosure.DisclosureType
import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.unsubmitted.UnsubmittedDisclosurePage

class DisclosureTypePageSpec extends PageBehaviours {

  "DisclosureTypePage" - {

    beRetrievable[DisclosureType](DisclosureTypePage)

    beSettable[DisclosureType](DisclosureTypePage)

    beRemovable[DisclosureType](DisclosureTypePage)
  }

  "must remove arrangement ID value if 'A new arrangement' is selected" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        val result = answers
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureIdentifyArrangementPage, 0, "GBA20210101ABC123")
          .success
          .value
          .set(DisclosureTypePage, 0, DisclosureType.Dac6new)
          .success
          .value

        result.get(DisclosureIdentifyArrangementPage, 0) mustBe None
    }
  }

  "must remove marketable arrangement value if 'An addition to an existing arrangement' is selected" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        val result = answers
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureMarketablePage, 0, true)
          .success
          .value
          .set(DisclosureTypePage, 0, DisclosureType.Dac6add)
          .success
          .value

        result.get(DisclosureMarketablePage, 0) mustBe None
    }
  }

}
