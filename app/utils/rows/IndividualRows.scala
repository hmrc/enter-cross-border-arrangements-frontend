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
import models.{Address, CheckMode}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

trait IndividualRows extends RowBuilder {

  // 1
  def individualName: Option[Row] = userAnswers.get(IndividualNamePage) map { answer =>
    toRow(
      msgKey  = "individualName",
      content = lit"${answer.firstName} ${answer.secondName}",
      href    = routes.IndividualNameController.onPageLoad(CheckMode).url
    )
  }


  // 2
  def individualDateOfBirth: Option[Row] = userAnswers.get(IndividualDateOfBirthPage) map { answer =>
    toRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(answer.format(dateFormatter)),
      href    = routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url
    )
  }

  // 3, 4
  def individualPlaceOfBirthGroup: Seq[Row] =
    (userAnswers.get(IsIndividualPlaceOfBirthKnownPage), userAnswers.get(IndividualPlaceOfBirthPage)) match {
      case (Some(true), Some(placeOfBirth)) =>
        Seq(isIndividualPlaceOfBirthKnown(true), individualPlaceOfBirth(placeOfBirth))
      case _ =>
        Seq(isIndividualPlaceOfBirthKnown(false))
    }

  // 3 /individual/do-you-know-birthplace
  private def isIndividualPlaceOfBirthKnown(isKnown: Boolean): Row =
    toRow(
      msgKey  = "isIndividualPlaceOfBirthKnown",
      content = yesOrNo(isKnown),
      href    = routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url
    )

  // 4 /individual/birthplace
  private def individualPlaceOfBirth(placeOfBirth: String): Row =
    toRow(
      msgKey  = "individualPlaceOfBirth",
      content = lit"$placeOfBirth",
      href    = routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url
    )

  // 5, 9 - 6, 7, 8 are not required because they are facilitators to the address
  def individualAddressGroup: Seq[Row] =
    (userAnswers.get(IsIndividualAddressKnownPage)
      , userAnswers.get(IndividualAddressPage)) match {
      case (Some(true), Some(address)) =>
        Seq(isIndividualAddressKnown(true)
          , individualAddress(address))
      case _ =>
        Seq(isIndividualAddressKnown(false))
    }

  // 5 /individual/do-you-know-address
  private def isIndividualAddressKnown(addressKnown: Boolean): Row =
    toRow(
      msgKey  = "isIndividualAddressKnown",
      content = yesOrNo(addressKnown),
      href    = routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url
    )

  // 6 /individual/live-in-uk
  // 7 /individual/postcode
  // 8 /individual/select-address
  // 9 /individual/address
  private def individualAddress(address: Address): Row =
      toRow(
        msgKey  = "individualAddress",
        content = formatAddress(address),
        href    = routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
      )

  // 10, 11
  def individualEmailAddressGroup: Seq[Row] =
    (userAnswers.get(EmailAddressQuestionForIndividualPage)
      , userAnswers.get(EmailAddressForIndividualPage)) match {
      case (Some(true), Some(email)) =>
        Seq(emailAddressQuestionForIndividual(true)
          , emailAddressForIndividual(email))
      case _ =>
        Seq(emailAddressQuestionForIndividual(false))
    }

  // 10 /individual/what-is-email-address
  private def emailAddressQuestionForIndividual(isKnown: Boolean): Row =
    toRow(
      msgKey  = "emailAddressQuestionForIndividual",
      content = yesOrNo(isKnown),
      href    = routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url
    )


  // 11 /individual/email-address
  private def emailAddressForIndividual(email: String): Row =
    toRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$email",
      href    = routes.EmailAddressForIndividualController.onPageLoad(CheckMode).url
    )

  // 12 - 17
  def individualTINGroup: Seq[Row] = Seq()

  // 12 /individual/which-country-tax
  def whichCountryTaxForIndividual: Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "whichCountryTaxForIndividual",
        content = lit"$answer",
        href    = routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode).url
      )
  }

  // 13 /individual/uk-tin-known
  def doYouKnowAnyTINForUKIndividual: Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "doYouKnowAnyTINForUKIndividual",
        content = yesOrNo(answer),
        href    = routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode).url
      )
  }

  // 14 /individual/uk-tax-numbers
  def whatAreTheTaxNumbersForUKIndividual: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage) map {
    answer =>
      toRow(
        msgKey  = "whatAreTheTaxNumbersForUKIndividual",
        content = lit"$answer",
        href    = routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode).url
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
        href    = routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode).url
      )
  }

}
