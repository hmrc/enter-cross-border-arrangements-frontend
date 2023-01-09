/*
 * Copyright 2023 HM Revenue & Customs
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

package models.reporter

import controllers.exceptions.SomeInformationIsMissingException
import models.individual.Individual
import models.organisation.Organisation
import models.{ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.ReporterOrganisationOrIndividualPage
import play.api.libs.json.{Json, OFormat}

case class ReporterDetails(individual: Option[Individual] = None, organisation: Option[Organisation] = None, liability: Option[ReporterLiability] = None) {

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => throw new RuntimeException("Reporter must be either an individual or an organisation.")
  }

  val isTaxpayer: Boolean = liability.exists(_.role == "taxpayer")

  val isIntermediary: Boolean = liability.exists(_.role == "intermediary")
}

object ReporterDetails {

  def buildReporterDetails(ua: UserAnswers, id: Int): ReporterDetails =
    ua.get(ReporterOrganisationOrIndividualPage, id) match {

      case Some(ReporterOrganisationOrIndividual.Organisation) =>
        new ReporterDetails(
          organisation = Some(Organisation.buildOrganisationDetailsForReporter(ua, id)),
          liability = Some(ReporterLiability.buildReporterLiability(ua, id))
        )

      case Some(ReporterOrganisationOrIndividual.Individual) =>
        new ReporterDetails(
          individual = Some(Individual.buildIndividualDetailsForReporter(ua, id)),
          liability = Some(ReporterLiability.buildReporterLiability(ua, id))
        )
      case _ => throw new SomeInformationIsMissingException(id, "Reporter must be either an organisation or an individual")
    }
  implicit val format: OFormat[ReporterDetails] = Json.format[ReporterDetails]
}
