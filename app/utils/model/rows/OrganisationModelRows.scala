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
import models.{Address, CheckMode, Country, TaxReferenceNumbers}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import uk.gov.hmrc.viewmodels._

trait OrganisationModelRows extends DisplayRowBuilder {

  def organisationName(id: Int, organisation: Organisation)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "organisationName",
      content = lit"${organisation.organisationName}",
      href    = controllers.organisation.routes.OrganisationNameController.onPageLoad(id, CheckMode).url
    )

  def buildOrganisationAddressGroup(id: Int, organisation: Organisation)(implicit messages: Messages): Seq[Row] =
    organisation.address match {
      case Some(manualAddress) =>
        Seq(isOrganisationAddressKnown(true, id), organisationAddress(manualAddress, id))
      case _ =>
        Seq(isOrganisationAddressKnown(false, id))
    }

  private def isOrganisationAddressKnown(addressKnown: Boolean, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "isOrganisationAddressKnown",
      content = yesOrNo(addressKnown),
      href    = controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(id, CheckMode).url
    )

  private def organisationAddress(manualAddress: Address, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "organisationAddress",
      content = formatAddress(manualAddress),
      href    = controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(id, CheckMode).url
    )

  def buildOrganisationEmailAddressGroup(id: Int, organisation: Organisation)(implicit messages: Messages): Seq[Row] =
   organisation.emailAddress match {
      case Some(email) =>
        Seq(emailAddressQuestionForOrganisation(true, id)
          , emailAddressForOrganisation(email, id))
      case _ =>
        Seq(emailAddressQuestionForOrganisation(false, id))
    }

  private def emailAddressQuestionForOrganisation(isKnown: Boolean, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "emailAddressQuestionForOrganisation",
      content = yesOrNo(isKnown),
      href    = controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(id, CheckMode).url
    )

  private def emailAddressForOrganisation(email: String, id: Int)(implicit messages: Messages): Row =
    toRow(
      msgKey  = "emailAddressForOrganisation",
      content = lit"$email",
      href    = controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(id, CheckMode).url
    )

  def buildTaxResidencySummaryForOrganisation(id: Int,organisation: Organisation)(implicit messages: Messages): Seq[Row] = {

    val validDetailsWithIndex: IndexedSeq[(TaxResidency, Int)] = organisation.taxResidencies.filter(_.country.isDefined).zipWithIndex

    val header: Row = toRow(
      msgKey = "whichCountryTaxForOrganisation",
      content = lit"",
      href = controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(id, CheckMode, 0).url
    )

    val details: IndexedSeq[Row] = validDetailsWithIndex flatMap {
      case (taxResidency, index) =>
        organisationCountryRow(taxResidency.country, index, validDetailsWithIndex.size) +: taxNumberRow(taxResidency.country, taxResidency.taxReferenceNumbers)
    }

    header +: details
  }

  private def organisationCountryRow(countryOption: Option[Country], index: Int, loopSize: Int): Row = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("whichCountryTaxForOrganisation.countryCounter")(loopSize > 1, (index + 1).toString)

    Row(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription")
    )
  }

  private def taxNumberRow(country: Option[Country], taxReferenceNumbers: Option[TaxReferenceNumbers])(implicit messages: Messages): Seq[Row] =
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

  private def taxNumberRow(msgKey: String, taxReferenceNumbers: TaxReferenceNumbers, country: Option[Country])(implicit messages: Messages): Seq[Row] = {

    val countryLabel = country.map(_.description).getOrElse("")
    val taxRefLabel: Text.Message =
      messageWithPluralFormatter(s"$msgKey.checkYourAnswersLabel", countryLabel)(taxReferenceNumbers.isSingleTaxReferenceNumber)

    Seq(Row(
      key     = Key(taxRefLabel, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"${formatReferenceNumbers(taxReferenceNumbers)}")
    ))
  }

  def whichCountryTaxForOrganisation(id: Int, organisation: Organisation)(implicit messages: Messages): Option[Row] =
  organisation.firstTaxResidency.flatMap(_.country) map { country =>

      toRow(
        msgKey  = "whichCountryTaxForOrganisation",
        content = lit"$country",
        href    = controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def doYouKnowAnyTINForUKOrganisation(id: Int, organisation: Organisation)(implicit messages: Messages): Option[Row] =
  organisation.firstTaxResidency.map(_.isUK).orElse(Some(false)).map { douYouKnowTIN =>
      toRow(
        msgKey  = "doYouKnowAnyTINForUKOrganisation",
        content = yesOrNo(douYouKnowTIN),
        href    = controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def whatAreTheTaxNumbersForUKOrganisation(id: Int, organisation: Organisation)(implicit messages: Messages): Option[Row] =
    organisation.firstTaxResidency.filter(_.isUK).flatMap(_.taxReferenceNumbers) map { taxnumbers =>
      toRow(
        msgKey  = "whatAreTheTaxNumbersForUKOrganisation",
        content = lit"$taxnumbers",
        href    = controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def isOrganisationResidentForTaxOtherCountries(id: Int)(implicit messages: Messages): Option[Row] =
      Some(toRow(
        msgKey  = "isOrganisationResidentForTaxOtherCountries",
        content = yesOrNo(false),
        href    = controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, CheckMode, 1).url
      ))

}
