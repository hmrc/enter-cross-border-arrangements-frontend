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
import models.{CheckMode, OrganisationLoopDetails, UserAnswers}
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
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"organisationName.checkYourAnswersLabel"))
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
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isOrganisationAddressKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def organisationAddress: Option[Row] = userAnswers.get(OrganisationAddressPage) map {
    answer =>

      val displayCountryNonUk =
        if (userAnswers.get(IsOrganisationAddressUkPage).contains(true)) "" else answer.country.description

      Row(
        key     = Key(msg"organisationAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(
          Html(s"""
              ${answer.addressLine1.fold("")(address => s"$address<br>")}
              ${answer.addressLine2.fold("")(address => s"$address<br>")}
              ${answer.addressLine3.fold("")(address => s"$address<br>")}
              ${answer.city}<br>
              ${answer.postCode.fold("")(postcode => s"$postcode<br>")}
              $displayCountryNonUk
              """)
        ),
        actions =
          List(
            Action(
              content            = msg"site.edit",
              href               = routes.IsOrganisationAddressUkController.onPageLoad(CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"organisationAddress.checkYourAnswersLabel"))
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
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"emailAddressQuestionForOrganisation.checkYourAnswersLabel"))
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
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"emailAddressForOrganisation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def buildOrganisationDetails: Seq[SummaryList.Row] = {

    val displayAddressQuestionWithAddress: Seq[Row] = {
      if (userAnswers.get(IsOrganisationAddressKnownPage).contains(true)) {
        Seq(isOrganisationAddressKnown.get, organisationAddress.get)
      } else {
        userAnswers.remove(OrganisationAddressPage)
        Seq(isOrganisationAddressKnown.get)
      }
    }

    val displayEmailQuestionWithEmail: Seq[Row] = {
      if (userAnswers.get(EmailAddressQuestionForOrganisationPage).contains(true)) {
        Seq(emailAddressQuestionForOrganisation.get, emailAddressForOrganisation.get)
      } else {
        userAnswers.remove(EmailAddressForOrganisationPage)
        Seq(emailAddressQuestionForOrganisation.get)
      }
    }

    Seq(
      organisationName.get,
      displayAddressQuestionWithAddress,
      displayEmailQuestionWithEmail
    ).flatten

  }

  //TODO Update start indexes of change links
  def whichCountryTaxForOrganisation: Option[Seq[Row]] = userAnswers.get(WhichCountryTaxForOrganisationPage) map {
    answer =>
      Seq(Row(
        key     = Key(msg"whichCountryTaxForOrganisation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit""),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, 0).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whichCountryTaxForOrganisation.checkYourAnswersLabel"))
          )
        )
      ))
  }

  def whatAreTheTaxNumbersForNonUKOrganisation: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForNonUKOrganisationPage) map {
    answer =>
      Row(
        key     = Key(msg"whatAreTheTaxNumbersForNonUKOrganisation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(CheckMode, 1).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatAreTheTaxNumbersForNonUKOrganisation.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isOrganisationResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsOrganisationResidentForTaxOtherCountriesPage) map {
    answer =>
      Row(
        key     = Key(msg"isOrganisationResidentForTaxOtherCountries.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isOrganisationResidentForTaxOtherCountries.checkYourAnswersLabel"))
          )
        )
      )
  }

  def whatAreTheTaxNumbersForUKOrganisation: Option[Seq[Row]] = userAnswers.get(WhatAreTheTaxNumbersForUKOrganisationPage) map {
    answer =>
      Seq(Row(
        key     = Key(msg"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel"))
          )
        )
      ))
  }

  def buildCountryWithTaxReferenceDetails(taxResidentCountriesLoop: IndexedSeq[OrganisationLoopDetails])= {

    //TODO - WORK OUT WHY UK WONT DISPLAY IN LOOP

    for {
      taxResidentCountry <- taxResidentCountriesLoop
      country <- taxResidentCountry.whichCountry
      taxReference <- taxResidentCountry.taxNumbersNonUK
    } yield {

      val displayUTRorTinKey: Text.Message =
        if (country.code.contains("UK")) {
          msg"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel"
        } else {
          msg"whatAreTheTaxNumbersForNonUKOrganisation.checkYourAnswersLabel".withArgs(country.description)
        }

      //TODO - REFACTOR BELOW IF COUNT DOES NOT WORK

      val rows: Iterable[Seq[SummaryList.Row]] = taxResidentCountry.whichCountry.zipWithIndex.map {
        case (country, count) =>
          Seq(
            Row(
              key = Key(msg"whichCountryTaxForOrganisation.countryKey".withArgs(count), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}")
            ),
            Row(
              key = Key(displayUTRorTinKey, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${taxReference.firstTaxNumber}"))
          )
      }
      rows
    }
  }

  def buildCountryWithReferenceSummary(taxResidentCountriesLoop: IndexedSeq[OrganisationLoopDetails]) = {

    //TODO - REFACTOR
    whichCountryTaxForOrganisation.get ++ buildCountryWithTaxReferenceDetails(taxResidentCountriesLoop).flatMap(x => x.flatten)
  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }
}


