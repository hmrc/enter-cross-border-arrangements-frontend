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

import controllers.routes
import helpers.JourneyHelpers.{currentIndexInsideLoop, incrementIndexOrganisation}
import models._
import pages._
import pages.organisation._
import play.api.mvc.{AnyContent, Call, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForOrganisation @Inject()() extends AbstractNavigator {

  private[navigation] val normalRoutes: Page => UserAnswers => Request[AnyContent] => Option[Call] = {

    //TODO: Make the urls dynamic for each organisation and individual type when available
    case OrganisationNamePage => _ => _ => Some(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(NormalMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(NormalMode)
    case SelectAddressPage => _ => _ => Some(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
    case OrganisationAddressPage => _ => _ => Some(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
    case EmailAddressQuestionForOrganisationPage => emailAddressQuestionRoutes(NormalMode)
    case EmailAddressForOrganisationPage => _ => _ => Some(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, 0))
    case DoYouKnowAnyTINForUKOrganisationPage => doYouKnowAnyTINForUKOrganisationRoutes(NormalMode)
    case DoYouKnowTINForNonUKOrganisationPage => doYouKnowTINForNonUKOrganisationRoutes(NormalMode)

    case WhichCountryTaxForOrganisationPage => whichCountryTaxForOrganisationRoutes(NormalMode)
    case WhatAreTheTaxNumbersForUKOrganisationPage => ua => request =>
      Some(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexOrganisation(ua, request)))
    case IsOrganisationResidentForTaxOtherCountriesPage => isOrganisationResidentForTaxOtherCountriesRoutes(NormalMode)
    case WhatAreTheTaxNumbersForNonUKOrganisationPage => ua => request =>
      Some(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexOrganisation(ua, request)))

    case PostcodePage => _ => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))
    case _ => _ => _ => Some(routes.IndexController.onPageLoad())
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Request[AnyContent] => Option[Call] = {
    case OrganisationNamePage => _ => _ => Some(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(CheckMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(CheckMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(CheckMode)
    case SelectAddressPage => _ => _ => Some(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
    case OrganisationAddressPage => _ => _ => Some(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
    case EmailAddressQuestionForOrganisationPage => emailAddressQuestionRoutes(CheckMode)
    case EmailAddressForOrganisationPage => ua => request =>
      Some(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, 0))
    case WhichCountryTaxForOrganisationPage => whichCountryTaxForOrganisationRoutes(CheckMode)
    case DoYouKnowAnyTINForUKOrganisationPage => doYouKnowAnyTINForUKOrganisationRoutes(CheckMode)
    case DoYouKnowTINForNonUKOrganisationPage => doYouKnowTINForNonUKOrganisationRoutes(CheckMode)
    case WhatAreTheTaxNumbersForUKOrganisationPage => ua => request =>
      Some(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexOrganisation(ua, request)))
    case IsOrganisationResidentForTaxOtherCountriesPage => isOrganisationResidentForTaxOtherCountriesRoutes(CheckMode)
    case WhatAreTheTaxNumbersForNonUKOrganisationPage => ua => request =>
      Some(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexOrganisation(ua, request)))

     case PostcodePage => _ => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

    case _ => _ => _ => Some(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
  }

  private def isOrganisationAddressKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsOrganisationAddressKnownPage) map {
      case true  => controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(mode)
      case false => controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(mode)
    }

  private def isOrganisationAddressUKRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsOrganisationAddressUkPage) map {
      case true  => controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(mode)   // TODO: Send to postcode page when ready
      case false => controllers.organisation.routes.OrganisationAddressController.onPageLoad(mode)
    }

  private def emailAddressQuestionRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(EmailAddressQuestionForOrganisationPage) map {
      case true  => controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(mode)
      case false => controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, 0)
    }

  private def whichCountryTaxForOrganisationRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(WhichCountryTaxForOrganisationPage) map {
      countryList =>
        countryList.code match {
          case "GB" => controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
          case _ => controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
        }
    }

  private def doYouKnowAnyTINForUKOrganisationRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowAnyTINForUKOrganisationPage) map {
      case true  => controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, incrementIndexOrganisation(ua, request))
    }

  private def doYouKnowTINForNonUKOrganisationRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowTINForNonUKOrganisationPage) map {
      case true  => controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, incrementIndexOrganisation(ua, request))
    }

  private def isOrganisationResidentForTaxOtherCountriesRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(IsOrganisationResidentForTaxOtherCountriesPage) map {
      case true => controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad()
    }
  }

}
