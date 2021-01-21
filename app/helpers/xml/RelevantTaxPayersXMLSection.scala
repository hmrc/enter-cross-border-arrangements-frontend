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
import models.{CompletionState, InProgress, ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage

import scala.xml.{Elem, NodeSeq}

object RelevantTaxPayersXMLSection extends XMLBuilder {

  private[xml] def buildReporterAsTaxpayer(userAnswers: UserAnswers): Either[CompletionState, NodeSeq] =
    userAnswers.get(RoleInArrangementPage).toRight(InProgress) map {
      case RoleInArrangement.Taxpayer =>

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
    }

  private[xml] def getRelevantTaxpayers(userAnswers: UserAnswers): Either[CompletionState, NodeSeq] =
    userAnswers.get(TaxpayerLoopPage).toRight(InProgress) map {
      case taxpayers =>
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
    }

  override def toXml(userAnswers: UserAnswers): Either[CompletionState, Elem] = {

    val content: Either[CompletionState, NodeSeq] = for {
      reporterAsTaxpayer <- buildReporterAsTaxpayer(userAnswers)
      relevantTaxpayers  <- getRelevantTaxpayers(userAnswers)
    } yield {
      (reporterAsTaxpayer ++ relevantTaxpayers).flatten
    }

    build(content) {
      nodes =>
        <RelevantTaxPayers>{nodes}</RelevantTaxPayers>
    }
  }
}
