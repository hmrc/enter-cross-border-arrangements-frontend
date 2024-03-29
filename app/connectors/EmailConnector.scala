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

import javax.inject.{Inject, Singleton}
import models.EmailRequest
import org.slf4j.LoggerFactory
import play.api.Logging
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailConnector @Inject() (val config: FrontendAppConfig, http: HttpClient)(implicit ex: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(getClass)

  def sendEmail(emailRequest: EmailRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    http.POST[EmailRequest, HttpResponse](s"${config.sendEmailUrl}/hmrc/email", emailRequest) map {
      resp =>
        resp.status match {
          case NOT_FOUND   => logger.warn("The template cannot be found within the email service")
          case BAD_REQUEST => logger.warn("Missing email or name parameter")
          case ACCEPTED    => logger.info("Email queued")
          case _           => logger.warn(s"Unhandled status received from email service ${resp.status}")
        }
        resp
    } recoverWith {
      case e: Exception =>
        logger.warn(s"The email could not be sent to the EMAIL service")
        logger.warn(s"${e.getMessage}")
        Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "The email could not be sent to the EMAIL service"))
    }

}
