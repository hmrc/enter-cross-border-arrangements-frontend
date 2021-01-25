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

package models.affected

import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers}
import pages.affected.AffectedTypePage
import play.api.libs.json.{Json, OFormat}

import java.util.UUID

case class Affected(affectedId: String, individual: Option[Individual] = None, organisation: Option[Organisation] = None) {

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => throw new RuntimeException("Other parties affected must contain either an individual or an organisation.")
  }
}

object Affected {

  private def generateId = UUID.randomUUID.toString

  private def buildIndividualAffected(ua: UserAnswers, id: Int): Affected =
    new Affected(
      affectedId = generateId,
      individual = Some(Individual.buildIndividualDetails(ua, id)),
      None
    )

  private def buildOrganisationAffected(ua: UserAnswers, id: Int): Affected =
    new Affected(
      affectedId = generateId,
      None,
      organisation = Some(Organisation.buildOrganisationDetails(ua, id))
    )


  def buildDetails(ua: UserAnswers, id: Int): Affected = {
    ua.get(AffectedTypePage, id) match {
      case Some(SelectType.Organisation) => buildOrganisationAffected(ua, id)
      case Some(SelectType.Individual)   => buildIndividualAffected(ua, id)
      case _                             => throw new Exception("Unable to retrieve other parties affected select type")
    }
  }

  implicit val format: OFormat[Affected] = Json.format[Affected]
}

