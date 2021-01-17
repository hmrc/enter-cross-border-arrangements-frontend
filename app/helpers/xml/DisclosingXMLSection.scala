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
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.intermediary.{IntermediaryRolePage, IntermediaryWhyReportInUKPage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object DisclosingXMLSection extends XMLBuilder {

  private[xml] def buildLiability(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(RoleInArrangementPage) match {
      case Some(RoleInArrangement.Taxpayer) => buildTaxpayerLiability(userAnswers)
      case _ => buildIntermediaryLiability(userAnswers)
    }
  }

  private[xml] def buildTaxpayerLiability(userAnswers: UserAnswers): NodeSeq = {
    lazy val nodeBuffer = new xml.NodeBuffer

    userAnswers.get(TaxpayerWhyReportInUKPage).fold(NodeSeq.Empty) {
      case TaxpayerWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty

      case taxpayerWhyReportInUK: TaxpayerWhyReportInUK =>
        val taxpayerNexus = <RelevantTaxpayerNexus>{taxpayerWhyReportInUK.toString}</RelevantTaxpayerNexus>

        val capacity: NodeSeq = userAnswers.get(TaxpayerWhyReportArrangementPage)
          .fold(NodeSeq.Empty) {
            case TaxpayerWhyReportArrangement.DoNotKnow => NodeSeq.Empty
            case reason: TaxpayerWhyReportArrangement =>
              <Capacity>{reason.toString}</Capacity>
          }

        <Liability>
          <RelevantTaxpayerDiscloser>
            {nodeBuffer ++
            taxpayerNexus ++
            capacity}
          </RelevantTaxpayerDiscloser>
        </Liability>
    }
  }

  private[xml] def buildIntermediaryLiability(userAnswers: UserAnswers): NodeSeq = {
    lazy val nodeBuffer = new xml.NodeBuffer

    userAnswers.get(IntermediaryWhyReportInUKPage).fold(NodeSeq.Empty) {
      case IntermediaryWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty

      case intermediaryWhyReportInUK: IntermediaryWhyReportInUK =>
        val intermediaryNexus = <IntermediaryNexus>{intermediaryWhyReportInUK.toString}</IntermediaryNexus>

        val capacity: NodeSeq = userAnswers.get(IntermediaryRolePage).fold(NodeSeq.Empty) {
          case IntermediaryRole.Unknown => NodeSeq.Empty
          case role: IntermediaryRole =>
            <Capacity>{role.toString}</Capacity>
        }

        <Liability>
          <IntermediaryDiscloser>
            {nodeBuffer ++
            intermediaryNexus ++
            capacity}
          </IntermediaryDiscloser>
        </Liability>
    }
  }

  private[xml] def buildDiscloseDetailsForOrganisation(userAnswers: UserAnswers): NodeSeq = {
    val nodeBuffer = new xml.NodeBuffer

    val organisationDetailsForReporter = Organisation.buildOrganisationDetailsForReporter(userAnswers)

    nodeBuffer ++
      RelevantTaxPayersXMLSection.buildIDForOrganisation(organisationDetailsForReporter) ++
      buildLiability(userAnswers)
  }


  private[xml] def buildDiscloseDetailsForIndividual(userAnswers: UserAnswers): NodeSeq = {
    val nodeBuffer = new xml.NodeBuffer

    val individualDetailsForReporter = Individual.buildIndividualDetailsForReporter(userAnswers)

    nodeBuffer ++
      RelevantTaxPayersXMLSection.buildIDForIndividual(individualDetailsForReporter) ++
      buildLiability(userAnswers)
  }

  override def toXml(userAnswers: UserAnswers): Either[Throwable, Elem] = {

    Try {
      userAnswers.get(ReporterOrganisationOrIndividualPage) match {
        case Some(ReporterOrganisationOrIndividual.Organisation) =>
          <Disclosing>
            {buildDiscloseDetailsForOrganisation(userAnswers)}
          </Disclosing>
        case _ =>
          <Disclosing>
            {buildDiscloseDetailsForIndividual(userAnswers)}
          </Disclosing>
      }
    }.toEither
  }
}
