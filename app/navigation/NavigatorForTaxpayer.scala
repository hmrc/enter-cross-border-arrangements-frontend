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

import controllers.taxpayer._
import controllers.mixins.{CheckRoute, DefaultRouting}
import models.{CheckMode, NormalMode}
import pages.Page
import pages.disclosure.DisclosureMarketablePage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForTaxpayer @Inject()() extends AbstractNavigator { //DisclosureMarketablePage

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case DisclosureMarketablePage =>
      checkRoute => value => _ =>
        value match {
          case Some(true) => controllers.routes.IndexController.onPageLoad() //TODO - change when implementation date page built
          case _ => jumpOrCheckYourAnswers(routes.TaxpayersCheckYourAnswersController.onPageLoad(), checkRoute)
        }

    case _ =>
      checkRoute => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => controllers.routes.IndexController.onPageLoad()
      }

  }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.TaxpayersCheckYourAnswersController.onPageLoad()

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case DefaultRouting(CheckMode)               => routes.TaxpayersCheckYourAnswersController.onPageLoad()
      case _                                       => jumpTo
    }
  }

}
