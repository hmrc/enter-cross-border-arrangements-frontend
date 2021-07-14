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

import models.UserAnswers
import models.reporter.ReporterDetails
import pages.ModelPage
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

case object ReporterDetailsPage extends ModelPage[ReporterDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reporterDetails"

  override def restore(userAnswers: UserAnswers, id: Int, from: Option[ReporterDetails]): Try[UserAnswers] =
    from.fold[Try[UserAnswers]](Success(userAnswers)) {
      reporterDetails =>
        implicit val org: ReporterDetails = implicitly(reporterDetails)
        userAnswers
          .set(ReporterOrganisationOrIndividualPage, id)
          .flatMap(_.set(ReporterOrganisationNamePage, id))
          .flatMap(_.set(ReporterOrganisationEmailAddressQuestionPage, id))
          .flatMap(_.set(ReporterOrganisationEmailAddressPage, id))
          .flatMap(_.set(ReporterOrganisationAddressPage, id))
          .flatMap(_.set(ReporterOrganisationIsAddressUkPage, id))
          .flatMap(_.set(ReporterOrganisationPostcodePage, id))
          .flatMap(_.set(ReporterOrganisationSelectAddressPage, id))
          .flatMap(_.set(ReporterSelectedAddressLookupPage, id))
          .flatMap(_.set(ReporterUKTaxNumbersPage, id))
          .flatMap(_.set(ReporterIndividualNamePage, id))
          .flatMap(_.set(ReporterIndividualDateOfBirthPage, id))
          .flatMap(_.set(ReporterIndividualPlaceOfBirthPage, id))
          .flatMap(_.set(ReporterIndividualEmailAddressQuestionPage, id))
          .flatMap(_.set(ReporterIndividualEmailAddressPage, id))
          .flatMap(_.set(ReporterIndividualAddressPage, id))
          .flatMap(_.set(ReporterSelectedAddressLookupPage, id))
          .flatMap(_.set(ReporterIndividualPostcodePage, id))
          .flatMap(_.set(ReporterIndividualSelectAddressPage, id))
          .flatMap(_.set(ReporterIsIndividualAddressUKPage, id))
          .flatMap(_.set(RoleInArrangementPage, id))
          .flatMap(_.set(IntermediaryDoYouKnowExemptionsPage, id))
          .flatMap(_.set(IntermediaryExemptionInEUPage, id))
          .flatMap(_.set(IntermediaryRolePage, id))
          .flatMap(_.set(IntermediaryWhichCountriesExemptPage, id))
          .flatMap(_.set(IntermediaryWhyReportInUKPage, id))
          .flatMap(_.set(TaxpayerWhyReportArrangementPage, id))
          .flatMap(_.set(TaxpayerWhyReportInUKPage, id))
          .flatMap(_.set(ReporterTaxpayersStartDateForImplementingArrangementPage, id))
          .flatMap(_.set(ReporterUKTaxNumbersPage, id))
          .flatMap(_.set(ReporterTaxResidencyLoopPage, id))
          .flatMap(_.set(ReporterTinNonUKQuestionPage, id))
          .flatMap(_.set(ReporterTaxResidentCountryPage, id))
          .flatMap(_.set(ReporterOtherTaxResidentQuestionPage, id))
          .flatMap(_.set(ReporterNonUKTaxNumbersPage, id))

    }
}
