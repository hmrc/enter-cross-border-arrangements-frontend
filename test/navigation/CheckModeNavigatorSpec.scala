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
import models.IsExemptionKnown.{No, Yes}
import models._
import models.hallmarks._
import models.intermediaries.ExemptCountries
import models.intermediaries.WhatTypeofIntermediary.{IDoNotKnow, Promoter, Serviceprovider}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.arrangement._
import pages.hallmarks._
import pages.intermediaries._
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
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("A").toSet)
                  .success
                  .value

            navigator
              .nextPage(HallmarkCategoriesPage, CheckMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkAController.onPageLoad(CheckMode))
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
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(CheckMode))
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
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(CheckMode))
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
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(CheckMode))
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
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(CheckMode))
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
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
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
              .mustBe(controllers.hallmarks.routes.MainBenefitProblemController.onPageLoad())
        }
      }

      def assertRedirect[A](page: QuestionPage[A], route: Call)
                           (f: UserAnswers => UserAnswers) = {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val navigator = new Navigator
            navigator
              .nextPage(page, CheckMode, f(answers))
              .mustBe(route)
        }

      }

      "must go from What is the arrangement called? page to Check your answers page" in {

        assertRedirect(WhatIsThisArrangementCalledPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      "must go from What is the implementation date? page to Check your answers page" in {

        assertRedirect(WhatIsTheImplementationDatePage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      "Do you know the reason Do you know the reason this arrangement must be reported now? page to " +
        "Why are you reporting this arrangement now? page when the answer is 'Yes'" in {

        assertRedirect(DoYouKnowTheReasonToReportArrangementNowPage,
          controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(CheckMode)) {
          _
            .set(DoYouKnowTheReasonToReportArrangementNowPage, true)
            .success
            .value

        }
      }

      "Do you know the reason Do you know the reason this arrangement must be reported now? page to " +
        "Check your answers page when the answer is No" in {

        assertRedirect(DoYouKnowTheReasonToReportArrangementNowPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) {
          _
            .set(DoYouKnowTheReasonToReportArrangementNowPage, false)
            .success
            .value

        }

      }

      "must go from the 'Why are reporting this arrangement now?' page to Check your answers page" in {

        assertRedirect(WhyAreYouReportingThisArrangementNowPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      "must go from 'Which of these countries are expected to be involved in this arrangement?' page to Check your answers page" in {

        assertRedirect(WhichExpectedInvolvedCountriesArrangementPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      "must go from 'What is the expected value of this arrangement?' page to Check your answers page" in {

        assertRedirect(WhatIsTheExpectedValueOfThisArrangementPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      "must go from 'Which national provisions is this arrangement based on?' page to Check your answers page" in {

        assertRedirect(WhichNationalProvisionsIsThisArrangementBasedOnPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      "must go from Details page to Check your answers page" in {

        assertRedirect(WhichNationalProvisionsIsThisArrangementBasedOnPage
          , controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()) _
      }

      // INTERMEDIARIES CHECKMODE

        "must go from 'Organisation or Individual' page to " +
          "'What is the name of the organisation?' page in the intermediaries journey when organisation" in {
          assertRedirect(IntermediariesTypePage,
            controllers.organisation.routes.OrganisationNameController.onPageLoad(CheckMode)) {
            _
              .set(IntermediariesTypePage, SelectType.Organisation)
              .success
              .value
          }
        }

        "must go from 'Organisation or Individual' page to " +
          "'What is the name of the individual?' page in the intermediaries journey when individual" in {
          assertRedirect(IntermediariesTypePage,
            controllers.individual.routes.IndividualNameController.onPageLoad(CheckMode)) {
            _
              .set(IntermediariesTypePage, SelectType.Individual)
              .success
              .value
          }
        }

      "must go from 'What type of intermediary is *name*?' page to " +
        "'Is *organisation* exempt from reporting in an EU member state, or the UK?' page in the intermediaries journey" +
        "when Promoter" in {
        assertRedirect(WhatTypeofIntermediaryPage,
          controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(CheckMode)) {
          _
            .set(WhatTypeofIntermediaryPage, Promoter)
            .success
            .value
        }
      }

      "must go from 'What type of intermediary is *name*?' page to " +
        "'Check your answers' page in the intermediaries journey when unknown" in {
        assertRedirect(WhatTypeofIntermediaryPage,
          controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()) {
          _
            .set(WhatTypeofIntermediaryPage, IDoNotKnow)
            .success
            .value
        }
      }

      "must go from 'What type of intermediary is *name*?' page to " +
        "'Check your answers' page in the intermediaries journey when I do not know" in {
        assertRedirect(WhatTypeofIntermediaryPage,
          controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()) {
          _
            .set(WhatTypeofIntermediaryPage, Serviceprovider)
            .success
            .value
        }
      }


      "must go from 'Is *name* exempt from reporting in an EU member state, or the UK?' page to " +
        "Do you know which countries *name* exempt from reporting in?' page in the intermediaries journey" +
        "when Yes" in {
        assertRedirect(IsExemptionKnownPage,
          controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(CheckMode)) {
          _
            .set(IsExemptionKnownPage, Yes)
            .success
            .value
        }
      }

      "must go from 'Is *name* exempt from reporting in an EU member state, or the UK?' page to " +
        "Check your answers?' page in the intermediaries journey when No" in {
        assertRedirect(IsExemptionKnownPage,
          controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()) {
          _
            .set(IsExemptionKnownPage, No)
            .success
            .value
        }
      }

      "must go from 'Is *name* exempt from reporting in an EU member state, or the UK?' page to " +
        "Check your answers?' page in the intermediaries journey when Unknown" in {
        assertRedirect(IsExemptionKnownPage,
          controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()) {
          _
            .set(IsExemptionKnownPage, IsExemptionKnown.Unknown)
            .success
            .value
        }
      }

      "must go from 'Do you know which countries *name* exempt from reporting in?' page to " +
        "Which countries is *name* exempt from reporting in?' page in the intermediaries journey" +
        "when Yes" in {
        assertRedirect(IsExemptionCountryKnownPage,
          controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(CheckMode)) {
          _
            .set(IsExemptionCountryKnownPage, true)
            .success
            .value
        }
      }

      "must go from 'Do you know which countries *name* exempt from reporting in?' page to " +
        "Check your answers' page in the intermediaries journey" +
        "when No" in {
        assertRedirect(IsExemptionCountryKnownPage,
          controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()) {
          _
            .set(IsExemptionCountryKnownPage, false)
            .success
            .value
        }
      }

      "must go from 'Which countries is *name* exempt from reporting in?' page to " +
        "'Check your answers' page in the intermediaries journey" in {
        assertRedirect(ExemptCountriesPage,
          controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()) {
          _
            .set(ExemptCountriesPage, ExemptCountries.enumerable.withName("uk").toSet)
            .success
            .value
        }
      }
    }
  }
}
