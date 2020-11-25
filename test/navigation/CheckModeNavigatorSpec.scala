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
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{QuestionPage, _}
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
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("A").toSet)
                  .success
                  .value

            navigator
              .nextPage(HallmarkCategoriesPage, CheckMode, updatedAnswers)
              .mustBe(routes.HallmarkAController.onPageLoad(CheckMode))
        }
      }

      "must go from 'Which categories of hallmarks are relevant to this arrangement' page " +
        "to 'Which parts of hallmark B apply to this arrangement?' page " +
        "when checkbox B is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("B").toSet)
                .success
                .value

            navigator
              .nextPage(HallmarkCategoriesPage, CheckMode, updatedAnswers)
              .mustBe(routes.HallmarkBController.onPageLoad(CheckMode))
        }
      }

      "must go from 'Which parts of hallmark A apply to this arrangement?' page " +
        "to 'Does the arrangement meet the Main Benefit Test?' page " +
        "when checkbox A1, A2a, A2b & A3 is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("A").toSet)
                .success.value
                .set(HallmarkAPage, HallmarkA.values.toSet)
                .success.value


            navigator
              .nextPage(HallmarkAPage, CheckMode, updatedAnswers)
              .mustBe(routes.MainBenefitTestController.onPageLoad(CheckMode))
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
              answers.set(HallmarkCategoriesPage, hallmarkCategories)
                .success.value
                .set(HallmarkAPage, HallmarkA.values.toSet)
                .success.value

            navigator
              .nextPage(HallmarkAPage, CheckMode, updatedAnswers)
              .mustBe(routes.HallmarkBController.onPageLoad(CheckMode))
        }
      }

      "must go from 'Which parts of hallmark B apply to this arrangement?' page " +
        "to 'Does the arrangement meet the Main Benefit Test?' page " +
        "when checkbox B1, B2, & B3 are selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("B").toSet)
                .success.value
                .set(HallmarkBPage, HallmarkB.values.toSet)
                .success.value

            navigator
              .nextPage(HallmarkBPage, CheckMode, updatedAnswers)
              .mustBe(routes.MainBenefitTestController.onPageLoad(CheckMode))
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
              .nextPage(MainBenefitTestPage, CheckMode, updatedAnswers)
              .mustBe(routes.CheckYourAnswersHallmarksController.onPageLoad())
        }
      }

      "must go from 'Does the arrangement meet the Main Benefit Test?' page " +
        "to Index page when No is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(MainBenefitTestPage, false)
                .success
                .value

            navigator
              .nextPage(MainBenefitTestPage, CheckMode, updatedAnswers)
              .mustBe(routes.MainBenefitProblemController.onPageLoad())
        }
      }

      def assertRedirect[A](page: QuestionPage[A], route: Call = routes.IndividualCheckYourAnswersController.onPageLoad)
                           (f: UserAnswers => UserAnswers) = {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(page, CheckMode, f(answers))
              .mustBe(route)
        }

      }

      // 1
      "must go from What is their name? page to Check your answers page" in {

        assertRedirect(IndividualNamePage) _
      }

      // 2
      "must go from What is {0}'s date of birth? page to Check your answers page" in {

        assertRedirect(IndividualDateOfBirthPage) _
      }

      // 3 + yes
      "must go from the Do you know where {0} was born? page to the Where was {0} born? when answer is 'Yes' " in {

        assertRedirect(IsIndividualPlaceOfBirthKnownPage, routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualPlaceOfBirthKnownPage, true)
            .success
            .value

        }
      }

      // 3 + No
      "must go from the Do you know where {0} was born? page to the Check your answers page when answer is 'No' " in {
        assertRedirect(IsIndividualPlaceOfBirthKnownPage) {
          _
            .set(IsIndividualPlaceOfBirthKnownPage, false)
            .success
            .value

        }
      }

      // 4
      "must go from Where was {0} born? page to Check your answers page" in {

        assertRedirect(IndividualPlaceOfBirthPage) _
      }

      // 5 + Yes
      "must go from Do you know {0} address to 'Does the individual live in the United Kingdom?' when answer is 'Yes'" in {

        assertRedirect(IsIndividualAddressKnownPage, routes.IsIndividualAddressUkController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressKnownPage, true)
            .success
            .value

        }
      }

      // 5 + No
      "must go from Do you know {0} address to Check your answers page when answer is 'No'" in {

        assertRedirect(IsIndividualAddressKnownPage, routes.IsIndividualAddressUkController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressKnownPage, true)
            .success
            .value

        }
      }

      // 6 + Yes
      "must go from Does {0} live in the United Kingdom? to What is {0}’s postcode? when answer is 'Yes'" in {

        assertRedirect(IsIndividualAddressUkPage, routes.IndividualPostcodeController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressUkPage, true)
            .success
            .value

        }
      }

      // 6 + No
      "must go from Does {0} live in the United Kingdom? to What is {0}’s address? page when answer is 'No'" in {

        assertRedirect(IsIndividualAddressUkPage, routes.IndividualAddressController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressUkPage, false)
            .success
            .value

        }
      }

      "must go from What is {0}’s address? to Check your answers page" in {

        assertRedirect(IndividualAddressPage) _
      }

      // 10 + No
      "must go from Do you know {0}’s email address? to Check your answers page when answer is 'No'" in {

        assertRedirect(EmailAddressQuestionForIndividualPage) {
          _
            .set(EmailAddressQuestionForIndividualPage, false)
            .success
            .value

        }
      }

      // 10 + No
      "must go from Do you know {0}’s email address? to What is {0}’s email address? page when answer is 'Yes'" in {

        assertRedirect(EmailAddressQuestionForIndividualPage, routes.EmailAddressForIndividualController.onPageLoad(CheckMode)) {
          _
            .set(EmailAddressQuestionForIndividualPage, true)
            .success
            .value

        }
      }

      // 11
      "must go from What is {0}’s email address? to Check your answers page'" in {

        assertRedirect(EmailAddressForIndividualPage) _
      }

    }
  }
}
