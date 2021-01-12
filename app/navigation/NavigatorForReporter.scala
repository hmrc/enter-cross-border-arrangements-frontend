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

import controllers.mixins.{CheckRoute, DefaultRouting}
import controllers.reporter.routes
import javax.inject.{Inject, Singleton}
import models.ReporterOrganisationOrIndividual.Organisation
import models.YesNoDoNotKnowRadios.Yes
import models._
import models.reporter.RoleInArrangement.Intermediary
import models.reporter.taxpayer.TaxpayerWhyReportInUK.DoNotKnow
import pages._
import pages.reporter.individual._
import pages.disclosure.DisclosureMarketablePage
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter._
import play.api.mvc.Call

@Singleton
class NavigatorForReporter @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case ReporterOrganisationOrIndividualPage => checkRoute => value => _ => value match {
      case Some(Organisation) => controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(checkRoute.mode)
      case _ => controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(checkRoute.mode)
    }

    case ReporterTaxResidentCountryPage =>
      checkRoute => value => index => value match {
        case Some(country: Country) =>
        country.code match {
          case "GB" => routes.ReporterTinUKQuestionController.onPageLoad(checkRoute.mode, index)
          case _    => routes.ReporterTinNonUKQuestionController.onPageLoad(checkRoute.mode, index)
      }
        case _ => throw new RuntimeException("Unable to retrieve selected country from loop")
    }

    case ReporterTinUKQuestionPage => checkRoute => value => index => value match {
      case Some(true) => controllers.reporter.routes.ReporterUKTaxNumbersController.onPageLoad(checkRoute.mode, index)
      case _ => controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(checkRoute.mode, index + 1)
    }

    case ReporterTinNonUKQuestionPage => checkRoute => value => index => value match {
      case Some(true) => controllers.reporter.routes.ReporterNonUKTaxNumbersController.onPageLoad(checkRoute.mode, index)
      case _ => controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(checkRoute.mode, index + 1)
    }

    case ReporterUKTaxNumbersPage | ReporterNonUKTaxNumbersPage => checkRoute => _ => index =>
      controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(checkRoute.mode, index + 1)

    case ReporterOtherTaxResidentQuestionPage => checkRoute =>value => index => value match {
      case Some(true) => controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, index)
      case _ => jumpOrCheckYourAnswers(controllers.reporter.routes.RoleInArrangementController.onPageLoad(checkRoute.mode), checkRoute)
    }

    case RoleInArrangementPage => checkRoute => value => _ => value match {
      case Some(Intermediary) => controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(checkRoute.mode)
      case _ => controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(checkRoute.mode)
    }

    // Reporter - Intermediary Journey Navigation

    case IntermediaryWhyReportInUKPage => checkRoute => _ => _ =>
      controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(checkRoute.mode)

    case IntermediaryRolePage => checkRoute =>_ =>_ =>
      controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(checkRoute.mode)

    case IntermediaryExemptionInEUPage => checkRoute => value => _ => value match {
      case Some(Yes) =>      controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(checkRoute.mode)
      case _ => routes.ReporterCheckYourAnswersController.onPageLoad()
    }

    case IntermediaryDoYouKnowExemptionsPage => checkRoute => value => _ => value match {
      case Some(true) => controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(checkRoute.mode)
      case _ => routes.ReporterCheckYourAnswersController.onPageLoad()
    }

    case IntermediaryWhichCountriesExemptPage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(), checkRoute)

    case ReporterTaxpayersStartDateForImplementingArrangementPage => _ =>_ =>_ =>
      controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad()

    // Reporter - Taxpayer Journey Navigation

    case TaxpayerWhyReportInUKPage => checkRoute => value =>_ => value match {
      case Some(DoNotKnow) =>
        controllers.reporter.taxpayer.routes.ReporterTaxpayersMarketableArrangementGatewayController.onRouting(checkRoute.mode)
      case _ =>
        controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(checkRoute.mode)
    }

    case TaxpayerWhyReportArrangementPage => checkRoute => _ =>_ =>
      controllers.reporter.taxpayer.routes.ReporterTaxpayersMarketableArrangementGatewayController.onRouting(checkRoute.mode)

    case DisclosureMarketablePage =>
      checkRoute => value => _ =>
        value match {
          case Some(true) =>
            controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(checkRoute.mode)
          case _ => jumpOrCheckYourAnswers(routes.ReporterCheckYourAnswersController.onPageLoad(), checkRoute)
        }

    // Reporter - Organisation Journey Navigation

    case ReporterOrganisationNamePage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(checkRoute.mode), checkRoute)

    case ReporterOrganisationIsAddressUkPage => checkRoute => value => _ => value match {
      case Some(true) =>
        controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(checkRoute.mode)
      case _ =>
        controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(checkRoute.mode)
    }

    case ReporterOrganisationPostcodePage => checkRoute => _ => _ =>
      controllers.reporter.organisation.routes.ReporterOrganisationSelectAddressController.onPageLoad(checkRoute.mode)

    case ReporterOrganisationSelectAddressPage | ReporterOrganisationAddressPage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(checkRoute.mode), checkRoute)

    case ReporterOrganisationEmailAddressQuestionPage => checkRoute => value => _ => value match {
      case Some(true) =>
        controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressController.onPageLoad(checkRoute.mode)
      case _ =>
        jumpOrCheckYourAnswers(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, 0), checkRoute)
    }

    case ReporterOrganisationEmailAddressPage => checkRoute => _ => index =>
      jumpOrCheckYourAnswers(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, 0), checkRoute)


    // Reporter - Individual Journey Navigation

    case ReporterIndividualNamePage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(checkRoute.mode), checkRoute)

    case ReporterIndividualDateOfBirthPage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(checkRoute.mode), checkRoute)

    case ReporterIndividualPlaceOfBirthPage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(checkRoute.mode), checkRoute)

    case ReporterIsIndividualAddressUKPage => checkRoute => value => _ => value match {
      case Some(true) => controllers.reporter.individual.routes.ReporterIndividualPostcodeController.onPageLoad(checkRoute.mode)
      case _ => controllers.reporter.individual.routes.ReporterIndividualAddressController.onPageLoad(checkRoute.mode)
    }

    case ReporterIndividualPostcodePage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIndividualSelectAddressController.onPageLoad(checkRoute.mode)

    case ReporterIndividualAddressPage | ReporterIndividualSelectAddressPage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(checkRoute.mode), checkRoute)

    case ReporterIndividualEmailAddressQuestionPage => checkRoute =>value => _ => value match {
      case Some(true) =>
        controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(checkRoute.mode)
      case _ =>
        jumpOrCheckYourAnswers(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, 0), checkRoute)
    }

    case ReporterIndividualEmailAddressPage => checkRoute => _ => _ =>
      jumpOrCheckYourAnswers(controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, 0), checkRoute)


    case _ => checkRoute => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.ReporterCheckYourAnswersController.onPageLoad()
      }
  }

  private[navigation] def orCheckYourAnswers(mode: Mode, route: Call): Call =
    mode match {
      case NormalMode => route
      case CheckMode  => routes.ReporterCheckYourAnswersController.onPageLoad()
    }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.ReporterCheckYourAnswersController.onPageLoad()

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case DefaultRouting(CheckMode)               => routes.ReporterCheckYourAnswersController.onPageLoad()
      case _                                       => jumpTo
    }
  }

}
