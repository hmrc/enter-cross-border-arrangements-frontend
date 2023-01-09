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

package models.enterprises

import controllers.exceptions.SomeInformationIsMissingException
import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers, WithIndividualOrOrganisation, WithRestore}
import pages.enterprises.{
  AssociatedEnterpriseCheckYourAnswersPage,
  AssociatedEnterpriseTypePage,
  IsAssociatedEnterpriseAffectedPage,
  SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
}
import play.api.libs.json.{Json, OFormat}

import java.util.UUID
import scala.util.Try

case class AssociatedEnterprise(enterpriseId: String,
                                individual: Option[Individual] = None,
                                organisation: Option[Organisation] = None,
                                associatedTaxpayers: List[String] = List.empty,
                                isAffectedBy: Boolean = false
) extends WithIndividualOrOrganisation
    with WithRestore {

  override def matchItem(itemId: String): Boolean = enterpriseId == itemId

  implicit val a: AssociatedEnterprise = implicitly(this)

  override def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    userAnswers
      .set(AssociatedEnterpriseCheckYourAnswersPage, id)
      .flatMap(_.set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id))
      .flatMap(_.set(AssociatedEnterpriseTypePage, id))
      .flatMap(_.set(IsAssociatedEnterpriseAffectedPage, id))
      .flatMap(restoreFromIndividualOrOrganisation(_, id))
}

object AssociatedEnterprise {

  private def generateId: String = UUID.randomUUID.toString

  def apply(ua: UserAnswers, id: Int): AssociatedEnterprise = {
    val enterprise: AssociatedEnterprise = (ua.get(AssociatedEnterpriseCheckYourAnswersPage, id).orElse(Some(generateId)),
                                            ua.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id),
                                            ua.get(IsAssociatedEnterpriseAffectedPage, id)
    ) match {
      case (Some(itemId), Some(associatedTaxpayers), Some(isAffectedBy)) =>
        this(itemId, None, None, associatedTaxpayers, isAffectedBy)
      case _ => throw new SomeInformationIsMissingException(id, "Unable to build associated enterprise")
    }
    ua.get(AssociatedEnterpriseTypePage, id) match {
      case Some(SelectType.Organisation) => enterprise.copy(organisation = Some(Organisation.buildOrganisationDetails(ua, id)))
      case Some(SelectType.Individual)   => enterprise.copy(individual = Some(Individual.buildIndividualDetails(ua, id)))
      case _                             => throw new SomeInformationIsMissingException(id, "Unable to retrieve Intermediary select type")
    }
  }

  implicit val format: OFormat[AssociatedEnterprise] = Json.format[AssociatedEnterprise]
}
