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

package helpers

import base.SpecBase
import generators.Generators
import helpers.TaskListHelper._
import models.UserAnswers
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureMarketablePage, DisclosureStatusPage, DisclosureTypePage}
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.ReporterStatusPage
import pages.taxpayer.RelevantTaxpayerStatusPage
import uk.gov.hmrc.viewmodels.Html

class TaskListHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val mockUrl = "home.gov.uk"
  val mockAltURL = "notHome.gov.uk"
  val mockLinkContent = "some link"

  "TaskListHelper" - {

    "taskListItemRestricted" - {

      "must return html for a restricted row with grey status on taskList page" in {

        taskListItemRestricted(mockLinkContent, "restricted") mustBe Html(s"" +
          s"<li class='app-task-list__item'><a class='app-task-list__task-name' aria-describedby='restricted'> $mockLinkContent</a>" +
          s"<strong class='govuk-tag govuk-tag--grey app-task-list__task-completed' id='section-restricted'>Cannot start</strong> </li>")
      }
    }

    "taskListHtmlProvider" - {

      "must return html for a standard row with blue status on taskList page" in {

        taskListHtmlProvider(mockUrl, "Completed", mockLinkContent, "completed", "link") mustBe Html(s"" +
          s"<li class='app-task-list__item'><a class='app-task-list__task-name' href='$mockUrl' aria-describedby='link'> $mockLinkContent</a>" +
          s"<strong class='govuk-tag app-task-list__task-completed' id='completed'>Completed</strong> </li>")
      }
    }

    "retrieveRowWithStatus" - {

      "must return html for a row with COMPLETED status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, Completed)
          .success
          .value

        retrieveRowWithStatus(userAnswers, ReporterStatusPage, mockUrl, mockLinkContent, "reporter", "aria") mustBe Html(s"" +
          s"<li class='app-task-list__item'><a class='app-task-list__task-name' href='$mockUrl' aria-describedby='aria'> $mockLinkContent</a>" +
          s"<strong class='govuk-tag app-task-list__task-completed' id='reporter-completed'>Completed</strong> </li>")
      }

      "must return html for a row with IN PROGRESS status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, InProgress)
          .success
          .value

        retrieveRowWithStatus(userAnswers, ReporterStatusPage, mockUrl, mockLinkContent, "reporter", "aria") mustBe Html(s"" +
          s"<li class='app-task-list__item'><a class='app-task-list__task-name' href='$mockUrl' aria-describedby='aria'> $mockLinkContent</a>" +
          s"<strong class='govuk-tag app-task-list__task-completed' id='reporter-inProgress'>In Progress</strong> </li>")
      }


      "must return html for a row with NOT STARTED status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, NotStarted)
          .success
          .value

        retrieveRowWithStatus(userAnswers, ReporterStatusPage, mockUrl, mockLinkContent, "reporter", "aria") mustBe Html(s"" +
          s"<li class='app-task-list__item'><a class='app-task-list__task-name' href='$mockUrl' aria-describedby='aria'> $mockLinkContent</a>" +
          s"<strong class='govuk-tag app-task-list__task-completed' id='reporter-notStarted'>Not Started</strong> </li>")
      }
    }

    "haveAllJourneysBeenCompleted" - {

      "must return TRUE when all Journeys are COMPLETED status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, Completed)
          .success
          .value
          .set(DisclosureStatusPage, Completed)
          .success
          .value

        val listOfPages = Seq(ReporterStatusPage, DisclosureStatusPage)

        haveAllJourneysBeenCompleted(listOfPages, userAnswers) mustBe true

      }

      "must return FALSE when all Journeys are NOT COMPLETE status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, InProgress)
          .success
          .value
          .set(DisclosureStatusPage, Completed)
          .success
          .value

        val listOfPages = Seq(ReporterStatusPage, DisclosureStatusPage)

        haveAllJourneysBeenCompleted(listOfPages, userAnswers) mustBe false

      }
    }

    "startJourneyOrCya" - {

      "must return Alternative URL (cya url) when relevant Journey status is COMPLETE" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, Completed)
          .success
          .value

        startJourneyOrCya(userAnswers, ReporterStatusPage, mockUrl, mockAltURL) mustBe mockAltURL
      }

      "must return standard URL (journey start url) when relevant Journey status is NOT COMPLETE" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterStatusPage, InProgress)
          .success
          .value

        startJourneyOrCya(userAnswers, ReporterStatusPage, mockUrl, mockAltURL) mustBe mockUrl
      }
    }

    "userCanSubmit" - {

      "must be true if user is doing ADDITIONAL DISCLOSURE & IS MARKETABLE and " +
        "has NOT started HALLMARKS or ARRANGEMENT DETAILS journey" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6add)
          .success
          .value
          .set(DisclosureMarketablePage, true)
          .success
          .value
          .set(ReporterStatusPage, Completed)
          .success
          .value
          .set(RelevantTaxpayerStatusPage, Completed)
          .success
          .value
          .set(IntermediariesStatusPage, Completed)
          .success
          .value
          .set(DisclosureStatusPage, Completed)
          .success
          .value
          .set(HallmarkStatusPage, NotStarted)
          .success
          .value
          .set(ArrangementStatusPage, NotStarted)
          .success
          .value

        userCanSubmit(userAnswers) mustBe true
      }

      "must be true if user is doing ANY DISCLOSURE & has COMPLETED " +
        "all relevant journeys" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6new)
          .success
          .value
          .set(DisclosureMarketablePage, true)
          .success
          .value
          .set(ReporterStatusPage, Completed)
          .success
          .value
          .set(RelevantTaxpayerStatusPage, Completed)
          .success
          .value
          .set(IntermediariesStatusPage, Completed)
          .success
          .value
          .set(DisclosureStatusPage, Completed)
          .success
          .value
          .set(HallmarkStatusPage, Completed)
          .success
          .value
          .set(ArrangementStatusPage, Completed)
          .success
          .value

        userCanSubmit(userAnswers) mustBe true
      }

      "must be false if user is doing any other DISCLOSURE combination & has " +
        "NOT COMPLETED all relevant journeys" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6new)
          .success
          .value
          .set(DisclosureMarketablePage, false)
          .success
          .value
          .set(ReporterStatusPage, Completed)
          .success
          .value
          .set(RelevantTaxpayerStatusPage, Completed)
          .success
          .value
          .set(IntermediariesStatusPage, Completed)
          .success
          .value
          .set(DisclosureStatusPage, Completed)
          .success
          .value
          .set(HallmarkStatusPage, NotStarted)
          .success
          .value
          .set(ArrangementStatusPage, NotStarted)
          .success
          .value

        userCanSubmit(userAnswers) mustBe false
      }
    }

    "displaySectionOptional" - {

      "must return string '(optional)' when user is disclosing an ADDITIONAL arrangement " +
        "and initialMA flag is true" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6add)
          .success
          .value
          .set(DisclosureMarketablePage, true)
          .success
          .value

        displaySectionOptional(userAnswers) mustBe "disclosureDetails.optional"
      }

      "must return an empty string when user is disclosing an other arrangement combo" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6new)
          .success
          .value
          .set(DisclosureMarketablePage, false)
          .success
          .value

        displaySectionOptional(userAnswers) mustBe ""
      }
    }

  }
}
