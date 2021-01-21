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

import helpers.xml.disclosing.DiscloseDetailsLiability
import models.IsExemptionKnown.{No, Unknown, Yes}
import models.individual.Individual
import models.intermediaries.WhatTypeofIntermediary.IDoNotKnow
import models.intermediaries.{ExemptCountries, Intermediary}
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.{InProgress, JourneyStatus, ReporterOrganisationOrIndividual, UserAnswers}
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}

import scala.xml.{Elem, NodeSeq}

object IntermediariesXMLSection extends XMLBuilder {


  private[xml] def buildReporterAsIntermediary(userAnswers: UserAnswers): Either[JourneyStatus, NodeSeq] = {

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
      if roleInArrangementPage == RoleInArrangement.Intermediary
      reporter                 <- organisationOrIndividual
      reporterCapacity         =  DiscloseDetailsLiability.buildReporterCapacity(userAnswers)
      reporterExemptions       =  DiscloseDetailsLiability.buildReporterExemptions(userAnswers)
    } yield reporter ++ reporterCapacity ++ reporterExemptions

    build(content.toRight(InProgress)) { nodes =>
      <Intermediary>{nodes}</Intermediary>
    }

  }

  private[xml] def getIntermediaryCapacity(intermediary: Intermediary): NodeSeq = {
    if (intermediary.whatTypeofIntermediary.equals(IDoNotKnow)) {
      NodeSeq.Empty
    } else {
      <Capacity>{intermediary.whatTypeofIntermediary.toString}</Capacity>
    }
  }

  private[xml] def buildNationalExemption(intermediary: Intermediary): NodeSeq = {

    val countryExemptions = intermediary.isExemptionCountryKnown.fold(NodeSeq.Empty) {
      case false => NodeSeq.Empty
      case true =>
        val getCountries = intermediary.exemptCountries.fold(NodeSeq.Empty)(setOfCountries =>
          setOfCountries.toList.map((country: ExemptCountries) =>
              <CountryExemption>{country}</CountryExemption>))

        <CountryExemptions>{getCountries}</CountryExemptions>
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

  private[xml] def getIntermediaries(userAnswers: UserAnswers): Either[JourneyStatus, NodeSeq] =

    userAnswers.get(IntermediaryLoopPage).toRight(InProgress) map {
      case intermediariesList =>
        intermediariesList.map {
          intermediary =>
            if (intermediary.individual.isDefined) {
              <Intermediary>
                {IndividualXMLSection.buildIDForIndividual(intermediary.individual.get) ++
                getIntermediaryCapacity(intermediary) ++
                buildNationalExemption(intermediary)}
              </Intermediary>
            } else {
              <Intermediary>
                {OrganisationXMLSection.buildIDForOrganisation(intermediary.organisation.get) ++
                getIntermediaryCapacity(intermediary) ++
                buildNationalExemption(intermediary)}
              </Intermediary>
            }
        }
    }

  override def toXml(userAnswers: UserAnswers): Either[JourneyStatus, Elem] = {
    val content: Either[JourneyStatus, NodeSeq] = for {
      reporterAsIntermediary <- buildReporterAsIntermediary(userAnswers)
      intermediaries         <- getIntermediaries(userAnswers)
    } yield {
      (reporterAsIntermediary ++ intermediaries).flatten
    }

    build(content) {
      nodes =>
        <Intermediaries>{nodes}</Intermediaries>
    }
  }
}
