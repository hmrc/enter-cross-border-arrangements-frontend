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

package controllers.taxpayer

import base.{ControllerMockFixtures, SpecBase}
import connectors.CrossBorderArrangementsConnector
import handlers.ErrorHandler
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.{NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.disclosure.DisclosureDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results.InternalServerError
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, status, writeableOf_AnyContentAsEmpty, _}
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.Future

class TaxpayersMarketableArrangementGatewayControllerSpec extends SpecBase with ControllerMockFixtures {

  val id = "ID"

  val httpClient: HttpClient = mock[HttpClient]

  val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]
  val mockErrorHandler                     = mock[ErrorHandler]

  override def beforeEach: Unit = {
    reset(mockCrossBorderArrangementsConnector, mockErrorHandler)
    super.beforeEach
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector), bind[ErrorHandler].toInstance(mockErrorHandler))

  "Marketable Arrangement Gateway Controller" - {

    "must redirect to 'What is {0}'s Implementation Date' page when the arrangement is marketable " - {

      "either from a new arrangement " in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6new,
          initialDisclosureMA = true
        )

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, disclosureDetails)
          .success
          .value

        retrieveUserAnswersData(userAnswers)

        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/implementation-date/0"
      }

      "or from an added arrangement " in {

        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6add,
          arrangementID = Some(id),
          initialDisclosureMA = true
        )

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, disclosureDetails)
          .success
          .value

        retrieveUserAnswersData(userAnswers)

        when(mockCrossBorderArrangementsConnector.isMarketableArrangement(any())(any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/implementation-date/0"
      }

    }

    "must redirect to 'Check your Answers' page when the arrangement is not marketable " - {

      "either from a new arrangement " in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6new,
          initialDisclosureMA = false
        )

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, disclosureDetails)
          .success
          .value

        retrieveUserAnswersData(userAnswers)
        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/check-answers/0"
      }

      "or from an added arrangement " in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6add,
          arrangementID = Some(id),
          initialDisclosureMA = false
        )

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, disclosureDetails)
          .success
          .value

        retrieveUserAnswersData(userAnswers)

        when(mockCrossBorderArrangementsConnector.isMarketableArrangement(any())(any())).thenReturn(Future.successful(false))

        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/check-answers/0"
      }

      "or from a replaced arrangement " in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6rep,
          arrangementID = Some(id),
          disclosureID = Some(id)
        )

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, disclosureDetails)
          .success
          .value

        retrieveUserAnswersData(userAnswers)

        when(mockCrossBorderArrangementsConnector.isMarketableArrangement(any())(any())).thenReturn(Future.successful(false))

        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/check-answers/0"
      }

    }

    "must return server error when connector call returns an error" in {
      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        disclosureType = DisclosureType.Dac6add,
        arrangementID = Some(id),
        initialDisclosureMA = false
      )

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      when(mockCrossBorderArrangementsConnector.isMarketableArrangement(any())(any())).thenReturn(Future.failed(new Exception("Error")))

      when(mockErrorHandler.onServerError(any(), any())).thenReturn(Future.successful(result = InternalServerError))

      val request = FakeRequest(GET, controllers.reporter.taxpayer.routes.ReporterTaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode).url)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }
  }
}
