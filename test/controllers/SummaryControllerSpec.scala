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

package controllers

import base.SpecBase
import helpers.data.ValidUserAnswersForSubmission.userAnswersForOrganisation
import models.ReporterOrganisationOrIndividual.Individual
import models.reporter.RoleInArrangement.Taxpayer
import models.reporter.intermediary.IntermediaryWhyReportInUK.TaxResidentUK
import models.{AddressLookup, Country, LoopDetails, Name, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import models.reporter.taxpayer.TaxpayerWhyReportArrangement.NoIntermediaries
import models.reporter.taxpayer.TaxpayerWhyReportInUK.UkTaxResident
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.reporter.individual.{ReporterIndividualDateOfBirthPage, ReporterIndividualEmailAddressPage, ReporterIndividualEmailAddressQuestionPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage}
import pages.reporter.intermediary.IntermediaryWhyReportInUKPage
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import java.time.LocalDate
import scala.concurrent.Future

class SummaryControllerSpec extends SpecBase with MockitoSugar {

  val addressLookup = AddressLookup(
    Some("addressLine 1"),
    Some("addressLine 2"),
    Some("addressLine 3"),
    Some("addressLine 4"),
    "town",
    Some("county"),
    "postcode")

  val france: Country = Country("valid", "FR", "FRANCE")
  val tins: TaxReferenceNumbers = TaxReferenceNumbers("TIN123123", Some("TIN123123"), Some("TIN123123"))
  val loopDetailsNonUK: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(Some(false), Some(france), Some(true), Some(tins),Some(false), None))

  "Summary Controller" - {

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers =   userAnswersForOrganisation
        .set(ReporterOrganisationOrIndividualPage, 0, Individual)
        .success.value
        .set(ReporterIndividualNamePage, 0, Name("firstname","surname"))
        .success.value
        .set(ReporterIndividualDateOfBirthPage,  0, LocalDate.of(1990, 1, 1))
        .success.value
        .set(ReporterIndividualPlaceOfBirthPage, 0, "Place of Birth")
        .success.value
        .set(ReporterSelectedAddressLookupPage, 0, addressLookup)
        .success.value
        .set(ReporterIndividualEmailAddressQuestionPage, 0, true)
        .success.value
        .set(ReporterIndividualEmailAddressPage, 0, "email@email.com")
        .success.value
        .set(ReporterTaxResidencyLoopPage, 0, loopDetailsNonUK)
        .success.value
        .set(RoleInArrangementPage, 0, Taxpayer)
        .success.value
        .set(IntermediaryWhyReportInUKPage, 0, TaxResidentUK)
        .success.value
        .set(TaxpayerWhyReportInUKPage, 0, UkTaxResident)
        .success.value
        .set(TaxpayerWhyReportArrangementPage, 0, NoIntermediaries)
        .success.value
        .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.of(2020, 1, 1))
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val getRequest = FakeRequest(GET, routes.SummaryController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

      //ToDo test output from page
      templateCaptor.getValue mustEqual "summary.njk"

      application.stop()
    }
  }
}
