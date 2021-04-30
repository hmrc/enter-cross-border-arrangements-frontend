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
import models.UserAnswers
import models.disclosure.DisclosureType
import pages.disclosure.{DisclosureDetailsPage, FirstInitialDisclosureMAPage}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsMarketableService @Inject()(historyConnector: HistoryConnector,
                                    sessionRepository: SessionRepository) {

  def isInitialDisclosureMarketable(userAnswers: UserAnswers, id: Int,
                                    )
                                   (implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[Boolean] = {

    val disclosureDetails = userAnswers.get(DisclosureDetailsPage, id) match {
      case Some(details) => details
      case None => throw new Exception("Missing disclosure details")
    }

    historyConnector.retrieveFirstDisclosureForArrangementID(disclosureDetails.arrangementID.getOrElse("")).flatMap {
      firstDisclosureDetails =>
        disclosureDetails.disclosureType match {
          case DisclosureType.Dac6add => Future.successful(firstDisclosureDetails.initialDisclosureMA)
          case DisclosureType.Dac6rep =>
            historyConnector.searchDisclosures(disclosureDetails.disclosureID.getOrElse("")).flatMap {
              submissionHistory =>
                for {
                  userAnswers <- Future.fromTry(userAnswers.setBase(FirstInitialDisclosureMAPage, firstDisclosureDetails.initialDisclosureMA))
                  _ <- sessionRepository.set(userAnswers)
                } yield {
                  if (submissionHistory.details.nonEmpty &&
                    submissionHistory.details.head.importInstruction == "Add" &&
                    firstDisclosureDetails.initialDisclosureMA) {
                    //Note: There should only be one submission returned with an ADD instruction for the given disclosure ID
                    true
                  } else {
                    false
                  }
                }
            }
          case _ => Future.successful(false)
        }
    }.recoverWith {
      case _ => Future.successful(false)
    }
  }

}
