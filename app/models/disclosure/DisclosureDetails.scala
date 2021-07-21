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

package models.disclosure

import controllers.exceptions.DisclosureInformationIsMissingException
import models.disclosure.DisclosureType.{Dac6add, Dac6new, Dac6rep}
import models.{
  DisclosureImportInstructionInvalidError,
  DisclosureInitialMarketableArrangementInvalidError,
  DisclosureNameEmptyError,
  SubmissionError,
  UserAnswers
}
import pages.MessageRefIDPage
import pages.disclosure._
import play.api.libs.json.{Json, OFormat}

case class DisclosureDetails(
  disclosureName: String,
  disclosureType: DisclosureType = DisclosureType.Dac6new,
  arrangementID: Option[String] = None,
  disclosureID: Option[String] = None,
  initialDisclosureMA: Boolean = false,
  messageRefId: Option[String] = None,
  firstInitialDisclosureMA: Option[Boolean] = None,
  sent: Boolean = false
) {

  def withDisclosureType(disclosureType: DisclosureType): DisclosureDetails = copy(disclosureType = disclosureType)

  def withIds(arrangementID: String, disclosureID: String): DisclosureDetails = copy(arrangementID = Option(arrangementID), disclosureID = Option(disclosureID))

  def withInitialDisclosureMA(firstInitialDisclosureMA: Option[Boolean]): DisclosureDetails =
    copy(initialDisclosureMA = (disclosureType, firstInitialDisclosureMA) match {
      case (Dac6add, _)           => false
      case (Dac6rep, None)        => throw new DisclosureInformationIsMissingException("Missing first InitialDisclosureMA flag for a replace")
      case (Dac6rep, Some(value)) => value
      case _                      => initialDisclosureMA
    })

  def validate: Either[SubmissionError, DisclosureDetails] =
    for {
      _ <- Either.cond(Option(disclosureName).exists(_.nonEmpty), disclosureName, DisclosureNameEmptyError)
      _ <- Either.cond(Option(disclosureType).isDefined, disclosureType, DisclosureImportInstructionInvalidError)
      _ <- Either.cond(Option(initialDisclosureMA).isDefined, initialDisclosureMA, DisclosureInitialMarketableArrangementInvalidError)
    } yield this
}

object DisclosureDetails {

  private def getDisclosureDetails(userAnswers: UserAnswers) = userAnswers
    .getBase(DisclosureDetailsPage)
    .orElse(Some(DisclosureDetails("")))
  private def getDisclosureName(userAnswers: UserAnswers)       = userAnswers.getBase(DisclosureNamePage)
  private def getDisclosureType(userAnswers: UserAnswers)       = userAnswers.getBase(DisclosureTypePage)
  private def getDisclosureMarketable(userAnswers: UserAnswers) = userAnswers.getBase(DisclosureMarketablePage).orElse(Some(false))

  private def getDisclosureIdentifyArrangement(userAnswers: UserAnswers) = userAnswers
    .getBase(DisclosureIdentifyArrangementPage)
    .orElse(throw new DisclosureInformationIsMissingException(s"Additional Arrangement must be identified"))
  private def getReplaceOrDeleteDisclosure(userAnswers: UserAnswers): Option[ReplaceOrDeleteADisclosure] = userAnswers.getBase(ReplaceOrDeleteADisclosurePage)
  private def getInitialDisclosureMA(userAnswers: UserAnswers): Boolean                                  = userAnswers.getBase(InitialDisclosureMAPage).getOrElse(false)
  private def getMessageRefId(userAnswers: UserAnswers)                                                  = userAnswers.getBase(MessageRefIDPage).orElse(None)

  def build(userAnswers: UserAnswers): DisclosureDetails =
    getDisclosureDetails(userAnswers)
      .flatMap {
        details =>
          getDisclosureName(userAnswers).map {
            disclosureName => details.copy(disclosureName = disclosureName)
          }
      }
      .flatMap {
        details =>
          getDisclosureType(userAnswers).flatMap {
            case Dac6new =>
              getDisclosureMarketable(userAnswers).map {
                initialDisclosureMA =>
                  details.copy(disclosureType = Dac6new, initialDisclosureMA = initialDisclosureMA)
              }
            case Dac6add =>
              getDisclosureIdentifyArrangement(userAnswers).flatMap {
                arrangementID =>
                  getDisclosureMarketable(userAnswers).map {
                    initialDisclosureMA =>
                      details.copy(disclosureType = Dac6add, arrangementID = Some(arrangementID), initialDisclosureMA = initialDisclosureMA)
                  }
              }
            case repOrDel =>
              getReplaceOrDeleteDisclosure(userAnswers).map {
                ids =>
                  details.copy(
                    disclosureType = repOrDel,
                    arrangementID = Some(ids.arrangementID),
                    disclosureID = Some(ids.disclosureID),
                    initialDisclosureMA = getInitialDisclosureMA(userAnswers)
                  )
              }
          }
      }
      .map {
        details =>
          details.copy(messageRefId = getMessageRefId(userAnswers))
      }
      .getOrElse(throw new DisclosureInformationIsMissingException("Unable to build disclose details"))

  implicit val format: OFormat[DisclosureDetails] = Json.format[DisclosureDetails]
}
