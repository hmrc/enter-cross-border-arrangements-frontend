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

package controllers.reporter.taxpayer

import base.SpecBase
import connectors.CrossBorderArrangementsConnector
import models.disclosure.DisclosureType
import models.{NormalMode, UserAnswers}
import org.scalatestplus.mockito.MockitoSugar
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureTypePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, status, writeableOf_AnyContentAsEmpty, _}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.Future

class ReportTaxpayersMarketableArrangementGatewayControllerSpec extends SpecBase with MockitoSugar {

  val id = "ID"

  val httpClient: HttpClient = mock[HttpClient]

  import scala.concurrent.ExecutionContext.Implicits.global

  def connectorStub(isMarketable: Boolean): CrossBorderArrangementsConnector = new CrossBorderArrangementsConnector(frontendAppConfig, httpClient) {

    override def isMarketableArrangement(arrangementId: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
      arrangementId must be (id)
      Future.successful(isMarketable)
    }
  }

  "Marketable Arrangement Gateway Controller" - {

    "must redirect to 'What is {0}'s Implementation Date' page when the arrangement is marketable " - {

      "either from a new arrangement " in {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, DisclosureType.Dac6new)
          .success.value
          .set(DisclosureMarketablePage, true)
          .success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/taxpayers/implementation-date"

        application.stop()
      }

      "or from an added arrangement " in {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, DisclosureType.Dac6add)
          .success.value
          .set(DisclosureIdentifyArrangementPage, id)
          .success.value
          .set(DisclosureMarketablePage, true)
          .success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CrossBorderArrangementsConnector].toInstance(connectorStub(true))
          )
          .build()

        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/taxpayers/implementation-date"

        application.stop()
      }

  }

    "must redirect to 'Check your Answers' page when the arrangement is not marketable " - {

      "either from a new arrangement " in {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, DisclosureType.Dac6new)
          .success.value
          .set(DisclosureMarketablePage, false)
          .success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/taxpayers/check-answers"

        application.stop()
      }

      "or from an added arrangement " in {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, DisclosureType.Dac6add)
          .success.value
          .set(DisclosureIdentifyArrangementPage, id)
          .success.value
          .set(DisclosureMarketablePage, false)
          .success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CrossBorderArrangementsConnector].toInstance(connectorStub(false))
          )
          .build()

        val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/taxpayers/check-answers"

        application.stop()}

    }
  }
}
