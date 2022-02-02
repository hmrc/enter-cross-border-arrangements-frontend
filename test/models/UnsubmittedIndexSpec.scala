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

package models

import base.SpecBase
import pages.SelectTypePage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.JsPath

class UnsubmittedIndexSpec extends SpecBase {
  "Unsubmitted Index" - {
    "must be constructable from an index and useranswers containing that index" in {
      val unsubmittedDisclosures = Seq(
        UnsubmittedDisclosure("1", "My First Disclosure"),
        UnsubmittedDisclosure("2", "The Revenge")
      )

      implicit val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures)
        .success
        .value

      val index = UnsubmittedIndex.fromQuestionPage(SelectTypePage, 0)
      index mustBe UnsubmittedIndex(JsPath \ "1-selectType", 0, SelectTypePage)
    }

    "must not construct from an index when useranswers doesn't have that index" in {
      val unsubmittedDisclosures = Seq(
        UnsubmittedDisclosure("1", "My First Disclosure"),
        UnsubmittedDisclosure("2", "The Revenge")
      )

      implicit val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures)
        .success
        .value

      assertThrows[Exception] {
        UnsubmittedIndex.fromQuestionPage(SelectTypePage, 4)
      }
    }

    "must not construct from an index when useranswers doesn't have any unsubmitted" in {
      implicit val userAnswers = UserAnswers(userAnswersId)

      assertThrows[Exception] {
        UnsubmittedIndex.fromQuestionPage(SelectTypePage, 0)
      }
    }
  }
}
