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
import helpers.JourneyHelpers.{currentIndexInsideLoop, incrementIndexIndividual, incrementIndexOrganisation}
import javax.inject.{Inject, Singleton}
import models.HallmarkC.C1
import models.HallmarkC1.{C1bi, C1c, C1d}
import models.HallmarkCategories.{CategoryA, CategoryB, CategoryC, CategoryD, CategoryE, orderingByName}
import models.HallmarkD.D1
import models.HallmarkD1.D1other
import models._
import pages._
import play.api.mvc.{AnyContent, Call, Request}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Request[AnyContent] => Option[Call] = {

    //TODO: Make the urls dynamic for each organisation and individual type when available
    case OrganisationNamePage => _ => _ => Some(routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(NormalMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(NormalMode)
    case IndividualNamePage => _ => _ => Some(routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
    case IndividualDateOfBirthPage => _ => _ => Some(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
    case IsIndividualPlaceOfBirthKnownPage => isIndividualPlaceOfBirthKnownRoutes(NormalMode)
    case IndividualPlaceOfBirthPage => _ => _ => Some(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
    case IsIndividualAddressKnownPage => isIndividualAddressKnownRoutes(NormalMode)
    case SelectAddressPage => _ => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
    case OrganisationAddressPage => _ => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
    case EmailAddressQuestionForOrganisationPage => emailAddressQuestionRoutes(NormalMode)
    case EmailAddressForOrganisationPage => _ => _ => Some(routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, 0))
    case WhichCountryTaxForOrganisationPage => whichCountryTaxForOrganisationRoutes(NormalMode)
    case DoYouKnowAnyTINForUKOrganisationPage => doYouKnowAnyTINForUKOrganisationRoutes(NormalMode)
    case DoYouKnowTINForNonUKOrganisationPage => doYouKnowTINForNonUKOrganisationRoutes(NormalMode)
    case WhatAreTheTaxNumbersForUKOrganisationPage => ua => request =>
      Some(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexOrganisation(ua, request)))
    case IsOrganisationResidentForTaxOtherCountriesPage => isOrganisationResidentForTaxOtherCountriesRoutes(NormalMode)
    case WhatAreTheTaxNumbersForNonUKOrganisationPage => ua => request =>
      Some(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexOrganisation(ua, request)))
    case WhatAreTheTaxNumbersForNonUKIndividualPage => ua => request =>
        Some(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexIndividual(ua, request)))
    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(NormalMode)
    case IndividualUkPostcodePage => _ => _ => Some(routes.IndividualSelectAddressController.onPageLoad(NormalMode))
    case IndividualAddressPage => _ => _ => Some(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
    case IndividualSelectAddressPage => _ => _ => Some(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
    case EmailAddressQuestionForIndividualPage => emailAddressQuestionForIndividualRoutes(NormalMode)
    case EmailAddressForIndividualPage =>
      ua => request => Some(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, incrementIndexIndividual(ua, request)))
    case WhichCountryTaxForIndividualPage => whichCountryTaxForIndividualRoutes(NormalMode)
    case DoYouKnowAnyTINForUKIndividualPage =>  doYouKnowAnyTINForUKIndividualRoutes(NormalMode)
    case DoYouKnowTINForNonUKIndividualPage => doYouKnowTINForNonUKIndividualRoutes(NormalMode)
    case WhatAreTheTaxNumbersForUKIndividualPage=>
      ua => request => Some(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexIndividual(ua, request)))
    case IsIndividualResidentForTaxOtherCountriesPage => isIndividualResidentForTaxOtherCountriesRoutes(NormalMode)

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(NormalMode)
    case HallmarkAPage => hallmarkARoutes(NormalMode)
    case HallmarkBPage => hallmarkBRoutes(NormalMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(NormalMode)
    case HallmarkCPage => hallmarkCRoutes(NormalMode)
    case HallmarkC1Page => hallmarkC1Routes(NormalMode)
    case HallmarkDPage => hallmarkDRoutes(NormalMode)
    case HallmarkD1Page => hallmarkD1Routes(NormalMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(NormalMode)
    case PostcodePage => _ => _ => Some(routes.OrganisationSelectAddressController.onPageLoad(NormalMode))
    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(NormalMode)
    case IndividualUkPostcodePage => _ => _ => Some(routes.IndividualSelectAddressController.onPageLoad(NormalMode))
    case HallmarkEPage => _ => _ => Some(routes.CheckYourAnswersHallmarksController.onPageLoad())
    case _ => _ => _ => Some(routes.IndexController.onPageLoad())
  }

  private val checkRouteMap: Page => UserAnswers => Request[AnyContent] => Option[Call] = {
    case OrganisationNamePage => _ => _ => Some(routes.IsOrganisationAddressKnownController.onPageLoad(CheckMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(CheckMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(CheckMode)
    case SelectAddressPage => _ => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
    case OrganisationAddressPage => _ => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
    case EmailAddressQuestionForOrganisationPage => emailAddressQuestionRoutes(CheckMode)
    case EmailAddressForOrganisationPage => ua => request =>
      Some(routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, 0))
    case WhichCountryTaxForOrganisationPage => whichCountryTaxForOrganisationRoutes(CheckMode)
    case WhichCountryTaxForIndividualPage => whichCountryTaxForIndividualRoutes(CheckMode)
    case DoYouKnowAnyTINForUKIndividualPage =>  doYouKnowAnyTINForUKIndividualRoutes(CheckMode)
    case IsIndividualResidentForTaxOtherCountriesPage => isIndividualResidentForTaxOtherCountriesRoutes(CheckMode)
    case DoYouKnowAnyTINForUKOrganisationPage => doYouKnowAnyTINForUKOrganisationRoutes(CheckMode)
    case DoYouKnowTINForNonUKOrganisationPage => doYouKnowTINForNonUKOrganisationRoutes(CheckMode)
    case WhatAreTheTaxNumbersForUKOrganisationPage => ua => request =>
      Some(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexOrganisation(ua, request)))
    case IsOrganisationResidentForTaxOtherCountriesPage => isOrganisationResidentForTaxOtherCountriesRoutes(CheckMode)
    case WhatAreTheTaxNumbersForNonUKOrganisationPage => ua => request =>
      Some(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexOrganisation(ua, request)))
    case WhatAreTheTaxNumbersForUKIndividualPage=>
      ua => request => Some(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexIndividual(ua, request)))

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(CheckMode)
    case HallmarkAPage => hallmarkARoutes(CheckMode)
    case HallmarkBPage => hallmarkBRoutes(CheckMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(CheckMode)
    case HallmarkCPage => hallmarkCRoutes(CheckMode)
    case HallmarkC1Page => hallmarkC1Routes(CheckMode)
    case HallmarkDPage => hallmarkDRoutes(CheckMode)
    case HallmarkD1Page => hallmarkD1Routes(CheckMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(CheckMode)
    case HallmarkEPage => _ => _ => Some(routes.CheckYourAnswersHallmarksController.onPageLoad())
    case PostcodePage => _ => _ => Some(routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

    case IsIndividualAddressKnownPage => isIndividualAddressKnownRoutes(NormalMode)
    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(CheckMode)
    case IndividualUkPostcodePage => _ => _ => Some(routes.IndividualSelectAddressController.onPageLoad(CheckMode))
    case _ => _ => _ => Some(routes.CheckYourAnswersHallmarksController.onPageLoad())
  }

 def catRoutes(key: HallmarkCategories): Mode => Call = key match {
   case CategoryA => routes.HallmarkAController.onPageLoad
   case CategoryB => routes.HallmarkBController.onPageLoad
   case CategoryC => routes.HallmarkCController.onPageLoad
   case CategoryD => routes.HallmarkDController.onPageLoad
   case CategoryE => routes.HallmarkEController.onPageLoad
 }

  private def hallmarkCategoryRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case catSet  =>  catRoutes(catSet.min(orderingByName))(mode)
      case _ => routes.IndexController.onPageLoad()
    }

  private def hallmarkARoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryB) =>
        routes.HallmarkBController.onPageLoad(mode)
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        routes.HallmarkCController.onPageLoad(mode)
      case _ =>
        routes.MainBenefitTestController.onPageLoad(mode)
    }

  private def hallmarkBRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        routes.HallmarkCController.onPageLoad(mode)
      case _ =>
        routes.MainBenefitTestController.onPageLoad(mode)
    }

  private def mainBenefitTestRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(MainBenefitTestPage) map {
      case true =>
        ua.get(HallmarkCategoriesPage) match {
          case Some(set) if set.contains(CategoryD) => routes.HallmarkDController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryE) => routes.HallmarkEController.onPageLoad(mode)
          case _ => routes.CheckYourAnswersHallmarksController.onPageLoad()
        }
      case false => routes.MainBenefitProblemController.onPageLoad()
    }

  private def hallmarkCRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCPage) map {
      case set: Set[HallmarkC] if set.contains(C1) => routes.HallmarkC1Controller.onPageLoad(mode)
      case  _ =>  ua.get(HallmarkCategoriesPage) match {
        case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) => routes.MainBenefitTestController.onPageLoad(mode)
        case Some(set) if set.contains(CategoryD) => routes.HallmarkDController.onPageLoad(mode)
        case Some(set) if set.contains(CategoryE) => routes.HallmarkEController.onPageLoad(mode)
        case _ => routes.CheckYourAnswersHallmarksController.onPageLoad()
      }
    }

  private def hallmarkC1Routes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkC1Page) map {
      case set: Set[HallmarkC1] if set.contains(C1c) || set.contains(C1bi) || set.contains(C1d) => routes.MainBenefitTestController.onPageLoad(mode)
      case  _ =>
        ua.get(HallmarkCategoriesPage) match {
          case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) => routes.MainBenefitTestController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryD) => routes.HallmarkDController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryE) => routes.HallmarkEController.onPageLoad(mode)
          case _ => routes.CheckYourAnswersHallmarksController.onPageLoad()
        }
    }

  private def hallmarkDRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkDPage) flatMap  {
      case set: Set[HallmarkD] if set.contains(D1) => Some(routes.HallmarkD1Controller.onPageLoad(mode))
       case  _ => ua.get(HallmarkCategoriesPage).map {
         case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
           routes.HallmarkEController.onPageLoad(mode)
         case _ => routes.CheckYourAnswersHallmarksController.onPageLoad()
       }
    }

   private def hallmarkD1Routes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkD1Page) flatMap  {
      case set: Set[HallmarkD1] if set.contains(D1other) => Some(routes.HallmarkD1OtherController.onPageLoad(mode))
      case  _ => ua.get(HallmarkCategoriesPage).map {
        case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
          routes.HallmarkEController.onPageLoad(mode)
        case _ => routes.CheckYourAnswersHallmarksController.onPageLoad()
      }
    }

   private def hallmarkD1OtherRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
     ua.get(HallmarkCategoriesPage) map {
       case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
         routes.HallmarkEController.onPageLoad(mode)
       case _ => routes.CheckYourAnswersHallmarksController.onPageLoad()
     }

  private def isIndividualPlaceOfBirthKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualPlaceOfBirthKnownPage) map {
      case true  => routes.IndividualPlaceOfBirthController.onPageLoad(mode)
      case false => routes.IsIndividualAddressKnownController.onPageLoad(mode)
    }

  private def isIndividualAddressKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualAddressKnownPage) map {
      case true  => routes.IsIndividualAddressUkController.onPageLoad(mode)
      case false => routes.EmailAddressQuestionForIndividualController.onPageLoad(mode)
    }

  private def emailAddressQuestionForIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(EmailAddressQuestionForIndividualPage) map {
      case true  => routes.EmailAddressForIndividualController.onPageLoad(mode)
      case false => routes.WhichCountryTaxForIndividualController.onPageLoad(mode, 0)
    }

  private def whichCountryTaxForIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(WhichCountryTaxForIndividualPage) map {
      countryList =>
        countryList.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
          case _ => routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request)) // TODO: Send to nonUk page when ready
        }
    }

  private def doYouKnowAnyTINForUKIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowAnyTINForUKIndividualPage) map {
      case true  => routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(mode)
      case false => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode,  incrementIndexIndividual(ua, request))
    }

  private def isIndividualResidentForTaxOtherCountriesRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualResidentForTaxOtherCountriesPage) map {
      case true  => routes.WhichCountryTaxForIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => routes.IndexController.onPageLoad() //TODO: Route to confirmation page
    }

  private def isOrganisationAddressKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsOrganisationAddressKnownPage) map {
      case true  => routes.IsOrganisationAddressUkController.onPageLoad(mode)
      case false => routes.EmailAddressQuestionForOrganisationController.onPageLoad(mode)
    }

  private def isOrganisationAddressUKRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsOrganisationAddressUkPage) map {
      case true  => routes.OrganisationPostcodeController.onPageLoad(mode)   // TODO: Send to postcode page when ready
      case false => routes.OrganisationAddressController.onPageLoad(mode)
    }

  private def isIndividualAddressUKRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualAddressUkPage) map {
      case true  => routes.IndividualPostcodeController.onPageLoad(mode)
      case false => routes.IndividualAddressController.onPageLoad(mode)
    }

  private def emailAddressQuestionRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(EmailAddressQuestionForOrganisationPage) map {
      case true  => routes.EmailAddressForOrganisationController.onPageLoad(mode)
      case false => routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, 0)
    }

  private def whichCountryTaxForOrganisationRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(WhichCountryTaxForOrganisationPage) map {
      countryList =>
        countryList.code match {
          case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
          case _ => routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
        }
    }

  private def doYouKnowAnyTINForUKOrganisationRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowAnyTINForUKOrganisationPage) map {
      case true  => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, incrementIndexOrganisation(ua, request))
    }

  private def doYouKnowTINForNonUKOrganisationRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowTINForNonUKOrganisationPage) map {
      case true  => routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode, incrementIndexOrganisation(ua, request))
    }

  private def isOrganisationResidentForTaxOtherCountriesRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(IsOrganisationResidentForTaxOtherCountriesPage) map {
      case true => routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => routes.CheckYourAnswersOrganisationController.onPageLoad()
    }
  }

  private def doYouKnowTINForNonUKIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowTINForNonUKIndividualPage) map {
      case true  => routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode, incrementIndexIndividual(ua, request))
    }


  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: Request[AnyContent]): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)(request) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()
      }
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(request) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()

      }
  }
}
