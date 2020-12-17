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

package models.individual

import java.time.LocalDate

import models.taxpayer.TaxResidency
import models.{Address, Name, UserAnswers}
import pages.individual._
import play.api.libs.json.{Json, OFormat}

case class Individual(individualName: Name,
                      birthDate: LocalDate,
                      birthPlace: Option[String] = None,
                      address: Option[Address] = None,
                      emailAddress: Option[String] = None,
                      taxResidencies: IndexedSeq[TaxResidency]
                     ) {

  val nameAsString: String = individualName.displayName
}

object Individual {
  implicit val format: OFormat[Individual] = Json.format[Individual]

  def buildIndividualDetails(ua: UserAnswers): Individual = {
    (ua.get(IndividualNamePage), ua.get(IndividualDateOfBirthPage),
      ua.get(IndividualPlaceOfBirthPage), ua.get(IndividualAddressPage),
      ua.get(EmailAddressForIndividualPage), ua.get(IndividualLoopPage)) match {

      case (Some(name), Some(dob),  Some(pob),Some(address), Some(email), Some(loop)) => // All details
        new Individual(name, dob, Some(pob), Some(address), Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), None, Some(address), Some(email), Some(loop)) => // No place of birth
        new Individual(name, dob, None, Some(address), Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), Some(pob), None, Some(email), Some(loop)) => // No address
        new Individual(name, dob, Some(pob), None, Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), Some(pob), Some(address), None, Some(loop)) => // No email address
        new Individual(name, dob, Some(pob), Some(address), None, TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), None, None, Some(email), Some(loop)) => // No place of birth or address
        new Individual(name, dob, None, None, Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), Some(pob), None, None, Some(loop)) => // No address or email address
        new Individual(name, dob, Some(pob), None, None, TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), None, Some(address), None, Some(loop)) => // No place of birth or email address
        new Individual(name, dob, None, Some(address), None, TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(dob), None, None, None, Some(loop)) => // No place of birth or address or email address
        new Individual(name, dob, None, None, None, TaxResidency.buildTaxResidency(loop))

      case _ => throw new Exception("Individual Taxpayer must contain a name and at minimum one tax residency")
    }
  }
}
