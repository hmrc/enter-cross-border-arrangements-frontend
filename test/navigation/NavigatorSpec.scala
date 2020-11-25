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
import models.HallmarkD.D2
import models.HallmarkD1.D1other
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val country: Country = Country("valid", "GB", "United Kingdom")
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

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
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("A").toSet)
                .success
                .value

            navigator
              .nextPage(HallmarkCategoriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkAController.onPageLoad(NormalMode))
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
              .nextPage(HallmarkCategoriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkBController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which categories of hallmarks are relevant to this arrangement' page " +
        "to 'Which parts of hallmark D apply to this arrangement?' page " +
        "when checkbox D is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success
                .value

            navigator
              .nextPage(HallmarkCategoriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkDController.onPageLoad(NormalMode))
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
              .nextPage(HallmarkAPage, NormalMode, updatedAnswers)
              .mustBe(routes.MainBenefitTestController.onPageLoad(NormalMode))
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
              .nextPage(HallmarkAPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkBController.onPageLoad(NormalMode))
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
              .nextPage(HallmarkBPage, NormalMode, updatedAnswers)
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
              .mustBe(routes.CheckYourAnswersHallmarksController.onPageLoad())
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
              .mustBe(routes.MainBenefitProblemController.onPageLoad())
        }
      }

      "must go from 'Which parts of hallmark D apply to this arrangement?' page " +
        "to 'Check your answers' page when D2 only is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkDPage, HallmarkD.values.toSet.filter(_ == D2))
                .success.value

            navigator
              .nextPage(HallmarkDPage, NormalMode, updatedAnswers)
              .mustBe(routes.CheckYourAnswersHallmarksController.onPageLoad())
        }
      }

      "must go from 'Which parts of hallmark D apply to this arrangement?' page " +
        "to 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "if Hallmark D1 was also selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkDPage, HallmarkD.values.toSet)
                .success.value

            navigator
              .nextPage(HallmarkDPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkD1Controller.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' page if D1: Other is not selected and categories contains E" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("E").toSet)
                .success.value
                .set(HallmarkD1Page, HallmarkD1.values.toSet.filter(_ != D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkEController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' page if D1: Other is not selected and categories does not contain E" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkD1Page, HallmarkD1.values.toSet.filter(_ != D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, NormalMode, updatedAnswers)
              .mustBe(routes.CheckYourAnswersHallmarksController.onPageLoad())
        }
      }


      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to D1: Other page if D1: Other is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkD1Page, HallmarkD1.values.toSet.filter(_ == D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkD1OtherController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement? D1: Other' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' if category E is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("E").toSet)
                .success.value
                .set(HallmarkD1Page, HallmarkD1.values.toSet.filter(_ == D1other))
                .success.value
                .set(HallmarkD1OtherPage, "")
                .success.value

            navigator
              .nextPage(HallmarkD1OtherPage, NormalMode, updatedAnswers)
              .mustBe(routes.HallmarkEController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which category E hallmarks apply to this arrangement?' page " +
        "to 'check your answers' if category E is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("E").toSet)
                .success.value
                .set(HallmarkEPage, HallmarkE.values.toSet)
                .success.value


            navigator
              .nextPage(HallmarkEPage, NormalMode, updatedAnswers)
              .mustBe(routes.CheckYourAnswersHallmarksController.onPageLoad())
        }
      }

      "must go from What is their name? page to What is {0}'s date of birth?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualNamePage, NormalMode, answers)
              .mustBe(routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
        }
      }

      "must go from What is {0}'s date of birth? page to Do you know where {0} was born?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualDateOfBirthPage, NormalMode, answers)
              .mustBe(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
        }
      }


      "must go from the Do you know where {0} was born?  page to the Where was {0} born? when answer is 'Yes' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualPlaceOfBirthKnownPage, true)
                .success
                .value

            navigator
              .nextPage(IsIndividualPlaceOfBirthKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(NormalMode))
        }
      }

      "must go from the Do you know where {0} was born?  page to the Do you know {0}'s address? when answer is 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualPlaceOfBirthKnownPage, false)
                .success
                .value

            navigator
              .nextPage(IsIndividualPlaceOfBirthKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from Where was {0} born? page to Do you know {0}'s address?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualPlaceOfBirthPage, NormalMode, answers)
              .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from Do you know {0} address yes to 'Does the individual live in the United Kingdom?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualAddressKnownPage, true)
                .success
                .value

            navigator
              .nextPage(IsIndividualAddressKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualAddressUkController.onPageLoad(NormalMode))
        }
      }

      "must go from What is the name of the organisation? page to Do you know {0}’s Address page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(OrganisationNamePage, NormalMode, answers)
              .mustBe(routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from the Do you know {0}’s Address page to the Is {0}’s main address in the United Kingdom? when answer is 'Yes' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsOrganisationAddressKnownPage, true)
                .success
                .value

            navigator
              .nextPage(IsOrganisationAddressKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsOrganisationAddressUkController.onPageLoad(NormalMode))
        }
      }

      "must go from the Do you know {0}’s Address page to " +
        "Do you know the email address for a main contact at the organisation? page when answer is 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsOrganisationAddressKnownPage, false)
                .success
                .value

            navigator
              .nextPage(IsOrganisationAddressKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }
      }

      "must go from the Is {0}’s main address in the United Kingdom? page to the Index page when answer is 'Yes' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsOrganisationAddressUkPage, true)
                .success
                .value

            navigator
              .nextPage(IsOrganisationAddressUkPage, NormalMode, updatedAnswers)
              .mustBe(routes.OrganisationPostcodeController.onPageLoad(NormalMode))
        }
      }

      "must go from the Is {0}’s main address in the United Kingdom? page to What is {0}’s main address? " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsOrganisationAddressUkPage, false)
                .success
                .value

            navigator
              .nextPage(IsOrganisationAddressUkPage, NormalMode, updatedAnswers)
              .mustBe(routes.OrganisationAddressController.onPageLoad(NormalMode))
        }
      }

      "must go from Postcode page to What is your main address page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(PostcodePage, "ZZ1 ZZ4")
                .success.value
                .set(HallmarkEPage, HallmarkE.values.toSet)
                .success.value


            navigator
              .nextPage(PostcodePage, NormalMode, updatedAnswers)
              .mustBe(routes.OrganisationSelectAddressController.onPageLoad(NormalMode))
        }
      }

      "must go from What is the organisation's main address page (/select-address) to " +
        "Do you know the email address for a main contact at the organisation? page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(SelectAddressPage, "25 Testing Close, Othertown, Z9 3WW")
                .success.value

            navigator
              .nextPage(SelectAddressPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }
      }

      "must go from What is the organisation's main address page (/address) to " +
        "Do you know the email address for a main contact at the organisation? page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
              Country("valid", "FR", "France"))

            val updatedAnswers =
              answers.set(OrganisationAddressPage, address)
                .success.value

            navigator
              .nextPage(OrganisationAddressPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }
      }

      "must go from Do you know the email address for a main contact at the organisation? page to " +
        "What is the email address for a main contact at the organisation? page if the answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(EmailAddressQuestionForOrganisationPage, true)
                .success.value

            navigator
              .nextPage(EmailAddressQuestionForOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressForOrganisationController.onPageLoad(NormalMode))
        }
      }

      "must go from Do you know the email address for a main contact at the organisation? page to " +
        "Which country is the organisation resident in for tax purposes? page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(EmailAddressQuestionForOrganisationPage, false)
                .success.value

            navigator
              .nextPage(EmailAddressQuestionForOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from What is the email address for a main contact at the organisation? page to " +
        "Which country is the organisation resident in for tax purposes? page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(EmailAddressForOrganisationPage, "email@email.com")
                .success.value

            navigator
              .nextPage(EmailAddressForOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Which country is the organisation resident in for tax purposes? page to " +
        "Do you know any of the organisation’s tax reference numbers for the United Kingdom? page if the answer is GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhichCountryTaxForOrganisationPage, country)
                .success.value

            navigator
              .nextPage(WhichCountryTaxForOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Which country is the organisation resident in for tax purposes? page to " +
        "Do you know the organisation’s tax identification numbers for the country? page if the answer is not GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhichCountryTaxForOrganisationPage, Country("valid", "FR", "France"))
                .success.value

            navigator
              .nextPage(WhichCountryTaxForOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Do you know any of the organisation’s tax reference numbers for the United Kingdom? page " +
        "to What are the organisation’s tax reference numbers for the United Kingdom? page if the answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowAnyTINForUKOrganisationPage, true)
                .success.value

            navigator
              .nextPage(DoYouKnowAnyTINForUKOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Do you know any of the organisation’s tax reference numbers for the United Kingdom? page " +
        "to Is the organisation resident for tax purposes in any other countries? page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowAnyTINForUKOrganisationPage, false)
                .success.value

            navigator
              .nextPage(DoYouKnowAnyTINForUKOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Do you know the organisation’s tax identification numbers for the country? page " +
        "to What are the organisation’s tax identification numbers for the country? page if the answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowTINForNonUKOrganisationPage, true)
                .success.value

            navigator
              .nextPage(DoYouKnowTINForNonUKOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Do you know the organisation’s tax identification numbers for the country? page " +
        "to Is the organisation resident for tax purposes in any other countries? page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowTINForNonUKOrganisationPage, false)
                .success.value

            navigator
              .nextPage(DoYouKnowTINForNonUKOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }

      "must go from What are the organisation’s tax reference numbers for the United Kingdom? " +
        "to Is the organisation resident for tax purposes in any other countries? page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatAreTheTaxNumbersForUKOrganisationPage, TaxReferenceNumbers("1234567890", None, None))
                .success.value

            navigator
              .nextPage(WhatAreTheTaxNumbersForUKOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "Which country is the organisation resident in for tax purposes? page if the answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IsOrganisationResidentForTaxOtherCountriesPage, true)
                .success.value

            navigator
              .nextPage(IsOrganisationResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "Check your answers for organisation page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IsOrganisationResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsOrganisationResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.CheckYourAnswersOrganisationController.onPageLoad())
        }
      }

      "must go from What are the organisation’s tax identification numbers for the country? page to " +
        "Is the organisation resident for tax purposes in any other countries? page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatAreTheTaxNumbersForNonUKOrganisationPage, TaxReferenceNumbers("1234567890", None, None))
                .success.value

            navigator
              .nextPage(WhatAreTheTaxNumbersForNonUKOrganisationPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }

      "must go from What is their name? page to What is individuals's date of birth?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualNamePage, NormalMode, answers)
              .mustBe(routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
        }
      }

      "must go from What is individuals's date of birth? page to Do you know where individuals was born?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualDateOfBirthPage, NormalMode, answers)
              .mustBe(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
        }
      }


      "must go from the Do you know where individuals was born?  page to the Where was individuals born? when answer is 'Yes' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualPlaceOfBirthKnownPage, true)
                .success
                .value

            navigator
              .nextPage(IsIndividualPlaceOfBirthKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(NormalMode))
        }
      }

      "must go from the Do you know where individuals was born?  page to the Do you know individuals's address? when answer is 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualPlaceOfBirthKnownPage, false)
                .success
                .value

            navigator
              .nextPage(IsIndividualPlaceOfBirthKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from Where was individuals born? page to Do you know individuals's address?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualPlaceOfBirthPage, NormalMode, answers)
              .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Do you know individuals address' to 'Does the individual live in the United Kingdom?' " +
        "when answer is 'Yes'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualAddressKnownPage, true)
                .success
                .value

            navigator
              .nextPage(IsIndividualAddressKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualAddressUkController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Do you know individuals address' to 'Do you know individuals email address?'" +
        " when answer is 'No'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualAddressKnownPage, false)
                .success
                .value

            navigator
              .nextPage(IsIndividualAddressKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Does the individual live in the United Kingdom? No to 'what is the individuals address'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualAddressUkPage, false)
                .success
                .value

            navigator
              .nextPage(IsIndividualAddressUkPage, NormalMode, updatedAnswers)
              .mustBe(routes.IndividualAddressController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Does the individual live in the United Kingdom? yes to 'what is the individuals post code'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsIndividualAddressUkPage, true)
                .success
                .value

            navigator
              .nextPage(IsIndividualAddressUkPage, NormalMode, updatedAnswers)
              .mustBe(routes.IndividualPostcodeController.onPageLoad(NormalMode))
        }
      }

      "must go from select 'What is the individuals postcode? to 'What is the individuals address?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IndividualUkPostcodePage, "A99 AA9")
                .success
                .value

            navigator
              .nextPage(IndividualUkPostcodePage, NormalMode, updatedAnswers)
              .mustBe(routes.IndividualSelectAddressController.onPageLoad(NormalMode))
        }
      }

      "must go from select 'What is the individuals address?' to 'Do you know individuals email address'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IndividualSelectAddressPage, "A99 AA9")
                .success
                .value

            navigator
              .nextPage(IndividualSelectAddressPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
        }
      }

      "must go from 'What is the individuals address?' entry to 'Do you know individuals email address'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IndividualAddressPage, Address(None, None, None, "", None,
                  Country("valid", "GB", "United Kingdom")))
                .success
                .value

            navigator
              .nextPage(IndividualAddressPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Do you know individuals email address' " +
        "to What is individuals email address? page if the answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(EmailAddressQuestionForIndividualPage, true)
                .success.value

            navigator
              .nextPage(EmailAddressQuestionForIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.EmailAddressForIndividualController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Do you know individuals email address' " +
        "to 'What is individuals email address?' page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(EmailAddressQuestionForIndividualPage, false)
                .success.value

            navigator
              .nextPage(EmailAddressQuestionForIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, index))
        }
      }

      "must go from 'What is individuals email address?' to" +
        "'Which country is the individual resident in for tax purposes?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(EmailAddressForIndividualPage, "test@email.com")
                .success.value

            navigator
              .nextPage(EmailAddressForIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, index))

        }
      }

      "must go from 'Which country is the individual resident in for tax purposes?' to" +
        "'Do you know the individuals tax identification numbers for the United Kingdom' when the country is GB" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers =
              answers
                .set(WhichCountryTaxForIndividualPage, country)
                .success
                .value

            navigator
              .nextPage(WhichCountryTaxForIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(NormalMode, index))
        }
      }

      "must go from 'Do you know the individuals tax identification numbers for the United Kingdom' to" +
        "'What are the individuals tax identification numbers for the United Kingdom when true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowAnyTINForUKIndividualPage, true)
                .success.value

            navigator
              .nextPage(DoYouKnowAnyTINForUKIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(NormalMode, index))

        }
      }

      "must go from 'Do you know the individuals tax identification numbers for the United Kingdom' to" +
        "'Is the individuals resident for tax purposes in any other countries?' when false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowAnyTINForUKIndividualPage, false)
                .success.value

            navigator
              .nextPage(DoYouKnowAnyTINForUKIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))

        }
      }

      "must go from 'What are the individuals tax identification numbers for the United Kingdom to" +
        "'Is the individuals resident for tax purposes in any other countries?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatAreTheTaxNumbersForUKIndividualPage, TaxReferenceNumbers("1234567890", None, None))
                .success.value

            navigator
              .nextPage(WhatAreTheTaxNumbersForUKIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))

        }
      }


      "must go from Is the individuals resident for tax purposes in any other countries? page to " +
        "Which country is the individuals resident in for tax purposes? page if the answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IsIndividualResidentForTaxOtherCountriesPage, true)
                .success.value

            navigator
              .nextPage(IsIndividualResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, index))
        }
      }
      "must go from Is the individuals resident for tax purposes in any other countries? page to " +
        "??? page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IsIndividualResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsIndividualResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }
      }

      "must go from What are the individuals’s tax identification numbers for the country? page to " +
        "Is the individual resident for tax purposes in any other countries?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatAreTheTaxNumbersForNonUKIndividualPage, TaxReferenceNumbers("1234567890", None, None))
                .success.value

            navigator
              .nextPage(WhatAreTheTaxNumbersForNonUKIndividualPage, NormalMode, updatedAnswers)
              .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }
    }
  }
}

