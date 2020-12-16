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
import models.SelectType.{Individual, Organisation}
import models._
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.hallmarks.HallmarkC.C1
import models.hallmarks.HallmarkC1.{C1bi, C1c, C1d}
import models.hallmarks.HallmarkCategories.{CategoryA, CategoryB, CategoryC, CategoryD, CategoryE, orderingByName}
import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import models.hallmarks._
import models.taxpayer.UpdateTaxpayer.{Later, No, Now}
import pages._
import pages.arrangement._
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.organisation._
import pages.taxpayer.UpdateTaxpayerPage
import play.api.mvc.{AnyContent, Call, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Request[AnyContent] => Option[Call] = {

    //TODO: Make the urls dynamic for each organisation and individual type when available
    case OrganisationNamePage => _ => _ => Some(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
    case IsOrganisationAddressKnownPage => isOrganisationAddressKnownRoutes(NormalMode)
    case IsOrganisationAddressUkPage => isOrganisationAddressUKRoutes(NormalMode)
    case IndividualNamePage => _ => _ => Some(controllers.individual.routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
    case IndividualDateOfBirthPage => _ => _ => Some(controllers.individual.routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
    case IsIndividualPlaceOfBirthKnownPage => isIndividualPlaceOfBirthKnownRoutes(NormalMode)
    case IndividualPlaceOfBirthPage => _ => _ => Some(controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
    case IsIndividualAddressKnownPage => isIndividualAddressKnownRoutes(NormalMode)
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
    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(NormalMode)
    case IndividualUkPostcodePage => _ => _ => Some(controllers.individual.routes.IndividualSelectAddressController.onPageLoad(NormalMode))
    case IndividualAddressPage => _ => _ => Some(controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
    case IndividualSelectAddressPage => _ => _ => Some(controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
    case EmailAddressQuestionForIndividualPage => emailAddressQuestionForIndividualRoutes(NormalMode)
    case EmailAddressForIndividualPage =>
      ua => request => Some(controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, incrementIndexIndividual(ua, request)))
    case WhichCountryTaxForIndividualPage => whichCountryTaxForIndividualRoutes(NormalMode)
    case DoYouKnowAnyTINForUKIndividualPage =>  doYouKnowAnyTINForUKIndividualRoutes(NormalMode)
    case WhatAreTheTaxNumbersForUKIndividualPage=>
      ua => request => Some(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexIndividual(ua, request)))
    case DoYouKnowTINForNonUKIndividualPage => doYouKnowTINForNonUKIndividualRoutes(NormalMode)
    case WhatAreTheTaxNumbersForNonUKIndividualPage => ua => request =>
      Some(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, incrementIndexIndividual(ua, request)))
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
    case HallmarkEPage => _ => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())

    case UpdateTaxpayerPage => updateTaxpayerRoutes(NormalMode)
    case SelectTypePage => selectTypeRoutes(NormalMode)

    case WhatIsThisArrangementCalledPage => _ => _ => Some(controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(NormalMode))
    case WhatIsTheImplementationDatePage => _ => _ => Some(controllers.arrangement.routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(NormalMode))
    case DoYouKnowTheReasonToReportArrangementNowPage =>  doYouKnowTheReasonToReportArrangementNowRoutes(NormalMode)
    case WhyAreYouReportingThisArrangementNowPage => _ => _ => Some(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(NormalMode))
    case WhichExpectedInvolvedCountriesArrangementPage => _ => _ => Some(controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(NormalMode))
    case WhatIsTheExpectedValueOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(NormalMode))
    case WhichNationalProvisionsIsThisArrangementBasedOnPage => _ => _ => Some(controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(NormalMode))
    case GiveDetailsOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad)
    case PostcodePage => _ => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))

    case YouHaveNotAddedAnyAssociatedEnterprisesPage => youHaveNotAddedAnyAssociatedEnterprisesPage(NormalMode)
    case SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage => _ => _ => Some(controllers.enterprises.routes.AssociatedEnterpriseTypeController.onPageLoad(NormalMode)) // TODO redirect
    case AssociatedEnterpriseTypePage => associatedEnterpriseTypeRoutes(NormalMode)
    case IsAssociatedEnterpriseAffectedPage => _ => _ =>
      Some(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
    case _ => _ => _ => Some(routes.IndexController.onPageLoad())
  }

  private val checkRouteMap: Page => UserAnswers => Request[AnyContent] => Option[Call] = {
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

    case WhichCountryTaxForIndividualPage => whichCountryTaxForIndividualRoutes(CheckMode)
    case DoYouKnowAnyTINForUKIndividualPage =>  doYouKnowAnyTINForUKIndividualRoutes(CheckMode)
    case WhatAreTheTaxNumbersForUKIndividualPage=>
      ua => request => Some(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexIndividual(ua, request)))
    case DoYouKnowTINForNonUKIndividualPage => doYouKnowTINForNonUKIndividualRoutes(CheckMode)
    case WhatAreTheTaxNumbersForNonUKIndividualPage => ua => request =>
      Some(controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, incrementIndexIndividual(ua, request)))
    case IsIndividualResidentForTaxOtherCountriesPage => isIndividualResidentForTaxOtherCountriesRoutes(CheckMode)

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(CheckMode)
    case HallmarkAPage => hallmarkARoutes(CheckMode)
    case HallmarkBPage => hallmarkBRoutes(CheckMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(CheckMode)
    case HallmarkCPage => hallmarkCRoutes(CheckMode)
    case HallmarkC1Page => hallmarkC1Routes(CheckMode)
    case HallmarkDPage => hallmarkDRoutes(CheckMode)
    case HallmarkD1Page => hallmarkD1Routes(CheckMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(CheckMode)
    case HallmarkEPage => _ => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
    case PostcodePage => _ => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

    case IndividualNamePage => _ => _ => Some(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())
    case IndividualDateOfBirthPage => _ => _ => Some(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())
    case IsIndividualPlaceOfBirthKnownPage => isIndividualPlaceOfBirthKnownRoutes(CheckMode)
    case IndividualPlaceOfBirthPage => _ => _ => Some(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())
    case IsIndividualAddressKnownPage => isIndividualAddressKnownRoutes(CheckMode)
    case IsIndividualAddressUkPage => isIndividualAddressUKRoutes(CheckMode) // TODO is necessary ?
    case IndividualUkPostcodePage => _ => _ => Some(controllers.individual.routes.IndividualSelectAddressController.onPageLoad(CheckMode))  // TODO is necessary ?
    case IndividualAddressPage => _ => _ => Some(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())
    case IndividualSelectAddressPage => _ => _ => Some(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())
    case EmailAddressQuestionForIndividualPage => emailAddressQuestionForIndividualRoutes(CheckMode)
    case EmailAddressForIndividualPage => _ => _ => Some(controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad())

    case WhatIsThisArrangementCalledPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhatIsTheImplementationDatePage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case DoYouKnowTheReasonToReportArrangementNowPage => doYouKnowTheReasonToReportArrangementNowRoutes(CheckMode)
    case WhyAreYouReportingThisArrangementNowPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhichExpectedInvolvedCountriesArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhatIsTheExpectedValueOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhichNationalProvisionsIsThisArrangementBasedOnPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case GiveDetailsOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad)

    case YouHaveNotAddedAnyAssociatedEnterprisesPage => youHaveNotAddedAnyAssociatedEnterprisesPage(CheckMode)
    case SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage => _ => _ => Some(controllers.enterprises.routes.AssociatedEnterpriseTypeController.onPageLoad(CheckMode)) // TODO redirect
    case AssociatedEnterpriseTypePage => associatedEnterpriseTypeRoutes(CheckMode)
    case IsAssociatedEnterpriseAffectedPage => _ => _ =>
      Some(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())

    case _ => _ => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad())
  }

 def catRoutes(key: HallmarkCategories): Mode => Call = key match {
   case CategoryA => controllers.hallmarks.routes.HallmarkAController.onPageLoad
   case CategoryB => controllers.hallmarks.routes.HallmarkBController.onPageLoad
   case CategoryC => controllers.hallmarks.routes.HallmarkCController.onPageLoad
   case CategoryD => controllers.hallmarks.routes.HallmarkDController.onPageLoad
   case CategoryE => controllers.hallmarks.routes.HallmarkEController.onPageLoad
 }

  private def hallmarkCategoryRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage) map  {catSet  =>  catRoutes(catSet.min(orderingByName))(mode)}

  private def hallmarkARoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryB) =>
        controllers.hallmarks.routes.HallmarkBController.onPageLoad(mode)
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        controllers.hallmarks.routes.HallmarkCController.onPageLoad(mode)
      case _ =>
        controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(mode)
    }

  private def hallmarkBRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        controllers.hallmarks.routes.HallmarkCController.onPageLoad(mode)
      case _ =>
        controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(mode)
    }

  private def mainBenefitTestRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(MainBenefitTestPage) map {
      case true =>
        ua.get(HallmarkCategoriesPage) match {
          case Some(set) if set.contains(CategoryD) => controllers.hallmarks.routes.HallmarkDController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryE) => controllers.hallmarks.routes.HallmarkEController.onPageLoad(mode)
          case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad()
        }
      case false => controllers.hallmarks.routes.MainBenefitProblemController.onPageLoad()
    }

  private def hallmarkCRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCPage) map {
      case set: Set[HallmarkC] if set.contains(C1) => controllers.hallmarks.routes.HallmarkC1Controller.onPageLoad(mode)
      case  _ =>  ua.get(HallmarkCategoriesPage) match {
        case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) => controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(mode)
        case Some(set) if set.contains(CategoryD) => controllers.hallmarks.routes.HallmarkDController.onPageLoad(mode)
        case Some(set) if set.contains(CategoryE) => controllers.hallmarks.routes.HallmarkEController.onPageLoad(mode)
        case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad()
      }
    }

  private def hallmarkC1Routes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkC1Page) map {
      case set: Set[HallmarkC1] if set.contains(C1c) || set.contains(C1bi) || set.contains(C1d) =>
        controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(mode)
      case  _ =>
        ua.get(HallmarkCategoriesPage) match {
          case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) =>
            controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryD) => controllers.hallmarks.routes.HallmarkDController.onPageLoad(mode)
          case Some(set) if set.contains(CategoryE) => controllers.hallmarks.routes.HallmarkEController.onPageLoad(mode)
          case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad()
        }
    }

  private def hallmarkDRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkDPage) flatMap  {
      case set: Set[HallmarkD] if set.contains(D1) => Some(controllers.hallmarks.routes.HallmarkD1Controller.onPageLoad(mode))
       case  _ => ua.get(HallmarkCategoriesPage).map {
         case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
           controllers.hallmarks.routes.HallmarkEController.onPageLoad(mode)
         case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad()
       }
    }

  private def hallmarkD1Routes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkD1Page) flatMap  {
      case set: Set[HallmarkD1] if set.contains(D1other) => Some(controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(mode))
      case  _ => ua.get(HallmarkCategoriesPage).map {
        case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
          controllers.hallmarks.routes.HallmarkEController.onPageLoad(mode)
        case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad()
      }
    }

   private def hallmarkD1OtherRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
     ua.get(HallmarkCategoriesPage) map {
       case set: Set[HallmarkCategories] if set.contains(CategoryE) =>
         controllers.hallmarks.routes.HallmarkEController.onPageLoad(mode)
       case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad()
     }

  private def updateTaxpayerRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(UpdateTaxpayerPage) map {
      case Now => controllers.routes.SelectTypeController.onPageLoad(mode)
      case Later | No => controllers.routes.IndexController.onPageLoad() // TODO: Link to disclose type page when ready
    }

  private def selectTypeRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(SelectTypePage) map {
      case Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(mode)
      case Individual => controllers.individual.routes.IndividualNameController.onPageLoad(mode)
    }

  private def isIndividualPlaceOfBirthKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualPlaceOfBirthKnownPage) map {
      case true  => controllers.individual.routes.IndividualPlaceOfBirthController.onPageLoad(mode)
      case false if mode == NormalMode => controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(mode)
      case _ => controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()
    }

  private def isIndividualAddressKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualAddressKnownPage) map {
      case true  => controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(mode)
      case false if mode == NormalMode => controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(mode)
      case _ => controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()
    }

  private def emailAddressQuestionForIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(EmailAddressQuestionForIndividualPage) map {
      case true  => controllers.individual.routes.EmailAddressForIndividualController.onPageLoad(mode)
      case false if mode == NormalMode => controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(mode, 0)
      case _ => controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()
    }

  private def whichCountryTaxForIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(WhichCountryTaxForIndividualPage) map {
      countryList =>
        countryList.code match {
          case "GB" => controllers.individual.routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
          case _ => controllers.individual.routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request)) // TODO: Send to nonUk page when ready
        }
    }

  private def doYouKnowAnyTINForUKIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowAnyTINForUKIndividualPage) map {
      case true  => controllers.individual.routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode,  incrementIndexIndividual(ua, request))
    }

  private def isIndividualResidentForTaxOtherCountriesRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    val associatedEnterpriseJourney: Boolean = ua.get(AssociatedEnterpriseTypePage) match {
      case Some(_) => true
      case None => false
    }

    ua.get(IsIndividualResidentForTaxOtherCountriesPage) map {
      case true => controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false if associatedEnterpriseJourney => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(mode)
      case false => controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad()
    }
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

  private def isIndividualAddressUKRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsIndividualAddressUkPage) map {
      case true  => controllers.individual.routes.IndividualPostcodeController.onPageLoad(mode)
      case false => controllers.individual.routes.IndividualAddressController.onPageLoad(mode)
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
    val associatedEnterpriseJourney: Boolean = ua.get(AssociatedEnterpriseTypePage) match {
      case Some(_) => true
      case None => false
    }

    ua.get(IsOrganisationResidentForTaxOtherCountriesPage) map {
      case true => controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false if associatedEnterpriseJourney => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(mode)
      case false => controllers.organisation.routes.CheckYourAnswersOrganisationController.onPageLoad()
    }
  }

  private def doYouKnowTINForNonUKIndividualRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(DoYouKnowTINForNonUKIndividualPage) map {
      case true  => controllers.individual.routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(mode, currentIndexInsideLoop(request))
      case false => controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode, incrementIndexIndividual(ua, request))
    }

  private def doYouKnowTheReasonToReportArrangementNowRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(DoYouKnowTheReasonToReportArrangementNowPage) map {
      case true => controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(mode)
      case false if mode == NormalMode => controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(mode)
      case _ => controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()
    }
  }

  private def youHaveNotAddedAnyAssociatedEnterprisesPage(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(YouHaveNotAddedAnyAssociatedEnterprisesPage) map {
      case YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow  =>
        controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(mode)
      case _ =>
        controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(mode)
    }

  private def associatedEnterpriseTypeRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(AssociatedEnterpriseTypePage) map {
      case SelectType.Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(mode)
      case SelectType.Individual => controllers.individual.routes.IndividualNameController.onPageLoad(mode)
    }
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
