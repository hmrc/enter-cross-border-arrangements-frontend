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

import controllers.organisation.routes
import controllers.mixins.{AssociatedEnterprisesRouting, CheckRoute, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import models._
import pages._
import pages.organisation._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForOrganisation @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case OrganisationNamePage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.IsOrganisationAddressKnownController.onPageLoad(id, checkRoute.mode), checkRoute)

    case IsOrganisationAddressKnownPage =>
      checkRoute => id => value => _ => value match {
        case Some(true)  => routes.IsOrganisationAddressUkController.onPageLoad(id, checkRoute.mode)
        case _           => jumpOrCheckYourAnswers(id, routes.EmailAddressQuestionForOrganisationController.onPageLoad(id, checkRoute.mode), checkRoute)
      }

    case IsOrganisationAddressUkPage =>
      checkRoute => id => value => _ => value match {
        case Some(true)  => routes.OrganisationPostcodeController.onPageLoad(id, checkRoute.mode)
        case _           => routes.OrganisationAddressController.onPageLoad(id, checkRoute.mode)
      }

    case PostcodePage =>
      checkRoute => id => _ => _ => routes.OrganisationSelectAddressController.onPageLoad(id, checkRoute.mode)

    case SelectAddressPage | OrganisationAddressPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.EmailAddressQuestionForOrganisationController.onPageLoad(id, checkRoute.mode), checkRoute)

    case EmailAddressQuestionForOrganisationPage =>
      checkRoute => id => value => _ => value match {
        case Some(true)  => routes.EmailAddressForOrganisationController.onPageLoad(id, checkRoute.mode)
        case _           => jumpOrCheckYourAnswers(id, routes.WhichCountryTaxForOrganisationController.onPageLoad(id, checkRoute.mode, 0), checkRoute)
      }

    case EmailAddressForOrganisationPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.WhichCountryTaxForOrganisationController.onPageLoad(id, checkRoute.mode, 0), checkRoute)

    case WhichCountryTaxForOrganisationPage =>
      checkRoute => id => value => index => value match { case Some(country: Country) =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
          case _    => routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
        }
      }

    case DoYouKnowAnyTINForUKOrganisationPage =>
      checkRoute => id => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
        case _           => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)
      }

    case DoYouKnowTINForNonUKOrganisationPage =>
      checkRoute => id => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
        case _           => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKOrganisationPage | WhatAreTheTaxNumbersForNonUKOrganisationPage =>
      checkRoute => id => _ => index => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)

    case IsOrganisationResidentForTaxOtherCountriesPage =>
      checkRoute => id => value => index => value match {
        case Some(true) => routes.WhichCountryTaxForOrganisationController.onPageLoad(id, checkRoute.mode, index)
        case _          => continueToParentJourney(id, checkRoute)
      }

    case _ =>
      checkRoute => id => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.OrganisationCheckYourAnswersController.onPageLoad(id)
      }
  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call =
    _ => _ => id => _ => _ => routes.OrganisationCheckYourAnswersController.onPageLoad(id)

  private[navigation] def orCheckYourAnswers(id:Int, mode: Mode, route: Call): Call =
    mode match {
      case NormalMode => route
      case CheckMode  => routes.OrganisationCheckYourAnswersController.onPageLoad(id)
    }

  private[navigation] def continueToParentJourney(id: Int, checkRoute: CheckRoute): Call = checkRoute match {
    case AssociatedEnterprisesRouting(NormalMode) => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(id, NormalMode)
    case TaxpayersRouting(NormalMode)             => controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(id, NormalMode)
    case IntermediariesRouting(NormalMode)        => controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(id, NormalMode)
    case _                                        => jumpOrCheckYourAnswers(id, routes.OrganisationCheckYourAnswersController.onPageLoad(id), checkRoute)
  }

  private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode)  => controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id)
      case TaxpayersRouting(CheckMode)              => controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id)
      case IntermediariesRouting(CheckMode)         => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id)
      case DefaultRouting(CheckMode)                => routes.OrganisationCheckYourAnswersController.onPageLoad(id)
      case _                                        => jumpTo
    }

}
