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

import config.FrontendAppConfig
import controllers.disclosure._
import controllers.mixins.CheckRoute
import models.disclosure.DisclosureType.{Dac6add, Dac6del, Dac6new, Dac6rep}
import pages.Page
import pages.disclosure.{DisclosureDetailsPage, DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage, RemoveDisclosurePage, ReplaceOrDeleteADisclosurePage, _}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForDisclosure @Inject()(appConfig: FrontendAppConfig) {

  val routeMap:  Page => CheckRoute => Option[Int] => Option[Any] => Int => Call = {

    case DisclosureNamePage =>
      checkRoute => _ => _ => _ => controllers.disclosure.routes.DisclosureTypeController.onPageLoad(checkRoute.mode)

    case DisclosureTypePage =>
      checkRoute => _ => value => _ => value match {
        case Some(Dac6new) => routes.DisclosureMarketableController.onPageLoad(checkRoute.mode)
        case Some(Dac6add) => routes.DisclosureIdentifyArrangementController.onPageLoad(checkRoute.mode)
        case _             => routes.ReplaceOrDeleteADisclosureController.onPageLoad(checkRoute.mode)
      }

    case RemoveDisclosurePage  =>
      _ => _ => value => _ => value match {
        case Some(true) => controllers.unsubmitted.routes.UnsubmittedDisclosureController.onPageLoad()
        case _          => Call("GET", appConfig.discloseArrangeLink)
      }

    case ReplaceOrDeleteADisclosurePage =>
      _ => _ => disclosureType => _ => disclosureType match {
        case None | Some(Dac6rep) => routes.DisclosureCheckYourAnswersController.onPageLoad()
        case Some(Dac6del)        => routes.DisclosureDeleteCheckYourAnswersController.onPageLoad()
      }

    case DisclosureMarketablePage =>
      _ => _ => _ => _ => routes.DisclosureCheckYourAnswersController.onPageLoad()

    case DisclosureIdentifyArrangementPage =>//
      _ => _ => _ => _ => controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad()

    case DisclosureDetailsPage =>
      _ => _ => _ => _ => controllers.unsubmitted.routes.UnsubmittedDisclosureController.onPageLoad()

    case DisclosureCheckYourAnswersPage =>
      _ => id => _ => _ => id match {
        case Some(n) => controllers.routes.DisclosureDetailsController.onPageLoad(n)
        case None => controllers.routes.IndexController.onPageLoad()
      }

    case  DisclosureDeleteCheckYourAnswersPage  =>
    _ =>  _ => _ => _ => controllers.confirmation.routes.YourDisclosureHasBeenDeletedController.onPageLoad()
  }

  val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call =
    _ => _ => _ => _ => controllers.routes.IndexController.onPageLoad()

}
