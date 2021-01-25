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

import java.time.LocalDate
import base.SpecBase
import controllers.routes
import generators.Generators
import models.SelectType.{Individual, Organisation}
import models._
import models.arrangement.WhyAreYouReportingThisArrangementNow.Dac6701
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement}
import models.hallmarks.HallmarkD.D2
import models.hallmarks.HallmarkD1._
import models.hallmarks._
import models.intermediaries.WhatTypeofIntermediary.{IDoNotKnow, Promoter, Serviceprovider}
import models.intermediaries.{ExemptCountries, YouHaveNotAddedAnyIntermediaries}
import models.taxpayer.UpdateTaxpayer.{Later, No, Now}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.arrangement._
import pages.hallmarks._
import pages.individual._
import pages.intermediaries._
import pages.organisation._
import pages.taxpayer.{TaxpayerSelectTypePage, UpdateTaxpayerPage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
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

            navigator.nextPage(UnknownPage, 0, NormalMode, answers)
              .mustBe(routes.IndexController.onPageLoad())
        }
      }

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
              .nextPage(HallmarkCategoriesPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkAController.onPageLoad(0, NormalMode))
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
              .nextPage(HallmarkCategoriesPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Which categories of hallmarks are relevant to this arrangement' page " +
        "to 'Which parts of hallmark D apply to this arrangement?' page " +
        "when checkbox D is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("D").toSet)
                .success
                .value

            navigator
              .nextPage(HallmarkCategoriesPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkDController.onPageLoad(0, NormalMode))
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
              .nextPage(HallmarkAPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(0, NormalMode))
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
              .nextPage(HallmarkAPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkBController.onPageLoad(0, NormalMode))
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
              .nextPage(HallmarkBPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(0, NormalMode))
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
              .nextPage(MainBenefitTestPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(0))
        }
      }

      "must go from 'Does the arrangement meet the Main Benefit Test?' page " +
        "to 'There is a problem' page when No is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(MainBenefitTestPage, 0, false)
                .success
                .value

            navigator
              .nextPage(MainBenefitTestPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.MainBenefitProblemController.onPageLoad(0))
        }
      }

      "must go from 'Which parts of hallmark D apply to this arrangement?' page " +
        "to 'Check your answers' page when D2 only is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkDPage, 0, HallmarkD.values.toSet.filter(_ == D2))
                .success.value

            navigator
              .nextPage(HallmarkDPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(0))
        }
      }

      "must go from 'Which parts of hallmark D apply to this arrangement?' page " +
        "to 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "if Hallmark D1 was also selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkDPage, 0, HallmarkD.values.toSet)
                .success.value

            navigator
              .nextPage(HallmarkDPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkD1Controller.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to 'Which parts of hallmark E apply to this arrangement?' page if D1: Other is not selected and categories does not contain E" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkD1Page, 0, values.toSet.filter(_ != D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(0))
        }
      }


      "must go from 'Which parts of hallmark D1 apply to this arrangement?' page " +
        "to D1: Other page if D1: Other is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("D").toSet)
                .success.value
                .set(HallmarkD1Page, 0, values.toSet.filter(_ == D1other))
                .success.value

            navigator
              .nextPage(HallmarkD1Page, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Which category E hallmarks apply to this arrangement?' page " +
        "to 'check your answers' if category E is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(HallmarkCategoriesPage, 0, HallmarkCategories.enumerable.withName("E").toSet)
                .success.value
                .set(HallmarkEPage, 0, HallmarkE.values.toSet)
                .success.value


            navigator
              .nextPage(HallmarkEPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(0))
        }
      }

      "must go from 'You have not added any taxpayers' page to 'Is this an organisation or an individual?' " +
        "if 'No, is selected'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(UpdateTaxpayerPage, 0, No)
                .success.value

            navigator
              .nextPage(UpdateTaxpayerPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'You have not added any taxpayers' page to 'Is this an organisation or an individual?' " +
        "if 'Yes, add later is selected'" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(UpdateTaxpayerPage, 0, Later)
                .success.value

            navigator
              .nextPage(UpdateTaxpayerPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'Is this an organisation or an individual?' page to 'What is the name of the organisation?' " +
        "if 'organisation' is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(TaxpayerSelectTypePage, 0, Organisation)
                .success.value

            navigator
              .nextPage(TaxpayerSelectTypePage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Is this an organisation or an individual?' page to 'What is their name?' " +
        "if 'individual' is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(TaxpayerSelectTypePage, 0, Individual)
                .success.value

            navigator
              .nextPage(TaxpayerSelectTypePage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'You have not added any intermediaries' page to " +
        "'Is this an organisation or an individual?' if answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(YouHaveNotAddedAnyIntermediariesPage, 0, YouHaveNotAddedAnyIntermediaries.YesAddNow)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyIntermediariesPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'You have not added any intermediaries' page to " +
        "Index page if answer is 'No'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(YouHaveNotAddedAnyIntermediariesPage, 0, YouHaveNotAddedAnyIntermediaries.No)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyIntermediariesPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'You have not added any intermediaries' page to " +
        "Index page if answer is 'YesAddLater'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(YouHaveNotAddedAnyIntermediariesPage, 0, YouHaveNotAddedAnyIntermediaries.YesAddLater)
                .success.value

            navigator
              .nextPage(YouHaveNotAddedAnyIntermediariesPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.routes.IndexController.onPageLoad())
        }
      }

      "must go from 'Is this an organisation or an individual?' intermediaries page to " +
        "'What is the name of the organisation?' if answer is Organisation" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IntermediariesTypePage, 0, SelectType.Organisation)
                .success.value

            navigator
              .nextPage(IntermediariesTypePage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Is this an organisation or an individual?' intermediaries page to " +
        "'What is their name?' if answer is Individual" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IntermediariesTypePage, 0, SelectType.Individual)
                .success.value

            navigator
              .nextPage(IntermediariesTypePage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'What type of intermediary is name?' page to " +
        "'Is *name* exempt from reporting in an EU member state, or the UK?' if answer is Promoter" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatTypeofIntermediaryPage, 0, Promoter)
                .success.value

            navigator
              .nextPage(WhatTypeofIntermediaryPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'What type of intermediary is name?' page to " +
        "'Is *name* exempt from reporting in an EU member state, or the UK?' if answer is Service Provider" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatTypeofIntermediaryPage, 0, Serviceprovider)
                .success.value

            navigator
              .nextPage(WhatTypeofIntermediaryPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }
      }

      "must go from 'What type of intermediary is name?' page to " +
        "'Is *name* exempt from reporting in an EU member state, or the UK?' if answer is I do not know" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatTypeofIntermediaryPage, 0, IDoNotKnow)
                .success.value

            navigator
              .nextPage(WhatTypeofIntermediaryPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }
      }

      "must go from ' Is *name* exempt from reporting in an EU member state, or the UK?' intermediaries page to " +
        "'Do you know which countries *name* is exempt from reporting in?' if answer is Yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IsExemptionKnownPage, 0, IsExemptionKnown.Yes)
                .success.value

            navigator
              .nextPage(IsExemptionKnownPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(0, NormalMode))
        }
      }

      "Is *name* exempt from reporting in an EU member state, or the UK?' intermediaries page to " +
        "'Do you know which countries *name* is exempt from reporting in?' if answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IsExemptionKnownPage, 0, IsExemptionKnown.No)
                .success.value

            navigator
              .nextPage(IsExemptionKnownPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }
      }

      "Is *name* exempt from reporting in an EU member state, or the UK?' intermediaries page to " +
        "'Do you know which countries *name* is exempt from reporting in?' if answer is I do not know" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IsExemptionKnownPage, 0, IsExemptionKnown.Unknown)
                .success.value

            navigator
              .nextPage(IsExemptionKnownPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }
      }


      "Do you know which countries *name* exempt from reporting in?' page to " +
        "'Which countries is *name* exempt from reporting in?' page in the intermediaries journey" +
        "when answer is true" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IsExemptionCountryKnownPage, 0, true)
                .success.value

            navigator
              .nextPage(IsExemptionCountryKnownPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(0, NormalMode))
        }
      }

      "Do you know which countries *name* exempt from reporting in?' page to " +
        "'Check your answers' page in the intermediaries journey when answer is false" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IsExemptionCountryKnownPage, 0, false)
                .success.value

            navigator
              .nextPage(IsExemptionCountryKnownPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }
      }

      "'Which countries is *name* exempt from reporting in?' page to " +
        "'Check your answers' page in the intermediaries journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>

              val updatedAnswers =
                answers
                  .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                  .set(ExemptCountriesPage, 0, ExemptCountries.enumerable.withName("uk").toSet)
                  .success
                  .value

              navigator
                .nextPage(ExemptCountriesPage, 0, NormalMode, updatedAnswers)
                .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
          }
        }

      "must go from What is the arrangement called? page to " +
        "what is the implementation date?" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatIsThisArrangementCalledPage, 0, "an arrangement")
                .success.value

            navigator
              .nextPage(WhatIsThisArrangementCalledPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(0, NormalMode))
        }
      }

      "must go from What is the implementation date? page to " +
        "Do you know the reason Do you know the reason this arrangement must be reported now? page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            navigator
              .nextPage(WhatIsTheImplementationDatePage, 0, NormalMode, answers)
              .mustBe(controllers.arrangement.routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(0, NormalMode))
        }
      }

      "Do you know the reason this arrangement must be reported now? page to " +
        "Why are you reporting this arrangement now? page when the answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(DoYouKnowTheReasonToReportArrangementNowPage, 0, true)
                .success.value

            navigator
              .nextPage(DoYouKnowTheReasonToReportArrangementNowPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(0, NormalMode))
        }
      }

      "Do you know the reason this arrangement must be reported now? page to " +
        "'Which of these countries are expected to be involved in this arrangement?' page when the answer is No" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(DoYouKnowTheReasonToReportArrangementNowPage, 0, false)
                .success.value

            navigator
              .nextPage(DoYouKnowTheReasonToReportArrangementNowPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(0, NormalMode))
        }
      }

      "must go from the 'Why are reporting this arrangement now?' page to " +
        "'Which of these countries are expected to be involved in this arrangement?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhyAreYouReportingThisArrangementNowPage, 0, Dac6701)
                .success.value

            navigator
              .nextPage(WhyAreYouReportingThisArrangementNowPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Which of these countries are expected to be involved in this arrangement?' page to " +
        "'What is the expected value of this arrangement?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhichExpectedInvolvedCountriesArrangementPage, 0, WhichExpectedInvolvedCountriesArrangement.enumerable.withName("GB").toSet)
                .success.value

            navigator
              .nextPage(WhichExpectedInvolvedCountriesArrangementPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'What is the expected value of this arrangement?' page to " +
        "'Which national provisions is this arrangement based on?'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, WhatIsTheExpectedValueOfThisArrangement("ALL", 0))
                .success.value

            navigator
              .nextPage(WhatIsTheExpectedValueOfThisArrangementPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(0, NormalMode))
        }
      }

      "must go from 'Which national provisions is this arrangement based on?' page to " +
        "'Give details of this arrangement'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, "provisions")
                .success.value

            navigator
              .nextPage(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(0, NormalMode))
        }
      }



      "What is [name]'s implementation date? page to " +
        "taxpayers check your answers page if organisation entered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now)
                .success.value
                .set(OrganisationNamePage, 0, "validAnswer")
                .success.value

            navigator
              .nextPage(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }
      }

      "What is [name]'s implementation date? page to " +
        "taxpayers check your answers page if individual entered" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now)
                .success.value
                .remove(OrganisationNamePage, 0)
                .success
                .value
                .set(IndividualNamePage, 0, Name("dummy","user"))
                .success.value

            navigator
              .nextPage(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, NormalMode, updatedAnswers)
              .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }
      }

      "Hallmarks Check Your Answers page to " +
        "Task list page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(HallmarksCheckYourAnswersPage, 0, NormalMode, answers)
              .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad(0))
        }
      }
    }
  }
}
