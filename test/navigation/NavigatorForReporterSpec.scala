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
import generators.Generators
import models.{YesNoDoNotKnowRadios, _}
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhichCountriesExemptPage, IntermediaryWhyReportInUKPage}
import pages.reporter.taxpayer.TaxpayerWhyReportInUKPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForReporterSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "NavigatorForReporter" - {

    "on INTERMEDIARY JOURNEY in Normal mode" - {

      "must go from 'What is your role in this arrangement?' page " +
        "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "when any 'INTERMEDIARY' option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value

            NavigatorForReporter.nextPage(RoleInArrangementPage, NormalMode, updatedAnswers.get(RoleInArrangementPage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'As an intermediary, what is your role in this arrangement' page " +
        "when any option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            NavigatorForReporter.nextPage(IntermediaryWhyReportInUKPage, NormalMode, answers.get(IntermediaryWhyReportInUKPage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(NormalMode))

        }
      }

      "must go from 'As an intermediary, what is your role in this arrangement' page " +
        "to 'Are you exempt from reporting in any of the EU member states?' page " +
        "when any option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            NavigatorForReporter.nextPage(IntermediaryRolePage, NormalMode, answers.get(IntermediaryRolePage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Do you know which countries you are exempt from reporting in?' page " +
        "when option YES is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers.set(IntermediaryExemptionInEUPage, YesNoDoNotKnowRadios.Yes).success.value

            NavigatorForReporter.nextPage(IntermediaryExemptionInEUPage, NormalMode, updatedAnswers.get(IntermediaryExemptionInEUPage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Check your answers' page " +
        "when option NO is selected" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers.set(IntermediaryExemptionInEUPage, YesNoDoNotKnowRadios.No).success.value

            //TODO - redirect to CYA page when built
            NavigatorForReporter.nextPage(IntermediaryExemptionInEUPage, NormalMode, updatedAnswers.get(IntermediaryExemptionInEUPage))
              .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Check your answers' page " +
        "when option I DO NOT KNOW is selected" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers.set(IntermediaryExemptionInEUPage, YesNoDoNotKnowRadios.No).success.value

            //TODO - redirect to CYA page when built
            NavigatorForReporter.nextPage(IntermediaryExemptionInEUPage, NormalMode, updatedAnswers.get(IntermediaryExemptionInEUPage))
              .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Do you know which countries you are exempt from reporting in?' page " +
        "to 'which countries are you exempt from reporting in' page " +
        "when option YES is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers.set(IntermediaryDoYouKnowExemptionsPage, true).success.value

            NavigatorForReporter.nextPage(IntermediaryDoYouKnowExemptionsPage, NormalMode, updatedAnswers.get(IntermediaryDoYouKnowExemptionsPage))
              .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Do you know which countries you are exempt from reporting in?' page " +
        "to 'Check your Answers' page " +
        "when option NO is selected" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers = answers.set(IntermediaryDoYouKnowExemptionsPage, false).success.value

            //TODO - redirect to CYA page when built
            NavigatorForReporter.nextPage(IntermediaryDoYouKnowExemptionsPage, NormalMode, updatedAnswers.get(IntermediaryDoYouKnowExemptionsPage))
                .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Which countries are you exempt from reporting in?' page " +
        "to 'Check your Answers' page " +
        "when any option is selected" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            //TODO - redirect to CYA page when built
            NavigatorForReporter.nextPage(IntermediaryWhichCountriesExemptPage, NormalMode, answers.get(IntermediaryDoYouKnowExemptionsPage))
                .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))

        }
      }
    }

    "on TAXPAYER JOURNEY in Normal Mode" - {

      "must go from 'What is your role in this arrangement?' page " +
        "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "when 'TAXPAYER' option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value

            NavigatorForReporter.nextPage(RoleInArrangementPage, NormalMode, updatedAnswers.get(RoleInArrangementPage))
              .mustBe(controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'Why are you reporting the arrangement as a taxpayer' page " +
        "when 'I DO NOT KNOW' is 'NOT' selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkTaxResident).success.value

            NavigatorForReporter.nextPage(TaxpayerWhyReportInUKPage, NormalMode, updatedAnswers.get(TaxpayerWhyReportInUKPage))
              .mustBe(controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'What is [name] start date for implementing this arrangement' page " +
        "when 'I DO NOT KNOW' is selected & its a marketable arrangement" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.DoNotKnow).success.value

            //TODO - redirect to implementing date page if marketable arrangement
            NavigatorForReporter.nextPage(TaxpayerWhyReportInUKPage, NormalMode, updatedAnswers.get(TaxpayerWhyReportInUKPage))
              .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
        }
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'What is [name] start date for implementing this arrangement' page " +
        "when 'I DO NOT KNOW' is selected & its 'NOT' a marketable arrangement" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.DoNotKnow).success.value

            //TODO - redirect to CYA page
            NavigatorForReporter.nextPage(TaxpayerWhyReportInUKPage, NormalMode, updatedAnswers.get(TaxpayerWhyReportInUKPage))
              .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
        }
      }
    }
  }
}

