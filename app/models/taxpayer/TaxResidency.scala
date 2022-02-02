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

package models.taxpayer

import models.{Country, LoopDetails, TaxReferenceNumbers}
import play.api.libs.json.{Json, OFormat}

case class TaxResidency(country: Option[Country], taxReferenceNumbers: Option[TaxReferenceNumbers]) extends Ordered[TaxResidency] {

  val isUK: Boolean = country.exists(_.code == "GB")

  val hasNumbers: Option[Boolean] = taxReferenceNumbers.map(_.firstTaxNumber.nonEmpty)

  override def compare(that: TaxResidency): Int =
    (country, that.country) match {
      case (Some(thisCountry), Some(otherCountry)) => thisCountry.compare(otherCountry)
      case _                                       => throw new IllegalArgumentException("Unable to order")
    }
}

object TaxResidency {

  implicit val format: OFormat[TaxResidency] = Json.format[TaxResidency]

  def apply(loopDetail: LoopDetails): TaxResidency = this(loopDetail.whichCountry, loopDetail.matchingTINS)

  def buildFromLoopDetails(loopDetails: IndexedSeq[LoopDetails]): IndexedSeq[TaxResidency] = loopDetails.map(apply)
}
