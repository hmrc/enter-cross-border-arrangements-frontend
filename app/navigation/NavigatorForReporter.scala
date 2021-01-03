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
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.api.mvc.Call

@Singleton
class NavigatorForReporter @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case ReporterOrganisationOrIndividualPage => checkRoute => value => _ => value match {
      case Some(Organisation) => controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(checkRoute.mode)
      case _ => controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(checkRoute.mode)
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
      case _ => routes.RoleInArrangementController.onPageLoad(checkRoute.mode) //TODO - Change redirect to CYA when built

    }

    case IntermediaryDoYouKnowExemptionsPage => checkRoute => value => _ => value match {
      case Some(true) => controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(checkRoute.mode)
      case _ => routes.RoleInArrangementController.onPageLoad(checkRoute.mode) // TODO - Change redirect to CYA when built
    }

    case IntermediaryWhichCountriesExemptPage => checkRoute => _ => _ =>
      routes.RoleInArrangementController.onPageLoad(checkRoute.mode) //TODO - Change redirect to CYA when built


    // Reporter - Taxpayer Journey Navigation

    case TaxpayerWhyReportInUKPage => checkRoute => value =>_ => value match {
      case Some(DoNotKnow) =>
        controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(checkRoute.mode)
      case _ =>
        controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(checkRoute.mode)
    }

    case TaxpayerWhyReportArrangementPage => checkRoute => _ =>_ =>
      controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(checkRoute.mode)


    // Reporter - Organisation Journey Navigation

    case ReporterOrganisationNamePage => checkRoute => _ => _ =>
      controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(checkRoute.mode)

    case ReporterOrganisationIsAddressUkPage => checkRoute => value => _ => value match {
      case Some(true) =>
        controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(checkRoute.mode)
      case _ =>
        controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(checkRoute.mode)
    }

    case ReporterOrganisationPostcodePage => checkRoute => _ => _ =>
      controllers.reporter.organisation.routes.ReporterOrganisationSelectAddressController.onPageLoad(checkRoute.mode)

    case ReporterOrganisationAddressPage => checkRoute => _ => _ =>
      controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(checkRoute.mode)

    case ReporterOrganisationSelectAddressPage => checkRoute => _ => _ =>
      controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(checkRoute.mode)

    case ReporterOrganisationEmailAddressQuestionPage => checkRoute => value => index => value match {
      case Some(true) =>
        controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressController.onPageLoad(checkRoute.mode)
      case _ =>
        controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, index)
    }

    case ReporterOrganisationEmailAddressPage => checkRoute => _ => index =>
      controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, index)


    // Reporter - Individual Journey Navigation

    case ReporterIndividualNamePage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(checkRoute.mode)

    case ReporterIndividualDateOfBirthPage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(checkRoute.mode)

    case ReporterIndividualPlaceOfBirthPage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(checkRoute.mode)

    case ReporterIsIndividualAddressUKPage => checkRoute => value => _ => value match {
      case Some(true) => controllers.reporter.individual.routes.ReporterIndividualPostcodeController.onPageLoad(checkRoute.mode)
      case _ => controllers.reporter.individual.routes.ReporterIndividualAddressController.onPageLoad(checkRoute.mode)
    }

    case ReporterIndividualPostcodePage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIndividualSelectAddressController.onPageLoad(checkRoute.mode)

    case ReporterIndividualAddressPage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(checkRoute.mode)

    case ReporterIndividualSelectAddressPage => checkRoute => _ => _ =>
      controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(checkRoute.mode)

    case ReporterIndividualEmailAddressQuestionPage => checkRoute =>value => _ => value match {
      case Some(true) =>
        controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(checkRoute.mode)
      case _ =>
        //TODO- redirect to Tax Residencies page when built
        controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(checkRoute.mode, 0)
    }

    case ReporterIndividualEmailAddressPage => checkRoute => _ => _ =>
      //TODO- redirect to Tax Residencies page when built
      indexRoute


    case _ => checkRoute => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.ReporterCheckYourAnswersController.onPageLoad()
      }
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
