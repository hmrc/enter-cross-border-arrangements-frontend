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

import controllers.mixins.{AffectedRouting, AssociatedEnterprisesRouting, CheckRoute, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import models.{CheckMode, NormalMode}
import pages.Page
import play.api.mvc.Call

abstract class AbstractNavigator {

  val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call

  val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call = _ => _ => _ => _ => _ => Call("GET", "/")

  private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode) => controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id, None)
      case TaxpayersRouting(CheckMode)             => controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(id, None)
      case IntermediariesRouting(CheckMode)        => controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(id, None)
      case AffectedRouting(CheckMode)              => controllers.affected.routes.AffectedCheckYourAnswersController.onPageLoad(id, None)
      case DefaultRouting(CheckMode)               => throwRoutingError
      case _                                       => jumpTo
    }

  private[navigation] def continueToParentJourney(id: Int, checkRoute: CheckRoute): Call = checkRoute match {
    case AssociatedEnterprisesRouting(mode) => controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(id, mode)
    case TaxpayersRouting(mode)             => controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(id, mode)
    case IntermediariesRouting(mode)        => controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(id, mode)
    case AffectedRouting(mode)              => controllers.affected.routes.AffectedCheckYourAnswersController.onPageLoad(id, None)
    case _                                  => throwRoutingError
  }

  private[navigation] def throwRoutingError = throw new IllegalStateException("Organisation or Individual journeys must be called from a parent journey")

  val indexRoute: Call = controllers.routes.IndexController.onPageLoad()

}
