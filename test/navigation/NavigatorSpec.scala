/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.routes
import generators.Generators
import pages._
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, NormalMode, answers)
              .mustBe(routes.IndexController.onPageLoad())
        }
      }

      "must go from 'Which categories of hallmarks are relevant to this arrangement' page " +
        "to 'Which parts of hallmark A apply to this arrangement?' page " +
        "when checkbox A is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("categoryA").toSet)
                  .success
                  .value

            navigator
              .nextPage(HallmarkCategoriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkAController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark A apply to this arrangement?' page " +
        "to 'Does the arrangement meet the Main Benefit Test?' page " +
        "when checkbox A1, A2a, A2b & A3 is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkAPage, HallmarkA.values.toSet)
                .success
                .value

            navigator
              .nextPage(HallmarkAPage, NormalMode, updatedAnswers)
              .mustBe(routes.MainBenefitTestController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Does the arrangement meet the Main Benefit Test?' page " +
        "to 'Check your answers' page when Yes is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(MainBenefitTestPage, true)
                .success
                .value

            navigator
              .nextPage(MainBenefitTestPage, NormalMode, updatedAnswers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad())
        }
      }

      "must go from 'Does the arrangement meet the Main Benefit Test?' page " +
        "to 'There is a problem' page when No is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(MainBenefitTestPage, false)
                .success
                .value

            navigator
              .nextPage(MainBenefitTestPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkCategoriesController.onPageLoad(NormalMode)) // TODO - change to There is a problem page
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator.nextPage(UnknownPage, CheckMode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad())
        }
      }
    }
  }
}
