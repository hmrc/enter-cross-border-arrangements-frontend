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
import models.hallmarks._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.arrangement._
import pages.hallmarks._
import pages.unsubmitted.UnsubmittedDisclosurePage
import pages.{QuestionPage, WhatIsTheExpectedValueOfThisArrangementPage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest

class CheckModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "Navigator" - {

    "in Check mode" - {

      "must go from 'Which categories of hallmarks are relevant to this arrangement' page " +
        "to 'Which parts of hallmark A apply to this arrangement?' page " +
        "when checkbox A is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("A").toSet)
                  .success
                  .value

            navigator
              .nextPage(HallmarkCategoriesPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkAController.onPageLoad(0, CheckMode))
        }
      }

      "must go from 'Which categories of hallmarks are relevant to this arrangement' page " +
        "to 'Which parts of hallmark B apply to this arrangement?' page " +
        "when checkbox B is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("B").toSet)
                .success
                .value

            navigator
              .nextPage(HallmarkCategoriesPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(0, CheckMode))
        }
      }

      "must go from 'Which parts of hallmark A apply to this arrangement?' page " +
        "to 'Does the arrangement meet the Main Benefit Test?' page " +
        "when checkbox A1, A2a, A2b & A3 is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("A").toSet)
                .success.value
                .set(HallmarkAPage, 0, HallmarkA.values.toSet)
                .success.value


            navigator
              .nextPage(HallmarkAPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(0, CheckMode))
        }
      }

      "must go from 'Which parts of hallmark A apply to this arrangement?' page " +
        "to 'Which parts of hallmark B apply to this arrangement?' page " +
        "if Hallmark B was also selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val hallmarkCategories = Set(HallmarkCategories.enumerable.withName("A").get,
                                         HallmarkCategories.enumerable.withName("B").get)

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, hallmarkCategories)
                .success.value
                .set(HallmarkAPage, 0, HallmarkA.values.toSet)
                .success.value

            navigator
              .nextPage(HallmarkAPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(0, CheckMode))
        }
      }

      "must go from 'Which parts of hallmark B apply to this arrangement?' page " +
        "to 'Does the arrangement meet the Main Benefit Test?' page " +
        "when checkbox B1, B2, & B3 are selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("B").toSet)
                .success.value
                .set(HallmarkBPage, 0, HallmarkB.values.toSet)
                .success.value

            navigator
              .nextPage(HallmarkBPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(0, CheckMode))
        }
      }

      "must go from 'Does the arrangement meet the Main Benefit Test?' page " +
        "to 'Check your answers' page when Yes is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(MainBenefitTestPage, 0, true)
                .success
                .value

            navigator
              .nextPage(MainBenefitTestPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(0))
        }
      }

      "must go from 'Does the arrangement meet the Main Benefit Test?' page " +
        "to Index page when No is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(MainBenefitTestPage, 0, false)
                .success
                .value

            navigator
              .nextPage(MainBenefitTestPage, 0, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.MainBenefitProblemController.onPageLoad(0))
        }
      }

      def assertRedirect[A](page: QuestionPage[A], route: Call)
                           (f: UserAnswers => UserAnswers) = {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val navigator = new Navigator
            navigator
              .nextPage(page, 0, CheckMode, f(answers))
              .mustBe(route)
        }

      }

      "must go from What is the arrangement called? page to Check your answers page" in {

        assertRedirect(WhatIsThisArrangementCalledPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from What is the implementation date? page to Check your answers page" in {

        assertRedirect(WhatIsTheImplementationDatePage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from the 'Why are reporting this arrangement now?' page to Check your answers page" in {

        assertRedirect(WhyAreYouReportingThisArrangementNowPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from 'Which of these countries are expected to be involved in this arrangement?' page to Check your answers page" in {

        assertRedirect(WhichExpectedInvolvedCountriesArrangementPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from 'What is the expected value of this arrangement?' page to Check your answers page" in {

        assertRedirect(WhatIsTheExpectedValueOfThisArrangementPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from 'Which national provisions is this arrangement based on?' page to Check your answers page" in {

        assertRedirect(WhichNationalProvisionsIsThisArrangementBasedOnPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }

      "must go from Details page to Check your answers page" in {

        assertRedirect(WhichNationalProvisionsIsThisArrangementBasedOnPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0)) _
      }
    }
  }
}
