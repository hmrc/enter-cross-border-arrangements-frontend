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

package utils.model.rows

import models.CheckMode
import models.disclosure.DisclosureDetails
import models.disclosure.DisclosureType._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait DisclosureModelRows extends DisplayRowBuilder {

  def disclosureNamePage(disclosure: DisclosureDetails)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "disclosureName",
      content = formatMaxChars(disclosure.disclosureName),
      href    = controllers.disclosure.routes.DisclosureNameController.onPageLoad(CheckMode).url,
      columnWidth = "govuk-!-width-one-third"
    )

  private def disclosureMarketablePage(disclosure: DisclosureDetails)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "disclosureMarketable",
      content = yesOrNo(disclosure.initialDisclosureMA),
      href    = controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(CheckMode).url,
      columnWidth = "govuk-!-width-one-third"
    )

  def disclosureTypePage(disclosure: DisclosureDetails)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "disclosureType",
      content = msg"disclosureType.${disclosure.disclosureType}",
      href    = controllers.disclosure.routes.DisclosureTypeController.onPageLoad(CheckMode).url,
      columnWidth = "govuk-!-width-one-third"
    )


  private def disclosureIdentifyArrangement(disclosureDetails: DisclosureDetails)(implicit messages: Messages): Option[Row] = {
    disclosureDetails.arrangementID map { arrangementID =>
      toRow(
        msgKey = "disclosureIdentifyArrangement",
        content = lit"$arrangementID",
        href = controllers.disclosure.routes.DisclosureIdentifyArrangementController.onPageLoad(CheckMode).url,
        columnWidth = "govuk-!-width-one-third"
      )
    }
  }

  private def replaceOrDeleteADisclosureRows(disclosureDetails: DisclosureDetails)(implicit messages: Messages): Seq[Row] =
     Seq(
      toRow(
        msgKey  = "replaceOrDeleteADisclosure.arrangementID",
        content = lit"${disclosureDetails.arrangementID}",
        href    = controllers.disclosure.routes.ReplaceOrDeleteADisclosureController.onPageLoad(CheckMode).url,
        columnWidth = "govuk-!-width-one-third"
      ),
      toRow(
        msgKey  = "replaceOrDeleteADisclosure.disclosureID",
        content = lit"${disclosureDetails.disclosureID}",
        href    = controllers.disclosure.routes.ReplaceOrDeleteADisclosureController.onPageLoad(CheckMode).url,
        columnWidth = "govuk-!-width-one-third"
      )
    )

  def buildDisclosureSummaryDetails(disclosureDetails: DisclosureDetails)(implicit messages: Messages): Seq[Row] =
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
