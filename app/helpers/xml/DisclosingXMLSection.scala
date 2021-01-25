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

import models.YesNoDoNotKnowRadios.{DoNotKnow, No, Yes}
import models.individual.Individual
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{CountriesListEUCheckboxes, ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.intermediary._
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object DisclosingXMLSection extends XMLBuilder {

  private[xml] def buildLiability(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(RoleInArrangementPage, id) match {
      case Some(RoleInArrangement.Taxpayer) => buildTaxpayerLiability(userAnswers, id)
      case _ => buildIntermediaryLiability(userAnswers, id)
    }
  }

  private[xml] def buildTaxpayerLiability(userAnswers: UserAnswers, id: Int): NodeSeq = {
    lazy val nodeBuffer = new xml.NodeBuffer

    userAnswers.get(TaxpayerWhyReportInUKPage, id).fold(NodeSeq.Empty) {
      case TaxpayerWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty

      case taxpayerWhyReportInUK: TaxpayerWhyReportInUK =>
        val taxpayerNexus = <RelevantTaxpayerNexus>{taxpayerWhyReportInUK.toString}</RelevantTaxpayerNexus>

        val capacity: NodeSeq = userAnswers.get(TaxpayerWhyReportArrangementPage, id)
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

  private[xml] def buildReporterCapacity(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(IntermediaryRolePage, id: Int).fold(NodeSeq.Empty) {
      case IntermediaryRole.Unknown => NodeSeq.Empty
      case role: IntermediaryRole =>
        <Capacity>{role.toString}</Capacity>
    }
  }

  private[xml] def buildReporterExemptions(userAnswers: UserAnswers, id: Int): NodeSeq = {

    val reporterCountryExemptions =
      userAnswers.get(IntermediaryDoYouKnowExemptionsPage, id).fold(NodeSeq.Empty) {
        case false => NodeSeq.Empty
        case true =>
          val countryList = userAnswers.get(IntermediaryWhichCountriesExemptPage, id).fold(NodeSeq.Empty)(setOfCountries =>
          setOfCountries.toList.map((country: CountriesListEUCheckboxes) => <CountryExemption>{country}</CountryExemption>))

            <CountryExemptions>
              {countryList}
            </CountryExemptions>
      }

    userAnswers.get(IntermediaryExemptionInEUPage, id).fold(NodeSeq.Empty) {
      case DoNotKnow => NodeSeq.Empty
      case No =>
        <NationalExemption>
          <Exemption>false</Exemption>
        </NationalExemption>
      case Yes =>
        <NationalExemption>
          <Exemption>true</Exemption>
          {reporterCountryExemptions}
        </NationalExemption>
    }
  }

  private[xml] def buildIntermediaryLiability(userAnswers: UserAnswers, id: Int): NodeSeq = {

    userAnswers.get(IntermediaryWhyReportInUKPage, id).fold(NodeSeq.Empty) {
      case IntermediaryWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty

      case intermediaryWhyReportInUK: IntermediaryWhyReportInUK =>

        <Liability>
          <IntermediaryDiscloser>
            <IntermediaryNexus>{intermediaryWhyReportInUK.toString}</IntermediaryNexus>
            {buildReporterCapacity(userAnswers, id)}
          </IntermediaryDiscloser>
        </Liability>
    }
  }

  private[xml] def buildDiscloseDetailsForOrganisation(userAnswers: UserAnswers, id: Int): NodeSeq = {
    val nodeBuffer = new xml.NodeBuffer

    val organisationDetailsForReporter = Organisation.buildOrganisationDetailsForReporter(userAnswers, id)

    nodeBuffer ++
      OrganisationXMLSection.buildIDForOrganisation(organisationDetailsForReporter) ++
      buildLiability(userAnswers, id)
  }


  private[xml] def buildDiscloseDetailsForIndividual(userAnswers: UserAnswers, id: Int): NodeSeq = {
    val nodeBuffer = new xml.NodeBuffer

    val individualDetailsForReporter = Individual.buildIndividualDetailsForReporter(userAnswers, id)

    nodeBuffer ++
      IndividualXMLSection.buildIDForIndividual(individualDetailsForReporter) ++
      buildLiability(userAnswers, id)
  }

  override def toXml(userAnswers: UserAnswers, id: Int): Either[Throwable, Elem] = {

    Try {
      userAnswers.get(ReporterOrganisationOrIndividualPage, id) match {
        case Some(ReporterOrganisationOrIndividual.Organisation) =>
          <Disclosing>
            {buildDiscloseDetailsForOrganisation(userAnswers, id)}
          </Disclosing>
        case _ =>
          <Disclosing>
            {buildDiscloseDetailsForIndividual(userAnswers, id)}
          </Disclosing>
      }
    }.toEither
  }
}
