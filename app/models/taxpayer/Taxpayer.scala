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

import models.individual.Individual
import models.organisation.Organisation
import models.{SelectType, UserAnswers, WithIndividualOrOrganisation}
import pages.taxpayer.{TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import java.util.UUID

case class Taxpayer(taxpayerId: String
                    , individual: Option[Individual] = None
                    , organisation: Option[Organisation] = None
                    , implementingDate: Option[LocalDate] = None) extends WithIndividualOrOrganisation

object Taxpayer {

  private def generateId = UUID.randomUUID.toString

  def buildTaxpayerDetails(ua: UserAnswers, id: Int): Taxpayer = {
    ua.get(TaxpayerSelectTypePage, id) match {
      case Some(SelectType.Organisation) =>
        new Taxpayer(
        taxpayerId = generateId,
          organisation = Some(Organisation.buildOrganisationDetails(ua, id)),
          implementingDate = ua.get(WhatIsTaxpayersStartDateForImplementingArrangementPage, id)
      )
      case Some(SelectType.Individual) =>
        new Taxpayer(
          taxpayerId = generateId,
          individual = Some(Individual.buildIndividualDetails(ua, id)),
          implementingDate = ua.get(WhatIsTaxpayersStartDateForImplementingArrangementPage, id)
        )
      case _ => throw new Exception("Unable to retrieve Taxpayer select type")
    }
  }

  implicit val format: OFormat[Taxpayer] = Json.format[Taxpayer]
}
