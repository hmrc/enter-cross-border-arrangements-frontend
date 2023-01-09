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

package utils.model.rows

import models.disclosure.DisclosureDetails
import models.disclosure.DisclosureType._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._
import utils.SummaryListDisplay.DisplayRow

trait DisclosureModelRows extends DisplayRowBuilder {

  def disclosureNamePage(disclosure: DisclosureDetails)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey = "disclosureName",
      content = formatMaxChars(disclosure.disclosureName)
    )

  private def disclosureMarketablePage(disclosure: DisclosureDetails)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey = "disclosureMarketable",
      content = yesOrNo(disclosure.initialDisclosureMA)
    )

  def disclosureTypePage(disclosure: DisclosureDetails)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey = "disclosureType",
      content = msg"disclosureType.${disclosure.disclosureType}"
    )

  private def disclosureIdentifyArrangement(disclosureDetails: DisclosureDetails)(implicit messages: Messages): Option[DisplayRow] =
    disclosureDetails.arrangementID map {
      arrangementID =>
        toDisplayRow(
          msgKey = "disclosureIdentifyArrangement",
          content = lit"$arrangementID"
        )
    }

  private def replaceOrDeleteADisclosureRows(disclosureDetails: DisclosureDetails)(implicit messages: Messages): Seq[DisplayRow] =
    Seq(
      toDisplayRow(
        msgKey = "replaceOrDeleteADisclosure.arrangementID",
        content = lit"${disclosureDetails.arrangementID.getOrElse(throw new RuntimeException("Cannot retrieve Arrangement ID"))}"
      ),
      toDisplayRow(
        msgKey = "replaceOrDeleteADisclosure.disclosureID",
        content = lit"${disclosureDetails.disclosureID.getOrElse(throw new RuntimeException("Cannot retrieve Disclosure ID"))}"
      )
    )

  def buildDisclosureSummaryDetails(disclosureDetails: DisclosureDetails)(implicit messages: Messages): Seq[DisplayRow] =
    disclosureDetails.disclosureType match {
      case Dac6new =>
        Seq(disclosureMarketablePage(disclosureDetails))
      case Dac6add =>
        disclosureIdentifyArrangement(disclosureDetails).toSeq
      case Dac6rep =>
        replaceOrDeleteADisclosureRows(disclosureDetails)
      case Dac6del =>
        replaceOrDeleteADisclosureRows(disclosureDetails)

    }
}
