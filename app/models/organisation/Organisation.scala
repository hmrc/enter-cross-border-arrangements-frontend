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

package models.organisation

import models.taxpayer.TaxResidency
import models.{Address, UserAnswers}
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import play.api.libs.json.{Json, OFormat}

case class Organisation(organisationName: String,
                        address: Option[Address] = None,
                        emailAddress: Option[String] = None,
                        taxResidencies: IndexedSeq[TaxResidency]
                       )

object Organisation {
  implicit val format: OFormat[Organisation] = Json.format[Organisation]

  def buildOrganisationDetails(ua: UserAnswers): Organisation = {
    (ua.get(OrganisationNamePage), ua.get(OrganisationAddressPage),
      ua.get(EmailAddressForOrganisationPage), ua.get(OrganisationLoopPage)) match {

      case (Some(name), Some(address), Some(email), Some(loop)) => // All details
        new Organisation(name, Some(address), Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), None, Some(email), Some(loop)) => // No address
        new Organisation(name, None, Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(address), None, Some(loop)) => // No email address
        new Organisation(name, Some(address), None, TaxResidency.buildTaxResidency(loop))

      case (Some(name), None, None, Some(loop)) => // No address or email address
        new Organisation(name, None, None, TaxResidency.buildTaxResidency(loop))

      case _ => throw new Exception("Organisation Taxpayer must contain a name and at minimum one tax residency")
    }
  }
}
