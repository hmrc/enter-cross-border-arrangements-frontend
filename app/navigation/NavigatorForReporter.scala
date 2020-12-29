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
import models.YesNoDoNotKnowRadios.Yes
import models._
import models.reporter.RoleInArrangement.Intermediary
import models.reporter.taxpayer.TaxpayerWhyReportInUK.DoNotKnow
import pages._
import pages.reporter.RoleInArrangementPage
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.api.mvc.Call
import javax.inject.{Inject, Singleton}
import pages.reporter.organisation.{ReporterOrganisationIsAddressUkPage, ReporterOrganisationNamePage}

@Singleton
class NavigatorForReporter @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

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
      case Some(DoNotKnow) => controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(checkRoute.mode)
      case _ => controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(checkRoute.mode)
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
        //TODO- Change redirect to manual address non UK page
        controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(checkRoute.mode)


    }

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

    case ReporterIndividualAddressPage => _ => _ => _ =>
      controllers.routes.IndexController.onPageLoad()

    case ReporterIndividualSelectAddressPage => _ => _ => _ =>
      controllers.routes.IndexController.onPageLoad()

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
