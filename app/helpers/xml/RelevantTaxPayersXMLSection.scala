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

package helpers.xml

import models.Submission

import scala.util.Try
import scala.xml.NodeSeq

case class RelevantTaxPayersXMLSection(submission: Submission, reporterSection: Option[ReporterXMLSection]) {

  val associatedEnterpriseSection: Option[AssociatedEnterprisesXMLSection] = Option(AssociatedEnterprisesXMLSection(submission))

  private[xml] def getRelevantTaxpayers: IndexedSeq[NodeSeq] =
    submission.taxpayers.map { taxpayer =>
      val date = taxpayer.implementingDate.fold(NodeSeq.Empty)(date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>)
      if (taxpayer.individual.isDefined) {
        <RelevantTaxpayer>
          {IndividualXMLSection.buildIDForIndividual(taxpayer.individual.get)}{date}{getAssociatedEnterprises(taxpayer.taxpayerId)}
        </RelevantTaxpayer>
      } else {
        <RelevantTaxpayer>
          {OrganisationXMLSection.buildIDForOrganisation(taxpayer.organisation.get)}{date}{getAssociatedEnterprises(taxpayer.taxpayerId)}
        </RelevantTaxpayer>
        }
    }

  private[xml] def getAssociatedEnterprises(taxpayerID: String) =
    associatedEnterpriseSection.map(_.buildAssociatedEnterprises(taxpayerID)).getOrElse(NodeSeq.Empty)

  def buildRelevantTaxpayers: NodeSeq =
    Try {
      <RelevantTaxPayers>
        {reporterSection.map(_.buildReporterAsTaxpayer).getOrElse(NodeSeq.Empty) ++ getRelevantTaxpayers}
      </RelevantTaxPayers>
    }.getOrElse(NodeSeq.Empty)

}
