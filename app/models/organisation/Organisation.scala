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

package models.organisation

import models.taxpayer.TaxResidency
import models.{Address, AddressLookup, Country, UserAnswers}
import pages.SelectedAddressLookupPage
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.{ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage}
import play.api.libs.json.{Json, OFormat}

case class Organisation(organisationName: String,
                        address: Option[Address] = None,
                        emailAddress: Option[String] = None,
                        taxResidencies: IndexedSeq[TaxResidency]
                       )

object Organisation {
  implicit val format: OFormat[Organisation] = Json.format[Organisation]

  private def convertAddressLookupToAddress(address: AddressLookup) = {
    val country = Country(state = "valid", code = "GB", description = "United Kingdom")

    Address(
      addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      addressLine3 = address.addressLine3,
      city = address.town,
      postCode = Some(address.postcode),
      country = country)
  }

  def buildOrganisationDetails(ua: UserAnswers, id: Int): Organisation = {
    val address: Option[Address] =
      (ua.get(OrganisationAddressPage, id), ua.get(SelectedAddressLookupPage, id)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _ => None
      }

    (ua.get(OrganisationNamePage, id), address,
      ua.get(EmailAddressForOrganisationPage, id), ua.get(OrganisationLoopPage, id)) match {

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

  def buildOrganisationDetailsForReporter(ua: UserAnswers, id: Int): Organisation = {

    val address: Option[Address] =
      (ua.get(ReporterOrganisationAddressPage, id), ua.get(ReporterSelectedAddressLookupPage, id)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _ => None
      }

    (ua.get(ReporterOrganisationNamePage, id), address,
      ua.get(ReporterOrganisationEmailAddressPage, id), ua.get(ReporterTaxResidencyLoopPage, id)) match {

      case (Some(name), Some(address), Some(email), Some(loop)) => // All details
        new Organisation(name, Some(address), Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), None, Some(email), Some(loop)) => // No address
        new Organisation(name, None, Some(email), TaxResidency.buildTaxResidency(loop))

      case (Some(name), Some(address), None, Some(loop)) => // No email address
        new Organisation(name, Some(address), None, TaxResidency.buildTaxResidency(loop))

      case (Some(name), None, None, Some(loop)) => // No address or email address
        new Organisation(name, None, None, TaxResidency.buildTaxResidency(loop))

      case _ => throw new Exception("Organisation reporter must contain a name and at minimum one tax residency")
    }
  }
}
