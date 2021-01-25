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

  def roleInArrangementPage(id: Int): Option[Row] = userAnswers.get(RoleInArrangementPage, id) map { answer =>
    toRow(
      msgKey  = "roleInArrangement",
      content = Literal(s"${answer.toString.capitalize}"),
      href    = controllers.reporter.routes.RoleInArrangementController.onPageLoad(id, CheckMode).url
    )
  }

  def reporterOrganisationOrIndividual(id: Int): Option[Row] = userAnswers.get(ReporterOrganisationOrIndividualPage, id) map {
    answer =>

    toRow(
      msgKey  = "reporterOrganisationOrIndividual",
      content = Literal(s"${answer.toString.capitalize}"),
      href    = controllers.reporter.routes.ReporterOrganisationOrIndividualController.onPageLoad(id, CheckMode).url
    )
  }

  //Reporter - Organisation Journey

  def reporterOrganisationName(id: Int): Option[Row] = userAnswers.get(ReporterOrganisationNamePage, id) map { answer =>
    toRow(
      msgKey  = "reporterOrganisationName",
      content = Literal(s"${answer.capitalize}"),
      href    = controllers.reporter.organisation.routes.ReporterOrganisationNameController.onPageLoad(id, CheckMode).url
    )
  }

  def buildOrganisationReporterAddressGroup(id: Int): Seq[Row] = {
    (userAnswers.get(ReporterOrganisationAddressPage, id), userAnswers.get(ReporterSelectedAddressLookupPage, id)) match {
      case (Some(address), _) => Seq(reporterOrganisationAddress(address, id))
      case (_, Some(addressLookup)) => Seq(reporterOrganisationAddressLookup(addressLookup, id))
      case _ => throw new Exception("Unable to retrieve Organisation reporter details address from user answers")
    }
  }

  private def reporterOrganisationAddress(manualAddress: Address, id: Int): Row =
    toRow(
      msgKey = "reporterOrganisationAddress",
      content = formatAddress(manualAddress),
      href = controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(id, CheckMode).url
    )

  private def reporterOrganisationAddressLookup(addressLookup: AddressLookup, id: Int): Row =
    toRow(
      msgKey = "reporterOrganisationAddress",
      content = formatAddress(addressLookup),
      href = controllers.reporter.organisation.routes.ReporterOrganisationIsAddressUkController.onPageLoad(id, CheckMode).url
    )

  def buildReporterOrganisationEmailGroup(id: Int): Seq[Row] =
    (userAnswers.get(ReporterOrganisationEmailAddressQuestionPage, id), userAnswers.get(ReporterOrganisationEmailAddressPage, id)) match {
      case (Some(true), Some(email)) =>
        Seq(reporterOrganisationEmailAddressQuestion(true, id), reporterOrganisationEmailAddress(email, id))
      case _ =>
        Seq(reporterOrganisationEmailAddressQuestion(false, id))
    }

  private def reporterOrganisationEmailAddressQuestion(isKnown: Boolean, id: Int): Row =
    toRow(
      msgKey  = "reporterOrganisationEmailAddressQuestion",
      content = yesOrNo(isKnown),
      href    = controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(id, CheckMode).url
    )

  private def reporterOrganisationEmailAddress(email: String, id: Int): Row =
    toRow(
      msgKey  = "reporterOrganisationEmailAddress",
      content = Literal(s"$email"),
      href    = controllers.reporter.organisation.routes.ReporterOrganisationEmailAddressController.onPageLoad(id, CheckMode).url
    )

  //Reporter - Individual Journey

  def reporterIndividualName(id: Int): Option[Row] = userAnswers.get(ReporterIndividualNamePage, id) map { answer =>
    toRow(
      msgKey  = "reporterIndividualName",
      content = Literal(s"${answer.displayName.capitalize}"),
      href    = controllers.reporter.individual.routes.ReporterIndividualNameController.onPageLoad(id, CheckMode).url
    )
  }

  def reporterIndividualPlaceOfBirth(id: Int): Option[Row] = userAnswers.get(ReporterIndividualPlaceOfBirthPage, id) map { answer =>
    toRow(
      msgKey  = "reporterIndividualPlaceOfBirth",
      content = Literal(s"${answer.capitalize}"),
      href    = controllers.reporter.individual.routes.ReporterIndividualPlaceOfBirthController.onPageLoad(id, CheckMode).url
    )
  }

  def reporterIndividualDateOfBirth(id: Int): Option[Row] = userAnswers.get(ReporterIndividualDateOfBirthPage, id) map { answer =>
    toRow(
      msgKey  = "reporterIndividualDateOfBirth",
      content = Literal(s"${answer.format(dateFormatter)}"),
      href    = controllers.reporter.individual.routes.ReporterIndividualDateOfBirthController.onPageLoad(id, CheckMode).url
    )
  }

  def buildReporterIndividualEmailGroup(id: Int): Seq[Row] =
    (userAnswers.get(ReporterIndividualEmailAddressQuestionPage, id), userAnswers.get(ReporterIndividualEmailAddressPage, id)) match {
      case (Some(true), Some(email)) =>
        Seq(reporterIndividualEmailAddressQuestion(true, id), reporterIndividualEmailAddress(email, id))
      case _ =>
        Seq(reporterIndividualEmailAddressQuestion(false, id))
    }

  private def reporterIndividualEmailAddressQuestion(isKnown: Boolean, id: Int): Row =
    toRow(
      msgKey  = "reporterIndividualEmailAddressQuestion",
      content = yesOrNo(isKnown),
      href    = controllers.reporter.individual.routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(id, CheckMode).url
    )

  private def reporterIndividualEmailAddress(email: String, id: Int): Row =
    toRow(
      msgKey  = "reporterIndividualEmailAddress",
      content = Literal(s"$email"),
      href    = controllers.reporter.individual.routes.ReporterIndividualEmailAddressController.onPageLoad(id, CheckMode).url
    )


  def buildIndividualReporterAddressGroup(id: Int): Seq[Row]= {
    (userAnswers.get(ReporterIndividualAddressPage, id), userAnswers.get(ReporterSelectedAddressLookupPage, id)) match {
      case (Some(address), _) => Seq(reporterIndividualAddress(address, id))
      case (_, Some(addressLookup)) => Seq(reporterIndividualAddressLookup(addressLookup, id))
      case _ => throw new Exception("Unable to retrieve Individual reporter details address from user answers")
    }
  }

  private def reporterIndividualAddress(manualAddress: Address, id: Int): Row =
    toRow(
      msgKey = "reporterIndividualAddress",
      content = formatAddress(manualAddress),
      href = controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(id, CheckMode).url
    )

  private def reporterIndividualAddressLookup(addressLookup: AddressLookup, id: Int): Row =
    toRow(
      msgKey = "reporterIndividualAddress",
      content = formatAddress(addressLookup),
      href = controllers.reporter.individual.routes.ReporterIsIndividualAddressUKController.onPageLoad(id, CheckMode).url
    )

  //Reporter - TaxResidency Loop

  def buildTaxResidencySummaryForReporter(id: Int): Seq[Row] = (userAnswers.get(ReporterTaxResidencyLoopPage, id) map {
    answer =>

    val validDetailsWithIndex: IndexedSeq[(LoopDetails, Int)] = answer
      .filter(_.whichCountry.isDefined)
      .zipWithIndex
    toRow(
      msgKey = "reporterTaxResidentCountry",
      content = lit"",
      href = controllers.reporter.routes.ReporterTaxResidentCountryController.onPageLoad(id, CheckMode, 0).url
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

  def intermediaryWhyReportInUKPage(id: Int): Option[Row] = userAnswers.get(IntermediaryWhyReportInUKPage, id) map { answer =>

    toRow(
      msgKey  = "whyReportInUK",
      content = msg"whyReportInUK.$answer",
      href    = controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(id, CheckMode).url
    )
  }

  def intermediaryRolePage(id: Int): Option[Row] = userAnswers.get(IntermediaryRolePage, id) map { answer =>

    toRow(
      msgKey  = "intermediaryRole",
      content = msg"intermediaryRole.$answer",
      href    = controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(id, CheckMode).url
    )
  }

  private def intermediaryExemptionInEUPage(answer: YesNoDoNotKnowRadios, id: Int): Row =
    toRow(
      msgKey  = "intermediaryExemptionInEU",
      content = msg"intermediaryExemptionInEU.$answer",
      href    = controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(id, CheckMode).url
    )

  private def intermediaryDoYouKnowExemptionsPage(answer: Boolean, id: Int): Row =
    toRow(
      msgKey  = "intermediaryDoYouKnowExemptions",
      content = yesOrNo(answer),
      href    = controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(id, CheckMode).url
    )

  private def intermediaryWhichCountriesExemptPage(id: Int): Option[Row] = userAnswers.get(IntermediaryWhichCountriesExemptPage, id) map {
    countryList =>
    toRow(
      msgKey  = "intermediaryWhichCountriesExempt",
      content = Html(formatExemptCountriesList(countryList, countryList.tail.isEmpty)),
      href    = controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(id, CheckMode).url
    )
  }

  private def formatExemptCountriesList(selectedCountries: Set[CountriesListEUCheckboxes], singleItem: Boolean) = {

    val getCountryName = selectedCountries.map(_.toString).toSeq.map(
      countryCode => msg"countriesListCheckboxes.$countryCode".resolve).sorted

    if (singleItem) {
      getCountryName.head
    } else {
      s"<ul class='govuk-list govuk-list--bullet'>${getCountryName.foldLeft("")((a, b) => s"$a<li>$b</li>")}</ul>"
    }
  }

  def buildExemptCountriesSummary(id: Int): Seq[Row] = {
    (userAnswers.get(IntermediaryExemptionInEUPage, id), userAnswers.get(IntermediaryDoYouKnowExemptionsPage, id)) match {
      case (Some(YesNoDoNotKnowRadios.Yes), Some(true)) =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.Yes, id),
          intermediaryDoYouKnowExemptionsPage(true, id)) ++ intermediaryWhichCountriesExemptPage(id).toSeq
      case (Some(YesNoDoNotKnowRadios.Yes), Some(false)) =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.Yes, id), intermediaryDoYouKnowExemptionsPage(false, id))
      case (Some(YesNoDoNotKnowRadios.No), _) =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.No, id))
      case _ =>
        Seq(intermediaryExemptionInEUPage(YesNoDoNotKnowRadios.DoNotKnow, id))
    }
  }

  //Reporter - Taxpayer Journey

  private def taxpayerWhyReportArrangementPage(answer: TaxpayerWhyReportArrangement, id: Int): Row = toRow(
      msgKey = "taxpayerWhyReportArrangement",
      content = msg"taxpayerWhyReportArrangement.$answer",
      href = controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(id, CheckMode).url
    )

  private def taxpayerWhyReportInUKPage(answer: TaxpayerWhyReportInUK, id: Int): Row =
    toRow(
      msgKey  = "taxpayerWhyReportInUK",
      content = msg"taxpayerWhyReportInUK.$answer",
      href    = controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(id, CheckMode).url
    )

  def taxpayerImplementationDate(id: Int): Option[Row] = userAnswers.get(ReporterTaxpayersStartDateForImplementingArrangementPage, id) map {
    answer =>
      toRow(
        msgKey  = "reporterTaxpayerImplementingArrangement",
        content = Literal(answer.format(dateFormatter)),
        href    = controllers.reporter.taxpayer.routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(id, CheckMode).url
      )
  }

  def buildTaxpayerReporterReasonGroup(id: Int): Seq[Row] =
    (userAnswers.get(TaxpayerWhyReportInUKPage, id), userAnswers.get(TaxpayerWhyReportArrangementPage, id)) match {
      case (Some(TaxpayerWhyReportInUK.DoNotKnow), _) => Seq(taxpayerWhyReportInUKPage(TaxpayerWhyReportInUK.DoNotKnow, id))
      case (Some(otherValue), Some(answer)) =>
        Seq(taxpayerWhyReportInUKPage(otherValue, id), taxpayerWhyReportArrangementPage(answer, id))
      case _ => throw new Exception("Unable to retrieve reporter details taxpayer's reason for reporting")
    }
}
