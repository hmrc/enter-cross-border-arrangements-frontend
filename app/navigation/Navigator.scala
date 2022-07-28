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

import controllers.routes
import javax.inject.{Inject, Singleton}
import models.SelectType.{Individual, Organisation}
import models._
import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import pages._
import pages.arrangement._
import pages.hallmarks._
import pages.organisation._
import pages.taxpayer.{TaxpayerCheckYourAnswersPage, TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.mvc.{AnyContent, Call, Request}

@Singleton
class Navigator @Inject() () {

  private val normalRoutes: Page => UserAnswers => Int => Request[AnyContent] => Option[Call] = {

    case HallmarkDPage       => hallmarkDRoutes(NormalMode)
    case HallmarkD1Page      => hallmarkD1Routes(NormalMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(NormalMode)

    case TaxpayerSelectTypePage => selectTypeRoutes(NormalMode)

    case WhatIsThisArrangementCalledPage =>
      _ => id => _ => Some(controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(id, NormalMode))
    case WhatIsTheImplementationDatePage =>
      _ => id => _ => Some(controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(id, NormalMode))
    case WhyAreYouReportingThisArrangementNowPage =>
      _ => id => _ => Some(controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(id, NormalMode))
    case WhichExpectedInvolvedCountriesArrangementPage =>
      _ => id => _ => Some(controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(id, NormalMode))
    case WhatIsTheExpectedValueOfThisArrangementPage =>
      _ => id => _ => Some(controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(id, NormalMode))
    case WhichNationalProvisionsIsThisArrangementBasedOnPage =>
      _ => id => _ => Some(controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(id, NormalMode))
    case GiveDetailsOfThisArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case PostcodePage                     => _ => id => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(id, NormalMode))

    case TaxpayerSelectTypePage => selectTypeRoutes(NormalMode)
    case WhatIsTaxpayersStartDateForImplementingArrangementPage =>
      _ => id => _ => Some(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id, None))
    case TaxpayerCheckYourAnswersPage => _ => id => _ => Some(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(id))

    case HallmarksCheckYourAnswersPage => _ => id => _ => Some(controllers.routes.DisclosureDetailsController.onPageLoad(id))

    case _ => _ => _ => _ => Some(routes.IndexController.onPageLoad)
  }

  private val checkRouteMap: Page => UserAnswers => Int => Request[AnyContent] => Option[Call] = {

    case HallmarkDPage       => hallmarkDRoutes(CheckMode)
    case HallmarkD1Page      => hallmarkD1Routes(CheckMode)
    case HallmarkD1OtherPage => hallmarkD1OtherRoutes(CheckMode)
    case PostcodePage        => _ => id => _ => Some(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(id, CheckMode))

    case WhatIsThisArrangementCalledPage          => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhatIsTheImplementationDatePage          => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhyAreYouReportingThisArrangementNowPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhichExpectedInvolvedCountriesArrangementPage =>
      _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhatIsTheExpectedValueOfThisArrangementPage =>
      _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case WhichNationalProvisionsIsThisArrangementBasedOnPage =>
      _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))
    case GiveDetailsOfThisArrangementPage => _ => id => _ => Some(controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(id))

    case TaxpayerSelectTypePage => selectTypeRoutes(CheckMode)
    case WhatIsTaxpayersStartDateForImplementingArrangementPage =>
      _ => id => _ => Some(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id, None))

    case TaxpayerCheckYourAnswersPage => _ => id => _ => Some(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(id))

    case _ => _ => id => _ => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
  }

  private def hallmarkDRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkDPage, id) match {
      case Some(set) if set.contains(D1) => Some(controllers.hallmarks.routes.HallmarkD1Controller.onPageLoad(id, mode))
      case _                             => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
    }

  private def hallmarkD1Routes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(HallmarkD1Page, id) match {
      case Some(set) if set.contains(D1other) => Some(controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(id, mode))
      case _                                  => Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))
    }

  private def hallmarkD1OtherRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    Some(controllers.hallmarks.routes.CheckYourAnswersHallmarksController.onPageLoad(id))

  private def selectTypeRoutes(mode: Mode)(ua: UserAnswers)(id: Int)(request: Request[AnyContent]): Option[Call] =
    ua.get(TaxpayerSelectTypePage, id) map {
      case Organisation => controllers.organisation.routes.OrganisationNameController.onPageLoad(id, mode)
      case Individual   => controllers.individual.routes.IndividualNameController.onPageLoad(id, mode)
    }

  def nextPage(page: Page, id: Int, mode: Mode, userAnswers: UserAnswers)(implicit request: Request[AnyContent]): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)(id)(request) match {
        case Some(call) => call
        case None       => routes.SessionExpiredController.onPageLoad
      }
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(id)(request) match {
        case Some(call) => call
        case None       => routes.SessionExpiredController.onPageLoad

      }
  }
}
