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

import models.enterprises.AssociatedEnterprise
import models.{CheckMode, SelectType}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait EnterpriseModelRows extends DisplayRowBuilder {


  def selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id: Int, associatedEnterprise: AssociatedEnterprise)(implicit messages: Messages):
  Seq[Row] =
          formatSelectedTaxpayers(associatedEnterprise.associatedTaxpayers, id)

  private def formatSelectedTaxpayers(taxpayerList: Seq[String], id: Int)(implicit messages: Messages): Seq[Row] = {

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

  def associatedEnterpriseType(id: Int, associatedEnterprise: AssociatedEnterprise)(implicit messages: Messages): Row = {
    val selectType = (associatedEnterprise.individual, associatedEnterprise.organisation) match {
      case (Some(_), None) => SelectType.Individual
      case (None, Some(_)) => SelectType.Organisation
      case _ => throw new Exception("Cannot retrieve associated enterprise type")
    }
      toRow(
        msgKey = "associatedEnterpriseType",
        content = msg"selectType.${selectType.toString}",
        href = controllers.enterprises.routes.AssociatedEnterpriseTypeController.onPageLoad(id, CheckMode).url
      )
  }


  def isAssociatedEnterpriseAffected(id: Int, associatedEnterprise: AssociatedEnterprise)(implicit messages: Messages): Row =
      toRow(
        msgKey = "isAssociatedEnterpriseAffected",
        content = yesOrNo(associatedEnterprise.isAffectedBy),
        href = controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(id, CheckMode).url
      )


}
