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
import pages.reporter.individual.ReporterIndividualNamePage
import pages.reporter.organisation.ReporterOrganisationNamePage
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

  private def getTaxpayerNames(id: Int): Seq[String] = {
    (userAnswers.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id), userAnswers.get(TaxpayerLoopPage, id)) match {
      case (Some(selectionList), Some(taxpayers)) =>

        val taxpayerNames: Seq[String] = selectionList.flatMap(eachID => taxpayers.filter(taxpayer => taxpayer.taxpayerId == eachID)).map(_.nameAsString)
        val org = "organisation-reporter"
        val individual = "individual-reporter"

        if (selectionList.contains(org)){
          getReporterIfSelected(org, id) ++ taxpayerNames
        } else if (selectionList.contains(individual)) {
          getReporterIfSelected(individual, id) ++ taxpayerNames
        } else {
          taxpayerNames
        }
      case _ => Seq.empty
    }
  }

  private def getReporterIfSelected(reporterValue: String, id: Int): Seq[String] = {
      (reporterValue, userAnswers.get(ReporterIndividualNamePage, id), userAnswers.get(ReporterOrganisationNamePage, id)) match {
        case ("individual-reporter", Some(individualName), None) =>
          Seq(individualName.displayName)
        case ("organisation-reporter", None, Some(organisationName)) =>
          Seq(organisationName)
        case _ => Seq.empty
      }
  }

  def selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id: Int): Seq[Row] = {

    val formattedTaxpayerList = if (getTaxpayerNames(id).size > 1) {
      s"""<ul class="govuk-list govuk-list--bullet">
         |${getTaxpayerNames(id).map(selectedTaxpayer => s"<li>$selectedTaxpayer</li>").mkString("\n")}
         |</ul>""".stripMargin
    } else {
      s"${getTaxpayerNames(id).head}"
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
