/*
 * Copyright 2020 HM Revenue & Customs
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

package models.intermediaries

import java.util.UUID

import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers}
import pages.intermediaries.IntermediariesTypePage
import play.api.libs.json.{Json, OFormat}

case class Intermediary (intermediaryId: String, individual: Option[Individual] = None, organisation: Option[Organisation] = None) {

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => throw new RuntimeException("Intermediary must contain either an individual or an organisation.")
  }
}

object Intermediary {

  private def generateId = UUID.randomUUID.toString

  def buildIntermediaryDetails(ua: UserAnswers): Intermediary = {
    ua.get(IntermediariesTypePage) match {
      case Some(SelectType.Organisation) =>
        new Intermediary(
          intermediaryId = generateId,
          organisation = Some(Organisation.buildOrganisationDetails(ua))
        )
      case Some(SelectType.Individual) =>
        new Intermediary(
          intermediaryId = generateId,
          individual = Some(Individual.buildIndividualDetails(ua))
        )
      case _ => throw new Exception("Unable to retrieve Intermediary select type")
    }
  }

  implicit val format: OFormat[Intermediary] = Json.format[Intermediary]
}
