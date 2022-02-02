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

import base.SpecBase
import controllers.mixins.TaxpayersRouting
import generators.Generators
import models.NormalMode
import models.taxpayer.UpdateTaxpayer
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.taxpayer.UpdateTaxpayerPage

class NavigatorForTaxpayerSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForTaxpayer

  "NavigatorForTaxpayers" - {

    "must go from 'You have not added any taxpayers' page to " +
      "'Is this an organisation or an individual?' if answer is yes" in {
        navigator
          .routeMap(UpdateTaxpayerPage)(TaxpayersRouting(NormalMode))(0)(Some(UpdateTaxpayer.Now))(0)
          .mustBe(controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(0, NormalMode))
      }

    "must go from 'You have not added any taxpayers' page to " +
      "'Task List page' if answer is 'No'" in {
        navigator
          .routeMap(UpdateTaxpayerPage)(TaxpayersRouting(NormalMode))(0)(Some(UpdateTaxpayer.No))(0)
          .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad(0))
      }

    "must go from 'You have not added any taxpayers' page to " +
      "'Task List page' if answer is 'Add Later'" in {
        navigator
          .routeMap(UpdateTaxpayerPage)(TaxpayersRouting(NormalMode))(0)(Some(UpdateTaxpayer.Later))(0)
          .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad(0))
      }

  }
}
