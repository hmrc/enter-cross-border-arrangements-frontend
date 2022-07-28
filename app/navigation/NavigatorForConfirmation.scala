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

import controllers.confirmation._
import controllers.mixins.{CheckRoute, DefaultRouting}
import models.CheckMode
import models.disclosure.DisclosureType.{Dac6add, Dac6del, Dac6new, Dac6rep}
import pages.Page
import pages.disclosure.DisclosureDetailsPage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForConfirmation @Inject() () extends AbstractNavigator {

  override val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case DisclosureDetailsPage =>
      _ =>
        id =>
          value =>
            _ =>
              value match {
                case Some(Dac6new)  => routes.NewDisclosureConfirmationController.onPageLoad(id)
                case Some(Dac6add)  => routes.AdditionalDisclosureConfirmationController.onPageLoad(id)
                case Some(Dac6rep)  => routes.ReplacementDisclosureConfirmationController.onPageLoad(id)
                case Some(Dac6del)  => routes.YourDisclosureHasBeenDeletedController.onPageLoad()
                case disclosureType => throw new IllegalStateException(s"Navigation to $disclosureType not yet implemented")
              }
  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call = _ => _ => _ => _ => _ => controllers.routes.IndexController.onPageLoad

  override private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case DefaultRouting(CheckMode) => controllers.routes.IndexController.onPageLoad
      case _                         => jumpTo
    }

}
