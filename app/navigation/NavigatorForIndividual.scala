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

import controllers.individual.routes
import controllers.mixins.{AssociatedEnterprisesRouting, CheckRoute, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import models._
import pages._
import pages.individual.{IndividualNamePage, _}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForIndividual @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case IndividualNamePage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.IsIndividualDateOfBirthKnownController.onPageLoad(checkRoute.mode), checkRoute)

    case IsIndividualDateOfBirthKnownPage =>
      checkRoute => value => _ => value match {
        case Some(true) => routes.IndividualDateOfBirthController.onPageLoad(checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(checkRoute.mode), checkRoute)
      }

    case IndividualDateOfBirthPage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(checkRoute.mode), checkRoute)

    case IsIndividualPlaceOfBirthKnownPage =>
      checkRoute => value => _ => value match {
        case Some(true) => routes.IndividualPlaceOfBirthController.onPageLoad(checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.IsIndividualAddressKnownController.onPageLoad(checkRoute.mode), checkRoute)
      }

    case IndividualPlaceOfBirthPage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.IsIndividualAddressKnownController.onPageLoad(checkRoute.mode), checkRoute)

    case IsIndividualAddressKnownPage =>
      checkRoute => value => _ => value match {
        case Some(true) => routes.IsIndividualAddressUkController.onPageLoad(checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.EmailAddressQuestionForIndividualController.onPageLoad(checkRoute.mode), checkRoute)
      }

    case IsIndividualAddressUkPage =>
      checkRoute => value => _ => value match {
        case Some(true)  => routes.IndividualPostcodeController.onPageLoad(checkRoute.mode)
        case _           => routes.IndividualAddressController.onPageLoad(checkRoute.mode)
      }

    case IndividualUkPostcodePage =>
      checkRoute => _ => _ => routes.IndividualSelectAddressController.onPageLoad(checkRoute.mode)

    case IndividualAddressPage | IndividualSelectAddressPage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.EmailAddressQuestionForIndividualController.onPageLoad(checkRoute.mode), checkRoute)

    case EmailAddressQuestionForIndividualPage =>
      checkRoute => value => _ => value match {
        case Some(true) => routes.EmailAddressForIndividualController.onPageLoad(checkRoute.mode)
        case _          => jumpOrCheckYourAnswers(routes.WhichCountryTaxForIndividualController.onPageLoad(checkRoute.mode, 0), checkRoute)
      }

    case EmailAddressForIndividualPage =>
      checkRoute => _ => index => jumpOrCheckYourAnswers(routes.WhichCountryTaxForIndividualController.onPageLoad(checkRoute.mode, index + 1), checkRoute)

    case WhichCountryTaxForIndividualPage =>
      checkRoute => value => index => value match { case Some(country: Country) =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(checkRoute.mode, index)
          case _    => routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(checkRoute.mode, index)
        }
      }

    case DoYouKnowAnyTINForUKIndividualPage =>
      checkRoute => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(checkRoute.mode, index)
        case _           => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(checkRoute.mode, index + 1)
      }

    case DoYouKnowTINForNonUKIndividualPage =>
      checkRoute => value => index => value match {
        case Some(true) => routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(checkRoute.mode, index)
        case _          => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(checkRoute.mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKIndividualPage | WhatAreTheTaxNumbersForNonUKIndividualPage =>
      checkRoute => _ => index => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(checkRoute.mode, index + 1)

    case IsIndividualResidentForTaxOtherCountriesPage =>
      checkRoute => value => index => value match {
        case Some(true) => routes.WhichCountryTaxForIndividualController.onPageLoad(checkRoute.mode, index)
        case _          => continueToParentJourney(checkRoute)
      }

    case _ =>
      checkRoute => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.IndividualCheckYourAnswersController.onPageLoad()
      }
  }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.IndividualCheckYourAnswersController.onPageLoad()

  private[navigation] def continueToParentJourney(checkRoute: CheckRoute): Call = checkRoute match {
    case AssociatedEnterprisesRouting(NormalMode) => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode)
    case TaxpayersRouting(NormalMode)             => controllers.taxpayer.routes.MarketableArrangementGatewayController.onRouting(NormalMode)
    case IntermediariesRouting(NormalMode)        => controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(NormalMode)
    case _                                        => jumpOrCheckYourAnswers(routes.IndividualCheckYourAnswersController.onPageLoad(), checkRoute)
  }

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode)  => controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()
      case TaxpayersRouting(CheckMode)              => controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad()
      case IntermediariesRouting(CheckMode)          => routes.IndividualCheckYourAnswersController.onPageLoad() // TODO replace when CYA page is build
      case DefaultRouting(CheckMode)                => throw ...routes.IndividualCheckYourAnswersController.onPageLoad()
      case _                                        => jumpTo
    }
  }

}
