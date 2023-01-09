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

import connectors.HistoryConnector
import controllers.exceptions.DisclosureInformationIsMissingException
import models.UserAnswers
import models.disclosure.DisclosureType.{Dac6add, Dac6new, Dac6rep}
import models.disclosure.ReplaceOrDeleteADisclosure
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MarketableDisclosureService @Inject() (historyConnector: HistoryConnector)(implicit executionContext: ExecutionContext) {

  def isNonUKArrangementID(arrangementID: String): Boolean =
    !arrangementID.substring(0, 3).contains("GBA")

  private def userSuppliedArrangementID(userAnswers: UserAnswers): String = userAnswers
    .getBase(DisclosureIdentifyArrangementPage)
    .fold(throw new DisclosureInformationIsMissingException("Unable to retrieve arrangement id from userAnswers"))(
      arrangementID => arrangementID
    )

  private def userSuppliedBothIDs(userAnswers: UserAnswers): ReplaceOrDeleteADisclosure =
    userAnswers
      .getBase(ReplaceOrDeleteADisclosurePage)
      .fold(throw new DisclosureInformationIsMissingException("Unable to retrieve ids from replace or delete model from userAnswers"))(
        ids => ids
      )

  def setMarketableFlagForCurrentDisclosure(ua: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    ua.getBase(DisclosureTypePage) match {

      case Some(Dac6add) if isNonUKArrangementID(userSuppliedArrangementID(ua)) =>
        Future.successful(false)

      case Some(Dac6rep) if isNonUKArrangementID(userSuppliedBothIDs(ua).arrangementID) =>
        Future.successful(false)

      case Some(Dac6add) =>
        Future.successful(false)

      case Some(Dac6rep) =>
        historyConnector.getSubmissionDetailForDisclosure(userSuppliedBothIDs(ua).disclosureID).flatMap {
          disclosureDetails =>
            if (disclosureDetails.importInstruction.equals("dac6add")) {
              Future.successful(false)
            } else {
              Future.successful(disclosureDetails.initialDisclosureMA)
            }
        }

      case _ =>
        ua
          .getBase(DisclosureMarketablePage)
          .fold(throw new DisclosureInformationIsMissingException("Unable to retrieve disclosureMarketable flag from userAnswers"))(
            bool => Future.successful(bool)
          )
    }

  def getMarketableFlagFromFirstInitialDisclosure(ua: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    ua.getBase(DisclosureTypePage) match {
      case Some(Dac6new) =>
        Future.successful(false)

      case Some(Dac6add) if isNonUKArrangementID(userSuppliedArrangementID(ua)) =>
        Future.successful(false)

      case Some(Dac6rep) if isNonUKArrangementID(userSuppliedBothIDs(ua).arrangementID) =>
        Future.successful(false)

      case Some(Dac6rep) =>
        historyConnector.getSubmissionDetailForDisclosure(userSuppliedBothIDs(ua).disclosureID).flatMap {
          submissionDetail =>
            //Check last previous submission import Type

            historyConnector
              .retrieveFirstDisclosureForArrangementID(
                submissionDetail.arrangementID.getOrElse(
                  throw new DisclosureInformationIsMissingException("Unable to retrieve ids from replace or delete model from userAnswers")
                )
              )
              .flatMap {
                initialDac6New => Future.successful(initialDac6New.initialDisclosureMA)
              }
        }

      case _ =>
        historyConnector.retrieveFirstDisclosureForArrangementID(userSuppliedArrangementID(ua)).flatMap {
          initialDac6New =>
            Future.successful(initialDac6New.initialDisclosureMA)
        }
    }
}
