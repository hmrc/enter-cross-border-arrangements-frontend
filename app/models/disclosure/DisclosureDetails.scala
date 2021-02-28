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

import models.disclosure.DisclosureType.{Dac6add, Dac6rep}
import models.{DisclosureImportInstructionInvalidError, DisclosureInitialMarketableArrangementInvalidError, DisclosureNameEmptyError, SubmissionError}
import play.api.libs.json.{Json, OFormat}

case class DisclosureDetails(
  disclosureName: String,
  disclosureType: DisclosureType = DisclosureType.Dac6new,
  arrangementID: Option[String] = None,
  disclosureID: Option[String]  = None,
  initialDisclosureMA: Boolean  = false,
  messageRefId: Option[String]  = None,
  firstInitialDisclosureMA: Option[Boolean] = None
) {

  def withDisclosureType(disclosureType: DisclosureType): DisclosureDetails = copy(disclosureType = disclosureType)

  def withIds(arrangementID: String, disclosureID: String): DisclosureDetails = copy(arrangementID = Option(arrangementID), disclosureID = Option(disclosureID))

  def withInitialDisclosureMA(firstInitialDisclosureMA: Option[Boolean]): DisclosureDetails =
    copy(initialDisclosureMA = (disclosureType, firstInitialDisclosureMA) match {
      case (Dac6add, _)     => false
      case (Dac6rep, None)  => throw new Exception("Missing first InitialDisclosureMA flag for a replace")
      case (Dac6rep, Some(value)) => value
      case _                => initialDisclosureMA
    })

  def validate: Either[SubmissionError, DisclosureDetails] =
    for {
      validDisclosureName      <- Either.cond(Option(disclosureName).exists(_.nonEmpty), disclosureName, DisclosureNameEmptyError)
      validImportInstruction   <- Either.cond(Option(disclosureType).isDefined, disclosureType, DisclosureImportInstructionInvalidError)
      validInitialDisclosureMA <- Either.cond(Option(initialDisclosureMA).isDefined, initialDisclosureMA, DisclosureInitialMarketableArrangementInvalidError)
    } yield copy(disclosureName = validDisclosureName, disclosureType = validImportInstruction, initialDisclosureMA = validInitialDisclosureMA)
}

object DisclosureDetails {

  implicit val format: OFormat[DisclosureDetails] = Json.format[DisclosureDetails]
}

