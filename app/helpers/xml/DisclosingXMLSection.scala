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

import models.{CompletionState, UserAnswers}
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import pages.reporter.RoleInArrangementPage
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object DisclosingXMLSection extends XMLBuilder {

  private[xml] def buildLiability(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(TaxpayerWhyReportInUKPage).fold(NodeSeq.Empty) {
      case TaxpayerWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty
      case taxpayerWhyReportInUK: TaxpayerWhyReportInUK =>
        val mandatoryRelevantTaxpayerNexus: NodeSeq =
          userAnswers.get(RoleInArrangementPage) match {
            case Some(RoleInArrangement.Taxpayer) =>
              <RelevantTaxpayerNexus>{taxpayerWhyReportInUK.toString}</RelevantTaxpayerNexus>
            case _ =>
              throw new Exception("Missing report details when building Disclosing XML section")
          }

        val capacity: NodeSeq = userAnswers.get(TaxpayerWhyReportArrangementPage)
          .fold(NodeSeq.Empty) {
            case TaxpayerWhyReportArrangement.DoNotKnow => NodeSeq.Empty
            case capacity: TaxpayerWhyReportArrangement => <Capacity>{capacity.toString}</Capacity>
          }

        val nodeBuffer = new xml.NodeBuffer
        val relevantTaxPayersNode = {
          nodeBuffer ++
            mandatoryRelevantTaxpayerNexus ++
            capacity
        }

        <Liability>
          <RelevantTaxpayerDiscloser>{relevantTaxPayersNode}</RelevantTaxpayerDiscloser>
        </Liability>
    }
  }

  private[xml] def buildDiscloseDetailsForOrganisation(userAnswers: UserAnswers): Either[CompletionState, NodeSeq] = {
    val nodeBuffer = new xml.NodeBuffer

    val organisationDetailsForReporter = Organisation.buildOrganisationDetailsForReporter(userAnswers)

    nodeBuffer ++
      RelevantTaxPayersXMLSection.buildIDForOrganisation(organisationDetailsForReporter) ++
      buildLiability(userAnswers)
  }

  override def toXml(userAnswers: UserAnswers): Either[Throwable, Elem] = {
    //TODO Need to check here if reporter is an individual or organisation then return correct section

    val content: Either[CompletionState, NodeSeq] = for {
      discloseDetailsForOrganisation <- buildDiscloseDetailsForOrganisation(userAnswers)
    } yield {
      discloseDetailsForOrganisation
    }

    build(content) { nodes =>
      <RelevantTaxPayers>{nodes}</RelevantTaxPayers>
    }

    Try {
      <Disclosing>
        {buildDiscloseDetailsForOrganisation(userAnswers)}
      </Disclosing>
    }.toEither
  }
}
