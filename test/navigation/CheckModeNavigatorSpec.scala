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
import controllers.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController
import generators.Generators
import models._
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.hallmarks._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.arrangement._
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
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

      "must go from What is their name? page to Check your answers page" in {

        assertRedirect(IndividualNamePage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) _
      }

      "must go from What is {0}'s date of birth? page to Check your answers page" in {

        assertRedirect(IndividualDateOfBirthPage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) _
      }

      "must go from the Do you know where {0} was born? page to the Where was {0} born? when answer is 'Yes' " in {

        assertRedirect(IsIndividualPlaceOfBirthKnownPage
          , controllers.individual.routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualPlaceOfBirthKnownPage, true)
            .success
            .value

        }
      }

      "must go from the Do you know where {0} was born? page to the Check your answers page when answer is 'No' " in {
        assertRedirect(IsIndividualPlaceOfBirthKnownPage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) {
          _
            .set(IsIndividualPlaceOfBirthKnownPage, false)
            .success
            .value

        }
      }

      "must go from Where was {0} born? page to Check your answers page" in {

        assertRedirect(IndividualPlaceOfBirthPage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) _
      }

      "must go from Do you know {0} address to 'Does the individual live in the United Kingdom?' when answer is 'Yes'" in {

        assertRedirect(IsIndividualAddressKnownPage
          , controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressKnownPage, true)
            .success
            .value

        }
      }

      "must go from Do you know {0} address to Check your answers page when answer is 'No'" in {

        assertRedirect(IsIndividualAddressKnownPage
          , controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressKnownPage, true)
            .success
            .value

        }
      }

      "must go from Does {0} live in the United Kingdom? to What is {0}’s postcode? when answer is 'Yes'" in {

        assertRedirect(IsIndividualAddressUkPage, controllers.individual.routes.IndividualPostcodeController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressUkPage, true)
            .success
            .value

        }
      }

      "must go from Does {0} live in the United Kingdom? to What is {0}’s address? page when answer is 'No'" in {

        assertRedirect(IsIndividualAddressUkPage, controllers.individual.routes.IndividualAddressController.onPageLoad(CheckMode)) {
          _
            .set(IsIndividualAddressUkPage, false)
            .success
            .value

        }
      }

      "must go from What is {0}’s address? to Check your answers page" in {

        assertRedirect(IndividualAddressPage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) _
      }

      "must go from Do you know {0}’s email address? to Check your answers page when answer is 'No'" in {

        assertRedirect(EmailAddressQuestionForIndividualPage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) {
          _
            .set(EmailAddressQuestionForIndividualPage, false)
            .success
            .value

        }
      }

      "must go from Do you know {0}’s email address? to What is {0}’s email address? page when answer is 'Yes'" in {

        assertRedirect(EmailAddressQuestionForIndividualPage, controllers.individual.routes.EmailAddressForIndividualController.onPageLoad(CheckMode)) {
          _
            .set(EmailAddressQuestionForIndividualPage, true)
            .success
            .value

        }
      }

      "must go from What is {0}’s email address? to Check your answers page" in {

        assertRedirect(EmailAddressForIndividualPage
          , controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()) _
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

      "must go from 'You have not added any taxpayers' page to " +
        "'Select any taxpayers this enterprise is associated with' if answer is 'Yes, add now'" in {

        assertRedirect(YouHaveNotAddedAnyAssociatedEnterprisesPage,
          controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(CheckMode)) {
          _
            .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)
            .success
            .value
        }
      }

      "must go from 'You have not added any taxpayers' page to " +
        "the same page if answer is 'Yes, add later'" in {

        assertRedirect(YouHaveNotAddedAnyAssociatedEnterprisesPage,
          controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(CheckMode)) {
          _
            .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.YesAddLater)
            .success
            .value
        }
      }

      "must go from 'You have not added any taxpayers' page to " +
        "Index page if answer is 'No'" in {
        assertRedirect(YouHaveNotAddedAnyAssociatedEnterprisesPage,
          controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(CheckMode)) {
          _
            .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.No)
            .success
            .value
        }
      }

      "must go from 'Is this an organisation or an individual?' associated enterprises page to " +
        "'What is the name of the organisation?' if answer is Organisation" in {

        assertRedirect(AssociatedEnterpriseTypePage,
          controllers.organisation.routes.OrganisationNameController.onPageLoad(CheckMode)) {
          _
            .set(AssociatedEnterpriseTypePage, SelectType.Organisation)
            .success
            .value
        }
      }

      "must go from 'Is this an organisation or an individual?' associated enterprises page to " +
        "'What is their name?' if answer is Individual" in {

        assertRedirect(AssociatedEnterpriseTypePage,
          controllers.individual.routes.IndividualNameController.onPageLoad(CheckMode)) {
          _
            .set(AssociatedEnterpriseTypePage, SelectType.Individual)
            .success
            .value
        }
      }

      "must go from 'Is *name* also affected by this arrangement?' page to " +
        "'Check your answers' page in the associated enterprise journey" in {
        forAll(arbitrary[UserAnswers], Gen.oneOf(Seq(true, false))) {
          (answers, affectedPageAnswer) =>

            val updatedAnswers =
              answers.set(IsAssociatedEnterpriseAffectedPage, affectedPageAnswer)
                .success.value

            navigator
              .nextPage(IsAssociatedEnterpriseAffectedPage, CheckMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }
      }
    }
  }
}
