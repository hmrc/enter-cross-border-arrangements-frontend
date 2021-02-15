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
import models.reporter.ReporterDetails
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.reporter.intermediary.IntermediaryRole
import models.reporter.taxpayer.TaxpayerWhyReportArrangement
import pages.reporter.ReporterDetailsPage

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object DisclosingXMLSection extends XMLBuilder {

  private[xml] def buildLiability(reporterDetails: ReporterDetails): NodeSeq = {

    reporterDetails.liability match {
      case Some(liability) =>

        val getCapacity = liability.capacity.fold(NodeSeq.Empty)(capacity =>
        if (capacity.equals(IntermediaryRole.Unknown.toString) ||
          capacity.equals(TaxpayerWhyReportArrangement.DoNotKnow.toString)){
          NodeSeq.Empty
        } else {
          <Capacity>{capacity}</Capacity>
        })

        (liability.role, liability.nexus) match {

          case (_, Some(nexus)) if nexus.equals("doNotKnow") => NodeSeq.Empty

          case (Taxpayer.toString, Some(nexus)) =>
              <Liability>
                <RelevantTaxpayerDiscloser>
                  <RelevantTaxpayerNexus>{nexus}</RelevantTaxpayerNexus>
                  {getCapacity}
                </RelevantTaxpayerDiscloser>
              </Liability>

          case (Intermediary.toString, Some(nexus)) =>
            <Liability>
              <IntermediaryDiscloser>
                <IntermediaryNexus>{nexus}</IntermediaryNexus>
                {getCapacity}
              </IntermediaryDiscloser>
            </Liability>

          case _ =>
            NodeSeq.Empty
        }
      case _ => NodeSeq.Empty
    }
  }

  private[xml] def buildReporterExemptions(reporterDetails: ReporterDetails): NodeSeq = {

    reporterDetails.liability match {
      case Some(liability) =>
        liability.nationalExemption match {
          case Some(true) =>
            <NationalExemption>
              <Exemption>true</Exemption>
              <CountryExemptions>
                {liability.exemptCountries.fold(NodeSeq.Empty)(countries => countries.map(country => <CountryExemption>{country}</CountryExemption>))}
              </CountryExemptions>
            </NationalExemption>

          case Some(false) =>
            <NationalExemption>
              <Exemption>false</Exemption>
            </NationalExemption>

          case _ => NodeSeq.Empty
        }
      case _ => throw new Exception("Unable to Construct XML for Reporter Exemptions")
    }
  }

  private[xml] def buildReporterCapacity(reporter: ReporterDetails): NodeSeq = {
    reporter.liability.fold(NodeSeq.Empty)(
      liability => liability.capacity match {
        case Some(IntermediaryRole.Unknown.toString) => NodeSeq.Empty
        case _ => <Capacity>{liability.capacity.get}</Capacity>
      }
    )
  }

  private[xml] def buildDiscloseDetailsForReporter(userAnswers: UserAnswers, id: Int): NodeSeq = {
    val nodeBuffer = new xml.NodeBuffer

    userAnswers.get(ReporterDetailsPage, id: Int).fold(throw new Exception("Unable to construct XML for ReporterDetails"))(
      reporterDetails =>
        if (reporterDetails.organisation.isDefined) {
          nodeBuffer ++
            OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get) ++
            buildLiability(reporterDetails)
        } else {
          nodeBuffer ++
            IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get) ++
            buildLiability(reporterDetails)
        }
    )
  }

  override def toXml(userAnswers: UserAnswers, id: Int): Either[Throwable, Elem] = {
    Try {
      <Disclosing>
        {buildDiscloseDetailsForReporter(userAnswers, id)}
      </Disclosing>
    }
  }.toEither
}
