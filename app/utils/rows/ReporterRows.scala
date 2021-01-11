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

import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{Address, AddressLookup, CheckMode, CountriesListEUCheckboxes, Country, LoopDetails, TaxReferenceNumbers, YesNoDoNotKnowRadios}
import pages.reporter._
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationEmailAddressQuestionPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

trait ReporterRows extends RowBuilder {

  def roleInArrangementPage: Option[Row] = userAnswers.get(RoleInArrangementPage) map { answer =>
    toRow(
      msgKey  = "roleInArrangement",
      content = Literal(s"${answer.toString.capitalize}"),
      href    = controllers.reporter.routes.RoleInArrangementController.onPageLoad(CheckMode).url
    )
  }

  def reporterOrganisationOrIndividual: Option[Row] = userAnswers.get(ReporterOrganisationOrIndividualPage) map {
    answer =>

    toRow(
      msgKey  = "reporterOrganisationOrIndividual",
      content = Literal(s"${answer.toString.capitalize}"),
      href    = controllers.reporter.routes.ReporterOrganisationOrIndividualController.onPageLoad(CheckMode).url
    )
  }

  //Reporter - Organisation Journey

  def reporterOrganisationName: Option[Row] = userAnswers.get(ReporterOrganisationNamePage) map { answer =>
    toRow(
      msgKey  = "reporterOrganisationName",
      content = Literal(s"${answer.capitalize}"),
      href    = controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(CheckMode).url
    )
  }

  def buildOrganisationReporterAddressGroup: Seq[Row] = {
    (userAnswers.get(ReporterOrganisationAddressPage), userAnswers.get(ReporterSelectedAddressLookupPage)) match {
      case (Some(address), _) => Seq(reporterOrganisationAddress(address))
      case (_, Some(addressLookup)) => Seq(reporterOrganisationAddressLookup(addressLookup))
      case _ => throw new Exception("Unable to retrieve Organisation reporter details address from user answers")
    }
  }

  private def reporterOrganisationAddress(manualAddress: Address): Row =
    toRow(
      msgKey = "reporterOrganisationAddress",
      content = formatAddress(manualAddress),
      href = controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(CheckMode).url
    )

  private def reporterOrganisationAddressLookup(addressLookup: AddressLookup): Row =
    toRow(
      msgKey = "reporterOrganisationAddress",
      content = formatAddress(addressLookup),
      href = controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(CheckMode).url
    )

  def buildReporterOrganisationEmailGroup: Seq[Row] =
    (userAnswers.get(ReporterOrganisationEmailAddressQuestionPage), userAnswers.get(ReporterOrganisationEmailAddressPage)) match {
      case (Some(true), Some(email)) =>
        Seq(reporterOrganisationEmailAddressQuestion(true), reporterOrganisationEmailAddress(email))
      case _ =>
        Seq(reporterOrganisationEmailAddressQuestion(false))
    }

  private def reporterOrganisationEmailAddressQuestion(isKnown: Boolean): Row =
    toRow(
      msgKey  = "reporterOrganisationEmailAddressQuestion",
      content = yesOrNo(isKnown),
      href    = controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(CheckMode).url
    )

  private def reporterOrganisationEmailAddress(email: String): Row =
    toRow(
      msgKey  = "reporterOrganisationEmailAddress",
      content = Literal(s"$email"),
      href    = controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressController.onPageLoad(CheckMode).url
    )

  //Reporter - Individual Journey

  def reporterIndividualName: Option[Row] = userAnswers.get(ReporterIndividualNamePage) map { answer =>
    toRow(
      msgKey  = "reporterIndividualName",
      content = Literal(s"${answer.displayName.capitalize}"),
      href    = controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(CheckMode).url
    )
  }

  def reporterIndividualPlaceOfBirth: Option[Row] = userAnswers.get(ReporterIndividualPlaceOfBirthPage) map { answer =>
    toRow(
      msgKey  = "reporterIndividualName",
      content = Literal(s"${answer.capitalize}"),
      href    = controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(CheckMode).url
    )
  }

  def reporterIndividualDateOfBirth: Option[Row] = userAnswers.get(ReporterIndividualDateOfBirthPage) map { answer =>
    toRow(
      msgKey  = "reporterIndividualName",
      content = Literal(s"${answer.format(dateFormatter)}"),
      href    = controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(CheckMode).url
    )
  }

  def buildReporterIndividualEmailGroup: Seq[Row] =
    (userAnswers.get(ReporterIndividualEmailAddressQuestionPage), userAnswers.get(ReporterIndividualEmailAddressPage)) match {
      case (Some(true), Some(email)) =>
        Seq(reporterIndividualEmailAddressQuestion(true), reporterIndividualEmailAddress(email))
      case _ =>
        Seq(reporterIndividualEmailAddressQuestion(false))
    }

  private def reporterIndividualEmailAddressQuestion(isKnown: Boolean): Row =
    toRow(
      msgKey  = "reporterIndividualEmailAddressQuestion",
      content = yesOrNo(isKnown),
      href    = controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(CheckMode).url
    )

  private def reporterIndividualEmailAddress(email: String): Row =
    toRow(
      msgKey  = "reporterIndividualEmailAddress",
      content = Literal(s"$email"),
      href    = controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(CheckMode).url
    )


  def buildIndividualReporterAddressGroup: Seq[Row]= {
    (userAnswers.get(ReporterIndividualAddressPage), userAnswers.get(ReporterSelectedAddressLookupPage)) match {
      case (Some(address), _) => Seq(reporterIndividualAddress(address))
      case (_, Some(addressLookup)) => Seq(reporterIndividualAddressLookup(addressLookup))
      case _ => throw new Exception("Unable to retrieve Individual reporter details address from user answers")
    }
  }

  private def reporterIndividualAddress(manualAddress: Address): Row =
    toRow(
      msgKey = "reporterIndividualAddress",
      content = formatAddress(manualAddress),
      href = controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(CheckMode).url
    )

  private def reporterIndividualAddressLookup(addressLookup: AddressLookup): Row =
    toRow(
      msgKey = "reporterIndividualAddress",
      content = formatAddress(addressLookup),
      href = controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(CheckMode).url
    )

  //Reporter - TaxResidency Loop

  def buildTaxResidencySummaryForReporter: Seq[Row] = (userAnswers.get(ReporterTaxResidencyLoopPage) map {
    answer =>

    val validDetailsWithIndex: IndexedSeq[(LoopDetails, Int)] = answer
      .filter(_.whichCountry.isDefined)
      .zipWithIndex
    toRow(
      msgKey = "reporterTaxResidentCountry",
      content = lit"",
      href = controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(CheckMode, 0).url
    ) +:
      validDetailsWithIndex.flatMap {
        case (loopDetail, index) =>
          reporterOrganisationCountryRow(loopDetail.whichCountry, index, validDetailsWithIndex.size) +: taxNumberRow(loopDetail)
      }
  }).getOrElse(Seq())


  private def reporterOrganisationCountryRow(countryOption: Option[Country], index: Int, loopSize: Int): Row = {

    val countryDescription = countryOption.map(_.description).getOrElse(
      throw new IllegalArgumentException("A country row must have a non-empty country"))
    val label = messageWithPluralFormatter("reporterTaxResidentCountry.countryCounter")(loopSize > 1, (index + 1).toString)

    Row(
      key     = Key(label, classes = Seq("govuk-!-width-one-half")),
      value   = Value(lit"$countryDescription")
    )
  }

  private def taxNumberRow(loopDetail: LoopDetails): Seq[Row] =
    if (loopDetail.doYouKnowUTR.contains(true) && loopDetail.whichCountry.exists(_.code == "GB")) {
      taxNumberRow("reporterUKTaxNumbers", loopDetail.taxNumbersUK, None)
    } else if (loopDetail.doYouKnowTIN.contains(true)) {
      taxNumberRow("reporterNonUKTaxNumbers", loopDetail.taxNumbersNonUK, loopDetail.whichCountry)
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
  //Reporter - Intermediary Journey

  def intermediaryWhyReportInUKPage: Option[Row] = userAnswers.get(IntermediaryWhyReportInUKPage) map { answer =>

    toRow(
      msgKey  = "whyReportInUK",
      content = msg"whyReportInUK.$answer",
      href    = controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(CheckMode).url
    )
  }

  def intermediaryRolePage: Option[Row] = userAnswers.get(IntermediaryRolePage) map { answer =>

    toRow(
      msgKey  = "intermediaryRole",
      content = msg"intermediaryRole.$answer",
      href    = controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(CheckMode).url
    )
  }

  private def intermediaryExemptionInEUPage(answer: YesNoDoNotKnowRadios): Row =
    toRow(
      msgKey  = "intermediaryExemptionInEU",
      content = msg"intermediaryExemptionInEU.$answer",
      href    = controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(CheckMode).url
    )

  private def intermediaryDoYouKnowExemptionsPage(answer: Boolean): Row =
    toRow(
      msgKey  = "intermediaryDoYouKnowExemptions",
      content = yesOrNo(answer),
      href    = controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(CheckMode).url
    )

  private def intermediaryWhichCountriesExemptPage: Option[Row] = userAnswers.get(IntermediaryWhichCountriesExemptPage) map { answer =>
    toRow(
      msgKey  = "intermediaryWhichCountriesExempt",
      content = Html(formatExemptCountriesList(answer)),
      href    = controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(CheckMode).url
    )
  }

  private def formatExemptCountriesList(selectedCountries: Set[CountriesListEUCheckboxes]) = {
    val getCountryName = selectedCountries.map(countryCode => msg"countriesListCheckboxes.$countryCode".resolve)

    if (selectedCountries.size == 1) {
      getCountryName.head
    } else {
      s"<ul class='govuk-list govuk-list--bullet'>${getCountryName.foldLeft("")((a, b) => s"$a<li>$b</li>")}</ul>"
    }
  }

  def buildExemptCountriesSummary: Seq[Row] = {
    (userAnswers.get(IntermediaryExemptionInEUPage), userAnswers.get(IntermediaryDoYouKnowExemptionsPage)) match {
      case (Some(YesNoDoNotKnowRadios.Yes), Some(true)) =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.Yes),
          intermediaryDoYouKnowExemptionsPage(true)) ++ intermediaryWhichCountriesExemptPage.toSeq
      case (Some(YesNoDoNotKnowRadios.Yes), Some(false)) =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.Yes), intermediaryDoYouKnowExemptionsPage(false))
      case (Some(YesNoDoNotKnowRadios.No), _) =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.No))
      case _ =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.DoNotKnow))
    }
  }

  //Reporter - Taxpayer Journey

  private def taxpayerWhyReportArrangementPage(answer: TaxpayerWhyReportArrangement): Row = toRow(
      msgKey = "taxpayerWhyReportArrangement",
      content = msg"taxpayerWhyReportArrangement.$answer",
      href = controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(CheckMode).url
    )

  private def taxpayerWhyReportInUKPage(answer: TaxpayerWhyReportInUK): Row =
    toRow(
      msgKey  = "taxpayerWhyReportInUK",
      content = msg"taxpayerWhyReportInUK.$answer",
      href    = controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(CheckMode).url
    )

  def taxpayerImplementationDate: Option[Row] = userAnswers.get(ReporterTaxpayersStartDateForImplementingArrangementPage) map {
    answer =>
      toRow(
        msgKey  = "reporterTaxpayerImplementingArrangement",
        content = Literal(answer.format(dateFormatter)),
        href    = controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(CheckMode).url
      )
  }

  def buildTaxpayerReporterReasonGroup: Seq[Row] =
    (userAnswers.get(TaxpayerWhyReportInUKPage), userAnswers.get(TaxpayerWhyReportArrangementPage)) match {
      case (Some(TaxpayerWhyReportInUK.DoNotKnow), _) => Seq(taxpayerWhyReportInUKPage(TaxpayerWhyReportInUK.DoNotKnow))
      case (Some(otherValue), Some(answer)) =>
        Seq(taxpayerWhyReportInUKPage(otherValue), taxpayerWhyReportArrangementPage(answer))
      case _ => throw new Exception("Unable to retrieve reporter details taxpayer's reason for reporting")
    }
}
