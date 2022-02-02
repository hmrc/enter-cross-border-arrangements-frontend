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

import controllers.arrangement._
import controllers.mixins.{CheckRoute, DefaultRouting}
import models.{CheckMode, NormalMode}
import pages.Page
import pages.arrangement.ArrangementCheckYourAnswersPage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForArrangement @Inject() () extends AbstractNavigator {

  override val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case ArrangementCheckYourAnswersPage =>
      _ => id => _ => _ => controllers.routes.DisclosureDetailsController.onPageLoad(id)

    case _ =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              checkRoute.mode match {
                case NormalMode => indexRoute
                case CheckMode  => controllers.routes.IndexController.onPageLoad()
              }

  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call =
    _ => _ => id => _ => _ => routes.ArrangementCheckYourAnswersController.onPageLoad(id)

  override private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case DefaultRouting(CheckMode) => routes.ArrangementCheckYourAnswersController.onPageLoad(id)
      case _                         => jumpTo
    }

}
