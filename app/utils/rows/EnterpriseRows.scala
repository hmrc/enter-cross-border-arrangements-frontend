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
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.reporter.ReporterDetailsPage
import pages.taxpayer.TaxpayerLoopPage
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait EnterpriseRows extends RowBuilder {

  def youHaveNotAddedAnyAssociatedEnterprises(id: Int): Option[Row] = userAnswers.get(YouHaveNotAddedAnyAssociatedEnterprisesPage, id) map { answer =>

    toRow(
      msgKey  = "youHaveNotAddedAnyAssociatedEnterprises",
      content = msg"youHaveNotAddedAnyAssociatedEnterprises.$answer",
      href    = controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(id, CheckMode).url
    )
  }

  def selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id: Int): Seq[Row] = {
    val reporterName = userAnswers.get(ReporterDetailsPage, id).fold("")(_.nameAsString)

    (userAnswers.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id), userAnswers.get(TaxpayerLoopPage, id)) match {
      case (Some(selectionList), Some(taxpayers)) =>
        val relevantTaxpayerNames: Seq[String] = selectionList.flatMap(eachID => taxpayers.filter(taxpayer => taxpayer.taxpayerId == eachID)).map(_.nameAsString)

        if (selectionList.contains(reporterName)) {
          formatSelectedTaxpayers(Seq(reporterName) ++ relevantTaxpayerNames, id)
        } else {
          formatSelectedTaxpayers(relevantTaxpayerNames, id)
        }

      case (Some(_), None) =>
        formatSelectedTaxpayers(Seq(reporterName), id)

      case _ => Seq.empty
    }
  }

  private def formatSelectedTaxpayers(taxpayerList: Seq[String], id: Int): Seq[Row] = {

    val formattedTaxpayerList = if (taxpayerList.size > 1) {
      s"""<ul class="govuk-list govuk-list--bullet">
         |${taxpayerList.map(selectedTaxpayer => s"<li>$selectedTaxpayer</li>").mkString("\n")}
         |</ul>""".stripMargin
    } else {
      s"${taxpayerList.head}"
    }

    Seq(toRow(
      msgKey  = "selectAnyTaxpayersThisEnterpriseIsAssociatedWith",
      content = Html(s"$formattedTaxpayerList"),
      href    = controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(id, CheckMode).url
    ))
  }

  def associatedEnterpriseType(id: Int): Option[Row] = userAnswers.get(AssociatedEnterpriseTypePage, id) map {
    answer =>
      toRow(
        msgKey = "associatedEnterpriseType",
        content = msg"selectType.$answer",
        href = controllers.enterprises.routes.AssociatedEnterpriseTypeController.onPageLoad(id, CheckMode).url
      )
  }

  def isAssociatedEnterpriseAffected(id: Int): Option[Row] = userAnswers.get(IsAssociatedEnterpriseAffectedPage, id) map {
    answer =>
      toRow(
        msgKey = "isAssociatedEnterpriseAffected",
        content = yesOrNo(answer),
        href = controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(id, CheckMode).url
      )
  }

}
