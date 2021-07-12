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

package pages.unsubmitted

import models.{UnsubmittedDisclosure, UserAnswers}
import pages.behaviours.PageBehaviours

class UnsubmittedDisclosurePageSpec extends PageBehaviours {

  "UnsubmittedDisclosurePage" - {

    "must return a valid unsubmitted disclosure from Index" in {

      val unsubmittedDisclosures = Seq(
        UnsubmittedDisclosure("0", "name_0"),
        UnsubmittedDisclosure("1", "name_1", true, true)
      )
      implicit val userAnswers = UserAnswers("internalId")
        .setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures)
        .success
        .value

      UnsubmittedDisclosurePage.fromIndex(1) mustBe (UnsubmittedDisclosure("1", "name_1", true, true))
    }
  }
}
