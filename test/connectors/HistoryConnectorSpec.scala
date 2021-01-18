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
            forAll(validArrangementID) {
                id =>
                    server.stubFor(
                    get(urlEqualTo(s"/disclose-cross-border-arrangements/history/submissions/$id"))
                        .willReturn(
                        aResponse()
                            .withStatus(OK)
                        )
                    )

                whenReady(connector.getSubmissionDetails(id)){
                result =>
                    result mustBe true
                }
            }
        }
        "should return serverError when details cannot be found" in {
            forAll(alphaStr) {
            invalidID =>
                server.stubFor(
                get(urlEqualTo(s"/disclose-cross-border-arrangements/history/submissions/$invalidID"))
                    .willReturn(
                    aResponse()
                        .withStatus(INTERNAL_SERVER_ERROR)
                    )
                )

                whenReady(connector.getSubmissionDetails(invalidID)){
                result =>
                    result mustBe false
                }
            }
        }
    }
}
