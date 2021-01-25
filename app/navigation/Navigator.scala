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
import pages._
import pages.arrangement._
import pages.hallmarks._
import pages.intermediaries._
import pages.organisation._
import pages.taxpayer.{TaxpayerCheckYourAnswersPage, TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.mvc.{AnyContent, Call, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Int => Request[AnyContent] => Option[Call] = {

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(NormalMode)
    case HallmarkAPage => hallmarkARoutes(NormalMode)
    case HallmarkBPage => hallmarkBRoutes(NormalMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(NormalMode)
    case HallmarkCPage => hallmarkCRoutes(NormalMode)
    case HallmarkC1Page => hallmarkC1Routes(NormalMode)
    case HallmarkDPage => hallmarkDRoutes(NormalMode)
    case HallmarkD1Page => hallmarkD1Routes(NormalMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(NormalMode)
    case HallmarkEPage => _ => id => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))

    case TaxpayerSelectTypePage => selectTypeRoutes(NormalMode)

    case WhatIsThisArrangementCalledPage => _ => id => _ => Some(controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(id, NormalMode))
    case WhatIsTheImplementationDatePage => _ => id => _ => Some(controllers.arrangement.routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(id, NormalMode))
    case DoYouKnowTheReasonToReportArrangementNowPage =>  doYouKnowTheReasonToReportArrangementNowRoutes(NormalMode)
    case WhyAreYouReportingThisArrangementNowPage => _ => id => _ => Some(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(id, NormalMode))
    case WhichExpectedInvolvedCountriesArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(id, NormalMode))
    case WhatIsTheExpectedValueOfThisArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(id, NormalMode))
    case WhichNationalProvisionsIsThisArrangementBasedOnPage => _ => id => _ => Some(controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(id, NormalMode))
    case GiveDetailsOfThisArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case PostcodePage => _ => id => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(id, NormalMode))

    case TaxpayerSelectTypePage => selectTypeRoutes(NormalMode)
    case WhatIsTaxpayersStartDateForImplementingArrangementPage => _ => id => _ => Some(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id))
    case TaxpayerCheckYourAnswersPage => _ => id => _ => Some(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(id))

    case YouHaveNotAddedAnyIntermediariesPage => youHaveNotAddedAnyIntermediariesRoutes(NormalMode)
    case IntermediariesTypePage => intermediaryTypeRoutes(NormalMode)
    case WhatTypeofIntermediaryPage => whatTypeofIntermediaryRoutes(NormalMode)
    case IsExemptionKnownPage => isExemptionKnownRoutes(NormalMode)
    case IsExemptionCountryKnownPage => isExemptionCountryKnownRoutes(NormalMode)
    case ExemptCountriesPage => _ => id => _ => Some(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id))

    case HallmarksCheckYourAnswersPage => _ => id => _ => Some(controllers.routes.DisclosureDetailsController.onPageLoad(id))

    case _ => _ => id => _ => Some(routes.IndexController.onPageLoad())
  }

  private val checkRouteMap: Page => UserAnswers => Int => Request[AnyContent] => Option[Call] = {

    case HallmarkCategoriesPage => hallmarkCategoryRoutes(CheckMode)
    case HallmarkAPage => hallmarkARoutes(CheckMode)
    case HallmarkBPage => hallmarkBRoutes(CheckMode)
    case MainBenefitTestPage => mainBenefitTestRoutes(CheckMode)
    case HallmarkCPage => hallmarkCRoutes(CheckMode)
    case HallmarkC1Page => hallmarkC1Routes(CheckMode)
    case HallmarkDPage => hallmarkDRoutes(CheckMode)
    case HallmarkD1Page => hallmarkD1Routes(CheckMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(CheckMode)
    case HallmarkEPage => _ => id => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
    case PostcodePage => _ => id => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(id, CheckMode))

    case WhatIsThisArrangementCalledPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhatIsTheImplementationDatePage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case DoYouKnowTheReasonToReportArrangementNowPage => doYouKnowTheReasonToReportArrangementNowRoutes(CheckMode)
    case WhyAreYouReportingThisArrangementNowPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhichExpectedInvolvedCountriesArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhatIsTheExpectedValueOfThisArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhichNationalProvisionsIsThisArrangementBasedOnPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case GiveDetailsOfThisArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))

    case TaxpayerSelectTypePage => selectTypeRoutes(CheckMode)
    case WhatIsTaxpayersStartDateForImplementingArrangementPage => _ => id => _ => Some(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id))

    case TaxpayerCheckYourAnswersPage => _ => id => _ => Some(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(id))

    case YouHaveNotAddedAnyIntermediariesPage => youHaveNotAddedAnyIntermediariesRoutes(CheckMode)
    case IntermediariesTypePage => intermediaryTypeRoutes(CheckMode)
    case WhatTypeofIntermediaryPage => whatTypeofIntermediaryRoutes(CheckMode)
    case IsExemptionKnownPage => isExemptionKnownRoutes(CheckMode)
    case IsExemptionCountryKnownPage => isExemptionCountryKnownRoutes(CheckMode)
    case ExemptCountriesPage => _ => id => _ => Some(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id))

    case _ => _ => id => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
  }

 def catRoutes(key: HallmarkCategories): (Int, Mode) => Call = key match {
   case CategoryA => controllers.hallmarks.routes.HallmarkAController.onPageLoad
   case CategoryB => controllers.hallmarks.routes.HallmarkBController.onPageLoad
   case CategoryC => controllers.hallmarks.routes.HallmarkCController.onPageLoad
   case CategoryD => controllers.hallmarks.routes.HallmarkDController.onPageLoad
   case CategoryE => controllers.hallmarks.routes.HallmarkEController.onPageLoad
 }

  private def hallmarkCategoryRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage, id) map  {catSet  =>  catRoutes(catSet.min(orderingByName))(id, mode)}

  private def hallmarkARoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage, id) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryB) =>
        controllers.hallmarks.routes.HallmarkBController.onPageLoad(id, mode)
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        controllers.hallmarks.routes.HallmarkCController.onPageLoad(id, mode)
      case _ =>
        controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(id, mode)
    }

  private def hallmarkBRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCategoriesPage, id) map {
      case set: Set[HallmarkCategories] if set.contains(CategoryC) =>
        controllers.hallmarks.routes.HallmarkCController.onPageLoad(id, mode)
      case _ =>
        controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(id, mode)
    }

  private def mainBenefitTestRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(MainBenefitTestPage, id) map {
      case true =>
        ua.get(HallmarkCategoriesPage, id) match {
          case Some(set) if set.contains(CategoryD) => controllers.hallmarks.routes.HallmarkDController.onPageLoad(id, mode)
          case Some(set) if set.contains(CategoryE) => controllers.hallmarks.routes.HallmarkEController.onPageLoad(id, mode)
          case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id)
        }
      case false => controllers.hallmarks.routes.MainBenefitProblemController.onPageLoad(id)
    }

  private def hallmarkCRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkCPage, id) map {
      case set: Set[HallmarkC] if set.contains(C1) => controllers.hallmarks.routes.HallmarkC1Controller.onPageLoad(id, mode)
      case  _ =>  ua.get(HallmarkCategoriesPage, id) match {
        case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) => controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(id, mode)
        case Some(set) if set.contains(CategoryD) => controllers.hallmarks.routes.HallmarkDController.onPageLoad(id, mode)
        case Some(set) if set.contains(CategoryE) => controllers.hallmarks.routes.HallmarkEController.onPageLoad(id, mode)
        case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id)
      }
    }

  private def hallmarkC1Routes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkC1Page, id) map {
      case set: Set[HallmarkC1] if set.contains(C1c) || set.contains(C1bi) || set.contains(C1d) =>
        controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(id, mode)
      case  _ =>
        ua.get(HallmarkCategoriesPage, id) match {
          case Some(set) if set.contains(CategoryA) | set.contains(CategoryB) =>
            controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(id, mode)
          case Some(set) if set.contains(CategoryD) => controllers.hallmarks.routes.HallmarkDController.onPageLoad(id, mode)
          case Some(set) if set.contains(CategoryE) => controllers.hallmarks.routes.HallmarkEController.onPageLoad(id, mode)
          case _ => controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id)
        }
    }

  private def hallmarkDRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkDPage, id) match  {
      case Some(set) if set.contains(D1) => Some(controllers.hallmarks.routes.HallmarkD1Controller.onPageLoad(id, mode))
       case  _ =>  Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
       }

  private def hallmarkD1Routes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkD1Page, id) match  {
      case Some(set) if set.contains(D1other) => Some(controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(id, mode))
      case  _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
        }

  private def hallmarkD1OtherRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))

  private def selectTypeRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(TaxpayerSelectTypePage, id) map {
      case Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(id, mode)
      case Individual => controllers.individual.routes.IndividualNameController.onPageLoad(id, mode)
    }

  private def doYouKnowTheReasonToReportArrangementNowRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] = {
    ua.get(DoYouKnowTheReasonToReportArrangementNowPage, id) map {
      case true => controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(id, mode)
      case false if mode == NormalMode => controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(id, mode)
      case _ => controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id)
    }
  }

  private def youHaveNotAddedAnyIntermediariesRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] = {
    ua.get(YouHaveNotAddedAnyIntermediariesPage, id) map {
      case YouHaveNotAddedAnyIntermediaries.YesAddNow => controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(id, mode)
      case _ => controllers.routes.IndexController.onPageLoad()
    }
  }

  private def intermediaryTypeRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] = {
    ua.get(IntermediariesTypePage, id) map {
      case SelectType.Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(id, mode)
      case SelectType.Individual => controllers.individual.routes.IndividualNameController.onPageLoad(id, mode)
    }
  }

  private def whatTypeofIntermediaryRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] = {
    ua.get(WhatTypeofIntermediaryPage, id) map {
      case Promoter => controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(id, mode)
      case Serviceprovider | IDoNotKnow => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id)
    }
  }

  private def isExemptionKnownRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] = {
    ua.get(IsExemptionKnownPage, id) map {
      case Yes => controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(id, mode)
      case models.IsExemptionKnown.No | Unknown => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id)
    }
  }

  private def isExemptionCountryKnownRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(IsExemptionCountryKnownPage, id) map {
      case true  => controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(id, mode)
      case false => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id)
    }

  def nextPage(page: Page, id: Int, mode: Mode, userAnswers: UserAnswers)(implicit request: Request[AnyContent]): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)(id)(request) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()
      }
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(id)(request) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()

      }
  }
}
