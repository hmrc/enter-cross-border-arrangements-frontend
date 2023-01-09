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
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import models.{SubmissionDetails, SubmissionHistory}

class HistoryConnector @Inject() (configuration: FrontendAppConfig, httpClient: HttpClient)(implicit ec: ExecutionContext) {

  val baseUrl = s"${configuration.crossBorderArrangementsUrl}/disclose-cross-border-arrangements"

  def getSubmissionsUrl(enrolmentid: String) = s"$baseUrl/history/submissions/$enrolmentid"

  def getSubmissionsByDisclosureIDUrl(disclosureID: String) = s"$baseUrl/history/disclosures/$disclosureID"

  def getSubmissionDetails(enrolmentid: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    httpClient.GET[SubmissionHistory](getSubmissionsUrl(enrolmentid)).map {
      subs =>
        subs.details match {
          case Seq() => false
          case _     => true
        }
    } recover {
      case _ => throw new Exception("History unavailable")
    }

  def getSubmissionDetailForDisclosure(disclosureID: String)(implicit hc: HeaderCarrier): Future[SubmissionDetails] =
    httpClient.GET[SubmissionDetails](getSubmissionsByDisclosureIDUrl(disclosureID)) recover {
      case e => throw new Exception(s"History unavailable with: $e")
    }

  def searchDisclosures(searchCriteria: String)(implicit hc: HeaderCarrier): Future[SubmissionHistory] =
    httpClient.GET[SubmissionHistory](s"$baseUrl/history/search-submissions/$searchCriteria").recover {
      case _ => SubmissionHistory(Seq())
    }

  def retrieveFirstDisclosureForArrangementID(arrangementID: String)(implicit hc: HeaderCarrier): Future[SubmissionDetails] =
    httpClient.GET[SubmissionDetails](s"$baseUrl/history/first-disclosure/$arrangementID")

}
