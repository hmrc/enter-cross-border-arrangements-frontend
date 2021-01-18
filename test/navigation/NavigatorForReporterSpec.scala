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
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForReporterSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForReporter
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")
  val addressUK: Address = Address(Some("addressLine1"), Some("addressLine2"), Some("addressLine3"), "city", Some("postcode"),
    Country("valid", "GB", "United Kingdom"))

  "NavigatorForReporter" - {

    "in Normal mode" - {

      "must go from 'Are you reporting as an organisation or individual?' page " +
        "to 'What is the name of the organisation you’re reporting for?' page " +
        "when any 'ORGANISATION' option is selected" in {

        navigator
          .routeMap(ReporterOrganisationOrIndividualPage)(DefaultRouting(NormalMode))(0)(Some(ReporterOrganisationOrIndividual.Organisation))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(0, NormalMode))
      }

      "must go from 'Are you reporting as an organisation or individual?' page " +
        "to 'What is your full name?' page " +
        "when any 'INDIVIDUAL' option is selected" in {

        navigator
          .routeMap(ReporterOrganisationOrIndividualPage)(DefaultRouting(NormalMode))(0)(Some(ReporterOrganisationOrIndividual.Individual))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(0, NormalMode))
      }

      "must go from 'Which country *are you/is org* resident in for tax purposes?' page " +
        "to 'Do *you have any/you know any of org's* tax identification numbers for the United Kingdom?' page " +
        "when UNITED KINGDOM is selected" in {

        navigator
          .routeMap(ReporterTaxResidentCountryPage)(DefaultRouting(NormalMode))(0)(Some(Country("valid", "GB", "United Kingdom")))(0)
          .mustBe(controllers.reporter.routes.ReporterTinUKQuestionController.onPageLoad(0, NormalMode, 0))
      }

      "must go from 'Which country *are you/is org* resident in for tax purposes?' page " +
        "to 'Do you *have any/ know organisation's* tax identification numbers for *country*?' page " +
        "when UNITED KINGDOM is NOT selected" in {

        navigator
          .routeMap(ReporterTaxResidentCountryPage)(DefaultRouting(NormalMode))(0)(Some(Country("valid", "FR", "France")))(0)
          .mustBe(controllers.reporter.routes.ReporterTinNonUKQuestionController.onPageLoad(0, NormalMode, 0))
      }

      "must go from 'Do *you have any/you know any of organisations* tax identification numbers for the United Kingdom?' page " +
        "to 'What are *your/organisation's* tax identification numbers for the United Kingdom?' page " +
        "when YES is selected" in {

        navigator
          .routeMap(ReporterTinUKQuestionPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.routes.ReporterUKTaxNumbersController.onPageLoad(0, NormalMode, 0))
      }

      "must go from 'Do *you have any/you know any of organisations* tax identification numbers for the United Kingdom?' page " +
        "to '*Are you/Is organisation* resident for tax purposes in any other countries?' page " +
        "when NO is selected" in {

        navigator
          .routeMap(ReporterTinUKQuestionPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(0, NormalMode, 1))
      }

      "must go from 'Do you *have any/ know organisation's* tax identification numbers for *country*' page " +
        "to 'What are *your/organisation's* tax identification numbers for *country*' page " +
        "when YES is selected" in {

        navigator
          .routeMap(ReporterTinNonUKQuestionPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.routes.ReporterNonUKTaxNumbersController.onPageLoad(0, NormalMode, 0))
      }

      "must go from 'Do *you have any/you know any of organisations* tax identification numbers for the *country*' page " +
        "to '*Are you/Is organisation* resident for tax purposes in any other countries?' page " +
        "when NO is selected" in {

        navigator
          .routeMap(ReporterTinUKQuestionPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(0, NormalMode, 1))
      }

      "must go from '*Are you/Is organisation* resident for tax purposes in any other countries?' page " +
        "to 'What is your role in this arrangement?' page " +
        "when NO is selected" in {

        navigator
          .routeMap(ReporterOtherTaxResidentQuestionPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.routes.RoleInArrangementController.onPageLoad(0, NormalMode))
      }

      "must go from '*Are you/Is organisation* resident for tax purposes in any other countries?' page " +
        "to 'Which country *are you/is org* resident in for tax purposes?' page " +
        "when YES is selected" in {

        navigator
          .routeMap(ReporterOtherTaxResidentQuestionPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(0, NormalMode, 0))
      }

      "must go from 'What is your role in this arrangement?' page " +
        "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "when any 'INTERMEDIARY' option is selected" in {

        navigator
          .routeMap(RoleInArrangementPage)(DefaultRouting(NormalMode))(0)(Some(RoleInArrangement.Intermediary))(0)
          .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(0, NormalMode))
      }
    }

    "must go from 'What is your role in this arrangement?' page " +
      "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
      "when 'TAXPAYER' option is selected" in {

      navigator
        .routeMap(RoleInArrangementPage)(DefaultRouting(NormalMode))(0)(Some(RoleInArrangement.Taxpayer))(0)
        .mustBe(controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(0, NormalMode))
    }

    "on REPORTER DETAILS - INTERMEDIARY JOURNEY in Normal mode" - {

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'As an intermediary, what is your role in this arrangement' page " +
        "when any option is selected" in {

        forAll(arbitrary[IntermediaryWhyReportInUK]) {
          answers =>

            navigator
              .routeMap(IntermediaryWhyReportInUKPage)(DefaultRouting(NormalMode))(0)(Some(answers))(0)
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(0, NormalMode))

        }
      }

      "must go from 'As an intermediary, what is your role in this arrangement' page " +
        "to 'Are you exempt from reporting in any of the EU member states?' page " +
        "when any option is selected" in {

        forAll(arbitrary[IntermediaryRole]) {
          answers =>

            navigator
              .routeMap(IntermediaryRolePage)(DefaultRouting(NormalMode))(0)(Some(answers))(0)
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(0, NormalMode))

        }
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Do you know which countries you are exempt from reporting in?' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(IntermediaryExemptionInEUPage)(DefaultRouting(NormalMode))(0)(Some(YesNoDoNotKnowRadios.Yes))(0)
          .mustBe(controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(0, NormalMode))
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Check your answers' page " +
        "when option NO is selected" in {

        navigator
          .routeMap(IntermediaryExemptionInEUPage)(DefaultRouting(NormalMode))(0)(Some(YesNoDoNotKnowRadios.DoNotKnow))(0)
          .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
      }

      "must go from 'Are you exempt from reporting in any of the EU member states?' page " +
        "to 'Check your answers' page " +
        "when option I DO NOT KNOW is selected" in {

        navigator
          .routeMap(IntermediaryExemptionInEUPage)(DefaultRouting(NormalMode))(0)(Some(YesNoDoNotKnowRadios.No))(0)
          .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
      }

      "must go from 'Do you know which countries you are exempt from reporting in?' page " +
        "to 'which countries are you exempt from reporting in' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(IntermediaryDoYouKnowExemptionsPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you know which countries you are exempt from reporting in?' page " +
        "to 'Check your Answers' page " +
        "when option NO is selected" in {

        navigator
          .routeMap(IntermediaryDoYouKnowExemptionsPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
      }

      "must go from 'Which countries are you exempt from reporting in?' page " +
        "to 'Check your Answers' page " +
        "when any option is selected" in {

        forAll(arbitrary[CountriesListEUCheckboxes]) {
          answers =>

            navigator
              .routeMap(IntermediaryWhichCountriesExemptPage)(DefaultRouting(NormalMode))(0)(Some(Seq(answers)))(0)
                .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
        }
      }
    }

    "on REPORTER DETAILS - TAXPAYER JOURNEY in Normal Mode" - {

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'Why are you reporting the arrangement as a taxpayer' page " +
        "when 'I DO NOT KNOW' is 'NOT' selected" in {

        navigator
          .routeMap(TaxpayerWhyReportInUKPage)(DefaultRouting(NormalMode))(0)(Some(TaxpayerWhyReportInUK.UkTaxResident))(0)
          .mustBe(controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(0, NormalMode))
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'What is [name] start date for implementing this arrangement' page " +
        "when 'I DO NOT KNOW' is selected & its a marketable arrangement" in {

        navigator
          .routeMap(TaxpayerWhyReportInUKPage)(DefaultRouting(NormalMode))(0)(Some(TaxpayerWhyReportInUK.DoNotKnow))(0)
          .mustBe(controllers.reporter.taxpayer.routes.ReporterTaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode))
      }
    }

    "on REPORTER DETAILS -  ORGANISATION JOURNEY in Normal Mode" - {

      "must go from 'What is the name of the organisation your're reporting for' page " +
        "to 'Is [name]'s address in the United Kingdom' page " +
        "when a valid name is entered" in {

        navigator
          .routeMap(ReporterOrganisationNamePage)(DefaultRouting(NormalMode))(0)(Some("name"))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(0, NormalMode))
      }

      "must go from 'Is [name]'s address in the United Kingdom' page " +
        "to 'What is [name]'s postcode' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterOrganisationIsAddressUkPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(0, NormalMode))
      }

      "must go from 'Is [name]'s address in the United Kingdom' page " +
        "to 'What is [name]'s address non UK' page" +
        "when option NO is selected" in {

        navigator
          .routeMap(ReporterOrganisationIsAddressUkPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is [name]'s postcode' page " +
        "to select 'What is [name]'s address' select address page " +
        "when valid postcode is entered" in {

        navigator
          .routeMap(ReporterOrganisationPostcodePage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationSelectAddressController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is [name]'s address' manual address' page " +
        "to select 'Do you have a contact email address at [name]' page " +
        "when a valid address is entered" in {

        navigator
          .routeMap(ReporterOrganisationAddressPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is [name]'s address' select address' page " +
        "to select 'Do you have a contact email address at [name]' page " +
        "when a valid address is selected" in {

        navigator
          .routeMap(ReporterOrganisationSelectAddressPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'What is the contact email for [name]' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterOrganisationSelectAddressPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'Which country is [name] resident in for tax purposes' page " +
        "when option NO is selected" in {

        navigator
          .routeMap(ReporterOrganisationEmailAddressQuestionPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(0, NormalMode, 0))
      }
    }


    "on REPORTER DETAILS - INDIVIDUAL JOURNEY in Normal Mode" - {

      "must go from 'What is your full name?' page " +
        "to 'What is your date of birth?' page " +
        "when a valid name is entered" in {

        navigator
          .routeMap(ReporterIndividualNamePage)(DefaultRouting(NormalMode))(0)(Some(Name("Some", "Name")))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is your date of birth?' page " +
        "to 'What is your place of birth?' page " +
        "when a valid DOB is entered" in {

        navigator
          .routeMap(ReporterIndividualDateOfBirthPage)(DefaultRouting(NormalMode))(0)(Some(LocalDate.now()))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is your place of birth?' page " +
        "to 'Do you live in the United Kingdom?" +
        "when a valid POB is entered" in {

        navigator
          .routeMap(ReporterIndividualPlaceOfBirthPage)(DefaultRouting(NormalMode))(0)(Some("Some place in some country"))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you live in the United Kingdom?' page " +
        "to select 'What is your postcode?' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterIsIndividualAddressUKPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualPostcodeController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you live in the United Kingdom?' page " +
        "to select 'What is your address?' manual address page " +
        "when option NO is selected" in {

        navigator
          .routeMap(ReporterIsIndividualAddressUKPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualAddressController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is your postcode?' page " +
        "to select 'What is your address' select address page " +
        "when valid postcode is entered" in {

        navigator
          .routeMap(ReporterIndividualPostcodePage)(DefaultRouting(NormalMode))(0)(Some("postcode"))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualSelectAddressController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is your address?' manual address' page " +
        "to select 'Do you have a preferred email address?' page " +
        "when a valid address is entered" in {

        navigator
          .routeMap(ReporterIndividualAddressPage)(DefaultRouting(NormalMode))(0)(Some(addressUK))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(0, NormalMode))
      }

      "must go from 'What is your address?' select address' page " +
        "to select 'Do you have a preferred email address?' page " +
        "when a valid address is selected" in {

        navigator
          .routeMap(ReporterIndividualSelectAddressPage)(DefaultRouting(NormalMode))(0)(Some(addressUK))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'What is the contact email for [name]' page " +
        "when option YES is selected" in {

        navigator
          .routeMap(ReporterIndividualEmailAddressQuestionPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
          .mustBe(controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(0, NormalMode))
      }

      "must go from 'Do you have a contact email address at [name]' page " +
        "to select 'Which country is [name] resident in for tax purposes' page " +
        "when option NO is selected" in {

        navigator
          .routeMap(ReporterIndividualEmailAddressQuestionPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
          .mustBe(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(0, NormalMode, 0))
      }
    }
  }

  "on REPORTER DETAILS -  ORGANISATION JOURNEY in CHECK Mode" - {

    "must go from 'What is the name of the organisation your're reporting for' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterOrganisationNamePage)(DefaultRouting(CheckMode))(0)(Some("name"))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }


    "must go from 'What is [name]'s address' manual address' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterOrganisationAddressPage)(DefaultRouting(CheckMode))(0)(Some(addressUK))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'What is [name]'s address' select address' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterOrganisationSelectAddressPage)(DefaultRouting(CheckMode))(0)(Some(addressUK))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'What is the contact email for [name]' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterOrganisationEmailAddressPage)(DefaultRouting(CheckMode))(0)(Some("email"))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'Do you have a contact email address at [name]' page " +
      "to 'Check your answers for your reporter details' page " +
      "when option NO is selected" in {

      navigator
        .routeMap(ReporterOrganisationEmailAddressQuestionPage)(DefaultRouting(CheckMode))(0)(Some(false))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }
  }

  "on REPORTER DETAILS - INDIVIDUAL JOURNEY in Normal Mode" - {

    "must go from 'What is your full name?' page " +
      "to 'Check your answers for your reporter details' page " in {


      navigator
        .routeMap(ReporterIndividualNamePage)(DefaultRouting(CheckMode))(0)(Some(Name("Some", "Name")))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'What is your date of birth?' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterIndividualDateOfBirthPage)(DefaultRouting(CheckMode))(0)(Some(LocalDate.now()))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'What is your place of birth?' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterIndividualPlaceOfBirthPage)(DefaultRouting(CheckMode))(0)(Some("Some place in some country"))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'What is your address?' manual address' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterIndividualAddressPage)(DefaultRouting(CheckMode))(0)(Some(addressUK))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'What is your address?' select address' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterIndividualSelectAddressPage)(DefaultRouting(CheckMode))(0)(Some(addressUK))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'Do you have a contact email address at [name]' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterIndividualEmailAddressQuestionPage)(DefaultRouting(CheckMode))(0)(Some(false))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }
  }

  "on REPORTER DETAILS - TAX RESIDENCY JOURNEY in Check mode" - {

    "must go from '*Are you/Is organisation* resident for tax purposes in any other countries?' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(ReporterOtherTaxResidentQuestionPage)(DefaultRouting(CheckMode))(0)(Some(false))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }
  }

  "on REPORTER DETAILS - TAXPAYER JOURNEY in Check Mode" - {
    
    "must go from 'Why are you reporting the arrangement as a taxpayer?' page " +
      "to 'Check your answers for your reporter details' page " in {

      navigator
        .routeMap(TaxpayerWhyReportArrangementPage)(DefaultRouting(CheckMode))(0)(Some(TaxpayerWhyReportInUK.DoNotKnow))(0)
        .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }
  }

  "on REPORTER DETAILS - INTERMEDIARY JOURNEY in Normal mode" - {

    "must go from 'As an intermediary, what is your role in this arrangement?' page " +
      "to 'Check your answers for your reporter details' page " in {

          navigator
            .routeMap(IntermediaryRolePage)(DefaultRouting(CheckMode))(0)(Some(Seq(IntermediaryRole.Promoter)))(0)
            .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))
    }

    "must go from 'Which countries are you exempt from reporting in?' page " +
      "to 'Check your answers for your reporter details' page " in {

      forAll(arbitrary[CountriesListEUCheckboxes]) {
        answers =>

          navigator
            .routeMap(IntermediaryWhichCountriesExemptPage)(DefaultRouting(CheckMode))(0)(Some(Seq(answers)))(0)
            .mustBe(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0))

      }
    }

      "must go from 'Check your answers?' page " +
        "to 'task list' page "  in {

        navigator
          .routeMap(ReporterCheckYourAnswersPage)(DefaultRouting(NormalMode))(0)(None)(0)
          .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad(0))
      }
  }
}

