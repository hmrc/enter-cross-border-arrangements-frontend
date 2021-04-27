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
import connectors.{CrossBorderArrangementsConnector, HistoryConnector, ValidationConnector}
import controllers.actions.{ContactRetrievalAction, FakeContactRetrievalAction}
import helpers.data.ValidUserAnswersForSubmission.userAnswersForOrganisation
import matchers.JsonMatchers
import models.ReporterOrganisationOrIndividual.Individual
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.hallmarks.JourneyStatus
import models.reporter.RoleInArrangement.Taxpayer
import models.reporter.intermediary.IntermediaryWhyReportInUK.TaxResidentUK
import models.{AddressLookup, Country, LoopDetails, Name, SubmissionDetails, SubmissionHistory, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import models.reporter.taxpayer.TaxpayerWhyReportArrangement.NoIntermediaries
import models.reporter.taxpayer.TaxpayerWhyReportInUK.UkTaxResident
import models.subscription.ContactDetails
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureDetailsPage, DisclosureStatusPage}
import pages.enterprises.AssociatedEnterpriseStatusPage
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterSelectedAddressLookupPage, ReporterStatusPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.reporter.individual.{ReporterIndividualDateOfBirthPage, ReporterIndividualEmailAddressPage, ReporterIndividualEmailAddressQuestionPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage}
import pages.reporter.intermediary.IntermediaryWhyReportInUKPage
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer.RelevantTaxpayerStatusPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.XMLGenerationService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.Future

class SummaryControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

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

  private val mockHistoryConnector = mock[HistoryConnector]

  val fakeDataRetrieval = new FakeContactRetrievalAction(userAnswersForOrganisation, Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com"))))

  "Summary Controller" - {

    "return OK and the correct view for a GET" in {

      val firstDisclosureSubmissionDetails = SubmissionDetails("id", LocalDateTime.now(), "test.xml",
        Some("arrangementID"), Some("disclosureID"), "New", initialDisclosureMA = true, "messageRefID")

      val submissionHistory = SubmissionHistory(Seq(firstDisclosureSubmissionDetails))

      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID"),
        disclosureType = DisclosureType.Dac6add
      )

      val userAnswers =   userAnswersForOrganisation
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success.value
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
        .set(ReporterStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(RelevantTaxpayerStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(IntermediariesStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(DisclosureStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(AffectedStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(AssociatedEnterpriseStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(HallmarkStatusPage, 0, JourneyStatus.Completed)
        .success.value
        .set(ArrangementStatusPage, 0, JourneyStatus.Completed)
        .success.value


      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[HistoryConnector].toInstance(mockHistoryConnector)
        ).build()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosureSubmissionDetails))

      val getRequest = FakeRequest(GET, routes.SummaryController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())
      verify(mockHistoryConnector, times(1)).retrieveFirstDisclosureForArrangementID(any())(any())

      //ToDo test output from page
      templateCaptor.getValue mustEqual "summary.njk"

      application.stop()
    }
  }
}
