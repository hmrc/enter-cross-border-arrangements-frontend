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

import java.time.LocalDate

import base.SpecBase
import controllers.routes
import generators.Generators
import models.SelectType.{Individual, Organisation}
import models._
import models.arrangement.WhyAreYouReportingThisArrangementNow.Dac6701
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement}
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.hallmarks.HallmarkD.D2
import models.hallmarks.HallmarkD1._
import models.hallmarks._
import models.intermediaries.YouHaveNotAddedAnyIntermediaries
import models.taxpayer.UpdateTaxpayer.{Later, No, Now}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.arrangement._
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.organisation._
import pages.taxpayer.{TaxpayerSelectTypePage, UpdateTaxpayerPage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import pages.intermediaries._

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
              .mustBe(controllers.hallmarks.routes.HallmarkAController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.HallmarkDController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
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
              .mustBe(controllers.hallmarks.routes.MainBenefitProblemController.onPageLoad())
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
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
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
              .mustBe(controllers.hallmarks.routes.HallmarkD1Controller.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' page if D1: Other is not selected and categories contains E" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("E").toSet)
                .success.value
                .set(HallmarkD1Page, values.toSet.filter(_ != D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkEController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' page if D1: Other is not selected and categories does not contain E" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkD1Page, values.toSet.filter(_ != D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
        }
      }


      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to D1: Other page if D1: Other is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkD1Page, values.toSet.filter(_ == D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement? D1: Other' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' if category E is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(HallmarkCategoriesPage, HallmarkCategories.enumerable.withName("E").toSet)
                .success.value
                .set(HallmarkD1Page, values.toSet.filter(_ == D1other))
                .success.value
                .set(HallmarkD1OtherPage, "")
                .success.value

            navigator
              .nextPage(HallmarkD1OtherPage, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkEController.onPageLoad(NormalMode))
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
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
        }
      }

      "must go from 'You have not added any taxpayers' page to 'Is this an organisation or an individual?' " +
        "if 'Yes, add now is selected'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(UpdateTaxpayerPage, Now)
                .success.value

            navigator
              .nextPage(UpdateTaxpayerPage, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(NormalMode))
        }
      }

      "must go from 'You have not added any taxpayers' page to 'Is this an organisation or an individual?' " +
        "if 'No, is selected'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(UpdateTaxpayerPage, No)
                .success.value

            navigator
              .nextPage(UpdateTaxpayerPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'You have not added any taxpayers' page to 'Is this an organisation or an individual?' " +
        "if 'Yes, add later is selected'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(UpdateTaxpayerPage, Later)
                .success.value

            navigator
              .nextPage(UpdateTaxpayerPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'Is this an organisation or an individual?' page to 'What is the name of the organisation?' " +
        "if 'organisation' is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(TaxpayerSelectTypePage, Organisation)
                .success.value

            navigator
              .nextPage(TaxpayerSelectTypePage, NormalMode, updatedAnswers)
              .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Is this an organisation or an individual?' page to 'What is their name?' " +
        "if 'individual' is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(TaxpayerSelectTypePage, Individual)
                .success.value

            navigator
              .nextPage(TaxpayerSelectTypePage, NormalMode, updatedAnswers)
              .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(NormalMode))
        }
      }


      "must go from What is their name? page to What is {0}'s date of birth?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualNamePage, NormalMode, answers)
              .mustBe(controllers.individual.routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
        }
      }

      "must go from What is {0}'s date of birth? page to Do you know where {0} was born?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualDateOfBirthPage, NormalMode, answers)
              .mustBe(controllers.individual.routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IndividualPlaceOfBirthController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from Where was {0} born? page to Do you know {0}'s address?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualPlaceOfBirthPage, NormalMode, answers)
              .mustBe(controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(NormalMode))
        }
      }

      "must go from What is the name of the organisation? page to Do you know {0}’s Address page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(OrganisationNamePage, NormalMode, answers)
              .mustBe(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "Check your answers for organisation page if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .remove(IsAssociatedEnterpriseAffectedPage)
                .success.value
                .remove(TaxpayerSelectTypePage)
                .success.value
                .remove(IntermediariesTypePage)
                .success.value
                .set(IsOrganisationResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsOrganisationResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "Check your answers for associated enterprise page if in the associated enterprise journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(AssociatedEnterpriseTypePage, SelectType.Organisation)
                .success.value
                .remove(IntermediariesTypePage)
                .success.value
                .remove(TaxpayerSelectTypePage)
                .success.value
                .set(IsOrganisationResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsOrganisationResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode))
        }
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "What type of intermediary is name? page if in the intermediaries journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IntermediariesTypePage, SelectType.Organisation)
                .success.value
                .remove(AssociatedEnterpriseTypePage)
                .success.value
                .remove(TaxpayerSelectTypePage)
                .success.value
                .set(IsOrganisationResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsOrganisationResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(NormalMode))
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
              .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }

      "must go from What is their name? page to What is individuals's date of birth?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualNamePage, NormalMode, answers)
              .mustBe(controllers.individual.routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
        }
      }

      "must go from What is individuals's date of birth? page to Do you know where individuals was born?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualDateOfBirthPage, NormalMode, answers)
              .mustBe(controllers.individual.routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IndividualPlaceOfBirthController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }
      }

      "must go from Where was individuals born? page to Do you know individuals's address?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(IndividualPlaceOfBirthPage, NormalMode, answers)
              .mustBe(controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IndividualAddressController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IndividualPostcodeController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IndividualSelectAddressController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.EmailAddressForIndividualController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, index))

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
              .mustBe(controllers.individual.routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(NormalMode, index))
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
              .mustBe(controllers.individual.routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(NormalMode, index))

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
              .mustBe(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))

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
              .mustBe(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))

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
              .mustBe(controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, index))
        }
      }

      "must go from Is the individuals resident for tax purposes in any other countries? page to " +
        "Check your answers for an Individual if the answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.remove(IsAssociatedEnterpriseAffectedPage)
                .success.value
                .remove(TaxpayerSelectTypePage)
                .success.value
                .remove(IntermediariesTypePage)
                .success.value
                .set(IsIndividualResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsIndividualResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())
        }
      }

      "must go from Is the individuals resident for tax purposes in any other countries? page to " +
        "Check your answers for associated enterprise page if in the associated enterprise journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(AssociatedEnterpriseTypePage, SelectType.Individual)
                .success.value
                .remove(TaxpayerSelectTypePage)
                .success.value
                .remove(IntermediariesTypePage)
                .success.value
                .set(IsIndividualResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsIndividualResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode))
        }
      }

      "must go from Is the individuals resident for tax purposes in any other countries? page to " +
        "'What type of intermediary is name?' if in the intermediaries journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IntermediariesTypePage, SelectType.Individual)
                .success.value
                .remove(AssociatedEnterpriseTypePage)
                .success.value
                .remove(TaxpayerSelectTypePage)
                .success.value
                .set(IsIndividualResidentForTaxOtherCountriesPage, false)
                .success.value

            navigator
              .nextPage(IsIndividualResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(NormalMode))
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
              .mustBe(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index))
        }
      }

      "must go from What is the arrangement called? page to " +
        "what is the implementation date?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatIsThisArrangementCalledPage, "an arrangement")
                .success.value

            navigator
              .nextPage(WhatIsThisArrangementCalledPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(NormalMode))
        }
      }

      "must go from What is the implementation date? page to " +
        "Do you know the reason Do you know the reason this arrangement must be reported now? page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(WhatIsTheImplementationDatePage, NormalMode, answers)
              .mustBe(controllers.arrangement.routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(NormalMode))
        }
      }

      "Do you know the reason Do you know the reason this arrangement must be reported now? page to " +
        "Why are you reporting this arrangement now? page when the answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowTheReasonToReportArrangementNowPage, true)
                .success.value

            navigator
              .nextPage(DoYouKnowTheReasonToReportArrangementNowPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(NormalMode))
        }
      }

      "Do you know the reason Do you know the reason this arrangement must be reported now? page to " +
        "'Which of these countries are expected to be involved in this arrangement?' page when the answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DoYouKnowTheReasonToReportArrangementNowPage, false)
                .success.value

            navigator
              .nextPage(DoYouKnowTheReasonToReportArrangementNowPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from the 'Why are reporting this arrangement now?' page to " +
        "'Which of these countries are expected to be involved in this arrangement?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhyAreYouReportingThisArrangementNowPage,Dac6701)
                .success.value

            navigator
              .nextPage(WhyAreYouReportingThisArrangementNowPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which of these countries are expected to be involved in this arrangement?' page to " +
        "'What is the expected value of this arrangement?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhichExpectedInvolvedCountriesArrangementPage,WhichExpectedInvolvedCountriesArrangement.enumerable.withName("GB").toSet)
                .success.value

            navigator
              .nextPage(WhichExpectedInvolvedCountriesArrangementPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from 'What is the expected value of this arrangement?' page to " +
        "'Which national provisions is this arrangement based on?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatIsTheExpectedValueOfThisArrangementPage,WhatIsTheExpectedValueOfThisArrangement("ALL", 0))
                .success.value

            navigator
              .nextPage(WhatIsTheExpectedValueOfThisArrangementPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Which national provisions is this arrangement based on?' page to " +
        "'Give details of this arrangement'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhichNationalProvisionsIsThisArrangementBasedOnPage,"provisions")
                .success.value

            navigator
              .nextPage(WhichNationalProvisionsIsThisArrangementBasedOnPage, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from 'You have not added any taxpayers' page to " +
        "'Select any taxpayers this enterprise is associated with' if answer is 'Yes, add now'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyAssociatedEnterprisesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(NormalMode))
        }
      }

      "must go from 'You have not added any taxpayers' page to " +
        "the same page if answer is 'Yes, add later'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.YesAddLater)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyAssociatedEnterprisesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(NormalMode))
        }
      }

      "must go from 'You have not added any taxpayers' page to " +
        "the same page if answer is 'No'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.No)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyAssociatedEnterprisesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Is this an organisation or an individual?' associated enterprises page to " +
        "'What is the name of the organisation?' if answer is Organisation" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(AssociatedEnterpriseTypePage, SelectType.Organisation)
                .success.value

            navigator
              .nextPage(AssociatedEnterpriseTypePage, NormalMode, updatedAnswers)
              .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Is this an organisation or an individual?' associated enterprises page to " +
        "'What is their name?' if answer is Individual" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(AssociatedEnterpriseTypePage, SelectType.Individual)
                .success.value

            navigator
              .nextPage(AssociatedEnterpriseTypePage, NormalMode, updatedAnswers)
              .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(NormalMode))
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
              .nextPage(IsAssociatedEnterpriseAffectedPage, NormalMode, updatedAnswers)
              .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }
      }

      "What is [name]'s implementation date? page to " +
        "taxpayers check your answers page if organisation entered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatIsTaxpayersStartDateForImplementingArrangementPage, LocalDate.now)
                .success.value
                .set(OrganisationNamePage, "validAnswer")
                .success.value

            navigator
              .nextPage(WhatIsTaxpayersStartDateForImplementingArrangementPage, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }
      }

      "What is [name]'s implementation date? page to " +
        "taxpayers check your answers page if individual entered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(WhatIsTaxpayersStartDateForImplementingArrangementPage, LocalDate.now)
                .success.value
                .remove(OrganisationNamePage)
                .success
                .value
                .set(IndividualNamePage, Name("dummy","user"))
                .success.value

            navigator
              .nextPage(WhatIsTaxpayersStartDateForImplementingArrangementPage, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }
      }

      "must go from Is the individuals resident for tax purposes in any other countries? page to " +
        "'What is [name]'s implementation date?' if the answer is false and in relevant taxpayer journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IsIndividualResidentForTaxOtherCountriesPage, false)
                .success.value
                .set(TaxpayerSelectTypePage, Individual)
                .success.value

            navigator
              .nextPage(IsIndividualResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "'What is [name]'s implementation date?' for organisation page if the answer is false and in relevant taxpayer journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IsOrganisationResidentForTaxOtherCountriesPage, false)
                .success.value
                .set(TaxpayerSelectTypePage, Organisation)
                .success.value

            navigator
              .nextPage(IsOrganisationResidentForTaxOtherCountriesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(NormalMode))
        }
      }

     "must go from 'You have not added any intermediaries' page to " +
        "'Is this an organisation or an individual?' if answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(YouHaveNotAddedAnyIntermediariesPage, YouHaveNotAddedAnyIntermediaries.YesAddNow)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyIntermediariesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(NormalMode))
        }
      }

      "must go from 'You have not added any intermediaries' page to " +
        "Index page if answer is 'No'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(YouHaveNotAddedAnyIntermediariesPage, YouHaveNotAddedAnyIntermediaries.No)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyIntermediariesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'You have not added any intermediaries' page to " +
        "Index page if answer is 'YesAddLater'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(YouHaveNotAddedAnyIntermediariesPage, YouHaveNotAddedAnyIntermediaries.YesAddLater)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyIntermediariesPage, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'Is this an organisation or an individual?' intermediaries page to " +
        "'What is the name of the organisation?' if answer is Organisation" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IntermediariesTypePage, SelectType.Organisation)
                .success.value

            navigator
              .nextPage(IntermediariesTypePage, NormalMode, updatedAnswers)
              .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Is this an organisation or an individual?' intermediaries page to " +
        "'What is their name?' if answer is Individual" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(IntermediariesTypePage, SelectType.Individual)
                .success.value

            navigator
              .nextPage(IntermediariesTypePage, NormalMode, updatedAnswers)
              .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(NormalMode))
        }
      }
    }
  }
}

