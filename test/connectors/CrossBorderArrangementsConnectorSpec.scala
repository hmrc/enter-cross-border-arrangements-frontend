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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import generators.Generators
import org.scalacheck.Gen.alphaStr
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{NOT_FOUND, NO_CONTENT}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

class CrossBorderArrangementsConnectorSpec extends SpecBase
  with ScalaCheckPropertyChecks
  with WireMockHelper
  with Generators {

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.cross-border-arrangements.port" -> server.port()
    ).build()

  lazy val connector: CrossBorderArrangementsConnector = injector.instanceOf[CrossBorderArrangementsConnector]

  "CrossBorderArrangementsConnector" - {

    "calling verifyArrangementId" - {
      "should return true if arrangement id is valid and was created by HMRC" in {
        forAll(validArrangementID) {
          id =>
            server.stubFor(
              get(urlEqualTo(s"/disclose-cross-border-arrangements/verify-arrangement-id/$id"))
                .willReturn(
                  aResponse()
                    .withStatus(NO_CONTENT)
                )
            )

            whenReady(connector.verifyArrangementId(id)){
              result =>
                result mustBe true
            }
        }
      }

      "should return false if arrangement id wasn't created by HMRC" in {
        forAll(alphaStr) {
          invalidID =>
            server.stubFor(
              get(urlEqualTo(s"/disclose-cross-border-arrangements/verify-arrangement-id/$invalidID"))
                .willReturn(
                  aResponse()
                    .withStatus(NOT_FOUND)
                )
            )

            whenReady(connector.verifyArrangementId(invalidID)){
              result =>
                result mustBe false
            }
        }
      }
    }
  }
}
