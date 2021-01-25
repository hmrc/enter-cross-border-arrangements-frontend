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

package models.enterprises

import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers}
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage}
import play.api.libs.json.{Json, OFormat}

import java.util.UUID

case class AssociatedEnterprise(enterpriseId: String,
                                individual: Option[Individual] = None,
                                organisation: Option[Organisation] = None,
                                associatedTaxpayers: List[String],
                                isAffectedBy: Boolean) {

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => throw new RuntimeException("Associated enterprise must contain either an individual or an organisation.")
  }
}

object AssociatedEnterprise {

  private def generateId: String = UUID.randomUUID.toString

  private def getAssociatedEnterpriseAnswers(ua: UserAnswers, id: Int): (List[String], Boolean) = {
    (ua.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id), ua.get(IsAssociatedEnterpriseAffectedPage, id)) match {
      case (Some(associatedTaxpayers), Some(isAffectedBy)) => (associatedTaxpayers, isAffectedBy)
      case _ => throw new Exception("Unable to build associated enterprise")
    }
  }

  def buildAssociatedEnterprise(ua: UserAnswers, id: Int): AssociatedEnterprise = {
    ua.get(AssociatedEnterpriseTypePage, id) match {
      case Some(SelectType.Organisation) =>
        new AssociatedEnterprise(
          enterpriseId = generateId,
          organisation = Some(Organisation.buildOrganisationDetails(ua, id)),
          associatedTaxpayers = getAssociatedEnterpriseAnswers(ua, id)._1,
          isAffectedBy = getAssociatedEnterpriseAnswers(ua, id)._2
        )
      case Some(SelectType.Individual) =>
        new AssociatedEnterprise(
          enterpriseId = generateId,
          individual = Some(Individual.buildIndividualDetails(ua, id)),
          associatedTaxpayers = getAssociatedEnterpriseAnswers(ua, id)._1,
          isAffectedBy = getAssociatedEnterpriseAnswers(ua, id)._2
        )
      case None => throw new Exception("Missing associated enterprise type")
    }
  }

  implicit val format: OFormat[AssociatedEnterprise] = Json.format[AssociatedEnterprise]
}
