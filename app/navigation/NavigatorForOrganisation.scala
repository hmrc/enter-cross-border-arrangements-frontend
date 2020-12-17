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

import controllers.organisation.routes
import controllers.mixins.{AssociatedEnterprisesRouting, CheckRoute, DefaultRouting, TaxpayerRouting}
import models._
import pages._
import pages.organisation._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForOrganisation @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case OrganisationNamePage =>
      checkMode => _ => _ => routes.IsOrganisationAddressKnownController.onPageLoad(checkMode.mode)

    case IsOrganisationAddressKnownPage =>
      checkMode => value => _ => value match {
        case Some(true)  => routes.IsOrganisationAddressUkController.onPageLoad(checkMode.mode)
        case _           => routes.EmailAddressQuestionForOrganisationController.onPageLoad(checkMode.mode)
      }

    case IsOrganisationAddressUkPage =>
      checkMode => value => _ => value match {
        case Some(true)  => routes.OrganisationPostcodeController.onPageLoad(checkMode.mode)
        case _           => routes.OrganisationAddressController.onPageLoad(checkMode.mode)
      }

    case PostcodePage =>
      checkMode => _ => _ => routes.OrganisationSelectAddressController.onPageLoad(checkMode.mode)

    case SelectAddressPage | OrganisationAddressPage =>
      checkMode => _ => _ => routes.EmailAddressQuestionForOrganisationController.onPageLoad(checkMode.mode)

    case EmailAddressQuestionForOrganisationPage =>
      checkMode => value => _ => value match {
        case Some(true)  => routes.EmailAddressForOrganisationController.onPageLoad(checkMode.mode)
        case _           => routes.WhichCountryTaxForOrganisationController.onPageLoad(checkMode.mode, 0)
      }

    case EmailAddressForOrganisationPage =>
      checkMode => _ => _ => routes.WhichCountryTaxForOrganisationController.onPageLoad(checkMode.mode, 0)

    case WhichCountryTaxForOrganisationPage =>
      checkMode => value => index => value match { case Some(country: Country) =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(checkMode.mode, index)
          case _    => routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(checkMode.mode, index)
        }
      }

    case DoYouKnowAnyTINForUKOrganisationPage =>
      checkMode => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(checkMode.mode, index)
        case _           => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(checkMode.mode, index + 1)
      }

    case DoYouKnowTINForNonUKOrganisationPage =>
      checkMode => value => index => value match {
        case Some(true)  => routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(checkMode.mode, index)
        case _           => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(checkMode.mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKOrganisationPage | WhatAreTheTaxNumbersForNonUKOrganisationPage =>
      checkMode => _ => index => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(checkMode.mode, index + 1)

    case IsOrganisationResidentForTaxOtherCountriesPage =>
      checkMode => value => index => value match {
        case Some(true) => routes.WhichCountryTaxForOrganisationController.onPageLoad(checkMode.mode, index)
        case _          => routes.CheckYourAnswersOrganisationController.onPageLoad()
      }

    // default

    case _ =>
      checkMode => _ => _ => checkMode.mode match {
        case NormalMode => indexRoute
        case CheckMode  => routes.CheckYourAnswersOrganisationController.onPageLoad()
      }
  }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.CheckYourAnswersOrganisationController.onPageLoad()

  private[navigation] def orCheckYourAnswers(mode: Mode, route: Call): Call =
    mode match {
      case NormalMode => route
      case CheckMode  => routes.CheckYourAnswersOrganisationController.onPageLoad()
    }

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode) => controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()
      case TaxpayerRouting(CheckMode)              => controllers.taxpayer.routes.CheckYourAnswersTaxpayersController.onPageLoad()
      case DefaultRouting(CheckMode)               => routes.CheckYourAnswersOrganisationController.onPageLoad()
      case _                                       => jumpTo
    }

}
