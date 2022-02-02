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

package models.individual

import controllers.exceptions.SomeInformationIsMissingException

import java.time.LocalDate
import models.taxpayer.TaxResidency
import models.{Address, AddressLookup, Country, Name, UserAnswers, WithRestore, WithTaxResidency}
import pages.SelectedAddressLookupPage
import pages.individual._
import pages.reporter.individual._
import pages.reporter.{ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage}
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import scala.util.Try

case class Individual(individualName: Name,
                      birthDate: Option[LocalDate],
                      birthPlace: Option[String] = None,
                      address: Option[Address] = None,
                      emailAddress: Option[String] = None,
                      taxResidencies: IndexedSeq[TaxResidency]
) extends WithRestore
    with WithTaxResidency {

  val nameAsString: String = individualName.displayName

  def firstTaxResidency: Option[TaxResidency] = taxResidencies.headOption

  implicit val org: Individual = implicitly(this)

  def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    for {
      ua1 <- userAnswers.set(IndividualNamePage, id)

      ua2 <- ua1.set(IsIndividualDateOfBirthKnownPage, id)
      ua3 <- ua2.set(IndividualDateOfBirthPage, id)
      ua4 <- ua3.set(IsIndividualPlaceOfBirthKnownPage, id)
      ua5 <- ua4.set(IndividualPlaceOfBirthPage, id)

      ua6 <- ua5.set(IsIndividualAddressKnownPage, id)
      ua7 <- ua6.set(IsIndividualAddressUkPage, id)
      ua8 <- ua7.set(IndividualAddressPage, id)
      ua9 <- ua8.set(IndividualUkPostcodePage, id)
      uaa <- ua9.set(IndividualSelectAddressPage, id)
      uab <- uaa.set(EmailAddressQuestionForIndividualPage, id)
      uac <- uab.set(EmailAddressForIndividualPage, id)

      uad <- uac.set(IndividualLoopPage, id)

      uae <- uad.set(WhichCountryTaxForIndividualPage, id)
      uaf <- uae.set(DoYouKnowAnyTINForUKIndividualPage, id)
      uag <- uaf.set(WhatAreTheTaxNumbersForUKIndividualPage, id)
      uah <- uag.set(DoYouKnowTINForNonUKIndividualPage, id)
      uai <- uah.set(WhatAreTheTaxNumbersForNonUKIndividualPage, id)
      uaj <- uai.set(IsIndividualResidentForTaxOtherCountriesPage, id)
    } yield uaj
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
      country = country
    )
  }

  private def getIndividualName(ua: UserAnswers, id: Int): Name =
    ua.get(IndividualNamePage, id) match {
      case Some(name) => name
      case None       => throw new SomeInformationIsMissingException(id, "Individual Taxpayer must contain a name")
    }

  private def getIndividualDateOfBirth(ua: UserAnswers, id: Int): Option[LocalDate] =
    ua.get(IndividualDateOfBirthPage, id)

  private def getTaxResidencies(ua: UserAnswers, id: Int): IndexedSeq[TaxResidency] =
    ua.get(IndividualLoopPage, id) match {
      case Some(loop) => TaxResidency.buildFromLoopDetails(loop)
      case None       => throw new SomeInformationIsMissingException(id, "Individual Taxpayer must contain at minimum one tax residency")
    }

  def buildIndividualDetails(ua: UserAnswers, id: Int): Individual = {

    val address: Option[Address] =
      (ua.get(IndividualAddressPage, id), ua.get(SelectedAddressLookupPage, id)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _                  => None
      }

    new Individual(
      individualName = getIndividualName(ua, id),
      birthDate = getIndividualDateOfBirth(ua, id),
      birthPlace = ua.get(IndividualPlaceOfBirthPage, id),
      address,
      emailAddress = ua.get(EmailAddressForIndividualPage, id),
      taxResidencies = getTaxResidencies(ua, id)
    )
  }

  private def getReporterIndividualName(ua: UserAnswers, id: Int): Name =
    ua.get(ReporterIndividualNamePage, id) match {
      case Some(name) => name
      case None       => throw new SomeInformationIsMissingException(id, "Individual Reporter must contain name")
    }

  private def getReporterIndividualDOB(ua: UserAnswers, id: Int): Option[LocalDate] =
    ua.get(ReporterIndividualDateOfBirthPage, id) match {
      case dob  => dob
      case None => throw new SomeInformationIsMissingException(id, "Individual Reporter must contain date of birth")
    }

  private def getReporterTaxResidencies(ua: UserAnswers, id: Int): IndexedSeq[TaxResidency] =
    ua.get(ReporterTaxResidencyLoopPage, id) match {
      case Some(loop) => TaxResidency.buildFromLoopDetails(loop)
      case None       => throw new SomeInformationIsMissingException(id, "Individual Reporter must contain at minimum one tax residency")
    }

  def buildIndividualDetailsForReporter(ua: UserAnswers, id: Int): Individual = {

    val address: Option[Address] =
      (ua.get(ReporterIndividualAddressPage, id), ua.get(ReporterSelectedAddressLookupPage, id)) match {
        case (Some(address), _) => Some(address)
        case (_, Some(address)) => Some(convertAddressLookupToAddress(address))
        case _                  => None
      }

    new Individual(
      individualName = getReporterIndividualName(ua, id),
      birthDate = getReporterIndividualDOB(ua, id),
      birthPlace = ua.get(ReporterIndividualPlaceOfBirthPage, id),
      address,
      emailAddress = ua.get(ReporterIndividualEmailAddressPage, id),
      taxResidencies = getReporterTaxResidencies(ua, id)
    )
  }
}
