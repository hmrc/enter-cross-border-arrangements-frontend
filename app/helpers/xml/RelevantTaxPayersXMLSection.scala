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

import models.taxpayer.Taxpayer

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

case class RelevantTaxPayersXMLSection(taxpayers: IndexedSeq[Taxpayer]
                                       , reporterSection: Option[ReporterXMLSection]
                                       , associatedEnterpriseSection: Option[AssociatedEnterprisesXMLSection]) {

  private[xml] def getRelevantTaxpayerst: IndexedSeq[NodeSeq] =
    taxpayers.map { taxpayer =>
      val date = taxpayer.implementingDate.fold(NodeSeq.Empty)(date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>)
      if (taxpayer.individual.isDefined) {
        <RelevantTaxpayer>
          {IndividualXMLSection.buildIDForIndividual(taxpayer.individual.get)}{date}{getAssociatedEnterprises(taxpayer.individual.get.nameAsString)}
        </RelevantTaxpayer>
      } else {
        <RelevantTaxpayer>
          {OrganisationXMLSection.buildIDForOrganisation(taxpayer.organisation.get)}{date}{getAssociatedEnterprises(taxpayer.organisation.get.organisationName)}
        </RelevantTaxpayer>
        }
    }

  private[xml] def getAssociatedEnterprises(name: String) =
    associatedEnterpriseSection.map(_.buildAssociatedEnterprises(name)).getOrElse(NodeSeq.Empty)

  def buildRelevantTaxpayers: Either[Throwable, Elem] =
    Try {
      <RelevantTaxPayers>
        {reporterSection.map(_.buildReporterAsTaxpayer).getOrElse(NodeSeq.Empty) ++ getRelevantTaxpayerst}
      </RelevantTaxPayers>
    }.toEither

}
