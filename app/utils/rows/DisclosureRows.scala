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
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait DisclosureRows extends RowBuilder {

  def disclosureNamePage: Option[Row] = userAnswers.getBase(DisclosureNamePage) map { answer =>
    toRow(
      msgKey  = "disclosureName",
      content = formatMaxChars(answer),
      href    = controllers.disclosure.routes.DisclosureNameController.onPageLoad(CheckMode).url
    )
  }

  private def disclosureMarketablePage: Option[Row] = userAnswers.getBase(DisclosureMarketablePage) map { answer =>
    toRow(
      msgKey  = "disclosureMarketable",
      content = yesOrNo(answer),
      href    = controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(CheckMode).url
    )
  }

  def disclosureTypePage: Option[Row] = userAnswers.getBase(DisclosureTypePage) map { answer =>
    toRow(
      msgKey  = "disclosureType",
      content = msg"disclosureType.$answer",
      href    = controllers.disclosure.routes.DisclosureTypeController.onPageLoad(CheckMode).url
    )
  }

  private def disclosureIdentifyArrangement: Option[Row] = userAnswers.getBase(DisclosureIdentifyArrangementPage) map { answer =>
    val arrangementID = answer.toUpperCase
    toRow(
      msgKey  = "disclosureIdentifyArrangement",
      content = lit"$arrangementID",
      href    = controllers.disclosure.routes.DisclosureIdentifyArrangementController.onPageLoad(CheckMode).url
    )
  }

  private def replaceOrDeleteADisclosureRows: Seq[Row] = userAnswers.getBase(ReplaceOrDeleteADisclosurePage).fold(Seq[Row]()){ answer =>
    Seq(
      toRow(
        msgKey  = "replaceOrDeleteADisclosure.arrangementID",
        content = lit"${answer.arrangementID}",
        href    = controllers.disclosure.routes.ReplaceOrDeleteADisclosureController.onPageLoad(CheckMode).url
      ),
      toRow(
        msgKey  = "replaceOrDeleteADisclosure.disclosureID",
        content = lit"${answer.disclosureID}",
        href    = controllers.disclosure.routes.ReplaceOrDeleteADisclosureController.onPageLoad(CheckMode).url
      )
    )
  }

  def buildDisclosureSummaryDetails: Seq[Row] =
    userAnswers.getBase(DisclosureTypePage) match {
      case Some(Dac6new) =>
        disclosureMarketablePage.toSeq
      case Some(Dac6add) =>
        disclosureIdentifyArrangement.toSeq
      case Some(Dac6rep) =>
        replaceOrDeleteADisclosureRows
      case Some(Dac6del) =>
        replaceOrDeleteADisclosureRows

    }
}
