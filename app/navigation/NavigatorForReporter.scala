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
class NavigatorForReporter @Inject() () extends AbstractNavigator {

  override val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case ReporterOrganisationOrIndividualPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(Organisation) => controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(id, checkRoute.mode)
                case _                  => controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(id, checkRoute.mode)
              }

    case ReporterTaxResidentCountryPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(country: Country) =>
                  country.code match {
                    case "GB" => routes.ReporterTinUKQuestionController.onPageLoad(id, checkRoute.mode, index)
                    case _    => routes.ReporterTinNonUKQuestionController.onPageLoad(id, checkRoute.mode, index)
                  }
                case _ => throw new RuntimeException("Unable to retrieve selected country from loop")
              }

    case ReporterTinUKQuestionPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(true) => controllers.reporter.routes.ReporterUKTaxNumbersController.onPageLoad(id, checkRoute.mode, index)
                case _          => controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(id, checkRoute.mode, index + 1)
              }

    case ReporterTinNonUKQuestionPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(true) => controllers.reporter.routes.ReporterNonUKTaxNumbersController.onPageLoad(id, checkRoute.mode, index)
                case _          => controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(id, checkRoute.mode, index + 1)
              }

    case ReporterUKTaxNumbersPage | ReporterNonUKTaxNumbersPage =>
      checkRoute => id => _ => index => controllers.reporter.routes.ReporterOtherTaxResidentQuestionController.onPageLoad(id, checkRoute.mode, index + 1)

    case ReporterOtherTaxResidentQuestionPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(true) => controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(id, checkRoute.mode, index)
                case _          => jumpOrCheckYourAnswers(id, controllers.reporter.routes.RoleInArrangementController.onPageLoad(id, checkRoute.mode), checkRoute)
              }

    case RoleInArrangementPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(Intermediary) => controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(id, checkRoute.mode)
                case _                  => controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(id, checkRoute.mode)
              }

          // Reporter - Intermediary Journey Navigation

    case IntermediaryWhyReportInUKPage =>
      checkRoute => id => _ => _ => controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(id, checkRoute.mode)

    case IntermediaryRolePage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(id,
                                     controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(id, checkRoute.mode),
                                     checkRoute
              )

    case IntermediaryExemptionInEUPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(Yes) => controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(id, checkRoute.mode)
                case _         => routes.ReporterCheckYourAnswersController.onPageLoad(id)
              }

    case IntermediaryDoYouKnowExemptionsPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(id, checkRoute.mode)
                case _          => routes.ReporterCheckYourAnswersController.onPageLoad(id)
              }

    case IntermediaryWhichCountriesExemptPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(id), checkRoute)

    case ReporterTaxpayersStartDateForImplementingArrangementPage =>
      _ =>
        id =>
          _ =>
            _ =>
              controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(id)

          // Reporter - Taxpayer Journey Navigation

    case TaxpayerWhyReportInUKPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(DoNotKnow) =>
                  controllers.reporter.taxpayer.routes.ReporterTaxpayersMarketableArrangementGatewayController.onRouting(id, checkRoute.mode)
                case _ =>
                  controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(id, checkRoute.mode)
              }

    case TaxpayerWhyReportArrangementPage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(
                id,
                controllers.reporter.taxpayer.routes.ReporterTaxpayersMarketableArrangementGatewayController.onRouting(id, checkRoute.mode),
                checkRoute
              )

    case DisclosureMarketablePage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) =>
                  controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(id, checkRoute.mode)
                case _ => jumpOrCheckYourAnswers(id, routes.ReporterCheckYourAnswersController.onPageLoad(id), checkRoute)
              }

          // Reporter - Organisation Journey Navigation

    case ReporterOrganisationNamePage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(id,
                                     controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(id, checkRoute.mode),
                                     checkRoute
              )

    case ReporterOrganisationIsAddressUkPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) =>
                  controllers.reporter.organisation.routes.ReporterOrganisationPostcodeController.onPageLoad(id, checkRoute.mode)
                case _ =>
                  controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(id, checkRoute.mode)
              }

    case ReporterOrganisationPostcodePage =>
      checkRoute => id => _ => _ => controllers.reporter.organisation.routes.ReporterOrganisationSelectAddressController.onPageLoad(id, checkRoute.mode)

    case ReporterOrganisationSelectAddressPage | ReporterOrganisationAddressPage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(
                id,
                controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(id, checkRoute.mode),
                checkRoute
              )

    case ReporterOrganisationEmailAddressQuestionPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) =>
                  controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressController.onPageLoad(id, checkRoute.mode)
                case _ =>
                  jumpOrCheckYourAnswers(id, controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(id, checkRoute.mode, 0), checkRoute)
              }

    case ReporterOrganisationEmailAddressPage =>
      checkRoute =>
        id =>
          _ =>
            index =>
              jumpOrCheckYourAnswers(id, controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(id, checkRoute.mode, 0), checkRoute)

          // Reporter - Individual Journey Navigation

    case ReporterIndividualNamePage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(id,
                                     controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(id, checkRoute.mode),
                                     checkRoute
              )

    case ReporterIndividualDateOfBirthPage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(id,
                                     controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(id, checkRoute.mode),
                                     checkRoute
              )

    case ReporterIndividualPlaceOfBirthPage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(id,
                                     controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(id, checkRoute.mode),
                                     checkRoute
              )

    case ReporterIsIndividualAddressUKPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => controllers.reporter.individual.routes.ReporterIndividualPostcodeController.onPageLoad(id, checkRoute.mode)
                case _          => controllers.reporter.individual.routes.ReporterIndividualAddressController.onPageLoad(id, checkRoute.mode)
              }

    case ReporterIndividualPostcodePage =>
      checkRoute => id => _ => _ => controllers.reporter.individual.routes.ReporterIndividualSelectAddressController.onPageLoad(id, checkRoute.mode)

    case ReporterIndividualAddressPage | ReporterIndividualSelectAddressPage =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              jumpOrCheckYourAnswers(id,
                                     controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(id, checkRoute.mode),
                                     checkRoute
              )

    case ReporterIndividualEmailAddressQuestionPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) =>
                  controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(id, checkRoute.mode)
                case _ =>
                  jumpOrCheckYourAnswers(id, controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(id, checkRoute.mode, 0), checkRoute)
              }

    case ReporterIndividualEmailAddressPage =>
      checkRoute =>
        id =>
          _ => _ => jumpOrCheckYourAnswers(id, controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(id, checkRoute.mode, 0), checkRoute)

    case ReporterCheckYourAnswersPage => _ => id => _ => _ => controllers.routes.DisclosureDetailsController.onPageLoad(id)

    case _ =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              checkRoute.mode match {
                case NormalMode => indexRoute
                case CheckMode  => routes.ReporterCheckYourAnswersController.onPageLoad(id)
              }
  }

  private[navigation] def orCheckYourAnswers(id: Int, mode: Mode, route: Call): Call =
    mode match {
      case NormalMode => route
      case CheckMode  => routes.ReporterCheckYourAnswersController.onPageLoad(id)
    }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call =
    _ => _ => id => _ => _ => routes.ReporterCheckYourAnswersController.onPageLoad(id)

  override private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case DefaultRouting(CheckMode) => routes.ReporterCheckYourAnswersController.onPageLoad(id)
      case _                         => jumpTo
    }
}
