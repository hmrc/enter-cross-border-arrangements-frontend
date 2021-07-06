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

import controllers.taxpayer._
import controllers.mixins.{CheckRoute, DefaultRouting}
import models.taxpayer.UpdateTaxpayer.Now
import models.{CheckMode, NormalMode}
import pages.Page
import pages.disclosure.DisclosureMarketablePage
import pages.taxpayer.UpdateTaxpayerPage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForTaxpayer @Inject() () extends AbstractNavigator {

  override val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case UpdateTaxpayerPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(Now) => controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(id, checkRoute.mode)
                case _         => controllers.routes.DisclosureDetailsController.onPageLoad(id)
              }

    case DisclosureMarketablePage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(id, checkRoute.mode)
                case _          => jumpOrCheckYourAnswers(id, routes.TaxpayersCheckYourAnswersController.onPageLoad(id, None), checkRoute)
              }

    case _ =>
      checkRoute =>
        _ =>
          _ =>
            _ =>
              checkRoute.mode match {
                case NormalMode => indexRoute
                case CheckMode  => controllers.routes.IndexController.onPageLoad()
              }

  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call =
    _ => _ => id => _ => _ => routes.TaxpayersCheckYourAnswersController.onPageLoad(id, None)

  override private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case DefaultRouting(CheckMode) => routes.TaxpayersCheckYourAnswersController.onPageLoad(id, None)
      case _                         => jumpTo
    }

}
