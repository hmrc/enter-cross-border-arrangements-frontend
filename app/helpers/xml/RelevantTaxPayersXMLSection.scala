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

import helpers.xml.IntermediariesXMLSection.build
import helpers.xml.disclosing.DiscloseDetailsLiability
import models.individual.Individual
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.{InProgress, JourneyStatus, ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage

import scala.xml.{Elem, NodeSeq}

object RelevantTaxPayersXMLSection extends XMLBuilder {

  private def implementingDate(userAnswers: UserAnswers) =
    userAnswers.get(ReporterTaxpayersStartDateForImplementingArrangementPage).map {date =>
      <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>
    }

  private[xml] def buildReporterAsTaxpayer(userAnswers: UserAnswers): Either[JourneyStatus, NodeSeq] = {

    val organisationOrIndividual: Option[NodeSeq] = userAnswers.get(ReporterOrganisationOrIndividualPage) map {
      case ReporterOrganisationOrIndividual.Organisation =>
        OrganisationXMLSection.buildIDForOrganisation {
          Organisation.buildOrganisationDetailsForReporter(userAnswers)
        }
      case ReporterOrganisationOrIndividual.Individual   =>
        IndividualXMLSection.buildIDForIndividual {
          Individual.buildIndividualDetailsForReporter(userAnswers)
        }
    }

    val content: Option[NodeSeq] = for {
      roleInArrangementPage    <- userAnswers.get(RoleInArrangementPage)
      if roleInArrangementPage == RoleInArrangement.Taxpayer
      reporter                 <- organisationOrIndividual
      date                     =  implementingDate(userAnswers)
    } yield reporter ++ date

    build(content.toRight(InProgress)) { nodes =>
      <RelevantTaxpayer>{nodes}</RelevantTaxpayer>
    }
  }

  private[xml] def getRelevantTaxpayers(userAnswers: UserAnswers): Either[JourneyStatus, NodeSeq] =
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

  override def toXml(userAnswers: UserAnswers): Either[JourneyStatus, Elem] = {

    val content: Either[JourneyStatus, NodeSeq] = for {
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
