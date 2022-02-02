/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.exceptions.SomeInformationIsMissingException
import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers, WithIndividualOrOrganisation, WithRestore}
import pages.affected.{AffectedCheckYourAnswersPage, AffectedTypePage}
import play.api.libs.json.{Json, OFormat}

import java.util.UUID
import scala.util.Try

case class Affected(affectedId: String, individual: Option[Individual] = None, organisation: Option[Organisation] = None)
    extends WithIndividualOrOrganisation
    with WithRestore {

  override def matchItem(itemId: String): Boolean = affectedId == itemId

  implicit val a: Affected = implicitly(this)

  override def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    userAnswers
      .set(AffectedCheckYourAnswersPage, id)
      .flatMap(_.set(AffectedTypePage, id))
      .flatMap(restoreFromIndividualOrOrganisation(_, id))
}

object Affected {

  def apply(ua: UserAnswers, id: Int): Affected = {

    val affected: Affected = ua
      .get(AffectedCheckYourAnswersPage, id)
      .fold(Affected(UUID.randomUUID.toString))(Affected(_))
    ua.get(AffectedTypePage, id) match {
      case Some(SelectType.Organisation) => affected.copy(organisation = Some(Organisation.buildOrganisationDetails(ua, id)))
      case Some(SelectType.Individual)   => affected.copy(individual = Some(Individual.buildIndividualDetails(ua, id)))
      case _                             => throw new SomeInformationIsMissingException(id, "Unable to retrieve other parties affected select type")
    }
  }

  implicit val format: OFormat[Affected] = Json.format[Affected]
}
