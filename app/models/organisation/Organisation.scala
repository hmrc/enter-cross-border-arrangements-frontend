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
import models.{Address, AddressLookup, Country, UserAnswers, WithRestore, WithTaxResidency}
import pages.SelectedAddressLookupPage
import pages.organisation._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.{ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage}
import play.api.libs.json.{Json, OFormat}

import scala.util.Try

case class Organisation(organisationName: String,
                        address: Option[Address] = None,
                        emailAddress: Option[String] = None,
                        taxResidencies: IndexedSeq[TaxResidency]
                       ) extends WithRestore with WithTaxResidency {

  def firstTaxResidency: Option[TaxResidency] = taxResidencies.headOption

  implicit val org: Organisation = implicitly(this)

  def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    for {
      ua1 <- userAnswers.set(OrganisationNamePage, id)
      ua2 <- ua1.set(IsOrganisationAddressKnownPage, id)
      ua3 <- ua2.set(IsOrganisationAddressUkPage, id)
      ua4 <- ua3.set(OrganisationAddressPage, id)
      ua5 <- ua4.set(PostcodePage, id)
      ua6 <- ua5.set(SelectAddressPage, id)
      ua7 <- ua6.set(EmailAddressQuestionForOrganisationPage, id)
      ua8 <- ua7.set(EmailAddressForOrganisationPage, id)

      ua9 <- ua8.set(OrganisationLoopPage, id)

      uaa <- ua9.set(WhichCountryTaxForOrganisationPage, id)
      uab <- uaa.set(DoYouKnowAnyTINForUKOrganisationPage, id)
      uac <- uab.set(WhatAreTheTaxNumbersForUKOrganisationPage, id)
      uad <- uac.set(DoYouKnowTINForNonUKOrganisationPage, id)
      uae <- uad.set(WhatAreTheTaxNumbersForNonUKOrganisationPage, id)
      uaf <- uae.set(IsOrganisationResidentForTaxOtherCountriesPage, id)
    } yield uaf
}

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

  private def getTaxResidencies(ua: UserAnswers, id: Int): IndexedSeq[TaxResidency] = {
    ua.get(OrganisationLoopPage, id) match {
      case Some(loop) => TaxResidency.buildFromLoopDetails(loop)
      case None => throw new Exception("Organisation Taxpayer must contain at minimum one tax residency")
    }
  }

  def buildOrganisationDetails(ua: UserAnswers, id: Int): Organisation = {
    val address: Option[Address] =
      (ua.get(OrganisationAddressPage, id), ua.get(SelectedAddressLookupPage, id)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _ => None
      }

    (ua.get(OrganisationNamePage, id)
      , address
      , ua.get(EmailAddressForOrganisationPage, id)
      , getTaxResidencies(ua, id)) match {

      case (Some(name), Some(address), Some(email), taxResidencies) => // All details
        new Organisation(name, Some(address), Some(email), taxResidencies)

      case (Some(name), None, Some(email), taxResidencies) => // No address
        new Organisation(name, None, Some(email), taxResidencies)

      case (Some(name), Some(address), None, taxResidencies) => // No email address
        new Organisation(name, Some(address), None, taxResidencies)

      case (Some(name), None, None, taxResidencies) => // No address or email address
        new Organisation(name, None, None, taxResidencies)

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
        new Organisation(name, Some(address), Some(email), TaxResidency.buildFromLoopDetails(loop))

      case (Some(name), None, Some(email), Some(loop)) => // No address
        new Organisation(name, None, Some(email), TaxResidency.buildFromLoopDetails(loop))

      case (Some(name), Some(address), None, Some(loop)) => // No email address
        new Organisation(name, Some(address), None, TaxResidency.buildFromLoopDetails(loop))

      case (Some(name), None, None, Some(loop)) => // No address or email address
        new Organisation(name, None, None, TaxResidency.buildFromLoopDetails(loop))

      case _ => throw new Exception("Organisation reporter must contain a name and at minimum one tax residency")
    }
  }
}
