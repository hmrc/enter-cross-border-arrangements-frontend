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

package models.taxpayer

import models.SelectType
import models.individual.Individual
import models.organisation.Organisation
import play.api.libs.json.{Json, OFormat}

import java.util.UUID

case class Taxpayer(taxpayerId: String, selectType: SelectType, individual: Option[Individual] = None, organisation: Option[Organisation] = None) {

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => throw new RuntimeException("Taxpayer must contain either an individual or an organisation.")
  }

}

object Taxpayer {

  private def generateId = UUID.randomUUID.toString

  def apply(individual: Individual) = new Taxpayer(
    taxpayerId = generateId
    , selectType = SelectType.Individual
    , individual = Some(individual)
  )

  def apply(organisation: Organisation) = new Taxpayer(
    taxpayerId = generateId
    , selectType = SelectType.Individual
    , organisation = Some(organisation)
  )

  implicit val format: OFormat[Taxpayer] = Json.format[Taxpayer]
}
