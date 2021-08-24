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

package services

import connectors.HistoryConnector
import controllers.exceptions.DisclosureInformationIsMissingException
import helpers.JourneyHelpers
import models.UserAnswers
import models.disclosure.DisclosureType.{Dac6add, Dac6rep}
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MarketableDisclosureService @Inject() (historyConnector: HistoryConnector)(implicit executionContext: ExecutionContext) {

  def retrieveAndSetInitialDisclosureMAFlag(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {

    lazy val userSuppliedBothIDs =
      userAnswers
        .getBase(ReplaceOrDeleteADisclosurePage)
        .fold(throw new DisclosureInformationIsMissingException("Unable to retrieve ids from replace or delete model from userAnswers"))(
          ids => ids
        )

    lazy val userSuppliedArrangementID = userAnswers
      .getBase(DisclosureIdentifyArrangementPage)
      .fold(throw new DisclosureInformationIsMissingException("Unable to retrieve arrangement id from userAnswers"))(
        arrangementID => arrangementID
      )

    userAnswers.getBase(DisclosureTypePage) match {

      case Some(Dac6add) =>
        historyConnector.retrieveFirstDisclosureForArrangementID(userSuppliedArrangementID).flatMap {
          firstDisclosure =>
            Future.successful(firstDisclosure.initialDisclosureMA)
        }

      case Some(Dac6rep) if !JourneyHelpers.isArrangementIDUK(userSuppliedBothIDs.arrangementID) =>
        Future.successful(false)

      case Some(Dac6rep) =>
        historyConnector.getSubmissionDetailForDisclosure(userSuppliedBothIDs.disclosureID).flatMap {
          lastPreviousDisclosure =>
            if (lastPreviousDisclosure.importInstruction.toUpperCase.contains("ADD")) {
              Future.successful(false)
            } else {
              Future.successful(lastPreviousDisclosure.initialDisclosureMA)
            }
        }

      case _ =>
        userAnswers
          .getBase(DisclosureMarketablePage)
          .fold(throw new DisclosureInformationIsMissingException("Unable to retrieve disclosureMarketable flag from userAnswers"))(
            bool => Future.successful(bool)
          )
    }
  }
}
