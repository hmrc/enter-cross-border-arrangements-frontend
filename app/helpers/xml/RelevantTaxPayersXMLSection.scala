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

import models.UserAnswers
import models.reporter.RoleInArrangement
import pages.reporter.ReporterDetailsPage
import pages.taxpayer.TaxpayerLoopPage

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object RelevantTaxPayersXMLSection extends XMLBuilder {

  private[xml] def buildReporterAsTaxpayer(userAnswers: UserAnswers, id: Int): NodeSeq = {

    userAnswers.get(ReporterDetailsPage, id) match {
      case Some(reporterDetails) =>
        val implementingDate = reporterDetails.liability.fold(NodeSeq.Empty)(liability => liability.implementingDate.fold(NodeSeq.Empty)(
          date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>
        ))

        reporterDetails.liability.fold(NodeSeq.Empty)(liability => liability.role match {
          case RoleInArrangement.Taxpayer.toString =>
            if (reporterDetails.organisation.isDefined) {
              <RelevantTaxpayer>
                {OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get)}
                {implementingDate}
              </RelevantTaxpayer>
            } else {
              <RelevantTaxpayer>
                {IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get)}
                {implementingDate}
              </RelevantTaxpayer>
            }
          case _ => NodeSeq.Empty
        })

      case _ => NodeSeq.Empty
    }
  }

  private[xml] def getRelevantTaxpayers(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(TaxpayerLoopPage, id) match {
      case Some(taxpayers) =>
        taxpayers.map {
          taxpayer =>
            val date = taxpayer.implementingDate.fold(NodeSeq.Empty)(date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>)
            if (taxpayer.individual.isDefined) {
              <RelevantTaxpayer>
                {IndividualXMLSection.buildIDForIndividual(taxpayer.individual.get)}
                {date}
                {AssociatedEnterprisesSection.buildAssociatedEnterprises(userAnswers, id, taxpayer.individual.get.nameAsString)}
              </RelevantTaxpayer>
            } else {
              <RelevantTaxpayer>
                {OrganisationXMLSection.buildIDForOrganisation(taxpayer.organisation.get)}
                {date}
                {AssociatedEnterprisesSection.buildAssociatedEnterprises(userAnswers, id, taxpayer.organisation.get.organisationName)}
              </RelevantTaxpayer>
            }
        }
      case _ => NodeSeq.Empty
    }
  }

  override def toXml(userAnswers: UserAnswers, id: Int): Either[Throwable, Elem] = {
    Try {
      <RelevantTaxPayers>
        {buildReporterAsTaxpayer(userAnswers, id) ++ getRelevantTaxpayers(userAnswers, id)}
      </RelevantTaxPayers>
    }.toEither
  }
}
