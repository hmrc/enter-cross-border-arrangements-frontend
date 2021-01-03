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
import controllers.mixins.DefaultRouting
import generators.Generators
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.{YesNoDoNotKnowRadios, _}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.TaxpayerWhyReportInUKPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForReporterSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForReporter
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")
  val addressUK: Address = Address(Some("addressLine1"), Some("addressLine2"), Some("addressLine3"), "city", Some("postcode"),
    Country("valid", "GB", "United Kingdom"))

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

      "must go from 'Are you reporting as an organisation or individual?' page " +
        "to 'What is the name of the organisation you’re reporting for?' page " +
        "when any 'ORGANISATION' option is selected" in {

        navigator
          .routeMap(ReporterOrganisationOrIndividualPage)(DefaultRouting(NormalMode))(Some(ReporterOrganisationOrIndividual.Organisation))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(NormalMode))
      }

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
        "to select 'What is [name]'s address' select address page " +
        "when valid postcode is entered" in {

        navigator
          .routeMap(ReporterOrganisationPostcodePage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationSelectAddressController.onPageLoad(NormalMode))
      }

      "must go from 'What is [name]'s address' manual address' page " +
        "to select 'Do you have a contact email address at [name]' page " +
        "when a valid address is entered" in {

        navigator
          .routeMap(ReporterOrganisationAddressPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(NormalMode))
      }

      "must go from 'What is [name]'s address' select address' page " +
        "to select 'Do you have a contact email address at [name]' page " +
        "when a valid address is selected" in {

        navigator
          .routeMap(ReporterOrganisationSelectAddressPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'What is the contact email for [name]' page " +
        "when option YES is selected" ignore {

        // TODO - add redirec tto what is email address page
        navigator
          .routeMap(ReporterOrganisationSelectAddressPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'Which country is [name] resident in for tax purposes' page " +
        "when option NO is selected" ignore {

        // TODO - add redirect to tax residency page
        navigator
          .routeMap(ReporterOrganisationSelectAddressPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(NormalMode))
      }
    }


    "on INDIVIDUAL JOURNEY in Normal Mode" - {

      "must go from 'Are you reporting as an organisation or individual?' page " +
        "to 'What is your full name?' page " +
        "when any 'INDIVIDUAL' option is selected" in {

        navigator
          .routeMap(ReporterOrganisationOrIndividualPage)(DefaultRouting(NormalMode))(Some(ReporterOrganisationOrIndividual.Individual))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(NormalMode))
      }

      "must go from 'What is your full name?' page " +
        "to 'What is your date of birth?' page " +
        "when a valid name is entered" in {

        navigator
          .routeMap(ReporterIndividualNamePage)(DefaultRouting(NormalMode))(Some(Name("Some", "Name")))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(NormalMode))
      }

      "must go from 'What is your date of birth?' page " +
        "to 'What is your place of birth?' page " +
        "when a valid DOB is entered" in {

        navigator
          .routeMap(ReporterIndividualDateOfBirthPage)(DefaultRouting(NormalMode))(Some(LocalDate.now()))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(NormalMode))
      }

      "must go from 'What is your place of birth?' page " +
        "to 'Do you live in the United Kingdom?" +
        "when a valid POB is entered" in {

        navigator
          .routeMap(ReporterIndividualPlaceOfBirthPage)(DefaultRouting(NormalMode))(Some("Some place in some country"))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(NormalMode))
      }

      "must go from 'Do you live in the United Kingdom?' page " +
        "to select 'What is your postcode?' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterIsIndividualAddressUKPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualPostcodeController.onPageLoad(NormalMode))
      }

      "must go from 'Do you live in the United Kingdom?' page " +
        "to select 'What is your address?' manual address page " +
        "when option NO is selected" in {

        navigator
          .routeMap(ReporterIsIndividualAddressUKPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualAddressController.onPageLoad(NormalMode))
      }

      "must go from 'What is your postcode?' page " +
        "to select 'What is your address' select address page " +
        "when valid postcode is entered" in {

        navigator
          .routeMap(ReporterIndividualPostcodePage)(DefaultRouting(NormalMode))(Some("postcode"))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualSelectAddressController.onPageLoad(NormalMode))
      }

      "must go from 'What is your address?' manual address' page " +
        "to select 'Do you have a preferred email address?' page " +
        "when a valid address is entered" in {

        navigator
          .routeMap(ReporterIndividualAddressPage)(DefaultRouting(NormalMode))(Some(addressUK))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(NormalMode))
      }

      "must go from 'What is your address?' select address' page " +
        "to select 'Do you have a preferred email address?' page " +
        "when a valid address is selected" in {

        navigator
          .routeMap(ReporterIndividualSelectAddressPage)(DefaultRouting(NormalMode))(Some(addressUK))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'What is the contact email for [name]' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterIndividualEmailAddressQuestionPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'Which country is [name] resident in for tax purposes' page " +
        "when option NO is selected" ignore {

        // TODO - add redirect to tax residency page
        navigator
          .routeMap(ReporterIndividualEmailAddressQuestionPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(NormalMode))
      }
    }
  }
}

