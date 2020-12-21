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

import controllers.routes
import models.IsExemptionKnown.{Unknown, Yes}
import models.SelectType.{Individual, Organisation}
import models._
import models.hallmarks.HallmarkC.C1
import models.hallmarks.HallmarkC1.{C1bi, C1c, C1d}
import models.hallmarks.HallmarkCategories.{CategoryA, CategoryB, CategoryC, CategoryD, CategoryE, orderingByName}
import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import models.hallmarks._
import models.intermediaries.WhatTypeofIntermediary.{IDoNotKnow, Promoter, Serviceprovider}
import models.intermediaries.YouHaveNotAddedAnyIntermediaries
import models.taxpayer.UpdateTaxpayer.{Later, Now}
import pages._
import pages.arrangement._
import pages.hallmarks._
import pages.intermediaries._
import pages.organisation._
import pages.taxpayer.{TaxpayerCheckYourAnswersPage, TaxpayerSelectTypePage, UpdateTaxpayerPage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.mvc.{AnyContent, Call, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Request[AnyContent] => Option[Call] = {

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
    case TaxpayerSelectTypePage => selectTypeRoutes(NormalMode)

    case WhatIsThisArrangementCalledPage => _ => _ => Some(controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(NormalMode))
    case WhatIsTheImplementationDatePage => _ => _ => Some(controllers.arrangement.routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(NormalMode))
    case DoYouKnowTheReasonToReportArrangementNowPage =>  doYouKnowTheReasonToReportArrangementNowRoutes(NormalMode)
    case WhyAreYouReportingThisArrangementNowPage => _ => _ => Some(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(NormalMode))
    case WhichExpectedInvolvedCountriesArrangementPage => _ => _ => Some(controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(NormalMode))
    case WhatIsTheExpectedValueOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(NormalMode))
    case WhichNationalProvisionsIsThisArrangementBasedOnPage => _ => _ => Some(controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(NormalMode))
    case GiveDetailsOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case PostcodePage => _ => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))

    case TaxpayerSelectTypePage => selectTypeRoutes(NormalMode)
    case WhatIsTaxpayersStartDateForImplementingArrangementPage => _ => _ => Some(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
    case TaxpayerCheckYourAnswersPage => _ => _ => Some(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad())

    case YouHaveNotAddedAnyIntermediariesPage => youHaveNotAddedAnyIntermediariesRoutes(NormalMode)
    case IntermediariesTypePage => intermediaryTypeRoutes(NormalMode)
    case WhatTypeofIntermediaryPage => whatTypeofIntermediaryRoutes(NormalMode)
    case IsExemptionKnownPage => isExemptionKnownRoutes(NormalMode)
    case IsExemptionCountryKnownPage => isExemptionCountryKnownRoutes(NormalMode)
    case ExemptCountriesPage => _ => _ => Some(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad())
    case IntermediariesCheckYourAnswersPage => _ => _ => Some(controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad())

    case _ => _ => _ => Some(routes.IndexController.onPageLoad())
  }

  private val checkRouteMap: Page => UserAnswers => Request[AnyContent] => Option[Call] = {

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

    case WhatIsThisArrangementCalledPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhatIsTheImplementationDatePage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case DoYouKnowTheReasonToReportArrangementNowPage => doYouKnowTheReasonToReportArrangementNowRoutes(CheckMode)
    case WhyAreYouReportingThisArrangementNowPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhichExpectedInvolvedCountriesArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhatIsTheExpectedValueOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case WhichNationalProvisionsIsThisArrangementBasedOnPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())
    case GiveDetailsOfThisArrangementPage => _ => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad())

    case TaxpayerSelectTypePage => selectTypeRoutes(CheckMode)
    case WhatIsTaxpayersStartDateForImplementingArrangementPage => _ => _ => Some(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())

    case TaxpayerCheckYourAnswersPage => _ => _ => Some(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad())

    case YouHaveNotAddedAnyIntermediariesPage => youHaveNotAddedAnyIntermediariesRoutes(CheckMode)
    case IntermediariesTypePage => intermediaryTypeRoutes(CheckMode)
    case WhatTypeofIntermediaryPage => whatTypeofIntermediaryRoutes(CheckMode)
    case IsExemptionKnownPage => isExemptionKnownRoutes(CheckMode)
    case IsExemptionCountryKnownPage => isExemptionCountryKnownRoutes(CheckMode)
    case ExemptCountriesPage => _ => _ => Some(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad())
    case IntermediariesCheckYourAnswersPage => _ => _ => Some(controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad())

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
      case Now => controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(mode)
      case Later | models.taxpayer.UpdateTaxpayer.No => controllers.routes.IndexController.onPageLoad() // TODO: Link to disclose type page when ready
    }

  private def selectTypeRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(TaxpayerSelectTypePage) map {
      case Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(mode)
      case Individual => controllers.individual.routes.IndividualNameController.onPageLoad(mode)
    }

  private def doYouKnowTheReasonToReportArrangementNowRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(DoYouKnowTheReasonToReportArrangementNowPage) map {
      case true => controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(mode)
      case false if mode == NormalMode => controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(mode)
      case _ => controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad()
    }
  }

  private def youHaveNotAddedAnyIntermediariesRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(YouHaveNotAddedAnyIntermediariesPage) map {
      case YouHaveNotAddedAnyIntermediaries.YesAddNow => controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(mode)
      case _ => controllers.routes.IndexController.onPageLoad()
    }
  }

  private def intermediaryTypeRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(IntermediariesTypePage) map {
      case SelectType.Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(mode)
      case SelectType.Individual => controllers.individual.routes.IndividualNameController.onPageLoad(mode)
    }
  }

  private def whatTypeofIntermediaryRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(WhatTypeofIntermediaryPage) map {
      case Promoter => controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(mode)
      case Serviceprovider | IDoNotKnow => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()
    }
  }

  private def isExemptionKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] = {
    ua.get(IsExemptionKnownPage) map {
      case Yes => controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(mode)
      case models.IsExemptionKnown.No | Unknown => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()
    }
  }

  private def isExemptionCountryKnownRoutes(mode: Mode)(ua: UserAnswers)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsExemptionCountryKnownPage) map {
      case true  => controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(mode)
      case false => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad()
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
