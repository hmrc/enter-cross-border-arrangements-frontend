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

package utils.model.rows

import models.individual.Individual
import models.taxpayer.TaxResidency
import models.{Address, AddressLookup, CheckMode, Country, LoopDetails, TaxReferenceNumbers}
import pages._
import pages.individual._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

import java.time.LocalDate

trait IndividualModelRows extends DisplayRowBuilder {

  def individualName(id: Int, individual: Individual)(implicit messages: Messages): Option[Row] =
    Some(toRow(
      msgKey  = "individualName",
      content = lit"${individual.individualName.firstName} ${individual.individualName.secondName}",
      href    = controllers.individual.routes.IndividualNameController.onPageLoad(id, CheckMode).url
    ))


  def buildIndividualDateOfBirthGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[Row] =
    individual.birthDate match {

      case Some(dateOfBirth) =>
        Seq(isIndividualDateOfBirthKnown(true, id), individualDateOfBirth(dateOfBirth, id))
      case _ =>
        Seq(isIndividualDateOfBirthKnown(false, id))
    }

  def isIndividualDateOfBirthKnown(isKnown: Boolean, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "isIndividualDateOfBirthKnown",
      content = yesOrNo(isKnown),
      href    = controllers.individual.routes.IsIndividualDateOfBirthKnownController.onPageLoad(id, CheckMode).url
    )

  def individualDateOfBirth(dateOfBirth: LocalDate, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(dateOfBirth.format(dateFormatter)),
      href    = controllers.individual.routes.IndividualDateOfBirthController.onPageLoad(id, CheckMode).url
    )

  def buildIndividualPlaceOfBirthGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[Row] =
   individual.birthPlace match {

      case Some(placeOfBirth) =>
        Seq(isIndividualPlaceOfBirthKnown(true, id), individualPlaceOfBirth(placeOfBirth, id))
      case _ =>
        Seq(isIndividualPlaceOfBirthKnown(false, id))
    }

  private def isIndividualPlaceOfBirthKnown(isKnown: Boolean, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "isIndividualPlaceOfBirthKnown",
      content = yesOrNo(isKnown),
      href    = controllers.individual.routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(id, CheckMode).url
    )

  private def individualPlaceOfBirth(placeOfBirth: String, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "individualPlaceOfBirth",
      content = lit"$placeOfBirth",
      href    = controllers.individual.routes.IndividualPlaceOfBirthController.onPageLoad(id, CheckMode).url
    )

  //ToDo addressLookup is not in the model look into this
  def buildIndividualAddressGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[Row] =
    individual.address
      match {
        case Some(manualAddress) =>
          Seq(isIndividualAddressKnown(true, id), individualAddress(manualAddress, id))
//        case (Some(true), _, Some(addressLookup)) =>
//          Seq(isIndividualAddressKnown(true, id), individualAddress(addressLookup, id))
        case _ =>
          Seq(isIndividualAddressKnown(false, id))
    }

  private def isIndividualAddressKnown(addressKnown: Boolean, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "isIndividualAddressKnown",
      content = yesOrNo(addressKnown),
      href    = controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(id, CheckMode).url
    )

  private def individualAddress(manualAddress: Address, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "individualAddress",
      content = formatAddress(manualAddress),
      href    = controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(id, CheckMode).url
    )

  private def individualAddress(addressLookup: AddressLookup, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "individualAddress",
      content = formatAddress(addressLookup),
      href    = controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(id, CheckMode).url
    )

  def buildIndividualEmailAddressGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[Row] =
    individual.emailAddress match {

      case Some(email) =>
        Seq(emailAddressQuestionForIndividual(true, id)
          , emailAddressForIndividual(email, id))
      case _ =>
        Seq(emailAddressQuestionForIndividual(false, id))
    }

  private def emailAddressQuestionForIndividual(isKnown: Boolean, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "emailAddressQuestionForIndividual",
      content = yesOrNo(isKnown),
      href    = controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(id, CheckMode).url
    )

  private def emailAddressForIndividual(email: String, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$email",
      href    = controllers.individual.routes.EmailAddressForIndividualController.onPageLoad(id, CheckMode).url
    )

//  def buildTaxResidencySummaryForIndividuals(id: Int, individual: Individual)(implicit messages: Messages): Seq[Row] = {
//
//    val validDetailsWithIndex: IndexedSeq[(TaxResidency, Int)] = individual.taxResidencies.filter(_.country.isDefined).zipWithIndex
//
//    val header = toRow(
//      msgKey = "whichCountryTaxForIndividual",
//      content = lit"",
//      href = controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(id, CheckMode, 0).url
//    )
//
//   val details = validDetailsWithIndex flatMap {
//
//        case (taxResidency, index) =>
//          individualCountryRow(taxResidency.country, index, validDetailsWithIndex.size) +: taxNumberRow(taxResidency.country, taxResidency.taxReferenceNumbers)
//      }
//
//  }


  private def individualCountryRow(countryOption: Option[Country], index: Int, loopSize: Int): Row = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("whichCountryTaxForIndividual.countryCounter")(loopSize > 1, (index + 1).toString)

    Row(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription")
    )
  }

  private def taxNumberRow(country: Option[Country], taxReferenceNumbers: Option[TaxReferenceNumbers]): Seq[Row] = {
    (country, taxReferenceNumbers) match {
            case (Some(c), Some(taxnumbers)) =>
              if (c.code == "GB") {
                taxNumberRow("whatAreTheTaxNumbersForUKIndividual", taxnumbers, country)
              }
              else
                {taxNumberRow("whatAreTheTaxNumbersForNonUKIndividual", taxnumbers, country)}
          case (_, None) => Seq()
        }
    }

  private def taxNumberRow(msgKey: String, taxReferenceNumber: TaxReferenceNumbers, country: Option[Country]): Seq[Row] = {

    val countryLabel = country.map(_.description).getOrElse("")
    val taxRefLabel: Text.Message =
      messageWithPluralFormatter(s"$msgKey.checkYourAnswersLabel", countryLabel)(taxReferenceNumber.isSingleTaxReferenceNumber)

    Seq(Row(
      key     = Key(taxRefLabel, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"${formatReferenceNumbers(taxReferenceNumber)}")
    ))
  }
//
//  def whichCountryTaxForIndividual(id: Int): Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage, id) map {
//    answer =>
//      toRow(
//        msgKey  = "whichCountryTaxForIndividual",
//        content = lit"$answer",
//        href    = controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(id, CheckMode, 1).url
//      )
//  }
//
//  def doYouKnowAnyTINForUKIndividual(id: Int): Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage, id) map {
//    answer =>
//      toRow(
//        msgKey  = "doYouKnowAnyTINForUKIndividual",
//        content = yesOrNo(answer),
//        href    = controllers.individual.routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(id, CheckMode, 1).url
//      )
//  }
//
//  def whatAreTheTaxNumbersForUKIndividual(id: Int): Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage, id) map {
//    answer =>
//      toRow(
//        msgKey  = "whatAreTheTaxNumbersForUKIndividual",
//        content = lit"$answer",
//        href    = controllers.individual.routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(id, CheckMode, 1).url
//      )
//  }
//
//  def isIndividualResidentForTaxOtherCountries(id: Int): Option[Row] = userAnswers.get(IsIndividualResidentForTaxOtherCountriesPage, id) map {
//    answer =>
//      toRow(
//        msgKey  = "isIndividualResidentForTaxOtherCountries",
//        content = yesOrNo(answer),
//        href    = controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(id, CheckMode, 1).url
//      )
//  }

}
