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

package utils

import controllers.routes
import models.{CheckMode, LoopDetails, TaxReferenceNumbers, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersOrganisationHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def organisationName: Option[Seq[Row]] = userAnswers.get(OrganisationNamePage) map {
    answer =>
      Seq(Row(
        key     = Key(msg"organisationName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.OrganisationNameController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"organisationName.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-org-name")
          )
        )
      ))
  }

  def isOrganisationAddressKnown: Option[Row] = userAnswers.get(IsOrganisationAddressKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isOrganisationAddressKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsOrganisationAddressKnownController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isOrganisationAddressKnown.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-do-you-know-address")
          )
        )
      )
  }

  def organisationAddress: Row = {

      val displayCountryNonUk = userAnswers.get(OrganisationAddressPage) match {
        case Some(address) if userAnswers.get(IsOrganisationAddressUkPage).contains(false) => address.country.description
        case _ => ""
      }

      val formattedAddress = (userAnswers.get(OrganisationAddressPage), userAnswers.get(SelectedAddressLookupPage)) match {
        case (Some(manualAddress), _) =>
          Html(s"""
              ${manualAddress.addressLine1.fold("")(address => s"$address<br>")}
              ${manualAddress.addressLine2.fold("")(address => s"$address<br>")}
              ${manualAddress.addressLine3.fold("")(address => s"$address<br>")}
              ${manualAddress.city}<br>
              ${manualAddress.postCode.fold("")(postcode => s"$postcode<br>")}
              $displayCountryNonUk
              """)

        case (_, Some(addressLookup)) =>
          Html(s"""
              ${addressLookup.addressLine1.fold("")(address => s"$address<br>")}
              ${addressLookup.addressLine2.fold("")(address => s"$address<br>")}
              ${addressLookup.addressLine3.fold("")(address => s"$address<br>")}
              ${addressLookup.addressLine4.fold("")(address => s"$address<br>")}
              ${s"${addressLookup.town}<br>"}
              ${addressLookup.county.fold("")(county => s"$county<br>")}
              ${addressLookup.postcode}
              """)
      }

      Row(
        key     = Key(msg"organisationAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(
          formattedAddress
        ),
        actions =
          List(
            Action(
              content            = msg"site.edit",
              href               = routes.IsOrganisationAddressUkController.onPageLoad(CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"organisationAddress.checkYourAnswersLabel")),
              attributes         = Map("id" -> "change-address")
            )
          )
      )
  }

  def emailAddressQuestionForOrganisation: Option[Row] = userAnswers.get(EmailAddressQuestionForOrganisationPage) map {
    answer =>
      Row(
        key     = Key(msg"emailAddressQuestionForOrganisation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"emailAddressQuestionForOrganisation.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-do-you-know-email-address")
          )
        )
      )
  }

  def emailAddressForOrganisation: Option[Row] = userAnswers.get(EmailAddressForOrganisationPage) map {
    answer =>
      Row(
        key     = Key(msg"emailAddressForOrganisation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.EmailAddressForOrganisationController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"emailAddressForOrganisation.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-email-address")
          )
        )
      )
  }

  def buildOrganisationDetails: Seq[SummaryList.Row] = {

    val displayAddressQuestionWithAddress: Seq[Row] = {
      if (userAnswers.get(IsOrganisationAddressKnownPage).contains(true)) {
        Seq(isOrganisationAddressKnown.get, organisationAddress)
      } else {
        Seq(isOrganisationAddressKnown.get)
      }
    }

    val displayEmailQuestionWithEmail: Seq[Row] = {
      if (userAnswers.get(EmailAddressQuestionForOrganisationPage).contains(true)) {
        Seq(emailAddressQuestionForOrganisation.get, emailAddressForOrganisation.get)
      } else {
        Seq(emailAddressQuestionForOrganisation.get)
      }
    }

    Seq(
      organisationName.get,
      displayAddressQuestionWithAddress,
      displayEmailQuestionWithEmail
    ).flatten
  }

  def whichCountryTaxForOrganisation: Seq[Row] = {
      Seq(Row(
        key     = Key(msg"whichCountryTaxForOrganisation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit""),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, 0).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whichCountryTaxForOrganisation.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-tax-residency")
          )
        )
      )
    )
  }

  def whatAreTheTaxNumbersForUKOrganisation(taxReferenceNumbers: TaxReferenceNumbers): Seq[Row] = {
    val taxRefLabelUK = if (taxReferenceNumbers.secondTaxNumber.isDefined || taxReferenceNumbers.thirdTaxNumber.isDefined) {
        msg"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel".withArgs("s")
      } else {
        msg"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel".withArgs("")
      }

      Seq(Row(
        key     = Key(taxRefLabelUK, classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"${formatReferenceNumbers(taxReferenceNumbers)}")
      ))
  }

  def whatAreTheTaxNumbersForNonUKOrganisation(country: String, taxReferenceNumbers: TaxReferenceNumbers): Seq[Row] = {
    val taxRefLabelOther = if (taxReferenceNumbers.secondTaxNumber.isDefined || taxReferenceNumbers.thirdTaxNumber.isDefined) {
      msg"whatAreTheTaxNumbersForNonUKOrganisation.checkYourAnswersLabel".withArgs("s",country)
    } else {
      msg"whatAreTheTaxNumbersForNonUKOrganisation.checkYourAnswersLabel".withArgs("", country)
    }

      Seq(Row(
        key     = Key(taxRefLabelOther, classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"${formatReferenceNumbers(taxReferenceNumbers)}")
      ))
  }

  def countryRow(country: String, index: Int, loopSize: Int): Seq[SummaryList.Row] = {
    val countryCounterDisplay = if (loopSize == 1) {
      msg"whichCountryTaxForOrganisation.countryCounter".withArgs("")
    } else {
      msg"whichCountryTaxForOrganisation.countryCounter".withArgs(index + 1)
    }

    Seq(Row(
      key     = Key(countryCounterDisplay, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$country")
    ))
  }

  def buildTaxResidencySummary(taxResidentCountriesLoop: IndexedSeq[LoopDetails]): Seq[SummaryList.Row] = {

    val rows: Seq[Row] = taxResidentCountriesLoop.zipWithIndex.flatMap {
      case (organisationLoopDetail, index) =>
        val loopSize = taxResidentCountriesLoop.size
        (organisationLoopDetail.whichCountry, organisationLoopDetail.doYouKnowUTR, organisationLoopDetail.doYouKnowTIN) match {
          case (Some(country), Some(true), _) if country.code == "GB" =>
            countryRow(country.description, index, loopSize) ++ whatAreTheTaxNumbersForUKOrganisation(organisationLoopDetail.taxNumbersUK.get)
          case (Some(country), _, Some(true)) =>
            countryRow(country.description, index, loopSize) ++ whatAreTheTaxNumbersForNonUKOrganisation(country.description, organisationLoopDetail.taxNumbersNonUK.get)
          case (Some(country), _, _) =>
            countryRow(country.description, index, loopSize)
          case _ => None
        }
    }
    whichCountryTaxForOrganisation ++ rows
  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  private def formatReferenceNumbers(referenceNumber: TaxReferenceNumbers): String = {
    val first = referenceNumber.firstTaxNumber
    (referenceNumber.secondTaxNumber, referenceNumber.thirdTaxNumber) match {
      case (Some(second), Some(third)) => s"$first, $second, $third"
      case (Some(second), None) => s"$first, $second"
      case (None, Some(third)) => s"$first, $third"
      case _ => s"$first"
    }
  }
}

