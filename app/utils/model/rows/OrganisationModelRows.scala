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

import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Address, Country, TaxReferenceNumbers}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Value}
import uk.gov.hmrc.viewmodels._
import utils.SummaryListDisplay.DisplayRow

trait OrganisationModelRows extends DisplayRowBuilder {

  def organisationName(organisation: Organisation)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "organisationName",
      content = lit"${organisation.organisationName}"
    )

  def buildOrganisationAddressGroup(organisation: Organisation)(implicit messages: Messages): Seq[DisplayRow] =
    organisation.address match {
      case Some(manualAddress) =>
        Seq(isOrganisationAddressKnown(true), organisationAddress(manualAddress))
      case _ =>
        Seq(isOrganisationAddressKnown(false))
    }

  private def isOrganisationAddressKnown(addressKnown: Boolean)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "isOrganisationAddressKnown",
      content = yesOrNo(addressKnown)
    )

  private def organisationAddress(manualAddress: Address)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "organisationAddress",
      content = formatAddress(manualAddress)
    )

  def buildOrganisationEmailAddressGroup(organisation: Organisation)(implicit messages: Messages): Seq[DisplayRow] =
   organisation.emailAddress match {
      case Some(email) =>
        Seq(emailAddressQuestionForOrganisation(true)
          , emailAddressForOrganisation(email))
      case _ =>
        Seq(emailAddressQuestionForOrganisation(false))
    }

  private def emailAddressQuestionForOrganisation(isKnown: Boolean)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "emailAddressQuestionForOrganisation",
      content = yesOrNo(isKnown)
    )

  private def emailAddressForOrganisation(email: String)(implicit messages: Messages): DisplayRow =
    toDisplayRow(
      msgKey  = "emailAddressForOrganisation",
      content = lit"$email"
    )

  def buildTaxResidencySummaryForOrganisation(organisation: Organisation)(implicit messages: Messages): Seq[DisplayRow] = {

    val validDetailsWithIndex: IndexedSeq[(TaxResidency, Int)] = organisation.taxResidencies.filter(_.country.isDefined).zipWithIndex

    val header: DisplayRow = toDisplayRowNoBorder(
      msgKey = "whichCountryTaxForOrganisation",
      content = lit""
    )

    val details: IndexedSeq[DisplayRow] = validDetailsWithIndex flatMap {
      case (taxResidency, index) =>
        organisationCountryRow(taxResidency.country, index, validDetailsWithIndex.size) +: taxNumberRow(taxResidency.country, taxResidency.taxReferenceNumbers)
    }

    header +: details
  }

  private def organisationCountryRow(countryOption: Option[Country], index: Int, loopSize: Int): DisplayRow = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("whichCountryTaxForOrganisation.countryCounter")(loopSize > 1, (index + 1).toString)

    DisplayRow(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription"),
      classes = Seq("govuk-summary-list--no-border")
    )
  }

  private def taxNumberRow(country: Option[Country], taxReferenceNumbers: Option[TaxReferenceNumbers])(implicit messages: Messages): Seq[DisplayRow] =
  {
    (country, taxReferenceNumbers) match {
      case (Some(c), Some(taxnumbers)) =>
        if (c.isUK) {
          taxNumberRow("whatAreTheTaxNumbersForUKOrganisation", taxnumbers, country)
        }
        else
        {taxNumberRow("whatAreTheTaxNumbersForNonUKOrganisation", taxnumbers, country)}
      case (_, None) => Seq()
    }
  }

  private def taxNumberRow(msgKey: String, taxReferenceNumbers: TaxReferenceNumbers, country: Option[Country])(implicit messages: Messages): Seq[DisplayRow] = {

    val countryLabel = country.map(_.description).getOrElse("")
    val taxRefLabel: Text.Message =
      messageWithPluralFormatter(s"$msgKey.checkYourAnswersLabel", countryLabel)(taxReferenceNumbers.isSingleTaxReferenceNumber)

    Seq(DisplayRow(
      key     = Key(taxRefLabel, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"${formatReferenceNumbers(taxReferenceNumbers)}"),
      classes = Seq("govuk-summary-list--no-border")
    ))
  }

  def whichCountryTaxForOrganisation(organisation: Organisation)(implicit messages: Messages): Option[DisplayRow] =
  organisation.firstTaxResidency.flatMap(_.country) map { country =>

      toDisplayRowNoBorder(
        msgKey  = "whichCountryTaxForOrganisation",
        content = lit"$country"
      )
  }

  def doYouKnowAnyTINForUKOrganisation(organisation: Organisation)(implicit messages: Messages): Option[DisplayRow] =
  organisation.firstTaxResidency.map(_.isUK).orElse(Some(false)).map { douYouKnowTIN =>
      toDisplayRow(
        msgKey  = "doYouKnowAnyTINForUKOrganisation",
        content = yesOrNo(douYouKnowTIN)
      )
  }

  def whatAreTheTaxNumbersForUKOrganisation(organisation: Organisation)(implicit messages: Messages): Option[DisplayRow] =
    organisation.firstTaxResidency.filter(_.isUK).flatMap(_.taxReferenceNumbers) map { taxnumbers =>
      toDisplayRow(
        msgKey  = "whatAreTheTaxNumbersForUKOrganisation",
        content = lit"$taxnumbers"
      )
  }

  def isOrganisationResidentForTaxOtherCountries(id: Int)(implicit messages: Messages): Option[DisplayRow] =
      Some(toDisplayRow(
        msgKey  = "isOrganisationResidentForTaxOtherCountries",
        content = yesOrNo(false)
      ))

}
