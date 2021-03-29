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

package models

import models.individual.Individual
import models.organisation.Organisation

import scala.util.{Success, Try}

trait WithIndividualOrOrganisation {

  val individual: Option[Individual]
  val organisation: Option[Organisation]

  val nameAsString: String = (individual, organisation) match {
    case (Some(i), _) => i.nameAsString
    case (_, Some(o)) => o.organisationName
    case _            => ""
  }

  val selectType: Try[SelectType] = (individual, organisation) match {
    case (Some(_), _) => Success(SelectType.Individual)
    case _            => Success(SelectType.Organisation)
  }

  def restoreFromIndividualOrOrganisation(userAnswers: UserAnswers, id: Int): Try[UserAnswers] = (individual, organisation) match {
    case (Some(i), _) => i.restore(userAnswers, id)
    case (_, Some(o)) => o.restore(userAnswers, id)
    case _            => Success(userAnswers)
  }
}
