/*
 * Copyright 2023 HM Revenue & Customs
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

import base.{ControllerMockFixtures, SpecBase}
import generators.Generators
import helpers.TaskListHelper._
import models.disclosure.DisclosureType.{Dac6add, Dac6new, Dac6rep}
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted, Restricted}
import models.reporter.RoleInArrangement
import models.{UnsubmittedDisclosure, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureDetailsPage, DisclosureMarketablePage, DisclosureStatusPage, DisclosureTypePage}
import pages.enterprises.AssociatedEnterpriseStatusPage
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.{ReporterStatusPage, RoleInArrangementPage}
import pages.taxpayer.RelevantTaxpayerStatusPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import uk.gov.hmrc.viewmodels.Html

class TaskListHelperSpec extends ControllerMockFixtures with SpecBase with ScalaCheckPropertyChecks with Generators {

  val mockUrl                   = "home.gov.uk"
  val mockAltURL                = "notHome.gov.uk"
  val mockLinkContent           = "some link"
  val index                     = 0
  val mockDisclosure            = DisclosureDetails("name", DisclosureType.Dac6new, Some("123"), Some("321"), initialDisclosureMA = true, Some("messageRefID"))
  val mockUnsubmittedDisclosure = UnsubmittedDisclosure("12", "name")

  "TaskListHelper" - {

    "taskListItemProvider with restriction" - {

      "must return html for a restricted row with grey status on taskList page" in {

        taskListItemProvider(None,
                             JourneyStatus.Restricted.toString,
                             mockLinkContent,
                             "section-restricted",
                             "restricted",
                             "item",
                             "govuk-tag govuk-tag--grey"
        ) mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name'  aria-describedby='restricted'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag govuk-tag--grey app-task-list__task-completed' id='section-restricted'>Cannot start</strong></li>"
        )
      }

      "must return html for a restricted row with grey status on taskList page with no bottom row line" in {

        taskListItemProvider(None,
                             JourneyStatus.Restricted.toString,
                             mockLinkContent,
                             "section-restricted",
                             "restricted",
                             "bottomless-item",
                             "govuk-tag govuk-tag--grey"
        ) mustBe Html(
          s"" +
            s"<li class='app-task-list__bottomless-item'><a class='app-task-list__task-name'  aria-describedby='restricted'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag govuk-tag--grey app-task-list__task-completed' id='section-restricted'>Cannot start</strong></li>"
        )
      }
    }

    "taskListItemProvider with completion" - {

      "must return html for a row with dark blue status on taskList page" in {

        taskListItemProvider(Some(mockUrl), JourneyStatus.Completed.toString, mockLinkContent, "completed", "link", "item", "govuk-tag") mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='link'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag app-task-list__task-completed' id='completed'>Completed</strong></li>"
        )
      }

      "must return html for a row with blue status on taskList page with no bottom row line" in {

        taskListItemProvider(Some(mockUrl), JourneyStatus.Completed.toString, mockLinkContent, "completed", "link", "bottomless-item", "govuk-tag") mustBe Html(
          s"" +
            s"<li class='app-task-list__bottomless-item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='link'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag app-task-list__task-completed' id='completed'>Completed</strong></li>"
        )
      }
    }

    "taskListItemProvider with in progress" - {

      "must return html for a row with dark light blue status on taskList page" in {

        taskListItemProvider(Some(mockUrl),
                             JourneyStatus.InProgress.toString,
                             mockLinkContent,
                             "completed",
                             "link",
                             "item",
                             "govuk-tag govuk-tag--blue"
        ) mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='link'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag govuk-tag--blue app-task-list__task-completed' id='completed'>In Progress</strong></li>"
        )
      }

      "must return html for a row with light blue status on taskList page with no bottom row line" in {

        taskListItemProvider(Some(mockUrl),
                             JourneyStatus.InProgress.toString,
                             mockLinkContent,
                             "completed",
                             "link",
                             "bottomless-item",
                             "govuk-tag govuk-tag--blue"
        ) mustBe Html(
          s"" +
            s"<li class='app-task-list__bottomless-item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='link'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag govuk-tag--blue app-task-list__task-completed' id='completed'>In Progress</strong></li>"
        )
      }
    }

    "taskListItemProvider with no Journey Link" - {

      "must return html for a row with no journey link but a blue status on taskList page" in {

        taskListItemProvider(None, JourneyStatus.Completed.toString, mockLinkContent, "completed", "link", "item", "govuk-tag") mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name'  aria-describedby='link'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag app-task-list__task-completed' id='completed'>Completed</strong></li>"
        )
      }

      "must return html for a row with no journey link but a blue status on taskList page with no bottom row line" in {

        taskListItemProvider(None, JourneyStatus.Completed.toString, mockLinkContent, "completed", "link", "bottomless-item", "govuk-tag") mustBe Html(
          s"" +
            s"<li class='app-task-list__bottomless-item'><a class='app-task-list__task-name'  aria-describedby='link'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag app-task-list__task-completed' id='completed'>Completed</strong></li>"
        )
      }
    }

    "retrieveRowWithStatus" - {

      "must return html for a row with COMPLETED status" in {

        retrieveRowWithStatus(Completed, Some("home.gov.uk"), mockLinkContent, "reporter", "aria", "item") mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='aria'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag app-task-list__task-completed' id='reporter-completed'>Completed</strong></li>"
        )
      }

      "must return html for a row with IN PROGRESS status" in {

        retrieveRowWithStatus(InProgress, Some("home.gov.uk"), mockLinkContent, "reporter", "aria", "item") mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='aria'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag govuk-tag--blue app-task-list__task-completed' id='reporter-inProgress'>In Progress</strong></li>"
        )
      }

      "must return html for a row with NOT STARTED status" in {

        retrieveRowWithStatus(NotStarted, Some("home.gov.uk"), mockLinkContent, "reporter", "aria", "item") mustBe Html(
          s"" +
            s"<li class='app-task-list__item'><a class='app-task-list__task-name' href=$mockUrl aria-describedby='aria'> $mockLinkContent</a>" +
            s"<strong class='govuk-tag govuk-tag--grey app-task-list__task-completed' id='reporter-notStarted'>Not Started</strong></li>"
        )
      }
    }

    "haveAllJourneysBeenCompleted" - {

      "must return TRUE when all Journeys are COMPLETED status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(ReporterStatusPage, index, Completed)
          .success
          .value
          .set(DisclosureStatusPage, index, Completed)
          .success
          .value

        val listOfPages = Seq(ReporterStatusPage, DisclosureStatusPage)

        haveAllJourneysBeenCompleted(listOfPages, userAnswers, index, mockDisclosure.copy(firstInitialDisclosureMA = Some(false))) mustBe true

      }

      "must return TRUE when mandatory Journeys have a COMPLETED status and optional Journeys are NOT STARTED" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(ReporterStatusPage, index, Completed)
          .success
          .value
          .set(DisclosureStatusPage, index, Completed)
          .success
          .value

        val listOfPages = Seq(ReporterStatusPage, DisclosureStatusPage, HallmarkStatusPage, ArrangementStatusPage)

        haveAllJourneysBeenCompleted(listOfPages, userAnswers, index, mockDisclosure.copy(firstInitialDisclosureMA = Some(true))) mustBe true

      }

      "must return FALSE when mandatory Journeys have a COMPLETED status and optional Journeys are IN PROGRESS" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(ReporterStatusPage, index, Completed)
          .success
          .value
          .set(DisclosureStatusPage, index, Completed)
          .success
          .value
          .set(HallmarkStatusPage, index, InProgress)
          .success
          .value
          .set(ArrangementStatusPage, index, InProgress)
          .success
          .value

        val listOfPages = Seq(ReporterStatusPage, DisclosureStatusPage, HallmarkStatusPage, ArrangementStatusPage)

        haveAllJourneysBeenCompleted(listOfPages, userAnswers, index, mockDisclosure.copy(firstInitialDisclosureMA = Some(true))) mustBe false

      }

      "must return FALSE when all Journeys are NOT COMPLETE status" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(ReporterStatusPage, index, InProgress)
          .success
          .value
          .set(DisclosureStatusPage, index, Completed)
          .success
          .value

        val listOfPages = Seq(ReporterStatusPage, DisclosureStatusPage)

        haveAllJourneysBeenCompleted(listOfPages, userAnswers, index, mockDisclosure.copy(firstInitialDisclosureMA = Some(false))) mustBe false

      }

      "must return false if user is doing a REPLACEMENT of an NEW DISCLOSURE that IS MARKETABLE and " +
        "have NOT started HALLMARKS or ARRANGEMENT DETAILS journey" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(ReporterStatusPage, index, Completed)
            .success
            .value
            .set(RelevantTaxpayerStatusPage, index, Completed)
            .success
            .value
            .set(IntermediariesStatusPage, index, Completed)
            .success
            .value
            .set(AffectedStatusPage, index, Completed)
            .success
            .value
            .set(AssociatedEnterpriseStatusPage, index, Completed)
            .success
            .value
            .set(DisclosureStatusPage, index, Completed)
            .success
            .value
            .set(HallmarkStatusPage, index, NotStarted)
            .success
            .value
            .set(ArrangementStatusPage, index, NotStarted)
            .success
            .value

          val listOfPages = Seq(
            ReporterStatusPage,
            RelevantTaxpayerStatusPage,
            IntermediariesStatusPage,
            AffectedStatusPage,
            AssociatedEnterpriseStatusPage,
            DisclosureStatusPage,
            HallmarkStatusPage,
            ArrangementStatusPage
          )

          haveAllJourneysBeenCompleted(listOfPages,
                                       userAnswers,
                                       index,
                                       mockDisclosure.copy(disclosureType = Dac6rep, initialDisclosureMA = true, firstInitialDisclosureMA = Some(true))
          ) mustBe false
        }
    }

    "hrefToStartJourneyOrCya" - {

      "must return Alternative URL (cya url) when relevant Journey status is COMPLETE" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(ReporterStatusPage, index, Completed)
          .success
          .value

        hrefToStartJourneyOrCya(userAnswers, ReporterStatusPage, mockUrl, mockAltURL, index) mustBe mockAltURL
      }

      "must return standard URL (journey start url) when relevant Journey status is NOT COMPLETE" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(ReporterStatusPage, index, InProgress)
          .success
          .value

        hrefToStartJourneyOrCya(userAnswers, ReporterStatusPage, mockUrl, mockAltURL, index) mustBe mockUrl
      }
    }

    "userCanSubmit" - {

      "must be true if user is doing ADDITIONAL DISCLOSURE & IS MARKETABLE and " +
        "has NOT started HALLMARKS or ARRANGEMENT DETAILS journey" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureDetailsPage, index, mockDisclosure.copy(disclosureType = Dac6add))
            .success
            .value
            .set(ReporterStatusPage, index, Completed)
            .success
            .value
            .set(RelevantTaxpayerStatusPage, index, Completed)
            .success
            .value
            .set(IntermediariesStatusPage, index, Completed)
            .success
            .value
            .set(AffectedStatusPage, index, Completed)
            .success
            .value
            .set(AssociatedEnterpriseStatusPage, index, Completed)
            .success
            .value
            .set(DisclosureStatusPage, index, Completed)
            .success
            .value

          userCanSubmit(userAnswers,
                        index,
                        mockDisclosure.copy(disclosureType = Dac6add, initialDisclosureMA = true, firstInitialDisclosureMA = Some(true))
          ) mustBe true
        }

      "must be true if user is doing REPLACEMENT DISCLOSURE OF ADDITIONAL DISCLOSURE & THAT IS NOT MARKETABLE but original DAC6NEW is marketable and " +
        "has NOT started HALLMARKS or ARRANGEMENT DETAILS journey" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureDetailsPage, index, mockDisclosure.copy(disclosureType = Dac6rep))
            .success
            .value
            .set(ReporterStatusPage, index, Completed)
            .success
            .value
            .set(RelevantTaxpayerStatusPage, index, Completed)
            .success
            .value
            .set(IntermediariesStatusPage, index, Completed)
            .success
            .value
            .set(AffectedStatusPage, index, Completed)
            .success
            .value
            .set(AssociatedEnterpriseStatusPage, index, Completed)
            .success
            .value
            .set(DisclosureStatusPage, index, Completed)
            .success
            .value

          userCanSubmit(userAnswers,
                        index,
                        mockDisclosure.copy(disclosureType = Dac6rep, initialDisclosureMA = false, firstInitialDisclosureMA = Some(true))
          ) mustBe true
        }

      "must be true if user is doing ANY DISCLOSURE & has COMPLETED " +
        "all relevant journeys" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureTypePage, index, Dac6new)
            .success
            .value
            .set(DisclosureMarketablePage, index, true)
            .success
            .value
            .set(ReporterStatusPage, index, Completed)
            .success
            .value
            .set(RelevantTaxpayerStatusPage, index, Completed)
            .success
            .value
            .set(AssociatedEnterpriseStatusPage, index, Completed)
            .success
            .value
            .set(IntermediariesStatusPage, index, Completed)
            .success
            .value
            .set(AffectedStatusPage, index, Completed)
            .success
            .value
            .set(DisclosureStatusPage, index, Completed)
            .success
            .value
            .set(HallmarkStatusPage, index, Completed)
            .success
            .value
            .set(ArrangementStatusPage, index, Completed)
            .success
            .value

          userCanSubmit(userAnswers, index, mockDisclosure) mustBe true
        }

      "must be false if user is doing any other DISCLOSURE combination & has " +
        "NOT COMPLETED all relevant journeys" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureTypePage, index, Dac6new)
            .success
            .value
            .set(DisclosureMarketablePage, index, false)
            .success
            .value
            .set(ReporterStatusPage, index, Completed)
            .success
            .value
            .set(RelevantTaxpayerStatusPage, index, Completed)
            .success
            .value
            .set(IntermediariesStatusPage, index, Completed)
            .success
            .value
            .set(AssociatedEnterpriseStatusPage, index, Completed)
            .success
            .value
            .set(AffectedStatusPage, index, Completed)
            .success
            .value
            .set(DisclosureStatusPage, index, Completed)
            .success
            .value
            .set(HallmarkStatusPage, index, NotStarted)
            .success
            .value
            .set(ArrangementStatusPage, index, NotStarted)
            .success
            .value

          userCanSubmit(userAnswers, index, mockDisclosure.copy(initialDisclosureMA = false)) mustBe false
        }
      "must be true if user has reported as a taxpayer but not added an associated enterprise" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(DisclosureTypePage, index, Dac6new)
          .success
          .value
          .set(DisclosureMarketablePage, index, true)
          .success
          .value
          .set(ReporterStatusPage, index, Completed)
          .success
          .value
          .set(RoleInArrangementPage, index, RoleInArrangement.Taxpayer)
          .success
          .value
          .set(RelevantTaxpayerStatusPage, index, Completed)
          .success
          .value
          .set(AssociatedEnterpriseStatusPage, index, Completed)
          .success
          .value
          .set(IntermediariesStatusPage, index, Completed)
          .success
          .value
          .set(AffectedStatusPage, index, Completed)
          .success
          .value
          .set(DisclosureStatusPage, index, Completed)
          .success
          .value
          .set(HallmarkStatusPage, index, Completed)
          .success
          .value
          .set(ArrangementStatusPage, index, Completed)
          .success
          .value

        userCanSubmit(userAnswers, index, mockDisclosure) mustBe true
      }

      "must be true if user has reported as a Intermediary but no associated enterprise" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
          .success
          .value
          .set(DisclosureTypePage, index, Dac6new)
          .success
          .value
          .set(DisclosureMarketablePage, index, true)
          .success
          .value
          .set(ReporterStatusPage, index, Completed)
          .success
          .value
          .set(RoleInArrangementPage, index, RoleInArrangement.Intermediary)
          .success
          .value
          .set(RelevantTaxpayerStatusPage, index, Completed)
          .success
          .value
          .set(AssociatedEnterpriseStatusPage, index, Restricted)
          .success
          .value
          .set(IntermediariesStatusPage, index, Completed)
          .success
          .value
          .set(AffectedStatusPage, index, Completed)
          .success
          .value
          .set(DisclosureStatusPage, index, Completed)
          .success
          .value
          .set(HallmarkStatusPage, index, Completed)
          .success
          .value
          .set(ArrangementStatusPage, index, Completed)
          .success
          .value

        userCanSubmit(userAnswers, index, mockDisclosure) mustBe true
      }

      "must be false if user is doing ADDITIONAL DISCLOSURE for an initial disclosure that IS MARKETABLE and " +
        "has completed HALLMARKS but not ARRANGEMENT DETAILS journey" in {

          val userAnswers = UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(mockUnsubmittedDisclosure))
            .success
            .value
            .set(DisclosureDetailsPage, index, mockDisclosure.copy(disclosureType = Dac6add))
            .success
            .value
            .set(ReporterStatusPage, index, Completed)
            .success
            .value
            .set(RelevantTaxpayerStatusPage, index, Completed)
            .success
            .value
            .set(IntermediariesStatusPage, index, Completed)
            .success
            .value
            .set(AffectedStatusPage, index, Completed)
            .success
            .value
            .set(AssociatedEnterpriseStatusPage, index, Completed)
            .success
            .value
            .set(DisclosureStatusPage, index, Completed)
            .success
            .value
            .set(HallmarkStatusPage, index, Completed)
            .success
            .value

          userCanSubmit(userAnswers, index, mockDisclosure.copy(disclosureType = Dac6add)) mustBe false
        }
    }

    "displaySectionOptional" - {

      "must return string '(optional)' when user is disclosing an ADDITIONAL arrangement " +
        "and firstInitialDisclosureIsMarketable flag is true" in {

          displaySectionOptional(mockDisclosure.copy(disclosureType = Dac6add, firstInitialDisclosureMA = Some(true))) mustBe "(optional)"
        }

      "must return string '(optional)' when user is submitting a REPLACEMENT disclosure " +
        "of an ADDITIONAL disclosure that was initially marketable" in {

          displaySectionOptional(
            mockDisclosure.copy(disclosureType = Dac6rep, initialDisclosureMA = false, firstInitialDisclosureMA = Some(true))
          ) mustBe "(optional)"
        }

      "must return an empty string when user is disclosing an other arrangement combo" in {

        displaySectionOptional(mockDisclosure.copy(disclosureType = Dac6add, firstInitialDisclosureMA = Some(false))) mustBe ""
      }
    }
  }
}
