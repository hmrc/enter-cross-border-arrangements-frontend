/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.mixins._
import controllers.organisation.routes
import models._
import pages._
import pages.organisation._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForOrganisation @Inject() () extends AbstractNavigator {

  override val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case OrganisationNamePage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.IsOrganisationAddressKnownController.onPageLoad(id, checkRoute.mode), checkRoute)

    case IsOrganisationAddressKnownPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => routes.IsOrganisationAddressUkController.onPageLoad(id, checkRoute.mode)
                case _          => jumpOrCheckYourAnswers(id, routes.EmailAddressQuestionForOrganisationController.onPageLoad(id, checkRoute.mode), checkRoute)
              }

    case IsOrganisationAddressUkPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => routes.OrganisationPostcodeController.onPageLoad(id, checkRoute.mode)
                case _          => routes.OrganisationAddressController.onPageLoad(id, checkRoute.mode)
              }

    case PostcodePage =>
      checkRoute => id => _ => _ => routes.OrganisationSelectAddressController.onPageLoad(id, checkRoute.mode)

    case SelectAddressPage | OrganisationAddressPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.EmailAddressQuestionForOrganisationController.onPageLoad(id, checkRoute.mode), checkRoute)

    case EmailAddressQuestionForOrganisationPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => routes.EmailAddressForOrganisationController.onPageLoad(id, checkRoute.mode)
                case _          => jumpOrCheckYourAnswers(id, routes.WhichCountryTaxForOrganisationController.onPageLoad(id, checkRoute.mode, 0), checkRoute)
              }

    case EmailAddressForOrganisationPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.WhichCountryTaxForOrganisationController.onPageLoad(id, checkRoute.mode, 0), checkRoute)

    case WhichCountryTaxForOrganisationPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(country: Country) =>
                  country.code match {
                    case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
                    case _    => routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
                  }
              }

    case DoYouKnowAnyTINForUKOrganisationPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(true) => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
                case _          => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)
              }

    case DoYouKnowTINForNonUKOrganisationPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(true) => routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(id, checkRoute.mode, index)
                case _          => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)
              }

    case WhatAreTheTaxNumbersForUKOrganisationPage | WhatAreTheTaxNumbersForNonUKOrganisationPage =>
      checkRoute => id => _ => index => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, checkRoute.mode, index + 1)

    case IsOrganisationResidentForTaxOtherCountriesPage =>
      checkRoute =>
        id =>
          value =>
            index =>
              value match {
                case Some(true) => routes.WhichCountryTaxForOrganisationController.onPageLoad(id, checkRoute.mode, index)
                case _          => continueToParentJourney(id, checkRoute)
              }
  }
}
