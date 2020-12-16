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
import models._
import pages._
import pages.organisation._
import play.api.mvc.Call

object NavigatorForOrganisation extends AbstractNavigator {

  val checkYourAnswersRoute = routes.CheckYourAnswersOrganisationController.onPageLoad()

  private[navigation] val routeMap: Page => Mode => Option[Any] => Int => Call = {

    case OrganisationNamePage =>
      mode => _ => _ => routes.IsOrganisationAddressKnownController.onPageLoad(mode)

    case IsOrganisationAddressKnownPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case true  => routes.IsOrganisationAddressUkController.onPageLoad(mode)
        case _     => routes.EmailAddressQuestionForOrganisationController.onPageLoad(mode)
      }

    case IsOrganisationAddressUkPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case true  => routes.OrganisationPostcodeController.onPageLoad(mode)
        case _     => routes.OrganisationAddressController.onPageLoad(mode)
      }

    case PostcodePage =>
      mode => _ => _ => routes.OrganisationSelectAddressController.onPageLoad(mode)

    case SelectAddressPage | OrganisationAddressPage =>
      mode => _ => _ => routes.EmailAddressQuestionForOrganisationController.onPageLoad(mode)

    case EmailAddressQuestionForOrganisationPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case true  => routes.EmailAddressForOrganisationController.onPageLoad(mode)
        case _     => routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, 0)
      }

    case EmailAddressForOrganisationPage =>
      mode => _ => _ => routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, 0)

    case WhichCountryTaxForOrganisationPage =>
      mode => value => index => value match { case country: Country =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(mode, index)
          case _    => routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(mode, index)
        }
      }

    case DoYouKnowAnyTINForUKOrganisationPage =>
      mode => value => index => value.asInstanceOf[Boolean] match {
        case true  => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(mode, index)
        case _     => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, index + 1)
      }

    case DoYouKnowTINForNonUKOrganisationPage =>
      mode => value => index => value.asInstanceOf[Boolean] match {
        case true  => routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(mode, index)
        case _     => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKOrganisationPage | WhatAreTheTaxNumbersForNonUKOrganisationPage =>
      mode => _ => index => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, index + 1)

    case IsOrganisationResidentForTaxOtherCountriesPage =>
      mode => value => index =>value.asInstanceOf[Boolean] match {
        case true => routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, index)
        case _    => checkYourAnswersRoute
      }

    case _ =>
      mode => _ => _ => mode match {
        case NormalMode => indexRoute
        case CheckMode  => checkYourAnswersRoute
      }
  }

  private[navigation] val alternativeRouteMap: Page => Call = {
    case _ => checkYourAnswersRoute
  }

}
