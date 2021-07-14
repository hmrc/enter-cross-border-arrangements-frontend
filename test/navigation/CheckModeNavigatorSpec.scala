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

package navigation

import base.SpecBase
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.arrangement._
import pages.QuestionPage
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest

class CheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator                                             = new Navigator
  val index: Int                                            = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "Navigator" - {

    "in Check mode" - {

      def assertRedirect[A](page: QuestionPage[A], route: Call)(f: UserAnswers => UserAnswers) =
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val navigator = new Navigator
            navigator
              .nextPage(page, 0, CheckMode, f(answers))
              .mustBe(route)
        }

      "must go from What is the arrangement called? page to Check your answers page" in {

        assertRedirect(WhatIsThisArrangementCalledPage, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from What is the implementation date? page to Check your answers page" in {

        assertRedirect(WhatIsTheImplementationDatePage, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from the 'Why are reporting this arrangement now?' page to Check your answers page" in {

        assertRedirect(WhyAreYouReportingThisArrangementNowPage, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from 'Which of these countries are expected to be involved in this arrangement?' page to Check your answers page" in {

        assertRedirect(WhichExpectedInvolvedCountriesArrangementPage, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from 'What is the expected value of this arrangement?' page to Check your answers page" in {

        assertRedirect(WhatIsTheExpectedValueOfThisArrangementPage, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from 'Which national provisions is this arrangement based on?' page to Check your answers page" in {

        assertRedirect(WhichNationalProvisionsIsThisArrangementBasedOnPage,
                       controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)
        ) _
      }

      "must go from Details page to Check your answers page" in {

        assertRedirect(WhichNationalProvisionsIsThisArrangementBasedOnPage,
                       controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)
        ) _
      }
    }
  }
}
