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

package utils.rows

import controllers.routes
import models.{Address, AddressLookup, CheckMode}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

trait IndividualRows extends RowBuilder {

  def individualName: Option[Row] = userAnswers.get(IndividualNamePage) map { answer =>
    toRow(
      msgKey  = "individualName",
      content = lit"${answer.firstName} ${answer.secondName}",
      href    = routes.IndividualNameController.onPageLoad(CheckMode).url
    )
  }

  def individualDateOfBirth: Option[Row] = userAnswers.get(IndividualDateOfBirthPage) map { answer =>
    toRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(answer.format(dateFormatter)),
      href    = routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url
    )
  }

  def individualPlaceOfBirthGroup: Seq[Row] =
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
      href    = routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url
    )

  private def individualPlaceOfBirth(placeOfBirth: String): Row =
    toRow(
      msgKey  = "individualPlaceOfBirth",
      content = lit"$placeOfBirth",
      href    = routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url
    )

  def individualAddressGroup: Seq[Row] =
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
      href    = routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url
    )

  private def individualAddress(manualAddress: Address): Row =
    toRow(
      msgKey  = "individualAddress",
      content = formatAddress(manualAddress),
      href    = routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
    )

  private def individualAddress(addressLookup: AddressLookup): Row =
    toRow(
      msgKey  = "individualAddress",
      content = formatAddress(addressLookup),
      href    = routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
    )

  def individualEmailAddressGroup: Seq[Row] =
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
      href    = routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url
    )

  private def emailAddressForIndividual(email: String): Row =
    toRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$email",
      href    = routes.EmailAddressForIndividualController.onPageLoad(CheckMode).url
    )

  def individualTINGroup: Seq[Row] = Seq()

  // 12 /individual/which-country-tax
  def whichCountryTaxForIndividual: Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "whichCountryTaxForIndividual",
        content = lit"$answer",
        href    = routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode, 1).url
      )
  }

  // 13 /individual/uk-tin-known
  def doYouKnowAnyTINForUKIndividual: Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "doYouKnowAnyTINForUKIndividual",
        content = yesOrNo(answer),
        href    = routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode, 1).url
      )
  }

  // 14 /individual/uk-tax-numbers
  def whatAreTheTaxNumbersForUKIndividual: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "whatAreTheTaxNumbersForUKIndividual",
        content = lit"$answer",
        href    = routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode, 1).url
      )
  }

  // Tax resident countries TODO
  // 15 /individual/non-uk-tin-known
  // 16 /individual/non-uk-tax-numbers

  // 17 /individual/tax-resident-countries
  def isIndividualResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsIndividualResidentForTaxOtherCountriesPage) map {
    answer =>
      toRow(
        msgKey  = "isIndividualResidentForTaxOtherCountries",
        content = yesOrNo(answer),
        href    = routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1).url
      )
  }

}
