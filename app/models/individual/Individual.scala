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

package models.individual

import java.time.LocalDate

import models.taxpayer.TaxResidency
import models.{Address, AddressLookup, Country, Name, UserAnswers}
import pages.SelectedAddressLookupPage
import pages.individual._
import pages.reporter.individual._
import pages.reporter.{ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage}
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

  private def getIndividualName(ua: UserAnswers): Name = {
    ua.get(IndividualNamePage) match {
      case Some(name) => name
      case None => throw new Exception("Individual Taxpayer must contain a name")
    }
  }

  private def getIndividualDateOfBirth(ua: UserAnswers): LocalDate = {
    ua.get(IndividualDateOfBirthPage) match {
      case Some(dob) => dob
      case None => LocalDate.of(1900,1,1)
    }
  }

  private def getTaxResidencies(ua: UserAnswers): IndexedSeq[TaxResidency] = {
    ua.get(IndividualLoopPage) match {
      case Some(loop) => TaxResidency.buildTaxResidency(loop)
      case None => throw new Exception("Individual Taxpayer must contain at minimum one tax residency")
    }
  }

  def buildIndividualDetails(ua: UserAnswers): Individual = {

    val address: Option[Address] =
      (ua.get(IndividualAddressPage), ua.get(SelectedAddressLookupPage)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _ => None
      }

    new Individual(
      individualName = getIndividualName(ua),
      birthDate = getIndividualDateOfBirth(ua),
      birthPlace = ua.get(IndividualPlaceOfBirthPage),
      address,
      emailAddress = ua.get(EmailAddressForIndividualPage),
      taxResidencies = getTaxResidencies(ua)
    )
  }

  private def getReporterIndividualName(ua: UserAnswers): Name = {
    ua.get(ReporterIndividualNamePage) match {
      case Some(name) => name
      case None => throw new Exception("Individual Reporter must contain name")
    }
  }

  private def getReporterIndividualDOB(ua: UserAnswers): LocalDate = {
    ua.get(ReporterIndividualDateOfBirthPage) match {
      case Some(dob) => dob
      case None => throw new Exception("Individual Reporter must contain date of birth")
    }
  }

  private def getReporterTaxResidencies(ua: UserAnswers): IndexedSeq[TaxResidency] = {
    ua.get(ReporterTaxResidencyLoopPage) match {
      case Some(loop) => TaxResidency.buildTaxResidency(loop)
      case None => throw new Exception("Individual Reporter must contain date of birth")
    }
  }

  def buildIndividualDetailsForReporter(ua: UserAnswers): Individual = {

    val address: Option[Address] =
      (ua.get(ReporterIndividualAddressPage), ua.get(ReporterSelectedAddressLookupPage)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _ => None
      }

    new Individual(
      individualName = getReporterIndividualName(ua),
      birthDate = getReporterIndividualDOB(ua),
      birthPlace = ua.get(ReporterIndividualPlaceOfBirthPage),
      address,
      emailAddress = ua.get(ReporterIndividualEmailAddressPage),
      taxResidencies = getReporterTaxResidencies(ua)
    )
  }
}
