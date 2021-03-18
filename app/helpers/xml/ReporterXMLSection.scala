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
import models.reporter.{ReporterDetails, RoleInArrangement}
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.reporter.intermediary.IntermediaryRole
import models.reporter.taxpayer.TaxpayerWhyReportArrangement

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

case class ReporterXMLSection(submission: Submission) {

  val reporterDetails: Option[ReporterDetails] = submission.reporterDetails
  val associatedEnterpriseSection: Option[AssociatedEnterprisesXMLSection] = Option(AssociatedEnterprisesXMLSection(submission))

  val empty: Elem =
    <ID>
      <Organisation>
        <OrganisationName>X</OrganisationName>
        <ResCountryCode>GB</ResCountryCode>
      </Organisation>
    </ID>

  def buildDisclosureDetails: NodeSeq = {
    Try {
      <Disclosing>
        {buildDiscloseDetailsForReporter}
      </Disclosing>
    }
  }.getOrElse(NodeSeq.Empty)

  private[xml] def buildLiability: NodeSeq = {

    reporterDetails.flatMap(_.liability) match {
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

  private[xml] def buildReporterExemptions: NodeSeq = {

    reporterDetails.flatMap(_.liability) match {
      case Some(liability) =>
        liability.nationalExemption match {
          case Some(true) =>
            val countryExemptions =
              if (liability.exemptCountries.isDefined && liability.exemptCountries.get.nonEmpty) {
                <CountryExemptions>
                  {liability.exemptCountries.get.map(country => <CountryExemption>{country}</CountryExemption>)}
                </CountryExemptions>
            } else {
              NodeSeq.Empty
            }

            <NationalExemption>
              <Exemption>true</Exemption>
              {countryExemptions}
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

  private[xml] def buildReporterCapacity: NodeSeq = {
    reporterDetails.flatMap(_.liability).fold(NodeSeq.Empty)(
      liability => liability.capacity match {
        case Some(IntermediaryRole.Unknown.toString) => NodeSeq.Empty
        case _ => <Capacity>{liability.capacity.get}</Capacity>
      }
    )
  }

  private[xml] def buildDiscloseDetailsForReporter: NodeSeq = {
    val nodeBuffer = new xml.NodeBuffer

    reporterDetails.fold[NodeSeq](empty)(
      reporterDetails =>
        if (reporterDetails.organisation.isDefined) {
          nodeBuffer ++
            OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get) ++
            buildLiability
        } else if (reporterDetails.individual.isDefined) {
          nodeBuffer ++
            IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get) ++
            buildLiability
        } else {
          throw new Exception("Unable to construct XML for ReporterDetails")
        }
    )
  }

  def buildReporterAsTaxpayer: NodeSeq = {

    reporterDetails match {
      case Some(reporterDetails) =>

        val implementingDate = reporterDetails.liability.fold(NodeSeq.Empty)(liability => liability.implementingDate.fold(NodeSeq.Empty)(
          date => <TaxpayerImplementingDate>{date}</TaxpayerImplementingDate>
        ))

        reporterDetails.liability.fold(NodeSeq.Empty)(liability => liability.role match {
          case RoleInArrangement.Taxpayer.toString =>
            if (reporterDetails.organisation.isDefined) {
              <RelevantTaxpayer>
                {OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get)}{implementingDate}{getAssociatedEnterprises(reporterDetails.organisation.get.organisationName)}
              </RelevantTaxpayer>
            } else {
              <RelevantTaxpayer>
                {IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get)}{implementingDate}{getAssociatedEnterprises(reporterDetails.individual.get.nameAsString)}
              </RelevantTaxpayer>
            }
          case _ => NodeSeq.Empty
        })
    }
  }

  private[xml] def getAssociatedEnterprises(name: String) =
    associatedEnterpriseSection.map(_.buildAssociatedEnterprises(name)).getOrElse(NodeSeq.Empty)

  private[xml] def buildReporterAsIntermediary: NodeSeq = {

    reporterDetails match {
      case Some(reporterDetails) =>
        reporterDetails.liability.fold(NodeSeq.Empty)(liability => liability.role match {
          case RoleInArrangement.Intermediary.toString =>
            if (reporterDetails.organisation.isDefined) {
              <Intermediary>
                {OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get)}
                {buildReporterCapacity}
                {buildReporterExemptions}
              </Intermediary>
            } else {
              <Intermediary>
                {IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get)}
                {buildReporterCapacity}
                {buildReporterExemptions}
              </Intermediary>
            }
          case _ => NodeSeq.Empty
        })
      case _ => throw new Exception("Unable to construct XML for Reporter Details as Intermediary")
    }
  }



}
