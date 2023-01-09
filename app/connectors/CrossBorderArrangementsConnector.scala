/*
 * Copyright 2023 HM Revenue & Customs
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

import config.FrontendAppConfig
import models.GeneratedIDs
import models.disclosure.IDVerificationStatus
import play.api.http.HeaderNames
import play.mvc.Http.Status.{NO_CONTENT, OK}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, NotFoundException}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class CrossBorderArrangementsConnector @Inject() (configuration: FrontendAppConfig, httpClient: HttpClient)(implicit ec: ExecutionContext) {

  val baseUrl = s"${configuration.crossBorderArrangementsUrl}/disclose-cross-border-arrangements"

  def verificationUrl(arrangementId: String): String =
    s"$baseUrl/verify-arrangement-id/$arrangementId"

  def isMarketableArrangementUrl(arrangementId: String): String =
    s"$baseUrl/history/is-marketable-arrangement/$arrangementId"

  def verifyArrangementId(arrangementId: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val verificationUrl = s"$baseUrl/verify-arrangement-id/$arrangementId"

    httpClient.GET[HttpResponse](verificationUrl).map {
      response =>
        response.status match {
          case 204 => true
          case _   => false
        }
    } recover {
      case _: Exception => false
    }
  }

  def verifyDisclosureIDs(arrangementId: String, disclosureId: String, enrolmentId: String)(implicit hc: HeaderCarrier): Future[IDVerificationStatus] = {

    val verificationUrl = s"$baseUrl/verify-ids/$arrangementId-$disclosureId-$enrolmentId"

    val arrangementIDNotFound = "Arrangement ID not found"
    val disclosureIDNotFound  = "Disclosure ID doesn't match enrolment ID"
    val idsDoNotMatch         = "Arrangement ID and Disclosure ID are not from the same submission"

    httpClient.GET[HttpResponse](verificationUrl).map {
      response =>
        response.status match {
          case NO_CONTENT => IDVerificationStatus(isValid = true, IDVerificationStatus.IDsFound)
          case _          => IDVerificationStatus(isValid = false, IDVerificationStatus.IDsNotFound)
        }
    } recover {
      case e: NotFoundException =>
        e.message match {
          case message if message.contains(arrangementIDNotFound) =>
            IDVerificationStatus(isValid = false, IDVerificationStatus.ArrangementIDNotFound)
          case message if message.contains(disclosureIDNotFound) =>
            IDVerificationStatus(isValid = false, IDVerificationStatus.DisclosureIDNotFound)
          case message if message.contains(idsDoNotMatch) =>
            IDVerificationStatus(isValid = false, IDVerificationStatus.IDsDoNotMatch)
        }
      case _: Exception => IDVerificationStatus(isValid = false, IDVerificationStatus.IDsNotFound)
    }
  }

  private val headers = Seq(
    HeaderNames.CONTENT_TYPE -> "application/xml"
  )

  def submitXML(xml: NodeSeq)(implicit hc: HeaderCarrier): Future[GeneratedIDs] =
    httpClient.POSTString[GeneratedIDs](s"$baseUrl/submit", xml.mkString, headers)

  def isMarketableArrangement(arrangementId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    httpClient.GET[HttpResponse](isMarketableArrangementUrl(arrangementId)).map {
      response =>
        (response.status, response.body) match {
          case (OK, isMarketableArrangement) => isMarketableArrangement.toBoolean
          case _                             => throw new Exception("Cannot retrieve marketable arrangement details")
        }
    } recover {
      case _: Exception => throw new Exception("Cannot retrieve marketable arrangement details")
    }

}
