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

import config.FrontendAppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CrossBorderArrangementsConnector @Inject()(configuration: FrontendAppConfig,
                                                 httpClient: HttpClient)(implicit ec: ExecutionContext) {

  val baseUrl = s"${configuration.crossBorderArrangementsUrl}/disclose-cross-border-arrangements"

  def verificationUrl(arrangementId: String): String = {
    s"$baseUrl/verify-arrangement-id/$arrangementId"
  }

  def isMarketableArrangementUrl(arrangementId: String): String = {
    s"$baseUrl/is-marketable-arrangement/$arrangementId"
  }

  def verifyArrangementId(arrangementId: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    httpClient.GET[HttpResponse](verificationUrl(arrangementId)).map { response =>
      response.status match {
        case 204 => true
        case _ => false
      }
    } recover {
      case _: Exception => false
    }
  }

  def isMarketableArrangement(arrangementId: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    httpClient.GET[HttpResponse](isMarketableArrangementUrl(arrangementId)).map { response =>
      (response.status, response.body) match {
        case (200, "true") => true
        case _ => false
      }
    } recover {
      case _: Exception => false // TODO see https://jira.tools.tax.service.gov.uk/browse/DAC6-531
    }
  }

}
