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

import models.individual.Individual
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.{ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object RelevantTaxPayersXMLSection extends XMLBuilder {

  //1
  private[xml] def buildReporterAsTaxpayer(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(RoleInArrangementPage) match {
      case Some(RoleInArrangement.Taxpayer) =>

        val implementingDate = userAnswers.get(ReporterTaxpayersStartDateForImplementingArrangementPage).fold(NodeSeq.Empty)(
          date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>
        )

        userAnswers.get(ReporterOrganisationOrIndividualPage) match {

          case Some(ReporterOrganisationOrIndividual.Organisation) =>
            val organisationDetailsForReporter = Organisation.buildOrganisationDetailsForReporter(userAnswers)

            <RelevantTaxpayer>
              {OrganisationXMLSection.buildIDForOrganisation(organisationDetailsForReporter)}
              {implementingDate}
            </RelevantTaxpayer>

          case _ =>
            val individualDetailsForReporter = Individual.buildIndividualDetailsForReporter(userAnswers)

            <RelevantTaxpayer>
              {IndividualXMLSection.buildIDForIndividual(individualDetailsForReporter)}
              {implementingDate}
            </RelevantTaxpayer>
        }
      case _ => NodeSeq.Empty
    }
  }

  //2
  private[xml] def getRelevantTaxpayers(userAnswers: UserAnswers) = {
    userAnswers.get(TaxpayerLoopPage) match {
      case Some(taxpayers) =>
        taxpayers.map {
          taxpayer =>
            val date = taxpayer.implementingDate.fold(NodeSeq.Empty)(date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>)
            if (taxpayer.individual.isDefined) {
              <RelevantTaxpayer>
                {IndividualXMLSection.buildIDForIndividual(taxpayer.individual.get)}
                {date}
              </RelevantTaxpayer>
            } else {
              <RelevantTaxpayer>
                {OrganisationXMLSection.buildIDForOrganisation(taxpayer.organisation.get)}
                {date}
              </RelevantTaxpayer>
            }
        }
      case _ => NodeSeq.Empty
    }
  }

  //3
  override def toXml(userAnswers: UserAnswers): Either[Throwable, Elem] = {
    Try {
      <RelevantTaxPayers>
        {buildReporterAsTaxpayer(userAnswers) ++ getRelevantTaxpayers(userAnswers)}
      </RelevantTaxPayers>
    }.toEither
  }
//  override def toXml(userAnswers: UserAnswers): Either[Throwable, Elem] = {
//    Try {
//      <RelevantTaxPayers>
//        {buildReporterAsTaxpayer(userAnswers) ++ getRelevantTaxpayers(userAnswers)}
//      </RelevantTaxPayers>
//    }.toEither
//  }
}
