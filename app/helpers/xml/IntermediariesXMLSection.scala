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

import models.IsExemptionKnown.{No, Unknown, Yes}
import models.{CountryList, Submission}
import models.intermediaries.WhatTypeofIntermediary.IDoNotKnow
import models.intermediaries.Intermediary
import models.reporter.RoleInArrangement

import scala.util.Try
import scala.xml.{Node, NodeSeq}

case class IntermediariesXMLSection(submission: Submission) {

  private val isReporterIntermediary: Option[Boolean] = submission.reporterDetails.flatMap(
    _.liability.map(
      x => x.role == RoleInArrangement.Intermediary.toString
    )
  )

  private[xml] def buildReporterAsIntermediary: NodeSeq =
    (isReporterIntermediary, submission.reporterDetails) match {
      case (Some(true), Some(reporterDetails)) =>
        if (reporterDetails.organisation.isDefined) {
          <Intermediary>
            {OrganisationXMLSection.buildIDForOrganisation(reporterDetails.organisation.get)}
            {ReporterXMLSection(submission).buildReporterCapacity(reporterDetails)}
            {ReporterXMLSection(submission).buildReporterExemptions}
          </Intermediary>
        } else {
          <Intermediary>
            {IndividualXMLSection.buildIDForIndividual(reporterDetails.individual.get)}
            {ReporterXMLSection(submission).buildReporterCapacity(reporterDetails)}
            {ReporterXMLSection(submission).buildReporterExemptions}
          </Intermediary>
        }

      case _ => NodeSeq.Empty
    }

  private[xml] def getIntermediaryCapacity(intermediary: Intermediary): NodeSeq =
    if (intermediary.whatTypeofIntermediary.equals(IDoNotKnow)) {
      NodeSeq.Empty
    } else {
      <Capacity>{intermediary.whatTypeofIntermediary.toString}</Capacity>
    }

  private[xml] def buildNationalExemption(intermediary: Intermediary): NodeSeq = {

    val countryExemptions = intermediary.isExemptionCountryKnown.fold(NodeSeq.Empty) {
      case false => NodeSeq.Empty
      case true =>
        val getCountries = intermediary.exemptCountries.fold(NodeSeq.Empty)(
          setOfCountries =>
            setOfCountries.toList.map(
              (country: CountryList) => <CountryExemption>{country}</CountryExemption>
            )
        )

        <CountryExemptions>
          {getCountries}
        </CountryExemptions>
    }

    val nationalExemption = intermediary.isExemptionKnown match {
      case Unknown => NodeSeq.Empty
      case No =>
        <NationalExemption>
          <Exemption>false</Exemption>
        </NationalExemption>
      case Yes =>
        <NationalExemption>
          <Exemption>true</Exemption>
          {countryExemptions}
        </NationalExemption>
    }
    nationalExemption
  }

  private[xml] def getIntermediaries: Seq[Node] =
    submission.intermediaries.map {
      intermediary =>
        if (intermediary.individual.isDefined) {
          <Intermediary>
            {
            IndividualXMLSection.buildIDForIndividual(intermediary.individual.get) ++
              getIntermediaryCapacity(intermediary) ++
              buildNationalExemption(intermediary)
          }
          </Intermediary>
        } else {
          <Intermediary>
            {
            OrganisationXMLSection.buildIDForOrganisation(intermediary.organisation.get) ++
              getIntermediaryCapacity(intermediary) ++
              buildNationalExemption(intermediary)
          }
          </Intermediary>
        }
    }

  def buildIntermediaries: NodeSeq =
    Try {
      <Intermediaries>
        {buildReporterAsIntermediary ++ getIntermediaries}
      </Intermediaries>
    }.getOrElse(NodeSeq.Empty)
}
