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
import controllers.mixins.DefaultRouting
import generators.Generators
import models.{YesNoDoNotKnowRadios, _}
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhichCountriesExemptPage, IntermediaryWhyReportInUKPage}
import pages.reporter.organisation.{ReporterOrganisationIsAddressUkPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.TaxpayerWhyReportInUKPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForReporterSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForReporter
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "NavigatorForReporter" - {

    "on INTERMEDIARY JOURNEY in Normal mode" - {

      "must go from 'What is your role in this arrangement?' page " +
        "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "when any 'INTERMEDIARY' option is selected" in {

        navigator
          .routeMap(RoleInArrangementPage)(DefaultRouting(NormalMode))(Some(RoleInArrangement.Intermediary))(0)
          .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(NormalMode))
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'As an intermediary, what is your role in this arrangement' page " +
        "when any option is selected" in {

        forAll(arbitrary[IntermediaryWhyReportInUK]) {
          answers =>

            navigator
              .routeMap(IntermediaryWhyReportInUKPage)(DefaultRouting(NormalMode))(Some(answers))(0)
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(NormalMode))

        }
      }

      "must go from 'As an intermediary, what is your role in this arrangement' page " +
        "to 'Are you exempt from reporting in any of the EU member states?' page " +
        "when any option is selected" in {

        forAll(arbitrary[IntermediaryRole]) {
          answers =>

            navigator
              .routeMap(IntermediaryRolePage)(DefaultRouting(NormalMode))(Some(answers))(0)
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Do you know which countries you are exempt from reporting in?' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(IntermediaryExemptionInEUPage)(DefaultRouting(NormalMode))(Some(YesNoDoNotKnowRadios.Yes))(0)
          .mustBe(controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(NormalMode))
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Check your answers' page " +
        "when option NO is selected" ignore {

        //TODO - redirect to CYA page when built
        navigator
          .routeMap(IntermediaryExemptionInEUPage)(DefaultRouting(NormalMode))(Some(YesNoDoNotKnowRadios.DoNotKnow))(0)
          .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Check your answers' page " +
        "when option I DO NOT KNOW is selected" ignore {

        //TODO - redirect to CYA page when built
        navigator
          .routeMap(IntermediaryExemptionInEUPage)(DefaultRouting(NormalMode))(Some(YesNoDoNotKnowRadios.No))(0)
          .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
      }

      "must go from 'Do you know which countries you are exempt from reporting in?' page " +
        "to 'which countries are you exempt from reporting in' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(IntermediaryDoYouKnowExemptionsPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(NormalMode))
      }

      "must go from 'Do you know which countries you are exempt from reporting in?' page " +
        "to 'Check your Answers' page " +
        "when option NO is selected" ignore {

        //TODO - redirect to CYA page when built
        navigator
          .routeMap(IntermediaryDoYouKnowExemptionsPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
      }

      "must go from 'Which countries are you exempt from reporting in?' page " +
        "to 'Check your Answers' page " +
        "when any option is selected" ignore {

        forAll(arbitrary[CountriesListEUCheckboxes]) {
          answers =>

            //TODO - redirect to CYA page when built
            navigator
              .routeMap(IntermediaryWhichCountriesExemptPage)(DefaultRouting(NormalMode))(Some(Seq(answers)))(0)
                .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))

        }
      }
    }

    "on TAXPAYER JOURNEY in Normal Mode" - {

      "must go from 'What is your role in this arrangement?' page " +
        "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "when 'TAXPAYER' option is selected" in {

        navigator
          .routeMap(RoleInArrangementPage)(DefaultRouting(NormalMode))(Some(RoleInArrangement.Taxpayer))(0)
          .mustBe(controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(NormalMode))
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'Why are you reporting the arrangement as a taxpayer' page " +
        "when 'I DO NOT KNOW' is 'NOT' selected" in {

        navigator
          .routeMap(TaxpayerWhyReportInUKPage)(DefaultRouting(NormalMode))(Some(TaxpayerWhyReportInUK.UkTaxResident))(0)
          .mustBe(controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(NormalMode))
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'What is [name] start date for implementing this arrangement' page " +
        "when 'I DO NOT KNOW' is selected & its a marketable arrangement" ignore {

        //TODO - redirect to implementing date page if marketable arrangement
        navigator
          .routeMap(TaxpayerWhyReportInUKPage)(DefaultRouting(NormalMode))(Some(TaxpayerWhyReportInUK.DoNotKnow))(0)
          .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'What is [name] start date for implementing this arrangement' page " +
        "when 'I DO NOT KNOW' is selected & its 'NOT' a marketable arrangement" ignore {

        //TODO - redirect to CYA page
        navigator
          .routeMap(TaxpayerWhyReportInUKPage)(DefaultRouting(NormalMode))(Some(TaxpayerWhyReportInUK.DoNotKnow))(0)
          .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(NormalMode))
      }
    }

    "on ORGANISATION JOURNEY in Normal Mode" - {

      "must go from 'What is the name of the organisation your're reporting for' page " +
        "to 'Is [name]'s address in the United Kingdom' page " +
        "when a valid name is entered" in {

        navigator
          .routeMap(ReporterOrganisationNamePage)(DefaultRouting(NormalMode))(Some("name"))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(NormalMode))
      }

      "must go from 'Is [name]'s address in the United Kingdom' page " +
        "to 'What is [name]'s postcode' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterOrganisationIsAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(NormalMode))
      }

      "must go from 'Is [name]'s address in the United Kingdom' page " +
        "to 'What is [name]'s address non UK' page" +
        "when option NO is selected" in {

        navigator
          .routeMap(ReporterOrganisationIsAddressUkPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(NormalMode))
      }

      "must go from 'What is [name]'s postcode' page " +
        "to select 'What is [name]'s address' page " +
        "when valid postcode is entered" in {

        //TODO - redirect to select address page
        navigator
          .routeMap(ReporterOrganisationIsAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(NormalMode))
      }

      "must go from 'What is [name]'s address' page " +
        "to select 'Do you have a contact email address at [name]' page " +
        "when a valid address is entered" ignore {

        //TODO - redirect to reporter details organisation email address page
        navigator
          .routeMap(ReporterOrganisationIsAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(NormalMode))
      }

    }
  }
}

