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

import base.SpecBase
import generators.Generators
import models.disclosure.DisclosureType
import models.{NormalMode, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureNamePage, DisclosureTypePage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForDisclosureSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "NavigatorForDisclosure" - {

    "in Normal Mode" - {

      "must go from 'Provide a name for this disclosure' page " +
        "to 'What type of disclosure would you like to make?' page " +
        "when a disclosure name is entered" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            NavigatorForDisclosure.nextPage(DisclosureNamePage, NormalMode, answers.get(DisclosureNamePage))
              .mustBe(controllers.disclosure.routes.DisclosureTypeController.onPageLoad(NormalMode))
        }
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'Is this a marketable arrangement?' page" +
        "when 'A NEW ARRANGEMENT' option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DisclosureTypePage, DisclosureType.Dac6new).success.value

            NavigatorForDisclosure.nextPage(DisclosureTypePage, NormalMode, updatedAnswers.get(DisclosureTypePage))
              .mustBe(controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(NormalMode))

        }
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'What is the arrangement ID?' page" +
        "when 'AN ADDITION TO AN EXISTING ARRANGEMENT' option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DisclosureTypePage, DisclosureType.Dac6add).success.value

            NavigatorForDisclosure.nextPage(DisclosureTypePage, NormalMode, updatedAnswers.get(DisclosureTypePage))
              .mustBe(controllers.disclosure.routes.DisclosureIdentifyArrangementController.onPageLoad(NormalMode))

        }
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'Which disclosure do you want to replace?' page" +
        "when 'A REPLACEMENT OF AN EXISTING DISCLOSURE' option is selected" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DisclosureTypePage, DisclosureType.Dac6rep).success.value

            //TODO - Redirect to replace disclosure when page is built
            NavigatorForDisclosure.nextPage(DisclosureTypePage, NormalMode, updatedAnswers.get(DisclosureTypePage))
              .mustBe(controllers.disclosure.routes.DisclosureTypeController.onPageLoad(NormalMode))

        }
      }

      "must go from 'What type of disclosure would you like to make?' page" +
        "to 'Which disclosure do you want to delete?' page" +
        "when 'A DELETION OF AN EXISTING DISCLOSURE' option is selected" ignore {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DisclosureTypePage, DisclosureType.Dac6del).success.value

            //TODO - Redirect to delete disclosure when page is built
            NavigatorForDisclosure.nextPage(DisclosureTypePage, NormalMode, updatedAnswers.get(DisclosureTypePage))
              .mustBe(controllers.disclosure.routes.DisclosureTypeController.onPageLoad(NormalMode))

        }
      }

      "must go from 'What is the arrangement ID for this disclosure?' page" +
        "to '???' page" +
        "when 'AN ADDITION TO AN EXISTING ARRANGEMENT' option is selected" in { //TODO Redirect to correct page when ready

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers.set(DisclosureIdentifyArrangementPage, "FRA20210101ABC123").success.value

            NavigatorForDisclosure.nextPage(DisclosureIdentifyArrangementPage, NormalMode, updatedAnswers.get(DisclosureIdentifyArrangementPage))
              .mustBe(controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(NormalMode))

        }
      }

    }
  }
}
