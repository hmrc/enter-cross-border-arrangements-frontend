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
import controllers.mixins.{AffectedRouting, AssociatedEnterprisesRouting, CheckRoute, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import models._
import pages._
import pages.organisation._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForOrganisation @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case OrganisationNamePage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.IsOrganisationAddressKnownController.onPageLoad(checkRoute.mode), checkRoute)

    case IsOrganisationAddressKnownPage =>
      checkRoute => value => _ => value match {
        case Some(true)  => routes.IsOrganisationAddressUkController.onPageLoad(checkRoute.mode)
        case _           => jumpOrCheckYourAnswers(routes.EmailAddressQuestionForOrganisationController.onPageLoad(checkRoute.mode), checkRoute)
      }

    case IsOrganisationAddressUkPage =>
      checkRoute => value => _ => value match {
        case Some(true)  => routes.OrganisationPostcodeController.onPageLoad(checkRoute.mode)
        case _           => routes.OrganisationAddressController.onPageLoad(checkRoute.mode)
      }

    case PostcodePage =>
      checkRoute => _ => _ => routes.OrganisationSelectAddressController.onPageLoad(checkRoute.mode)

    case SelectAddressPage | OrganisationAddressPage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.EmailAddressQuestionForOrganisationController.onPageLoad(checkRoute.mode), checkRoute)

    case EmailAddressQuestionForOrganisationPage =>
      checkRoute => value => _ => value match {
        case Some(true)  => routes.EmailAddressForOrganisationController.onPageLoad(checkRoute.mode)
        case _           => jumpOrCheckYourAnswers(routes.WhichCountryTaxForOrganisationController.onPageLoad(checkRoute.mode, 0), checkRoute)
      }

    case EmailAddressForOrganisationPage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.WhichCountryTaxForOrganisationController.onPageLoad(checkRoute.mode, 0), checkRoute)

    case WhichCountryTaxForOrganisationPage =>
      checkRoute => value => index => value match { case Some(country: Country) =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(checkRoute.mode, index)
          case _    => routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(checkRoute.mode, index)
        }
      }

    case DoYouKnowAnyTINForUKOrganisationPage =>
      checkRoute => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(checkRoute.mode, index)
        case _           => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(checkRoute.mode, index + 1)
      }

    case DoYouKnowTINForNonUKOrganisationPage =>
      checkRoute => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(checkRoute.mode, index)
        case _           => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(checkRoute.mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKOrganisationPage | WhatAreTheTaxNumbersForNonUKOrganisationPage =>
      checkRoute => _ => index => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(checkRoute.mode, index + 1)

    case IsOrganisationResidentForTaxOtherCountriesPage =>
      checkRoute => value => index => value match {
        case Some(true) => routes.WhichCountryTaxForOrganisationController.onPageLoad(checkRoute.mode, index)
        case _          => continueToParentJourney(checkRoute)
      }

    case _ =>
      checkRoute => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.OrganisationCheckYourAnswersController.onPageLoad()
      }
  }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.OrganisationCheckYourAnswersController.onPageLoad()

  private[navigation] def orCheckYourAnswers(mode: Mode, route: Call): Call =
    mode match {
      case NormalMode => route
      case CheckMode  => routes.OrganisationCheckYourAnswersController.onPageLoad()
    }

  private[navigation] def continueToParentJourney(checkRoute: CheckRoute): Call = checkRoute match {
    case AssociatedEnterprisesRouting(NormalMode) => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode)
    case TaxpayersRouting(NormalMode)             => controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(NormalMode)
    case IntermediariesRouting(NormalMode)        => controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(NormalMode)
    case AffectedRouting(NormalMode)              => controllers.affected.routes.YouHaveNotAddedAnyAffectedController.onPageLoad()
    case _                                        => jumpOrCheckYourAnswers(routes.OrganisationCheckYourAnswersController.onPageLoad(), checkRoute)
  }

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode)  => controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()
      case TaxpayersRouting(CheckMode)              => controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad()
      case IntermediariesRouting(CheckMode)         => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()
      case AffectedRouting(CheckMode)               => controllers.affected.routes.AffectedCheckYourAnswersController.onPageLoad()
      case DefaultRouting(CheckMode)                => routes.OrganisationCheckYourAnswersController.onPageLoad()
      case _                                        => jumpTo
    }

}
