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

package models.disclosure

import play.api.libs.json.{Json, OFormat}

case class DisclosureDetails(
  disclosureName: String,
  disclosureType: DisclosureType = DisclosureType.Dac6new,
  arrangementID: Option[String] = None,
  disclosureID: Option[String]  = None,
  initialDisclosureMA: Boolean  = false,
  messageRefId: Option[String]  = None
) {

  def setDisclosureType(disclosureType: DisclosureType): DisclosureDetails = copy(disclosureType = disclosureType)

  def setIds(arrangementID: String, disclosureID: String): DisclosureDetails = copy(arrangementID = Option(arrangementID), disclosureID = Option(disclosureID))
}

object DisclosureDetails {

  implicit val format: OFormat[DisclosureDetails] = Json.format[DisclosureDetails]
}

