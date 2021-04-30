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
import models.{Address, Country, TaxReferenceNumbers}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.SummaryListDisplay.DisplayRow

import java.time.LocalDate


trait IndividualModelRows extends DisplayRowBuilder {

  def individualName(id: Int, individual: Individual)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "individualName",
      content = lit"${individual.individualName.firstName} ${individual.individualName.secondName}"
    )


  def buildIndividualDateOfBirthGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[DisplayRow] =
    individual.birthDate match {

      case Some(dateOfBirth) =>
        Seq(isIndividualDateOfBirthKnown(true, id), individualDateOfBirth(dateOfBirth, id))
      case _ =>
        Seq(isIndividualDateOfBirthKnown(false, id))
    }

  def isIndividualDateOfBirthKnown(isKnown: Boolean, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "isIndividualDateOfBirthKnown",
      content = yesOrNo(isKnown)
    )

  def individualDateOfBirth(dateOfBirth: LocalDate, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(dateOfBirth.format(dateFormatter))
    )

  def buildIndividualPlaceOfBirthGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[DisplayRow] =
   individual.birthPlace match {

      case Some(placeOfBirth) =>
        Seq(isIndividualPlaceOfBirthKnown(true, id), individualPlaceOfBirth(placeOfBirth, id))
      case _ =>
        Seq(isIndividualPlaceOfBirthKnown(false, id))
    }

  private def isIndividualPlaceOfBirthKnown(isKnown: Boolean, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "isIndividualPlaceOfBirthKnown",
      content = yesOrNo(isKnown)
    )

  private def individualPlaceOfBirth(placeOfBirth: String, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "individualPlaceOfBirth",
      content = lit"$placeOfBirth"
    )

  def buildIndividualAddressGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[DisplayRow] =
    individual.address
    match {
      case Some(address) =>
        Seq(isIndividualAddressKnown(true, id), individualAddress(address, id))
      case _ =>
        Seq(isIndividualAddressKnown(false, id))
    }

  private def isIndividualAddressKnown(addressKnown: Boolean, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "isIndividualAddressKnown",
      content = yesOrNo(addressKnown)
    )

  private def individualAddress(manualAddress: Address, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "individualAddress",
      content = formatAddress(manualAddress)
    )

  def buildIndividualEmailAddressGroup(id: Int, individual: Individual)(implicit messages: Messages): Seq[DisplayRow] =
    individual.emailAddress match {

      case Some(email) =>
        Seq(emailAddressQuestionForIndividual(true, id)
          , emailAddressForIndividual(email, id))
      case _ =>
        Seq(emailAddressQuestionForIndividual(false, id))
    }

  private def emailAddressQuestionForIndividual(isKnown: Boolean, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "emailAddressQuestionForIndividual",
      content = yesOrNo(isKnown)
    )

  private def emailAddressForIndividual(email: String, id: Int)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$email"
    )

  def buildTaxResidencySummaryForIndividuals(id: Int, individual: Individual)(implicit messages: Messages): Seq[DisplayRow] = {

    val validDetailsWithIndex: IndexedSeq[(TaxResidency, Int)] = individual.taxResidencies.filter(_.country.isDefined).zipWithIndex

    val header: DisplayRow = toDisplayRowNoBorder(
      msgKey = "whichCountryTaxForIndividual",
      content = lit""
    )

   val details: IndexedSeq[DisplayRow] = validDetailsWithIndex flatMap {
        case (taxResidency, index) =>
          individualCountryRow(taxResidency.country, index, validDetailsWithIndex.size) +: taxNumberRow(taxResidency.country, taxResidency.taxReferenceNumbers)
      }

    header +: details
  }

  private def individualCountryRow(countryOption: Option[Country], index: Int, loopSize: Int)(implicit messages: Messages): DisplayRow = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("whichCountryTaxForIndividual.countryCounter")(loopSize > 1, (index + 1).toString)

    DisplayRow(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription"), classes = Seq("govuk-summary-list--no-border")
    )
  }

  private def taxNumberRow(country: Option[Country], taxReferenceNumbers: Option[TaxReferenceNumbers])(implicit messages: Messages): Seq[DisplayRow] = {
    (country, taxReferenceNumbers) match {
            case (Some(c), Some(taxnumbers)) =>
              if (c.isUK) {
                taxNumberRow("whatAreTheTaxNumbersForUKIndividual", taxnumbers, country)
              }
              else
                {taxNumberRow("whatAreTheTaxNumbersForNonUKIndividual", taxnumbers, country)}
          case (_, None) => Seq()
        }
    }

  private def taxNumberRow(msgKey: String, taxReferenceNumber: TaxReferenceNumbers, country: Option[Country])(implicit messages: Messages): Seq[DisplayRow] = {

    val countryLabel = country.map(_.description).getOrElse("")
    val taxRefLabel: Text.Message =
      messageWithPluralFormatter(s"$msgKey.checkYourAnswersLabel", countryLabel)(taxReferenceNumber.isSingleTaxReferenceNumber)

    Seq(DisplayRow(
      key     = Key(taxRefLabel, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"${formatReferenceNumbers(taxReferenceNumber)}"),
      classes = Seq("govuk-summary-list--no-border")
    ))
  }

  def whichCountryTaxForIndividual(id: Int, individual: Individual)(implicit messages: Messages): Option[DisplayRow] = {
    individual.firstTaxResidency.flatMap(_.country) map { country =>
      toDisplayRowNoBorder(
        msgKey = "whichCountryTaxForIndividual",
        content = lit"$country"
      )
    }
  }

  def doYouKnowAnyTINForUKIndividual(id: Int, individual: Individual)(implicit messages: Messages): Option[DisplayRow] =
    individual.firstTaxResidency.map(_.isUK).orElse(Some(false)).map { douYouKnowTIN =>
    toDisplayRow(
      msgKey = "doYouKnowAnyTINForUKIndividual",
      content = yesOrNo(douYouKnowTIN)
    )
  }

  def whatAreTheTaxNumbersForUKIndividual(id: Int, individual: Individual)(implicit messages: Messages): Option[DisplayRow] =
    individual.firstTaxResidency.filter(_.isUK).flatMap(_.taxReferenceNumbers) map { taxnumbers =>
      toDisplayRow(
        msgKey = "whatAreTheTaxNumbersForUKIndividual",
        content = lit"$taxnumbers"
      )
    }

  def whatAreTheTaxNumbersForNonUKIndividual(id: Int, individual: Individual)(implicit messages: Messages): Option[DisplayRow] =
    individual.firstTaxResidency.filter(_.isUK).flatMap(_.taxReferenceNumbers) map { taxnumber =>
      toDisplayRow(
        msgKey = "whatAreTheTaxNumbersForNonUKIndividual",
        content = lit"$taxnumber"
      )
    }

  def isIndividualResidentForTaxOtherCountries(id: Int)(implicit messages: Messages): Option[DisplayRow] =
     Some( toDisplayRow(
        msgKey  = "isIndividualResidentForTaxOtherCountries",
        content = yesOrNo(false)
      ))

}
