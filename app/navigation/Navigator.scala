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
import javax.inject.{Inject, Singleton}
import models.HallmarkC.C1
import models.HallmarkC1.{C1bi, C1c, C1d}
import models.HallmarkCategories.{CategoryA, CategoryB, CategoryC, CategoryD, CategoryE, orderingByName}
import models.HallmarkD.D1
import models.HallmarkD1.D1other
import models._
import pages._
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Option[Call] = {

    //TODO: Make the urls dynamic for each organisation and individual type when available
    case OrganisationNamePage => _ => Some(routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(NormalMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(NormalMode)
    case IndividualNamePage => _ => Some(routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
    case IndividualDateOfBirthPage => _ => Some(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
    case IsIndividualPlaceOfBirthKnownPage => isIndividualPlaceOfBirthKnownRoutes(NormalMode)
    case IndividualPlaceOfBirthPage => _ => Some(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
    case SelectAddressPage => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
    case OrganisationAddressPage => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
    case EmailAddressQuestionForOrganisationPage => emailAddressQuestionRoutes(NormalMode)
    case EmailAddressForOrganisationPage => _ => Some(routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode))
    case WhichCountryTaxForOrganisationPage => whichCountryTaxForOrganisationRoutes(NormalMode)
    case DoYouKnowAnyTINForUKOrganisationPage => doYouKnowAnyTINForUKOrganisationRoutes(NormalMode)
    case WhatAreTheTaxNumbersForUKOrganisationPage => _ => Some(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode))

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(NormalMode)
    case HallmarkAPage => hallmarkARoutes(NormalMode)
    case HallmarkBPage => hallmarkBRoutes(NormalMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(NormalMode)
    case HallmarkCPage => hallmarkCRoutes(NormalMode)
    case HallmarkC1Page => hallmarkC1Routes(NormalMode)
    case HallmarkDPage => hallmarkDRoutes(NormalMode)
    case HallmarkD1Page => hallmarkD1Routes(NormalMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(NormalMode)
    case PostcodePage => _ => Some(routes.OrganisationSelectAddressController.onPageLoad(NormalMode))
    case HallmarkEPage => _ => Some(routes.CheckYourAnswersController.onPageLoad())

    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(NormalMode)
    case IndividualUkPostcodePage => _ => Some(routes.IndividualSelectAddressController.onPageLoad(NormalMode))
    case _ => _ => Some(routes.IndexController.onPageLoad())
  }

  private val checkRouteMap: Page => UserAnswers => Option[Call] = {
    case OrganisationNamePage => _ => Some(routes.IsOrganisationAddressKnownController.onPageLoad(CheckMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(CheckMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(CheckMode)
    case SelectAddressPage => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
    case OrganisationAddressPage => _ => Some(routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
    case EmailAddressQuestionForOrganisationPage => emailAddressQuestionRoutes(CheckMode)
    case EmailAddressForOrganisationPage => _ => Some(routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode))
    case WhichCountryTaxForOrganisationPage => whichCountryTaxForOrganisationRoutes(CheckMode)
    case DoYouKnowAnyTINForUKOrganisationPage => doYouKnowAnyTINForUKOrganisationRoutes(CheckMode)
    case WhatAreTheTaxNumbersForUKOrganisationPage => _ => Some(routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode))

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(CheckMode)
    case HallmarkAPage => hallmarkARoutes(CheckMode)
    case HallmarkBPage => hallmarkBRoutes(CheckMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(CheckMode)
    case HallmarkCPage => hallmarkCRoutes(CheckMode)
    case HallmarkC1Page => hallmarkC1Routes(CheckMode)
    case HallmarkDPage => hallmarkDRoutes(CheckMode)
    case HallmarkD1Page => hallmarkD1Routes(CheckMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(CheckMode)
    case HallmarkEPage => _ => Some(routes.CheckYourAnswersController.onPageLoad())
    case PostcodePage => _ => Some(routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(CheckMode)
    case IndividualUkPostcodePage => _ => Some(routes.IndividualSelectAddressController.onPageLoad(CheckMode))
    case _ => _ => Some(routes.CheckYourAnswersController.onPageLoad())
  }

 def catRoutes(key: HallmarkCategories): Mode => Call = key match {
   case CategoryA => routes.HallmarkAController.onPageLoad
   case CategoryB => routes.HallmarkBController.onPageLoad
   case CategoryC => routes.HallmarkCController.onPageLoad
   case CategoryD => routes.HallmarkDController.onPageLoad
   case CategoryE => routes.HallmarkEController.onPageLoad
 }

  private def hallmarkCategoryRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case catSet  =>  catRoutes(catSet.min(orderingByName))(mode)
      case _ => routes.IndexController.onPageLoad()
    }

  private def hallmarkARoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryB) =>
        routes.HallmarkBController.onPageLoad(mode)
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        routes.HallmarkCController.onPageLoad(mode)
      case _ =>
        routes.MainBenefitTestController.onPageLoad(mode)
    }

  private def hallmarkBRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        routes.HallmarkCController.onPageLoad(mode)
      case _ =>
        routes.MainBenefitTestController.onPageLoad(mode)
    }

  private def mainBenefitTestRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(MainBenefitTestPage) map {
      case true =>
        ua.get(HallmarkCategoriesPage) match {
          case Some(set) if set.contains(CategoryD) => routes.HallmarkDController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryE) => routes.HallmarkEController.onPageLoad(mode)
          case _ => routes.CheckYourAnswersController.onPageLoad()
        }
      case false => routes.MainBenefitProblemController.onPageLoad()
    }

  private def hallmarkCRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkCPage) map {
      case set: Set[HallmarkC] if set.contains(C1) => routes.HallmarkC1Controller.onPageLoad(mode)
      case  _ =>  ua.get(HallmarkCategoriesPage) match {
        case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) => routes.MainBenefitTestController.onPageLoad(mode)
        case Some(set) if set.contains(CategoryD) => routes.HallmarkDController.onPageLoad(mode)
        case Some(set) if set.contains(CategoryE) => routes.HallmarkEController.onPageLoad(mode)
        case _ => routes.CheckYourAnswersController.onPageLoad()
      }
    }

  private def hallmarkC1Routes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkC1Page) map {
      case set: Set[HallmarkC1] if set.contains(C1c) || set.contains(C1bi) || set.contains(C1d) => routes.MainBenefitTestController.onPageLoad(mode)
      case  _ =>
        ua.get(HallmarkCategoriesPage) match {
          case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) => routes.MainBenefitTestController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryD) => routes.HallmarkDController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryE) => routes.HallmarkEController.onPageLoad(mode)
          case _ => routes.CheckYourAnswersController.onPageLoad()
        }
    }

  private def hallmarkDRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkDPage) flatMap  {
      case set: Set[HallmarkD] if set.contains(D1) => Some(routes.HallmarkD1Controller.onPageLoad(mode))
       case  _ => ua.get(HallmarkCategoriesPage).map {
         case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
           routes.HallmarkEController.onPageLoad(mode)
         case _ => routes.CheckYourAnswersController.onPageLoad()
       }
    }

   private def hallmarkD1Routes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkD1Page) flatMap  {
      case set: Set[HallmarkD1] if set.contains(D1other) => Some(routes.HallmarkD1OtherController.onPageLoad(mode))
      case  _ => ua.get(HallmarkCategoriesPage).map {
        case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
          routes.HallmarkEController.onPageLoad(mode)
        case _ => routes.CheckYourAnswersController.onPageLoad()
      }
    }

   private def hallmarkD1OtherRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
     ua.get(HallmarkCategoriesPage) map {
       case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
         routes.HallmarkEController.onPageLoad(mode)
       case _ => routes.CheckYourAnswersController.onPageLoad()
     }

  private def isIndividualPlaceOfBirthKnownRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(IsIndividualPlaceOfBirthKnownPage) map {
      case true  => routes.IndividualPlaceOfBirthController.onPageLoad(mode)
      case false => routes.IsIndividualAddressKnownController.onPageLoad(mode)
    }

  private def isOrganisationAddressKnownRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(IsOrganisationAddressKnownPage) map {
      case true  => routes.IsOrganisationAddressUkController.onPageLoad(mode)
      case false => routes.EmailAddressQuestionForOrganisationController.onPageLoad(mode)
    }

  private def isOrganisationAddressUKRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(IsOrganisationAddressUkPage) map {
      case true  => routes.OrganisationPostcodeController.onPageLoad(mode)
      case false => routes.OrganisationAddressController.onPageLoad(mode)
    }

  private def isIndividualAddressUKRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(IsIndividualAddressUkPage) map {
      case true  => routes.IndividualPostcodeController.onPageLoad(mode)
      case false => routes.IndividualAddressController.onPageLoad(mode)
    }

  private def emailAddressQuestionRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(EmailAddressQuestionForOrganisationPage) map {
      case true  => routes.EmailAddressForOrganisationController.onPageLoad(mode)
      case false => routes.WhichCountryTaxForOrganisationController.onPageLoad(mode)
    }

  private def whichCountryTaxForOrganisationRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(WhichCountryTaxForOrganisationPage) map {
      _.code match {
        case "GB" => routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(mode)
        case _ => routes.IndexController.onPageLoad() //TODO Redirect to /organisation/non-uk-tin-known when ready. Add UT
      }
    }

  private def doYouKnowAnyTINForUKOrganisationRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(DoYouKnowAnyTINForUKOrganisationPage) map {
      case true  => routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(mode)
      case false => routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode)
    }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()
      }
    case CheckMode =>
      checkRouteMap(page)(userAnswers) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()

      }
  }
}
