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
import javax.inject.Inject
import models.AddressLookup
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.Reads
import uk.gov.hmrc.http._

import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {
  private val logger: Logger = Logger(this.getClass)

  def addressLookupByPostcode(postCode: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[AddressLookup]] = {

    val addressLookupUrl: String = s"${config.addressLookUpUrl}/v2/uk/addresses"
    val urlParams = Seq ("postcode" -> postCode)

    implicit val reads: Reads[Seq[AddressLookup]] = AddressLookup.addressesLookupReads

    http.GET[HttpResponse](
      url = addressLookupUrl,
      queryParams = urlParams,
      headers = Seq("X-Hmrc-Origin" -> "DAC6")
    ) flatMap {
      case response if response.status equals OK =>
        Future.successful(response.json.as[Seq[AddressLookup]].filterNot(address =>
          address.addressLine1.isEmpty && address.addressLine2.isEmpty)
        )
      case response =>
        val message = s"Address Lookup failed with status ${response.status} Response body: ${response.body}"
        Future.failed(new HttpException(message, response.status))
    } recover {
      case e: Exception =>
        logger.error("Exception in Address Lookup", e)
        throw e
    }
  }
}
