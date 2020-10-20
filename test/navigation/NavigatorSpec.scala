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
              .mustBe(routes.CheckYourAnswersController.onPageLoad())
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
              .mustBe(routes.CheckYourAnswersController.onPageLoad())
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
              .mustBe(routes.CheckYourAnswersController.onPageLoad())
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

      "must go from the Do you know {0}’s Address page to the Is {0}’s main address in the United Kingdom? when answer is 'No' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(IsOrganisationAddressKnownPage, false)
                .success
                .value

            navigator
              .nextPage(IsOrganisationAddressKnownPage, NormalMode, updatedAnswers)
              .mustBe(routes.IndexController.onPageLoad())
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
              .mustBe(routes.PostcodeController.onPageLoad(NormalMode))
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
              .mustBe(routes.SelectAddressController.onPageLoad(NormalMode))
        }
      }
    }
  }
}

