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

package models.intermediaries

import controllers.exceptions.SomeInformationIsMissingException
import models.individual.Individual
import models.intermediaries.WhatTypeofIntermediary.IDoNotKnow
import models.organisation.Organisation
import models.{CountryList, IsExemptionKnown, SelectType, UserAnswers, WithIndividualOrOrganisation, WithRestore}
import pages.intermediaries._
import play.api.libs.json.{Json, OFormat}

import java.util.UUID
import scala.util.Try

case class Intermediary(intermediaryId: String,
                        individual: Option[Individual] = None,
                        organisation: Option[Organisation] = None,
                        whatTypeofIntermediary: WhatTypeofIntermediary = IDoNotKnow,
                        isExemptionKnown: IsExemptionKnown = IsExemptionKnown.Unknown,
                        isExemptionCountryKnown: Option[Boolean] = None,
                        exemptCountries: Option[Set[CountryList]] = None
) extends WithIndividualOrOrganisation
    with WithRestore {

  override def matchItem(itemId: String): Boolean = intermediaryId == itemId

  implicit val a: Intermediary = implicitly(this)

  override def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    userAnswers
      .set(IntermediariesCheckYourAnswersPage, id)
      .flatMap(_.set(IntermediariesTypePage, id))
      .flatMap(_.set(ExemptCountriesPage, id))
      .flatMap(_.set(IsExemptionCountryKnownPage, id))
      .flatMap(_.set(IsExemptionKnownPage, id))
      .flatMap(_.set(WhatTypeofIntermediaryPage, id))
      .flatMap(restoreFromIndividualOrOrganisation(_, id))
}

object Intermediary {

  private def generateId = UUID.randomUUID.toString

  def apply(ua: UserAnswers, id: Int): Intermediary = {
    val intermediary = (ua.get(IntermediariesCheckYourAnswersPage, id).orElse(Some(generateId)),
                        ua.get(WhatTypeofIntermediaryPage, id),
                        ua.get(IsExemptionKnownPage, id),
                        ua.get(IsExemptionCountryKnownPage, id),
                        ua.get(ExemptCountriesPage, id)
    ) match {
      case (Some(itemId), Some(whatTypeOfIntermediary), Some(IsExemptionKnown.Yes), Some(true), Some(exemptCountries)) =>
        this(itemId, None, None, whatTypeOfIntermediary, IsExemptionKnown.Yes, Some(true), Some(exemptCountries))
      case (Some(itemId), Some(whatTypeOfIntermediary), Some(IsExemptionKnown.Yes), Some(false), None) =>
        this(itemId, None, None, whatTypeOfIntermediary, IsExemptionKnown.Yes, Some(false), None)
      case (Some(itemId), Some(whatTypeOfIntermediary), Some(IsExemptionKnown.No), None, None) =>
        this(itemId, None, None, whatTypeOfIntermediary, IsExemptionKnown.No, None, None)
      case (Some(itemId), Some(whatTypeOfIntermediary), Some(IsExemptionKnown.Unknown), None, None) =>
        this(itemId, None, None, whatTypeOfIntermediary, IsExemptionKnown.Unknown, None, None)
      case _ =>
        throw new SomeInformationIsMissingException(id, "Unable to build intermediary")
    }
    ua.get(IntermediariesTypePage, id) match {
      case Some(SelectType.Organisation) => intermediary.copy(organisation = Some(Organisation.buildOrganisationDetails(ua, id)))
      case Some(SelectType.Individual)   => intermediary.copy(individual = Some(Individual.buildIndividualDetails(ua, id)))
      case _                             => throw new Exception("Unable to retrieve Intermediary select type")
    }
  }

  implicit val format: OFormat[Intermediary] = Json.format[Intermediary]
}
