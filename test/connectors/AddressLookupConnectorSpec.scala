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

package connectors

import base.{MockServiceApp, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import generators.Generators
import helpers.WireMockServerHandler
import models.AddressLookup
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global

class AddressLookupConnectorSpec extends SpecBase
  with MockServiceApp
  with WireMockServerHandler
  with Generators
  with ScalaCheckPropertyChecks {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.address-lookup.port" -> server.port()
    )

  lazy val connector: AddressLookupConnector = app.injector.instanceOf[AddressLookupConnector]
  val addressLookupUrl = "/v2/uk/addresses?postcode=ZZ1+1ZZ"
  val postcode: String = "ZZ1 1ZZ"

  def addressJson: String =
    s"""[{
       |  "id": "GB200000698110",
       |  "uprn": 200000706253,
       |  "address": {
       |     "lines": [
       |         "1 Address line 1 Road",
       |         "Address line 2 Road"
       |     ],
       |     "town": "Town",
       |     "county": "County",
       |     "postcode": "$postcode",
       |     "subdivision": {
       |         "code": "GB-ENG",
       |         "name": "England"
       |     },
       |     "country": {
       |         "code": "UK",
       |         "name": "United Kingdom"
       |     }
       |  },
       |  "localCustodian": {
       |      "code": 1760,
       |      "name": "Test Valley"
       |  },
       |  "location": [
       |      50.9986451,
       |      -1.4690977
       |  ],
       |  "language": "en"
       |}]""".stripMargin

  "AddressLookupConnector" - {
    "when calling addressLookupByPostcode" - {
      "must return 200 (OK) status and an empty list if no match found" in {
        stubResponse(addressLookupUrl, OK, "[]")

        val result = connector.addressLookupByPostcode(postcode)
        result.futureValue mustBe Nil
      }

      "must return 200 (OK) status for submission of valid postcode" in {

        stubResponse(addressLookupUrl, OK, addressJson)

        val addressLookupResult = Seq(
          AddressLookup(Some("1 Address line 1 Road"), Some("Address line 2 Road"), None, None, "Town", Some("County"), postcode)
        )

        val result = connector.addressLookupByPostcode(postcode)
        result.futureValue mustBe addressLookupResult
      }

      "must throw an exception when address lookup returns a 400 (BAD_REQUEST) status" in {
        stubResponse(addressLookupUrl, BAD_REQUEST, "Some error")

        val result = connector.addressLookupByPostcode(postcode)

        assertThrows[Exception] {
          result.futureValue
        }
      }

      "must throw an exception when address lookup returns a 404 (NOT_FOUND) status" in {
        stubResponse(addressLookupUrl, NOT_FOUND, "Some error")

        val result = connector.addressLookupByPostcode(postcode)

        assertThrows[Exception] {
          result.futureValue
        }
      }

      "must throw an exception when address lookup returns a 405 (METHOD_NOT_ALLOWED) status" in {
        stubResponse(addressLookupUrl, METHOD_NOT_ALLOWED, "Some error")

        val result = connector.addressLookupByPostcode(postcode)

        assertThrows[Exception] {
          result.futureValue
        }
      }

      "must throw an exception when address lookup returns a 500 (INTERNAL_SERVER_ERROR) status" in {
        stubResponse(addressLookupUrl, INTERNAL_SERVER_ERROR, "Some error")

        val result = connector.addressLookupByPostcode(postcode)

        assertThrows[Exception] {
          result.futureValue
        }
      }
    }
  }

  private def stubResponse(expectedUrl: String, expectedStatus: Int, expectedBody: String): StubMapping =
    server.stubFor(
      get(urlEqualTo(expectedUrl))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )
}
