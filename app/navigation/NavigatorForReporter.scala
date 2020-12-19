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

import models._
import pages._
import pages.reporter.RoleInArrangementPage
import play.api.mvc.Call


object NavigatorForReporter extends AbstractNavigator {

  override val checkYourAnswersRoute: Call = controllers.routes.IndexController.onPageLoad() //TODO - change when CYA page built

  private[navigation] val routeMap: Page => Mode => Option[Any] => Int => Call = {

    case RoleInArrangementPage => _ => _ => _ => controllers.routes.IndexController.onPageLoad() //TODO - change when next page built

    case _ => mode => _ => _ => mode match {
        case NormalMode => indexRoute
        case CheckMode  => checkYourAnswersRoute
      }

  }

  private[navigation] val alternativeRouteMap: Page => Call = {

    case _ => checkYourAnswersRoute

  }

}
