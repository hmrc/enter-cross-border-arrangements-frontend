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
import models.{ManualSubmissionValidationFailure, ManualSubmissionValidationResult, ManualSubmissionValidationSuccess}
import play.api.http.HeaderNames
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Elem

class ValidationConnector @Inject() (http: HttpClient, config: FrontendAppConfig) {

  val url = s"${config.crossBorderArrangementsUrl}/disclose-cross-border-arrangements/validate-manual-submission"

  private val headers = Seq(
    HeaderNames.CONTENT_TYPE -> "application/xml"
  )

  def sendForValidation(xml: Elem)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Seq[String], String]] =
    http.POSTString[ManualSubmissionValidationResult](url, xml.mkString, headers).map {
      case ManualSubmissionValidationSuccess(a) => Right(a)
      case ManualSubmissionValidationFailure(a) => Left(a)
    }
}
