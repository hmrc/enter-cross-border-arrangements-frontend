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
import connectors.HistoryConnector
import helpers.data.ValidUserAnswersForSubmission.userAnswersForOrganisation
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.{SubmissionDetails, SubmissionHistory}
import org.mockito.ArgumentMatchers.any
import pages.disclosure.DisclosureDetailsPage
import play.api.inject.bind
import repositories.SessionRepository

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MarketableDisclosureServiceSpec extends SpecBase with MockServiceApp {

  val mockHistoryConnector = mock[HistoryConnector]
  val firstDisclosureSubmissionDetailsMarketable = SubmissionDetails("id", LocalDateTime.now(), "test.xml",
    Some("arrangementID"), Some("disclosureID"), "New", initialDisclosureMA = true, "messageRefID")

  val firstDisclosureSubmissionDetailsNotMarketable = SubmissionDetails("id", LocalDateTime.now(), "test.xml",
    Some("arrangementID"), Some("disclosureID"), "New", initialDisclosureMA = false, "messageRefID")

  val submissionHistory = SubmissionHistory(Seq(firstDisclosureSubmissionDetailsMarketable))

  val disclosureDetails = DisclosureDetails(
    disclosureName = "",
    arrangementID = Some("arrangementID"),
    disclosureID = Some("disclosureID"),
    disclosureType = DisclosureType.Dac6add
  )


  "isMarketableService" - {
    "must return true when the arrangement is marketable" in {

    val userAnswers =   userAnswersForOrganisation
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[HistoryConnector].toInstance(mockHistoryConnector),
          bind[SessionRepository].toInstance(mockSessionRepository)
        ).build()

      val service = application.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosureSubmissionDetailsMarketable))

      Await.result(service.isInitialDisclosureMarketable(userAnswers, 0), 10.seconds) mustBe true
    }
    "must return false when the arrangement is not marketable" in {

    val userAnswers =   userAnswersForOrganisation
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[HistoryConnector].toInstance(mockHistoryConnector),
          bind[SessionRepository].toInstance(mockSessionRepository)
        ).build()

      val service = application.injector.instanceOf[MarketableDisclosureService]

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosureSubmissionDetailsNotMarketable))

      Await.result(service.isInitialDisclosureMarketable(userAnswers, 0), 10.seconds) mustBe false
    }
  }
}
