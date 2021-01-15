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

import base.SpecBase
import controllers.mixins.DefaultRouting
import generators.Generators
import models.NormalMode
import models.disclosure.DisclosureType
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.disclosure._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForDisclosureSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForDisclosure
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "NavigatorForDisclosure" - {

    "in Normal Mode" - {

      "must go from 'Provide a name for this disclosure' page " +
        "to 'What type of disclosure would you like to make?' page " +
        "when a disclosure name is entered" in {

          navigator.routeMap(DisclosureNamePage)(DefaultRouting(NormalMode))(Some("Disclosure Name"))(0)
            .mustBe(controllers.disclosure.routes.DisclosureTypeController.onPageLoad(NormalMode))
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'Is this a marketable arrangement?' page" +
        "when 'A NEW ARRANGEMENT' option is selected" in {

          navigator.routeMap(DisclosureTypePage)(DefaultRouting(NormalMode))(Some(DisclosureType.Dac6new))(0)
            .mustBe(controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(NormalMode))
      }


      "must go from 'Is this a marketable arrangement?' page" +
        "to 'Disclosure check your answers' page" +
        "when an option is selected" in {

        navigator.routeMap(DisclosureMarketablePage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad())
        }
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'What is the arrangement ID?' page" +
        "when 'AN ADDITION TO AN EXISTING ARRANGEMENT' option is selected" in {

          navigator.routeMap(DisclosureTypePage)(DefaultRouting(NormalMode))(Some(DisclosureType.Dac6add))(0)
            .mustBe(controllers.disclosure.routes.DisclosureIdentifyArrangementController.onPageLoad(NormalMode))
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'Which disclosure do you want to replace?' page" +
        "when 'A REPLACEMENT OF AN EXISTING DISCLOSURE' option is selected" ignore {

          //TODO - Redirect to replace disclosure when page is built
          navigator.routeMap(DisclosureTypePage)(DefaultRouting(NormalMode))(Some(DisclosureType.Dac6rep))(0)
            .mustBe(controllers.disclosure.routes.DisclosureTypeController.onPageLoad(NormalMode))
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'Which disclosure do you want to delete?' page" +
        "when 'A DELETION OF AN EXISTING DISCLOSURE' option is selected" ignore {

          //TODO - Redirect to delete disclosure when page is built
          navigator.routeMap(DisclosureTypePage)(DefaultRouting(NormalMode))(Some(DisclosureType.Dac6del))(0)
            .mustBe(controllers.disclosure.routes.DisclosureTypeController.onPageLoad(NormalMode))
      }

      "must go from 'What is the arrangement ID for this disclosure?' page" +
        "to 'Disclosure check your answers' page" +
        "when an arrangement ID is entered" in {

          navigator.routeMap(DisclosureIdentifyArrangementPage)(DefaultRouting(NormalMode))(Some("FRA20210101ABC123"))(0)
            .mustBe(controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad())
      }

    "must go from 'Disclosure check your answers' page" +
      "to 'Task list' page" in {

      navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(None)(0)
        .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad())
    }
  }
}
