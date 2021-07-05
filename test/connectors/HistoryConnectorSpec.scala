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
import generators.Generators
import models.{SubmissionDetails, SubmissionHistory}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, Json}
import uk.gov.hmrc.http.UpstreamErrorResponse
import utils.WireMockHelper

import java.time.LocalDateTime

class HistoryConnectorSpec extends SpecBase
  with MockServiceApp
  with ScalaCheckPropertyChecks
  with WireMockHelper
  with Generators {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      "microservice.services.cross-border-arrangements.port" -> server.port()
    )
    
    lazy val connector: HistoryConnector = app.injector.instanceOf[HistoryConnector]

    "HistoryConnector" - {

      "getSubmissionDetails" - {
        "should return Ok when called with valid enrolmentid" in {

          val json = Json.obj(
            "details" -> JsArray(Seq(Json.obj(
              "enrolmentID" -> "enrolmentID", //1196676930000L
              "submissionTime" -> LocalDateTime.now.toString,
              "fileName" -> "fileName",
              "importInstruction" -> "New",
              "initialDisclosureMA" -> false,
              "messageRefId" -> "GB0000000XXX"
            )))
          )

          server.stubFor(
            get(urlEqualTo("/disclose-cross-border-arrangements/history/submissions/enrolmentID"))
              .willReturn(
                aResponse()
                  .withStatus(OK)
                  .withBody(json.toString())
              )
          )

          whenReady(connector.getSubmissionDetails("enrolmentID")) {
            result =>
              result mustBe true
          }
        }
        "should return false when no data is returned" in {

          val json = Json.obj(
            "details" -> JsArray(Seq())
          )

          server.stubFor(
            get(urlEqualTo("/disclose-cross-border-arrangements/history/submissions/enrolmentID"))
              .willReturn(
                aResponse()
                  .withStatus(OK)
                  .withBody(json.toString())
              )
          )

          whenReady(connector.getSubmissionDetails("enrolmentID")) {
            result =>
              result mustBe false
          }

        }
      }

      "searchDisclosures" - {
        val search = "GBD20200601AAA000"

        "should return the submission history for search criteria" in {
          val submissionDetails =  SubmissionDetails(
            enrolmentID = "enrolmentID",
            submissionTime = LocalDateTime.now(),
            fileName = search,
            arrangementID = Some("GBA20200601AAA000"),
            disclosureID = Some("GBD20200601AAA000"),
            importInstruction = "Add",
            initialDisclosureMA = false,
            messageRefId = "GB0000000XXX")

          val submissionHistory = SubmissionHistory(Seq(submissionDetails))

          server.stubFor(
            get(urlEqualTo(s"/disclose-cross-border-arrangements/history/search-submissions/$search"))
              .willReturn(
                aResponse()
                  .withStatus(OK)
                  .withBody(Json.toJson(submissionHistory).toString())
              )
          )

          whenReady(connector.searchDisclosures("GBD20200601AAA000")) {
            _ mustBe submissionHistory
          }

        }

        "return an empty submission history for search criteria if 404 is received" in {
          server.stubFor(
            get(urlEqualTo(s"/disclose-cross-border-arrangements/history/search-submissions/$search"))
              .willReturn(
                aResponse()
                  .withStatus(NOT_FOUND)
              )
          )

          whenReady(connector.searchDisclosures(search)) {
            _ mustBe SubmissionHistory(Seq())
          }
        }
      }

      "retrieveFirstDisclosureForArrangementID" - {
        val arrangementID = "GBA20200904AAAAAA"
        val disclosureID = "GBD20200904AAAAAA"

        "should return the submission details of the first disclosure of the given arrangement ID" in {
          val json = Json.obj(
            "enrolmentID" -> "enrolmentID",
            "submissionTime" -> "2020-05-14T17:10:00",
            "fileName" -> "fileName",
            "arrangementID" -> arrangementID,
            "disclosureID" -> disclosureID,
            "importInstruction" -> "New",
            "initialDisclosureMA" -> true,
            "messageRefId" -> "GB0000000XXX"
          )

          server.stubFor(
            get(urlEqualTo(s"/disclose-cross-border-arrangements/history/first-disclosure/$arrangementID"))
              .willReturn(
                aResponse()
                  .withStatus(OK)
                  .withBody(json.toString())
              )
          )

          whenReady(connector.retrieveFirstDisclosureForArrangementID(arrangementID)) {
            result =>
              result mustBe
                SubmissionDetails(
                  "enrolmentID",
                  LocalDateTime.parse("2020-05-14T17:10:00"),
                  "fileName",
                  Some(arrangementID),
                  Some(disclosureID),
                  "New",
                  initialDisclosureMA = true,
                  messageRefId = "GB0000000XXX"
                )
          }
        }

        "return throw an exception if the first disclosure isn't found" in {
          server.stubFor(
            get(urlEqualTo(s"/disclose-cross-border-arrangements/history/first-disclosure/$arrangementID"))
              .willReturn(
                aResponse()
                  .withStatus(NOT_FOUND)
              )
          )

          val result = connector.retrieveFirstDisclosureForArrangementID(arrangementID)

          whenReady(result.failed){ e =>
            e mustBe an[UpstreamErrorResponse]
            val error = e.asInstanceOf[UpstreamErrorResponse]
            error.statusCode mustBe NOT_FOUND
          }
        }
      }
    }
}
