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
import pages.organisation._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

trait OrganisationRows extends RowBuilder {

  def organisationName(id: Int): Option[Row] = userAnswers.get(OrganisationNamePage, id) map { answer =>
    toRow(
      msgKey  = "organisationName",
      content = lit"$answer",
      href    = controllers.organisation.routes.OrganisationNameController.onPageLoad(id, CheckMode).url
    )
  }

  def buildOrganisationAddressGroup(id: Int): Seq[Row] =
    (userAnswers.get(IsOrganisationAddressKnownPage, id)
      , userAnswers.get(OrganisationAddressPage, id)
      , userAnswers.get(SelectedAddressLookupPage, id)) match {
      case (Some(true), Some(manualAddress), _) =>
        Seq(isOrganisationAddressKnown(true, id), organisationAddress(manualAddress, id))
      case (Some(true), _, Some(addressLookup)) =>
        Seq(isOrganisationAddressKnown(true, id), organisationLookupAddress(addressLookup, id))
      case _ =>
        Seq(isOrganisationAddressKnown(false, id))
    }

  private def isOrganisationAddressKnown(addressKnown: Boolean, id: Int): Row =
    toRow(
      msgKey  = "isOrganisationAddressKnown",
      content = yesOrNo(addressKnown),
      href    = controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(id, CheckMode).url
    )

  private def organisationAddress(manualAddress: Address, id: Int): Row =
    toRow(
      msgKey  = "organisationAddress",
      content = formatAddress(manualAddress),
      href    = controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(id, CheckMode).url
    )

  private def organisationLookupAddress(addressLookup: AddressLookup, id: Int): Row =
    toRow(
      msgKey  = "organisationAddress",
      content = formatAddress(addressLookup),
      href    = controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(id, CheckMode).url
    )

  def buildOrganisationEmailAddressGroup(id: Int): Seq[Row] =
    (userAnswers.get(EmailAddressQuestionForOrganisationPage, id)
      , userAnswers.get(EmailAddressForOrganisationPage, id)) match {

      case (Some(true), Some(email)) =>
        Seq(emailAddressQuestionForOrganisation(true, id)
          , emailAddressForOrganisation(email, id))
      case _ =>
        Seq(emailAddressQuestionForOrganisation(false, id))
    }

  private def emailAddressQuestionForOrganisation(isKnown: Boolean, id: Int): Row =
    toRow(
      msgKey  = "emailAddressQuestionForOrganisation",
      content = yesOrNo(isKnown),
      href    = controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(id, CheckMode).url
    )

  private def emailAddressForOrganisation(email: String, id: Int): Row =
    toRow(
      msgKey  = "emailAddressForOrganisation",
      content = lit"$email",
      href    = controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(id, CheckMode).url
    )

  def buildTaxResidencySummaryForOrganisation(id: Int): Seq[Row] = (userAnswers.get(OrganisationLoopPage, id) map { answer =>

    val validDetailsWithIndex: IndexedSeq[(LoopDetails, Int)] = answer
      .filter(_.whichCountry.isDefined)
      .zipWithIndex

    toRow(
      msgKey = "whichCountryTaxForOrganisation",
      content = lit"",
      href = controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(id, CheckMode, 0).url
    ) +:
      validDetailsWithIndex.flatMap {

        case (loopDetail, index) =>
          organisationCountryRow(loopDetail.whichCountry, index, validDetailsWithIndex.size) +: taxNumberRow(loopDetail)
      }

  }).getOrElse(Seq())


  private def organisationCountryRow(countryOption: Option[Country], index: Int, loopSize: Int): Row = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("whichCountryTaxForOrganisation.countryCounter")(loopSize > 1, (index + 1).toString)

    Row(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription")
    )
  }

  private def taxNumberRow(loopDetail: LoopDetails): Seq[Row] =
    if (loopDetail.doYouKnowUTR.contains(true) && loopDetail.whichCountry.exists(_.code == "GB")) {
      taxNumberRow("whatAreTheTaxNumbersForUKOrganisation", loopDetail.taxNumbersUK, None)
    } else if (loopDetail.doYouKnowTIN.contains(true)) {
      taxNumberRow("whatAreTheTaxNumbersForNonUKOrganisation", loopDetail.taxNumbersNonUK, loopDetail.whichCountry)
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

  def whichCountryTaxForOrganisation(id: Int): Option[Row] = userAnswers.get(WhichCountryTaxForOrganisationPage, id) map {
    answer =>
      toRow(
        msgKey  = "whichCountryTaxForOrganisation",
        content = lit"$answer",
        href    = controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def doYouKnowAnyTINForUKOrganisation(id: Int): Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKOrganisationPage, id) map {
    answer =>
      toRow(
        msgKey  = "doYouKnowAnyTINForUKOrganisation",
        content = yesOrNo(answer),
        href    = controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def whatAreTheTaxNumbersForUKOrganisation(id: Int): Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKOrganisationPage, id) map {
    answer =>
      toRow(
        msgKey  = "whatAreTheTaxNumbersForUKOrganisation",
        content = lit"$answer",
        href    = controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def isOrganisationResidentForTaxOtherCountries(id: Int): Option[Row] = userAnswers.get(IsOrganisationResidentForTaxOtherCountriesPage, id) map {
    answer =>
      toRow(
        msgKey  = "isOrganisationResidentForTaxOtherCountries",
        content = yesOrNo(answer),
        href    = controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(id, CheckMode, 1).url
      )
  }

  def selectAddress: Option[Row] = userAnswers.get(SelectAddressPage) map {
    answer =>
      Row(
        key     = Key(msg"selectAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"selectAddress.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def postcode: Option[Row] = userAnswers.get(PostcodePage) map {
    answer =>
      Row(
        key     = Key(msg"postcode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"postcode.checkYourAnswersLabel"))
          )
        )
      )
  }


}
