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

import models.{CheckMode, Mode, NormalMode}
import pages.Page
import play.api.mvc.Call

abstract class AbstractNavigator {

  private[navigation] val routeMap:  Page => Mode => Option[Any] => Int => Call
  private[navigation] val alternativeRouteMap: Page => Call

  def nextPage[A](page: Page, mode: Mode, value: Option[A], index: Int = 0, alternative: Boolean = false): Call = {
    if (alternative) {
      alternativeRouteMap(page)
    }
    else {
      routeMap(page)(mode)(value)(index)
    }
  }

  private[navigation] def orCheckYourAnswers(mode: Mode, route: Call): Call =
    mode match {
      case NormalMode => route
      case CheckMode  => checkYourAnswersRoute
    }

  val indexRoute = controllers.routes.IndexController.onPageLoad()

  val checkYourAnswersRoute: Call

}
