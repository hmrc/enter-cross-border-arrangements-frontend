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

import controllers.individual.routes
import controllers.mixins.{AssociatedEnterprisesRouting, CheckRoute, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import models._
import pages._
import pages.individual.{IndividualNamePage, _}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForIndividual @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case IndividualNamePage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(routes.IsIndividualDateOfBirthKnownController.onPageLoad(id, checkRoute.mode), checkRoute)

    case IsIndividualDateOfBirthKnownPage =>
      checkRoute => id => value => _ => value match {
        case Some(true) => routes.IndividualDateOfBirthController.onPageLoad(id, checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(id, checkRoute.mode), checkRoute)
      }

    case IndividualDateOfBirthPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(id, checkRoute.mode), checkRoute)

    case IsIndividualPlaceOfBirthKnownPage =>
      checkRoute => id => value => _ => value match {
        case Some(true) => routes.IndividualPlaceOfBirthController.onPageLoad(id, checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.IsIndividualAddressKnownController.onPageLoad(id, checkRoute.mode), checkRoute)
      }

    case IndividualPlaceOfBirthPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(routes.IsIndividualAddressKnownController.onPageLoad(id, checkRoute.mode), checkRoute)

    case IsIndividualAddressKnownPage =>
      checkRoute => id => value => _ => value match {
        case Some(true) => routes.IsIndividualAddressUkController.onPageLoad(id, checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.EmailAddressQuestionForIndividualController.onPageLoad(id, checkRoute.mode), checkRoute)
      }

    case IsIndividualAddressUkPage =>
      checkRoute => id => value => _ => value match {
        case Some(true)  => routes.IndividualPostcodeController.onPageLoad(id, checkRoute.mode)
        case _           => routes.IndividualAddressController.onPageLoad(id, checkRoute.mode)
      }

    case IndividualUkPostcodePage =>
      checkRoute => id => _ => _ => routes.IndividualSelectAddressController.onPageLoad(id, checkRoute.mode)

    case IndividualAddressPage | IndividualSelectAddressPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(routes.EmailAddressQuestionForIndividualController.onPageLoad(id, checkRoute.mode), checkRoute)

    case EmailAddressQuestionForIndividualPage =>
      checkRoute => id => value => _ => value match {
        case Some(true) => routes.EmailAddressForIndividualController.onPageLoad(id, checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.WhichCountryTaxForIndividualController.onPageLoad(id, checkRoute.mode, 0), checkRoute)
      }

    case EmailAddressForIndividualPage =>
      checkRoute => id => _ => index => jumpOrCheckYourAnswers(routes.WhichCountryTaxForIndividualController.onPageLoad(id, checkRoute.mode, 0), checkRoute)

    case WhichCountryTaxForIndividualPage =>
      checkRoute => id => value => index => value match { case Some(country: Country) =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(id, checkRoute.mode, index)
          case _    => routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(id, checkRoute.mode, index)
        }
      }

    case DoYouKnowAnyTINForUKIndividualPage =>
      checkRoute => id => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(id, checkRoute.mode, index)
        case _           => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)
      }

    case DoYouKnowTINForNonUKIndividualPage =>
      checkRoute => id => value => index => value match {
        case Some(true) => routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(id, checkRoute.mode, index)
        case _          => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKIndividualPage | WhatAreTheTaxNumbersForNonUKIndividualPage =>
      checkRoute => id => _ => index => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)

    case IsIndividualResidentForTaxOtherCountriesPage =>
      checkRoute => id => value => index => value match {
        case Some(true) => routes.WhichCountryTaxForIndividualController.onPageLoad(id, checkRoute.mode, index)
        case _          => continueToParentJourney(id, checkRoute)
      }

    case _ =>
      checkRoute => id => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.IndividualCheckYourAnswersController.onPageLoad(id)
      }
  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call =
    _ => _ => id => _ => _ => routes.IndividualCheckYourAnswersController.onPageLoad(id)

  private[navigation] def continueToParentJourney(id: Int, checkRoute: CheckRoute): Call = checkRoute match {
    case AssociatedEnterprisesRouting(NormalMode) => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(id, NormalMode)
    case TaxpayersRouting(NormalMode)             => controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(id, NormalMode)
    case IntermediariesRouting(NormalMode)        => controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(id, NormalMode)
    case _                                        => jumpOrCheckYourAnswers(routes.IndividualCheckYourAnswersController.onPageLoad(id), checkRoute)
  }

  private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode)  => controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id)
      case TaxpayersRouting(CheckMode)              => controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id)
      case IntermediariesRouting(CheckMode)          => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id)
      case DefaultRouting(CheckMode)                => routes.IndividualCheckYourAnswersController.onPageLoad(id)
      case _                                        => jumpTo
    }
  }

}
