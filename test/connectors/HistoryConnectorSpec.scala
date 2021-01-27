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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import generators.Generators
import org.scalacheck.Gen.alphaStr
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{OK, INTERNAL_SERVER_ERROR}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper
import play.api.libs.json.{JsArray, Json}

class HistoryConnectorSpec extends SpecBase
  with ScalaCheckPropertyChecks
  with WireMockHelper
  with Generators {

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.cross-border-arrangements.port" -> server.port()
    ).build()
    
    lazy val connector: HistoryConnector = injector.instanceOf[HistoryConnector]

    "HistoryConnector" - {
        "should return Ok when called with valid enrolmentid" in {

                val json = Json.obj(
                    "details" -> JsArray(Seq(Json.obj(
                    "enrolmentID" -> "enrolmentID",
                    "submissionTime" -> Json.obj(
                        "$date" -> 1196676930000L
                    ),
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
}
