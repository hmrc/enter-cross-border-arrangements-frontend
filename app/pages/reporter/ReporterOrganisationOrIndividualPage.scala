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

package pages.reporter

import models.ReporterOrganisationOrIndividual.{Individual, Organisation}
import models.{ReporterOrganisationOrIndividual, UserAnswers}
import pages.QuestionPage
import pages.intermediaries.WhatTypeofIntermediaryPage
import pages.reporter.individual._
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhichCountriesExemptPage, IntermediaryWhyReportInUKPage}
import pages.reporter.organisation._
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.api.libs.json.JsPath

import scala.util.Try

case object ReporterOrganisationOrIndividualPage extends QuestionPage[ReporterOrganisationOrIndividual] {

  val intermediariesPageList = List(
    IntermediaryExemptionInEUPage,
    IntermediaryWhyReportInUKPage,
    WhatTypeofIntermediaryPage,
    IntermediaryRolePage,
    IntermediaryDoYouKnowExemptionsPage,
    IntermediaryWhichCountriesExemptPage
  )

  val taxpayersPageList = List(
    TaxpayerWhyReportInUKPage,
    TaxpayerWhyReportArrangementPage
  )

  val reporterPageList = List(
    ReporterTaxResidentCountryPage,
    ReporterTaxResidencyLoopPage,
    ReporterTinNonUKQuestionPage,
    ReporterNonUKTaxNumbersPage,
    RoleInArrangementPage,
    ReporterOtherTaxResidentQuestionPage
  )

  val reporterIndividualPageList = List(
    ReporterIndividualNamePage,
    ReporterIndividualDateOfBirthPage,
    ReporterIndividualPlaceOfBirthPage,
    ReporterIndividualEmailAddressQuestionPage,
    ReporterIndividualEmailAddressPage,
    ReporterIndividualAddressPage,
    ReporterSelectedAddressLookupPage,
    ReporterIndividualPostcodePage,
    ReporterIndividualSelectAddressPage,
    ReporterIsIndividualAddressUKPage
  )

  val reporterOrganisationPageList = List(
    ReporterOrganisationNamePage,
    ReporterOrganisationEmailAddressQuestionPage,
    ReporterOrganisationEmailAddressPage,
    ReporterOrganisationAddressPage,
    ReporterOrganisationIsAddressUkPage,
    ReporterOrganisationPostcodePage,
    ReporterOrganisationSelectAddressPage,
    ReporterSelectedAddressLookupPage
  )

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reporterOrganisationOrIndividual"

  override def cleanup(value: Option[ReporterOrganisationOrIndividual], userAnswers: UserAnswers, id: Int): Try[UserAnswers] = {

    val clearOrganisationList = intermediariesPageList ++ taxpayersPageList ++ reporterPageList ++ reporterOrganisationPageList
    val clearIndividualList = intermediariesPageList ++ taxpayersPageList ++ reporterPageList ++ reporterIndividualPageList

    value match {
      case Some(Organisation) =>
        clearIndividualList.foldLeft(Try(userAnswers)) { case (ua, page) => ua.flatMap(x => x.remove(page, id)) }

      case Some(Individual) =>
        clearOrganisationList.foldLeft(Try(userAnswers)) { case (ua, page) => ua.flatMap(x => x.remove(page, id)) }

      case _ => super.cleanup(value, userAnswers, id)
    }
  }
}
