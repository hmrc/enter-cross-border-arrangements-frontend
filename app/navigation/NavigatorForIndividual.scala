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
import models._
import pages._
import pages.individual._
import play.api.mvc.Call

object NavigatorForIndividual extends AbstractNavigator {

  val checkYourAnswersRoute = routes.IndividualCheckYourAnswersController.onPageLoad()

  private[navigation] val routeMap: Page => Mode => Option[Any] => Int => Call = {

    case IndividualNamePage =>
      mode => _ => _ => orCheckYourAnswers(mode, routes.IndividualDateOfBirthController.onPageLoad(mode))

    case IndividualDateOfBirthPage =>
      mode => _ => _ => orCheckYourAnswers(mode, routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(mode))

    case IsIndividualPlaceOfBirthKnownPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case false if mode == NormalMode => routes.IsIndividualAddressKnownController.onPageLoad(mode)
        case _  => orCheckYourAnswers(mode, routes.IndividualPlaceOfBirthController.onPageLoad(mode))
      }

    case IndividualPlaceOfBirthPage =>
      mode => _ => _ => orCheckYourAnswers(mode, routes.IsIndividualAddressKnownController.onPageLoad(mode))

    case IsIndividualAddressKnownPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case false if mode == NormalMode => routes.EmailAddressQuestionForIndividualController.onPageLoad(mode)
        case _  => orCheckYourAnswers(mode, routes.IsIndividualAddressUkController.onPageLoad(mode))
      }

    case IsIndividualAddressUkPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case true  => routes.IndividualPostcodeController.onPageLoad(mode)
        case _     => routes.IndividualAddressController.onPageLoad(mode)
      }

    case IndividualUkPostcodePage =>
      mode => _ => _ => routes.IndividualSelectAddressController.onPageLoad(mode)

    case IndividualAddressPage | IndividualSelectAddressPage =>
      mode => _ => _ => orCheckYourAnswers(mode, routes.EmailAddressQuestionForIndividualController.onPageLoad(mode))

    case EmailAddressQuestionForIndividualPage =>
      mode => value => _ => value.asInstanceOf[Boolean] match {
        case false if mode == NormalMode => routes.WhichCountryTaxForIndividualController.onPageLoad(mode, 0)
        case _  => orCheckYourAnswers(mode, routes.EmailAddressForIndividualController.onPageLoad(mode))
      }

    case EmailAddressForIndividualPage =>
      mode => _ => index => orCheckYourAnswers(mode, routes.WhichCountryTaxForIndividualController.onPageLoad(mode, index + 1))

    case WhichCountryTaxForIndividualPage =>
      mode => value => index => value match { case country: Country =>
        country.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(mode, index)
          case _    => routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(mode, index)
        }
      }

    case DoYouKnowAnyTINForUKIndividualPage =>
      mode => value => index => value.asInstanceOf[Boolean] match {
        case true  => routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(mode, index)
        case _     => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode, index + 1)
      }

    case DoYouKnowTINForNonUKIndividualPage =>
      mode => value => index => value.asInstanceOf[Boolean] match {
        case true  => routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(mode, index)
        case _     => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode, index + 1)
      }

    case WhatAreTheTaxNumbersForUKIndividualPage | WhatAreTheTaxNumbersForNonUKIndividualPage =>
      mode => _ => index => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode, index + 1)

    case IsIndividualResidentForTaxOtherCountriesPage =>
      mode => value => index => value.asInstanceOf[Boolean] match {
        case true => routes.WhichCountryTaxForIndividualController.onPageLoad(mode, index)
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
