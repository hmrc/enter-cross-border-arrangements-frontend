/*
 * Copyright 2022 HM Revenue & Customs
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
import models.GeneratedIDs
import models.disclosure.IDVerificationStatus
import org.mockito.ArgumentMatchers.any
import org.scalacheck.Gen.alphaStr
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, NO_CONTENT, OK}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.{HttpClient, HttpResponse}
import utils.WireMockHelper

import scala.concurrent.Future
import scala.xml.NodeSeq

class CrossBorderArrangementsConnectorSpec extends SpecBase with MockServiceApp with ScalaCheckPropertyChecks with WireMockHelper with Generators {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      "microservice.services.cross-border-arrangements.port" -> server.port()
    )

  lazy val connector: CrossBorderArrangementsConnector = app.injector.instanceOf[CrossBorderArrangementsConnector]

  "CrossBorderArrangementsConnector" - {

    "calling verifyArrangementId" - {
      "should return true if arrangement id is valid and was created by HMRC" in {
        forAll(validArrangementID) {
          id =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-arrangement-id/$id",
              NO_CONTENT
            )

            whenReady(connector.verifyArrangementId(id)) {
              result =>
                result mustBe true
            }
        }
      }

      "should return false if arrangement id wasn't created by HMRC" in {
        forAll(alphaStr) {
          invalidID =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-arrangement-id/$invalidID",
              NOT_FOUND
            )

            whenReady(connector.verifyArrangementId(invalidID)) {
              result =>
                result mustBe false
            }
        }
      }
    }

    "calling verifyDisclosureIDs" - {
      val enrolmentID = "XADAC0001234567"

      "should return true if arrangement and disclosure ids exist and are from the same submission" in {
        forAll(validArrangementID, validDisclosureID) {
          (arrangementID, disclosureID) =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-ids/$arrangementID-$disclosureID-$enrolmentID",
              NO_CONTENT
            )

            whenReady(connector.verifyDisclosureIDs(arrangementID, disclosureID, enrolmentID)) {
              result =>
                result mustBe IDVerificationStatus(isValid = true, IDVerificationStatus.IDsFound)
            }
        }
      }

      "should return false if arrangement id is not found" in {
        forAll(validArrangementID, validDisclosureID) {
          (arrangementID, disclosureID) =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-ids/$arrangementID-$disclosureID-$enrolmentID",
              NOT_FOUND,
              "Arrangement ID not found"
            )

            whenReady(connector.verifyDisclosureIDs(arrangementID, disclosureID, enrolmentID)) {
              result =>
                result mustBe IDVerificationStatus(isValid = false, IDVerificationStatus.ArrangementIDNotFound)
            }
        }
      }

      "should return false if disclosure id is not found for an enrolment id" in {
        forAll(validArrangementID, validDisclosureID) {
          (arrangementID, disclosureID) =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-ids/$arrangementID-$disclosureID-$enrolmentID",
              NOT_FOUND,
              "Disclosure ID doesn't match enrolment ID"
            )

            whenReady(connector.verifyDisclosureIDs(arrangementID, disclosureID, enrolmentID)) {
              result =>
                result mustBe IDVerificationStatus(isValid = false, IDVerificationStatus.DisclosureIDNotFound)
            }
        }
      }

      "should return false if arrangement and disclosure ids are not from the same submission" in {
        forAll(validArrangementID, validDisclosureID) {
          (arrangementID, disclosureID) =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-ids/$arrangementID-$disclosureID-$enrolmentID",
              NOT_FOUND,
              "Arrangement ID and Disclosure ID are not from the same submission"
            )

            whenReady(connector.verifyDisclosureIDs(arrangementID, disclosureID, enrolmentID)) {
              result =>
                result mustBe IDVerificationStatus(isValid = false, IDVerificationStatus.IDsDoNotMatch)
            }
        }
      }

      "should return false if arrangement and disclosure ids are not found" in {
        forAll(validArrangementID, validDisclosureID) {
          (arrangementID, disclosureID) =>
            stubResponse(
              s"/disclose-cross-border-arrangements/verify-ids/$arrangementID-$disclosureID-$enrolmentID",
              BAD_REQUEST,
              "IDs not found"
            )

            whenReady(connector.verifyDisclosureIDs(arrangementID, disclosureID, enrolmentID)) {
              result =>
                result mustBe IDVerificationStatus(isValid = false, IDVerificationStatus.IDsNotFound)
            }
        }
      }
    }

    "submitXMl" - {
      val mockHttpClient: HttpClient = mock[HttpClient]

      val generatedIDs = GeneratedIDs(Some("ArrangementID"), Some("disclosureID"), None, None)

      lazy val application: Application = new GuiceApplicationBuilder()
        .overrides(bind[HttpClient].toInstance(mockHttpClient))
        .build()

      val conn = application.injector.instanceOf[CrossBorderArrangementsConnector]

      "must return list of generated ids" in {

        when(mockHttpClient.POSTString[GeneratedIDs](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(generatedIDs))

        val expected: NodeSeq =
          <testXml>
          <name>test</name>
        </testXml>

        whenReady(conn.submitXML(expected)) {
          result =>
            result mustBe generatedIDs
        }
      }

      "must return true when Ok is returned from BackEnd" in {

        when(mockHttpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(OK, "true")))

        val arrangementID = "dummy"

        whenReady(conn.isMarketableArrangement(arrangementID)) {
          result =>
            result mustBe true
        }
      }
    }

  }

  private def stubResponse(expectedUrl: String, expectedStatus: Int, expectedBody: String = ""): StubMapping =
    server.stubFor(
      get(urlEqualTo(expectedUrl))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )
}
