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

package utils.rows

import models.CheckMode
import models.disclosure.DisclosureType._
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait DisclosureRows extends RowBuilder {

  def disclosureNamePage(id: Int): Option[Row] = userAnswers.get(DisclosureNamePage, id) map { answer =>
    toRow(
      msgKey  = "disclosureName",
      content = formatMaxChars(answer),
      href    = controllers.disclosure.routes.DisclosureNameController.onPageLoad(id, CheckMode).url
    )
  }

  private def disclosureMarketablePage(id: Int): Option[Row] = userAnswers.get(DisclosureMarketablePage, id) map { answer =>
    toRow(
      msgKey  = "disclosureMarketable",
      content = yesOrNo(answer),
      href    = controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(id, CheckMode).url
    )
  }

  def disclosureTypePage(id: Int): Option[Row] = userAnswers.get(DisclosureTypePage, id) map { answer =>
    toRow(
      msgKey  = "disclosureType",
      content = msg"disclosureType.$answer",
      href    = controllers.disclosure.routes.DisclosureTypeController.onPageLoad(id, CheckMode).url
    )
  }

  private def disclosureIdentifyArrangement(id: Int): Option[Row] = userAnswers.get(DisclosureIdentifyArrangementPage, id) map { answer =>
    val arrangementID = answer.toUpperCase
    toRow(
      msgKey  = "disclosureIdentifyArrangement",
      content = lit"$arrangementID",
      href    = controllers.disclosure.routes.DisclosureIdentifyArrangementController.onPageLoad(id, CheckMode).url
    )
  }

  def buildDisclosureSummaryDetails(id: Int): Seq[Row] =
    userAnswers.get(DisclosureTypePage, id) match {
      case Some(Dac6new) =>
        disclosureMarketablePage(id).toSeq
      case Some(Dac6add) =>
        disclosureIdentifyArrangement(id).toSeq

    }
}
