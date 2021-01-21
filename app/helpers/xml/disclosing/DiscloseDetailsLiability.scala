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

package helpers.xml.disclosing

import helpers.xml.XMLFragmentBuilder
import models.{JourneyStatus, CountriesListEUCheckboxes, InProgress, UserAnswers}
import models.YesNoDoNotKnowRadios.{DoNotKnow, No, Yes}
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhichCountriesExemptPage, IntermediaryWhyReportInUKPage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}

import scala.xml.NodeSeq

object DiscloseDetailsLiability extends XMLFragmentBuilder {

  def build(userAnswers: UserAnswers): Either[JourneyStatus, NodeSeq] =

    userAnswers.get(RoleInArrangementPage).toRight(InProgress) map {
      case RoleInArrangement.Taxpayer     => buildTaxpayerLiability(userAnswers)
      case RoleInArrangement.Intermediary => buildIntermediaryLiability(userAnswers)
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

  private[xml] def buildReporterCapacity(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(IntermediaryRolePage).fold(NodeSeq.Empty) {
      case IntermediaryRole.Unknown => NodeSeq.Empty
      case role: IntermediaryRole =>
        <Capacity>{role.toString}</Capacity>
    }
  }

  private[xml] def buildReporterExemptions(userAnswers: UserAnswers): NodeSeq = {

    val reporterCountryExemptions =
      userAnswers.get(IntermediaryDoYouKnowExemptionsPage).fold(NodeSeq.Empty) {
        case false => NodeSeq.Empty
        case true =>
          val countryList = userAnswers.get(IntermediaryWhichCountriesExemptPage).fold(NodeSeq.Empty)(setOfCountries =>
            setOfCountries.toList.map((country: CountriesListEUCheckboxes) => <CountryExemption>{country}</CountryExemption>))

          <CountryExemptions>
            {countryList}
          </CountryExemptions>
      }

    userAnswers.get(IntermediaryExemptionInEUPage).fold(NodeSeq.Empty) {
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

  private[xml] def buildIntermediaryLiability(userAnswers: UserAnswers): NodeSeq = {

    userAnswers.get(IntermediaryWhyReportInUKPage).fold(NodeSeq.Empty) {
      case IntermediaryWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty

      case intermediaryWhyReportInUK: IntermediaryWhyReportInUK =>

        <Liability>
          <IntermediaryDiscloser>
            <IntermediaryNexus>{intermediaryWhyReportInUK.toString}</IntermediaryNexus>
            {buildReporterCapacity(userAnswers)}
          </IntermediaryDiscloser>
        </Liability>
    }
  }

}
