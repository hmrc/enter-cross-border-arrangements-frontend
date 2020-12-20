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
import models._
import models.reporter.RoleInArrangement
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryExemptionInEUPage, IntermediaryWhyReportInUKPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForReporterSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "NavigatorForReporter" - {

    "in Normal mode" - {

      "must go from 'What is your role in this arrangement?' page " +
        "to 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "when any 'INTERMEDIARY' option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            val updatedAnswers =
              answers
                .set(RoleInArrangementPage, RoleInArrangement.Intermediary)
                .success
                .value

            NavigatorForReporter.nextPage(RoleInArrangementPage, NormalMode, updatedAnswers.get(RoleInArrangementPage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(NormalMode))

        }
      }

      "must go from 'Why are you required to report this arrangement in the United Kingdom?' page " +
        "to 'As an intermediary, what is your role in this arrangement' page " +
        "when any option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            NavigatorForReporter.nextPage(IntermediaryWhyReportInUKPage, NormalMode, answers.get(IntermediaryWhyReportInUKPage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(NormalMode))

        }
      }

      "must go from 'As an intermediary, what is your role in this arrangement' page " +
        "to 'Are you exempt from reporting in any of the EU member states?' page " +
        "when any option is selected" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>

            NavigatorForReporter.nextPage(IntermediaryExemptionInEUPage, NormalMode, answers.get(IntermediaryExemptionInEUPage))
                .mustBe(controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(NormalMode))

        }
      }
    }
  }
}

