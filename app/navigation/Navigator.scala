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
import models.HallmarkCategories.{CategoryA, CategoryB}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Option[Call] = {
    case HallmarkCategoriesPage => hallmarkCategoryRoutes(NormalMode)
    case HallmarkAPage => _ => Some(routes.MainBenefitTestController.onPageLoad(NormalMode))
    case HallmarkBPage => _ => Some(routes.MainBenefitTestController.onPageLoad(NormalMode))
    case MainBenefitTestPage => mainBenefitTestRoutes(NormalMode)
    case _ => _ => Some(routes.IndexController.onPageLoad())
  }

  private val checkRouteMap: Page => UserAnswers => Option[Call] = {
    case HallmarkCategoriesPage => hallmarkCategoryRoutes(CheckMode)
    case HallmarkAPage => _ => Some(routes.MainBenefitTestController.onPageLoad(CheckMode))
    case HallmarkBPage => _ => Some(routes.MainBenefitTestController.onPageLoad(CheckMode))
    case MainBenefitTestPage => mainBenefitTestRoutes(CheckMode)
    case _ => _ => Some(routes.CheckYourAnswersController.onPageLoad())
  }

  private def hallmarkCategoryRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(HallmarkCategoriesPage) map {
      case set: Set[HallmarkCategories] if set.head == CategoryA =>
        routes.HallmarkAController.onPageLoad(mode)
      case set: Set[HallmarkCategories] if set.head == CategoryB =>
        routes.HallmarkBController.onPageLoad(mode)
    }

  private def mainBenefitTestRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(MainBenefitTestPage) map {
      case true => routes.CheckYourAnswersController.onPageLoad()
      case false => routes.HallmarkCategoriesController.onPageLoad(mode) //TODO - change to there is a problem page
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
