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

import controllers.disclosure._
import controllers.mixins.{CheckRoute, DefaultRouting}
import models.CheckMode
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import pages.Page
import pages.disclosure.{DisclosureDetailsPage, DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForDisclosure @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case DisclosureNamePage =>
      checkRoute => id => _ => _ => controllers.disclosure.routes.DisclosureTypeController.onPageLoad(id, checkRoute.mode)

    case DisclosureTypePage =>
      checkRoute => id => value => _ => value match {
        case Some(Dac6new) => routes.DisclosureMarketableController.onPageLoad(id, checkRoute.mode)
        case Some(Dac6add) => routes.DisclosureIdentifyArrangementController.onPageLoad(id, checkRoute.mode)
        case _             => routes.DisclosureMarketableController.onPageLoad(id, checkRoute.mode) //TODO - redirect to which disclosure do you want to replace/delete page when built
      }

    case DisclosureMarketablePage =>
      _ => id => _ => _ => routes.DisclosureCheckYourAnswersController.onPageLoad(id)

    case DisclosureIdentifyArrangementPage =>
      _ => id => _ => _ => controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad(id)

    case DisclosureDetailsPage =>
      _ => id => _ => _ => controllers.routes.DisclosureDetailsController.onPageLoad(???)
  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call = _ =>
    _ => _ => _ => _ => controllers.routes.IndexController.onPageLoad() //TODO - change when CYA page built

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case DefaultRouting(CheckMode)               => controllers.routes.IndexController.onPageLoad() //TODO - change when CYA page built
      case _                                       => jumpTo
    }
  }

}
