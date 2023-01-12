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

package services

import connectors.EmailConnector
import models.disclosure.DisclosureDetails
import models.requests.DataRequestWithContacts
import models.subscription.ContactDetails
import models.{EmailRequest, GeneratedIDs}
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.RegexConstants

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailService @Inject() (emailConnector: EmailConnector)(implicit executionContext: ExecutionContext) extends RegexConstants {

  private val logger = LoggerFactory.getLogger(getClass)

  def processEmail(details: DisclosureDetails, ids: GeneratedIDs)(implicit
    request: DataRequestWithContacts[_],
    hc: HeaderCarrier
  ): Future[Option[HttpResponse]] = ids match {

    //new
    case GeneratedIDs(Some(arrangementID), Some(disclosureID), Some(messageRefID), _) =>
      sendEmail(request.contacts, GeneratedIDs(Some(arrangementID), Some(disclosureID)), details.disclosureType.toString, messageRefID)

    // add
    case GeneratedIDs(None, Some(disclosureID), Some(messageRefID), _) =>
      sendEmail(request.contacts, GeneratedIDs(details.arrangementID, Some(disclosureID)), details.disclosureType.toString, messageRefID)

    //rep
    case GeneratedIDs(None, None, Some(messageRefID), _) =>
      sendEmail(request.contacts, GeneratedIDs(details.arrangementID, details.disclosureID), details.disclosureType.toString, messageRefID)

    case GeneratedIDs(_, _, None, _) =>
      logger.warn(s"EmailService: Generated Ids does not contain the MessageRefID")
      Future.successful(None)
    case _ =>
      logger.warn(s"EmailService: Generated Ids does not contain the DisclosureID or MessageRefID")
      Future.successful(None)
  }

  def sendEmail(contacts: Option[ContactDetails], ids: GeneratedIDs, importInstruction: String, messageRefID: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[HttpResponse]] =
    contacts match {
      case Some(contactDetails) =>
        val emailAddress          = contactDetails.contactEmail
        val primaryContactName    = contactDetails.contactName
        val secondaryEmailAddress = contactDetails.secondEmail
        val secondaryName         = contactDetails.secondContactName

        (ids.arrangementID, ids.disclosureID) match {
          case (Some(arrangementID), Some(disclosureID)) =>
            val dateSubmitted = DateTimeFormat
              .forPattern("hh:mma 'on' d MMMM yyyy")
              .print(new LocalDateTime())
              .replace("AM", "am")
              .replace("PM", "pm")

            for {
              primaryResponse <- emailAddress
                .filter(emailRegex.r.matches)
                .fold(Future.successful(Option.empty[HttpResponse])) {
                  email =>
                    emailConnector
                      .sendEmail(
                        EmailRequest.sendConfirmation(email, importInstruction, arrangementID, disclosureID, dateSubmitted, messageRefID, primaryContactName)
                      )
                      .map(Some.apply)
                }

              _ <- secondaryEmailAddress
                .filter(emailRegex.r.matches)
                .fold(Future.successful(Option.empty[HttpResponse])) {
                  secondaryEmailAddress =>
                    emailConnector
                      .sendEmail(
                        EmailRequest
                          .sendConfirmation(secondaryEmailAddress, importInstruction, arrangementID, disclosureID, dateSubmitted, messageRefID, secondaryName)
                      )
                      .map(Some.apply)
                }
            } yield primaryResponse
          case _ => Future.successful(None)
        }
      case _ => Future.successful(None)
    }
}
