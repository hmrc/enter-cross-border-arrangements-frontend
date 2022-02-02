/*
 * Copyright 2022 HM Revenue & Customs
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

package helpers.xml

import models.Submission
import models.reporter.RoleInArrangement

import scala.util.Try
import scala.xml.NodeSeq

case class RelevantTaxPayersXMLSection(submission: Submission) {

  val associatedEnterpriseSection: Option[AssociatedEnterprisesXMLSection] = Option(AssociatedEnterprisesXMLSection(submission))

  private val isReporterTaxpayer: Option[Boolean] = submission.reporterDetails.flatMap(
    _.liability.map(
      x => x.role == RoleInArrangement.Taxpayer.toString
    )
  )

  private[xml] def getAssociatedEnterprisesForReporter(name: String) =
    associatedEnterpriseSection.map(_.buildAssociatedEnterprises(name)).getOrElse(NodeSeq.Empty)

  private[xml] def buildReporterAsTaxpayer: NodeSeq =
    (isReporterTaxpayer, submission.reporterDetails) match {
      case (Some(true), Some(reporterDetails)) =>
        val implementingDate = reporterDetails.liability.fold(NodeSeq.Empty)(
          liability =>
            liability.implementingDate.fold(NodeSeq.Empty)(
              date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>
            )
        )

        if (reporterDetails.organisation.isDefined) {
          <RelevantTaxpayer>
            {OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get)}{implementingDate}{
            getAssociatedEnterprisesForReporter(reporterDetails.organisation.get.organisationName)
          }
          </RelevantTaxpayer>
        } else {
          <RelevantTaxpayer>
            {IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get)}{implementingDate}{
            getAssociatedEnterprisesForReporter(reporterDetails.individual.get.nameAsString)
          }
          </RelevantTaxpayer>
        }

      case _ => NodeSeq.Empty
    }

  private[xml] def getAssociatedEnterprisesForTaxpayers(taxpayerID: String) =
    associatedEnterpriseSection.map(_.buildAssociatedEnterprises(taxpayerID)).getOrElse(NodeSeq.Empty)

  private[xml] def getRelevantTaxpayers: IndexedSeq[NodeSeq] =
    submission.taxpayers.map {
      taxpayer =>
        val date = taxpayer.implementingDate.fold(NodeSeq.Empty)(
          date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>
        )
        if (taxpayer.individual.isDefined) {
          <RelevantTaxpayer>
          {IndividualXMLSection.buildIDForIndividual(taxpayer.individual.get)}{date}{getAssociatedEnterprisesForTaxpayers(taxpayer.taxpayerId)}
        </RelevantTaxpayer>
        } else {
          <RelevantTaxpayer>
          {OrganisationXMLSection.buildIDForOrganisation(taxpayer.organisation.get)}{date}{getAssociatedEnterprisesForTaxpayers(taxpayer.taxpayerId)}
        </RelevantTaxpayer>
        }
    }

  def buildRelevantTaxpayers: NodeSeq =
    Try {
      <RelevantTaxPayers>
        {buildReporterAsTaxpayer ++ getRelevantTaxpayers}
      </RelevantTaxPayers>
    }.getOrElse(NodeSeq.Empty)

}
