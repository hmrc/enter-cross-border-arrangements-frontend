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

package models.intermediaries

import java.util.UUID
import models.individual.Individual
import models.organisation.Organisation
import models.{IsExemptionKnown, SelectType, UserAnswers}
import pages.intermediaries.{ExemptCountriesPage, IntermediariesTypePage, IsExemptionCountryKnownPage, IsExemptionKnownPage, WhatTypeofIntermediaryPage}
import play.api.libs.json.{Json, OFormat}

case class Intermediary(intermediaryId: String,
                        individual: Option[Individual] = None,
                        organisation: Option[Organisation] = None,
                        whatTypeofIntermediary: WhatTypeofIntermediary,
                        isExemptionKnown: IsExemptionKnown,
                        isExemptionCountryKnown: Option[Boolean],
                        exemptCountries: Option[Set[ExemptCountries]]){

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => throw new RuntimeException("Intermediary must contain either an individual or an organisation.")
  }
}

object Intermediary {

  private def generateId = UUID.randomUUID.toString

  def getIntermediaryAnswers(ua: UserAnswers): (WhatTypeofIntermediary, IsExemptionKnown, Option[Boolean], Option[Set[ExemptCountries]]) = {
    (ua.get(WhatTypeofIntermediaryPage),
      ua.get(IsExemptionKnownPage),
      ua.get(IsExemptionCountryKnownPage),
      ua.get(ExemptCountriesPage)) match {
      case (Some(whatTypeOfIntermediary), Some(isExemptionKnown), Some(isExemptionCountryKnown),Some(exemptCountries)) =>
        (whatTypeOfIntermediary,isExemptionKnown, Some(isExemptionCountryKnown), Some(exemptCountries))
      case (Some(whatTypeOfIntermediary), Some(isExemptionKnown), Some(isExemptionCountryKnown),None)
        => (whatTypeOfIntermediary,isExemptionKnown, Some(isExemptionCountryKnown), None)
      case (Some(whatTypeOfIntermediary), Some(isExemptionKnown), None ,None)
      => (whatTypeOfIntermediary, isExemptionKnown, None, None)
      case _ =>
        throw new Exception("Unable to build intermediary")
    }
  }

  private def buildIndividualIntermediary(ua: UserAnswers): Intermediary = {
    val intermediaryAnswers = getIntermediaryAnswers(ua)
    new Intermediary(
                intermediaryId = generateId,
                individual = Some(Individual.buildIndividualDetails(ua)),
                None,
                intermediaryAnswers._1,
                intermediaryAnswers._2,
                intermediaryAnswers._3,
                intermediaryAnswers._4
              )
  }

  private def buildOrganisationIntermediary(ua: UserAnswers): Intermediary = {
    val intermediaryAnswers = getIntermediaryAnswers(ua)
    new Intermediary(
      intermediaryId = generateId,
      None,
      organisation = Some(Organisation.buildOrganisationDetails(ua)),
      intermediaryAnswers._1,
      intermediaryAnswers._2,
      intermediaryAnswers._3,
      intermediaryAnswers._4
    )
  }


  def buildIntermediaryDetails(ua: UserAnswers): Intermediary = {
    ua.get(IntermediariesTypePage) match {
      case Some(SelectType.Organisation) =>buildOrganisationIntermediary(ua)
      case Some(SelectType.Individual) => buildIndividualIntermediary(ua)
      case _ => throw new Exception("Unable to retrieve Intermediary select type")
    }
  }

  implicit val format: OFormat[Intermediary] = Json.format[Intermediary]
}
