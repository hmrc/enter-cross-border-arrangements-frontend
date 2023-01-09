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

package models

import base.SpecBase
import models.SelectType.Organisation
import pages.SelectTypePage
import pages.unsubmitted.UnsubmittedDisclosurePage

class UserAnswersSpec extends SpecBase {
  "UserAnswers" - {
    "must be able to retrieve a question from an unsubmitted disclosure" in {
      val unsubmittedDisclosures = Seq(
        UnsubmittedDisclosure("1", "My First Disclosure"),
        UnsubmittedDisclosure("2", "The Revenge")
      )

      implicit val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures)
        .success
        .value
        .setBase(SelectTypePage, Organisation)
        .success
        .value

      userAnswers.get(SelectTypePage, 0) mustBe None
    }
  }
}
