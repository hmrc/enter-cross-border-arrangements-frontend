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

package utils.rows

import models.{Address, AddressLookup, CheckMode, Country, LoopDetails, TaxReferenceNumbers}
import pages._
import pages.individual._
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

import java.time.LocalDate

trait IndividualRows extends RowBuilder {

  def individualName: Option[Row] = userAnswers.get(IndividualNamePage) map { answer =>

    toRow(
      msgKey  = "individualName",
      content = lit"${answer.firstName} ${answer.secondName}",
      href    = controllers.individual.routes.IndividualNameController.onPageLoad(CheckMode).url
    )
  }

  def buildIndividualDateOfBirthGroup: Seq[Row] =
    (userAnswers.get(IsIndividualDateOfBirthKnownPage), userAnswers.get(IndividualDateOfBirthPage)) match {

      case (Some(true), Some(dateOfBirth)) =>
        Seq(isIndividualDateOfBirthKnown(true), individualDateOfBirth(dateOfBirth))
      case _ =>
        Seq(isIndividualDateOfBirthKnown(false))
    }

  def isIndividualDateOfBirthKnown(isKnown: Boolean): Row =
    toRow(
      msgKey  = "isIndividualDateOfBirthKnown",
      content = yesOrNo(isKnown),
      href    = controllers.individual.routes.IsIndividualDateOfBirthKnownController.onPageLoad(CheckMode).url
    )

  def individualDateOfBirth(dateOfBirth: LocalDate): Row =
    toRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(dateOfBirth.format(dateFormatter)),
      href    = controllers.individual.routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url
    )

  def buildIndividualPlaceOfBirthGroup: Seq[Row] =
    (userAnswers.get(IsIndividualPlaceOfBirthKnownPage), userAnswers.get(IndividualPlaceOfBirthPage)) match {

      case (Some(true), Some(placeOfBirth)) =>
        Seq(isIndividualPlaceOfBirthKnown(true), individualPlaceOfBirth(placeOfBirth))
      case _ =>
        Seq(isIndividualPlaceOfBirthKnown(false))
    }

  private def isIndividualPlaceOfBirthKnown(isKnown: Boolean): Row =
    toRow(
      msgKey  = "isIndividualPlaceOfBirthKnown",
      content = yesOrNo(isKnown),
      href    = controllers.individual.routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url
    )

  private def individualPlaceOfBirth(placeOfBirth: String): Row =
    toRow(
      msgKey  = "individualPlaceOfBirth",
      content = lit"$placeOfBirth",
      href    = controllers.individual.routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url
    )

  def buildIndividualAddressGroup: Seq[Row] =
    (userAnswers.get(IsIndividualAddressKnownPage)
      , userAnswers.get(IndividualAddressPage)
      , userAnswers.get(SelectedAddressLookupPage)) match {
      case (Some(true), Some(manualAddress), _) =>
        Seq(isIndividualAddressKnown(true), individualAddress(manualAddress))
      case (Some(true), _, Some(addressLookup)) =>
        Seq(isIndividualAddressKnown(true), individualAddress(addressLookup))
      case _ =>
        Seq(isIndividualAddressKnown(false))
    }

  private def isIndividualAddressKnown(addressKnown: Boolean): Row =
    toRow(
      msgKey  = "isIndividualAddressKnown",
      content = yesOrNo(addressKnown),
      href    = controllers.individual.routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url
    )

  private def individualAddress(manualAddress: Address): Row =
    toRow(
      msgKey  = "individualAddress",
      content = formatAddress(manualAddress),
      href    = controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
    )

  private def individualAddress(addressLookup: AddressLookup): Row =
    toRow(
      msgKey  = "individualAddress",
      content = formatAddress(addressLookup),
      href    = controllers.individual.routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
    )

  def buildIndividualEmailAddressGroup: Seq[Row] =
    (userAnswers.get(EmailAddressQuestionForIndividualPage)
      , userAnswers.get(EmailAddressForIndividualPage)) match {

      case (Some(true), Some(email)) =>
        Seq(emailAddressQuestionForIndividual(true)
          , emailAddressForIndividual(email))
      case _ =>
        Seq(emailAddressQuestionForIndividual(false))
    }

  private def emailAddressQuestionForIndividual(isKnown: Boolean): Row =
    toRow(
      msgKey  = "emailAddressQuestionForIndividual",
      content = yesOrNo(isKnown),
      href    = controllers.individual.routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url
    )

  private def emailAddressForIndividual(email: String): Row =
    toRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$email",
      href    = controllers.individual.routes.EmailAddressForIndividualController.onPageLoad(CheckMode).url
    )

  def buildTaxResidencySummaryForIndividuals: Seq[Row] = (userAnswers.get(IndividualLoopPage) map { answer =>

    val validDetailsWithIndex: IndexedSeq[(LoopDetails, Int)] = answer
      .filter(_.whichCountry.isDefined)
      .zipWithIndex

    toRow(
      msgKey = "whichCountryTaxForIndividual",
      content = lit"",
      href = controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode, 0).url
    ) +:
      validDetailsWithIndex.flatMap {

        case (loopDetail, index) =>
          individualCountryRow(loopDetail.whichCountry, index, validDetailsWithIndex.size) +: taxNumberRow(loopDetail)
      }

  }).getOrElse(Seq())


  private def individualCountryRow(countryOption: Option[Country], index: Int, loopSize: Int): Row = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("whichCountryTaxForIndividual.countryCounter")(loopSize > 1, (index + 1).toString)

    Row(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription")
    )
  }

  private def taxNumberRow(loopDetail: LoopDetails): Seq[Row] =
    if (loopDetail.doYouKnowUTR.contains(true) && loopDetail.whichCountry.exists(_.code == "GB")) {
      taxNumberRow("whatAreTheTaxNumbersForUKIndividual", loopDetail.taxNumbersUK, None)
    } else if (loopDetail.doYouKnowTIN.contains(true)) {
      taxNumberRow("whatAreTheTaxNumbersForNonUKIndividual", loopDetail.taxNumbersNonUK, loopDetail.whichCountry)
    } else {
      Seq()
    }

  private def taxNumberRow(msgKey: String, taxReferenceOption: Option[TaxReferenceNumbers], country: Option[Country]): Seq[Row] = {

    val taxReferenceNumber = taxReferenceOption.getOrElse(
      throw new IllegalArgumentException("A tax reference row must have a tax reference number"))
    val countryLabel = country.map(_.description).getOrElse("")
    val taxRefLabel: Text.Message =
      messageWithPluralFormatter(s"$msgKey.checkYourAnswersLabel", countryLabel)(taxReferenceNumber.isSingleTaxReferenceNumber)

    Seq(Row(
      key     = Key(taxRefLabel, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"${formatReferenceNumbers(taxReferenceNumber)}")
    ))
  }

  def whichCountryTaxForIndividual: Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "whichCountryTaxForIndividual",
        content = lit"$answer",
        href    = controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode, 1).url
      )
  }

  def doYouKnowAnyTINForUKIndividual: Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "doYouKnowAnyTINForUKIndividual",
        content = yesOrNo(answer),
        href    = controllers.individual.routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode, 1).url
      )
  }

  def whatAreTheTaxNumbersForUKIndividual: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "whatAreTheTaxNumbersForUKIndividual",
        content = lit"$answer",
        href    = controllers.individual.routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode, 1).url
      )
  }

  def isIndividualResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsIndividualResidentForTaxOtherCountriesPage) map {
    answer =>
      toRow(
        msgKey  = "isIndividualResidentForTaxOtherCountries",
        content = yesOrNo(answer),
        href    = controllers.individual.routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1).url
      )
  }

}
