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

package services

import base.{MockServiceApp, SpecBase}
import connectors.{CrossBorderArrangementsConnector, HistoryConnector}
import models.disclosure.{DisclosureType, ReplaceOrDeleteADisclosure}
import models.{SubmissionDetails, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.SessionRepository

import java.time.LocalDateTime
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MarketableDisclosureServiceSpec extends SpecBase with MockServiceApp {

  val mockHistoryConnector     = mock[HistoryConnector]
  val mockSessionRepository    = mock[SessionRepository]
  val mockCrossBorderConnector = mock[CrossBorderArrangementsConnector]

  override def beforeEach: Unit = {
    reset(mockHistoryConnector, mockSessionRepository, mockCrossBorderConnector)
    super.beforeEach
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(
      bind[HistoryConnector].toInstance(mockHistoryConnector),
      bind[SessionRepository].toInstance(mockSessionRepository),
      bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderConnector)
    )

  "retrieveAndSetInitialDisclosureMAFlag" - {

    "must return initialDisclosureMA flag from user answers when import instruction DAC6NEW" in {

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6new)
        .success
        .value
        .setBase(DisclosureMarketablePage, true)
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe true
    }

    "must return initialDisclosureMA flag from user answers when import instruction is DAC6DEL" in {

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6del)
        .success
        .value
        .setBase(DisclosureMarketablePage, false)
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe false
    }

    "must return initialDisclosureMA flag 'true' if the disclosure being added to has a flag of 'true' with import instruction DAC6ADD" in {

      val lastPreviousSubmission = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID"),
        importInstruction = "DAC6NEW",
        initialDisclosureMA = true,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA123")
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(lastPreviousSubmission))

      Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe true
    }

    "must return initialDisclosureMA flag 'false' if the additional disclosure has a non UK arrangement ID" in {

      val lastPreviousSubmission = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID"),
        importInstruction = "DAC6NEW",
        initialDisclosureMA = true,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "CZA123")
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(lastPreviousSubmission))

      Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe false
    }

    "must return initialDisclosureMA flag 'false' if the disclosure being added to has a flag of 'false' with import instruction DAC6ADD" in {

      val lastPreviousSubmission = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID"),
        importInstruction = "DAC6NEW",
        initialDisclosureMA = false,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA123")
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(lastPreviousSubmission))

      Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe false
    }

    "must return submissionDetails initial disclosureMA flag as false when user selected import instruction is DAC6REP" +
      "& previous submission is DAC6ADD" in {

        val lastPreviousSubmission = SubmissionDetails(
          enrolmentID = "enrolmentID",
          submissionTime = LocalDateTime.now(),
          fileName = "submission",
          arrangementID = Some("arrangementID"),
          disclosureID = Some("disclosureID"),
          importInstruction = "dac6add",
          initialDisclosureMA = true,
          messageRefId = "messageRefID"
        )

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .setBase(DisclosureTypePage, DisclosureType.Dac6rep)
          .success
          .value
          .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA123", "GBD321"))
          .success
          .value

        val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

        when(mockHistoryConnector.getSubmissionDetailForDisclosure(any())(any()))
          .thenReturn(Future.successful(lastPreviousSubmission))

        Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe false
      }

    "must return submissionDetails initial disclosureMA flag as false when user selected import instruction is DAC6REP" +
      "& arrangement ID is NON GB" in {

        val lastPreviousSubmission = SubmissionDetails(
          enrolmentID = "enrolmentID",
          submissionTime = LocalDateTime.now(),
          fileName = "submission",
          arrangementID = Some("DEA123"),
          disclosureID = Some("GBD321"),
          importInstruction = "dac6add",
          initialDisclosureMA = true,
          messageRefId = "messageRefID"
        )

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .setBase(DisclosureTypePage, DisclosureType.Dac6rep)
          .success
          .value
          .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("DEA123", "GBD321"))
          .success
          .value

        val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

        when(mockHistoryConnector.getSubmissionDetailForDisclosure(any())(any()))
          .thenReturn(Future.successful(lastPreviousSubmission))

        Await.result(marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers), 10.seconds) mustBe false

      }

    "must throw an exception if user has not supplied arrangement id or disclosure id for replacement arrangement" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6rep)
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      val ex = intercept[Exception] {
        marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers)
      }

      ex.getMessage mustBe "Unable to retrieve ids from replace or delete model from userAnswers"
    }

    "must throw an exception if user has not supplied arrangement id for additional arrangement" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      val ex = intercept[Exception] {
        marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers)
      }

      ex.getMessage mustBe "Unable to retrieve arrangement id from userAnswers"
    }

    "must throw an exception if user has not supplied answer for is disclosure marketable for deletion of arrangement" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6del)
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      val ex = intercept[Exception] {
        marketableDisclosureService.retrieveAndSetInitialDisclosureMAFlag(userAnswers)
      }

      ex.getMessage mustBe "Unable to retrieve disclosureMarketable flag from userAnswers"
    }
  }

  "displayOptionalContentInTaskList" - {

    "must return false when completing the first initial disclosure that is marketable" in {

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6new)
        .success
        .value
        .setBase(DisclosureMarketablePage, true)
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      Await.result(marketableDisclosureService.displayOptionalContentInTaskList(userAnswers), 10.seconds) mustBe false
    }

    "must return true when disclosing an additional arrangement and first initial disclosure has MA = true" in {

      val firstDisclosure = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("GBA123"),
        disclosureID = Some("GBD321"),
        importInstruction = "dac6new",
        initialDisclosureMA = true,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA123")
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosure))

      Await.result(marketableDisclosureService.displayOptionalContentInTaskList(userAnswers), 10.seconds) mustBe true

    }

    "must return false when disclosing an additional arrangement and first initial disclosure has MA = false" in {

      val firstDisclosure = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("GBA123"),
        disclosureID = Some("GBD321"),
        importInstruction = "dac6new",
        initialDisclosureMA = false,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA123")
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosure))

      Await.result(marketableDisclosureService.displayOptionalContentInTaskList(userAnswers), 10.seconds) mustBe false

    }

    "must return true when disclosing a replacement arrangement of an additional disclosure and the first initial disclosure has MA = true" in {

      val firstDisclosure = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("CZA123"),
        disclosureID = Some("CZA321"),
        importInstruction = "dac6new",
        initialDisclosureMA = true,
        messageRefId = "messageRefID"
      )

      val lastPreviousDisclosure = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("CZA123"),
        disclosureID = Some("CZA421"),
        importInstruction = "dac6add",
        initialDisclosureMA = false,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6rep)
        .success
        .value
        .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA123", "GBD321"))
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.getSubmissionDetailForDisclosure(any())(any()))
        .thenReturn(Future.successful(lastPreviousDisclosure))

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosure))

      Await.result(marketableDisclosureService.displayOptionalContentInTaskList(userAnswers), 10.seconds) mustBe true

    }

    "must return false when disclosing an additional NonUK arrangement and first initial disclosure has MA = true" in {

      val firstDisclosure = SubmissionDetails(
        enrolmentID = "enrolmentID",
        submissionTime = LocalDateTime.now(),
        fileName = "submission",
        arrangementID = Some("GBA123"),
        disclosureID = Some("GBD321"),
        importInstruction = "dac6new",
        initialDisclosureMA = true,
        messageRefId = "messageRefID"
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "CZA123")
        .success
        .value

      val marketableDisclosureService = app.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosure))

      Await.result(marketableDisclosureService.displayOptionalContentInTaskList(userAnswers), 10.seconds) mustBe true

    }
  }
}
