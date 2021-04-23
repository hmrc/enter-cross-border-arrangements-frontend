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

package models.taxpayer

import java.time.LocalDate
import java.util.UUID

import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers, WithIndividualOrOrganisation, WithRestore}
import pages.taxpayer.{TaxpayerCheckYourAnswersPage, TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.libs.json.{Json, OFormat}

import scala.util.Try

case class Taxpayer(taxpayerId: String
                    , individual: Option[Individual] = None
                    , organisation: Option[Organisation] = None
                    , implementingDate: Option[LocalDate] = None) extends WithIndividualOrOrganisation with WithRestore {

  override def matchItem(itemId: String): Boolean = taxpayerId == itemId

  implicit val t: Taxpayer = implicitly(this)

  override def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    userAnswers
      .set(TaxpayerCheckYourAnswersPage, id)
      .flatMap(_.set(TaxpayerSelectTypePage, id))
      .flatMap(_.set(WhatIsTaxpayersStartDateForImplementingArrangementPage, id))
      .flatMap(restoreFromIndividualOrOrganisation(_, id))
}

object Taxpayer {
  private def generateId: String = UUID.randomUUID.toString
  def apply(ua: UserAnswers, id: Int): Taxpayer = {
    val taxpayer: Taxpayer = (ua.get(TaxpayerCheckYourAnswersPage, id).orElse(Some(generateId))
      , ua.get(WhatIsTaxpayersStartDateForImplementingArrangementPage, id)) match {
      case (Some(itemId), Some(startDate)) =>
        this(itemId, None, None, Some(startDate))
      case (Some(itemId), None) =>
        this(itemId, None, None, None)
      case _ => throw new Exception("Unable to build taxpayer")
    }
    ua.get(TaxpayerSelectTypePage, id) match {
      case Some(SelectType.Organisation) => taxpayer.copy(organisation = Some(Organisation.buildOrganisationDetails(ua, id)))
      case Some(SelectType.Individual) => taxpayer.copy(individual = Some(Individual.buildIndividualDetails(ua, id)))
      case _ => throw new Exception("Unable to retrieve taxpayer select type")
    }
  }
  implicit val format: OFormat[Taxpayer] = Json.format[Taxpayer]
}
