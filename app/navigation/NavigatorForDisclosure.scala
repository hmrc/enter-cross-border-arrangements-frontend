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

import models.Mode
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import pages.Page
import pages.disclosure._
import play.api.mvc.Call

object NavigatorForDisclosure extends AbstractNavigator {

  override val checkYourAnswersRoute: Call = controllers.routes.IndexController.onPageLoad() //TODO - change when CYA page built

  private[navigation] val routeMap: Page => Mode => Option[Any] => Int => Call = {

    case DisclosureNamePage => mode => value => _ => controllers.disclosure.routes.DisclosureTypeController.onPageLoad(mode)

    case DisclosureTypePage => mode => value => _ => value match {
      case Some(Dac6new) => controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(mode)
      case Some(Dac6add) => controllers.disclosure.routes.DisclosureIdentifyArrangementController.onPageLoad(mode)
      case _ => controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(mode) //TODO - redirect to which disclosure do you want to replace/delete page when built
    }

    case DisclosureMarketablePage => mode => value => _ => controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(mode)
    case DisclosureIdentifyArrangementPage => mode => _ => _ => controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(mode)

  }


  private[navigation] val alternativeRouteMap: Page => Call = {
    case _ => checkYourAnswersRoute

  }
}
