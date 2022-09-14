/*
 * Copyright 2022 HM Revenue & Customs
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

import base.{ControllerMockFixtures, SpecBase}
import connectors.HistoryConnector
import controllers.actions.FakeContactRetrievalAction
import helpers.data.ValidUserAnswersForSubmission.{userAnswersForOrganisation, validIndividual}
import matchers.JsonMatchers
import models.ReporterOrganisationOrIndividual.Individual
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.hallmarks.JourneyStatus
import models.reporter.RoleInArrangement.Taxpayer
import models.reporter.intermediary.IntermediaryWhyReportInUK.TaxResidentUK
import models.reporter.taxpayer.TaxpayerWhyReportArrangement.NoIntermediaries
import models.reporter.taxpayer.TaxpayerWhyReportInUK.UkTaxResident
import models.reporter.{ReporterDetails, ReporterLiability}
import models.subscription.ContactDetails
import models.{AddressLookup, Country, LoopDetails, Name, Submission, SubmissionDetails, TaxReferenceNumbers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureDetailsPage, DisclosureStatusPage}
import pages.enterprises.AssociatedEnterpriseStatusPage
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter._
import pages.reporter.individual._
import pages.reporter.intermediary.IntermediaryWhyReportInUKPage
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer.RelevantTaxpayerStatusPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.Future

class SummaryControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val addressLookup =
    AddressLookup(Some("addressLine 1"), Some("addressLine 2"), Some("addressLine 3"), Some("addressLine 4"), "town", Some("county"), "postcode")

  val france: Country                           = Country("valid", "FR", "FRANCE")
  val tins: TaxReferenceNumbers                 = TaxReferenceNumbers("TIN123123", Some("TIN123123"), Some("TIN123123"))
  val loopDetailsNonUK: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(Some(false), Some(france), Some(true), Some(tins), Some(false), None))

  private val mockHistoryConnector = mock[HistoryConnector]

  val fakeDataRetrieval = new FakeContactRetrievalAction(
    userAnswersForOrganisation,
    Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com")))
  )

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(bind[HistoryConnector].toInstance(mockHistoryConnector))

  "Summary Controller" - {

    "return OK and the correct view for a GET" in {

      val firstDisclosureSubmissionDetails =
        SubmissionDetails("id", LocalDateTime.now(), "test.xml", Some("arrangementID"), Some("disclosureID"), "New", initialDisclosureMA = true, "messageRefID")

      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID"),
        disclosureType = DisclosureType.Dac6add
      )

      val userAnswers = userAnswersForOrganisation
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success
        .value
        .set(ReporterOrganisationOrIndividualPage, 0, Individual)
        .success
        .value
        .set(ReporterIndividualNamePage, 0, Name("firstname", "surname"))
        .success
        .value
        .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.of(1990, 1, 1))
        .success
        .value
        .set(ReporterIndividualPlaceOfBirthPage, 0, "Place of Birth")
        .success
        .value
        .set(ReporterSelectedAddressLookupPage, 0, addressLookup)
        .success
        .value
        .set(ReporterIndividualEmailAddressQuestionPage, 0, true)
        .success
        .value
        .set(ReporterIndividualEmailAddressPage, 0, "email@email.com")
        .success
        .value
        .set(ReporterTaxResidencyLoopPage, 0, loopDetailsNonUK)
        .success
        .value
        .set(RoleInArrangementPage, 0, Taxpayer)
        .success
        .value
        .set(IntermediaryWhyReportInUKPage, 0, TaxResidentUK)
        .success
        .value
        .set(TaxpayerWhyReportInUKPage, 0, UkTaxResident)
        .success
        .value
        .set(TaxpayerWhyReportArrangementPage, 0, NoIntermediaries)
        .success
        .value
        .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.of(2020, 1, 1))
        .success
        .value
        .set(ReporterStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(RelevantTaxpayerStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(IntermediariesStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(DisclosureStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(AffectedStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(AssociatedEnterpriseStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(HallmarkStatusPage, 0, JourneyStatus.Completed)
        .success
        .value
        .set(ArrangementStatusPage, 0, JourneyStatus.Completed)
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val getRequest     = FakeRequest(GET, routes.SummaryController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

      templateCaptor.getValue mustEqual "summary.njk"
    }

    "do not display associated enterprises under certain conditions" in {

      val disclosureDetails = DisclosureDetails(disclosureName = "name")
      val reporterLiability = ReporterLiability("intermediary")
      val reporterDetails   = ReporterDetails(individual = Some(validIndividual), liability = Some(reporterLiability))

      val submission = Submission(enrolmentID = "enrolmentID", disclosureDetails = disclosureDetails, reporterDetails = Some(reporterDetails))

      // conditions:
      submission.disclosureDetails.firstInitialDisclosureMA mustBe None
      submission.reporterDetails.exists(_.isIntermediary) mustBe true
      submission.taxpayers.isEmpty mustBe true

      // therefore
      submission.displayAssociatedEnterprises mustBe true
    }

    "display associated enterprises otherwise break condition 1" in {

      val disclosureDetails = DisclosureDetails(disclosureName = "name", firstInitialDisclosureMA = Some(false))
      val reporterLiability = ReporterLiability("intermediary")
      val reporterDetails   = ReporterDetails(individual = Some(validIndividual), liability = Some(reporterLiability))

      val submission = Submission(enrolmentID = "enrolmentID", disclosureDetails = disclosureDetails, reporterDetails = Some(reporterDetails))

      // break condition 1:
      submission.disclosureDetails.firstInitialDisclosureMA mustBe Some(false)
      submission.reporterDetails.exists(_.isIntermediary) mustBe true
      submission.taxpayers.isEmpty mustBe true

      submission.displayAssociatedEnterprises mustBe true
    }

    "display associated enterprises otherwise break condition 2" in {

      val disclosureDetails = DisclosureDetails(disclosureName = "name", firstInitialDisclosureMA = Some(true))
      val reporterLiability = ReporterLiability("taxpayer")
      val reporterDetails   = ReporterDetails(individual = Some(validIndividual), liability = Some(reporterLiability))

      val submission = Submission(enrolmentID = "enrolmentID", disclosureDetails = disclosureDetails, reporterDetails = Some(reporterDetails))

      // break condition 2:
      submission.disclosureDetails.firstInitialDisclosureMA mustBe Some(true)
      submission.reporterDetails.exists(_.isIntermediary) mustBe false
      submission.taxpayers.isEmpty mustBe true

      submission.displayAssociatedEnterprises mustBe true
    }

    "display associated enterprises otherwise break condition 3" in {

      val disclosureDetails = DisclosureDetails(disclosureName = "name", firstInitialDisclosureMA = Some(true))
      val reporterLiability = ReporterLiability("intermediary")
      val reporterDetails   = ReporterDetails(individual = Some(validIndividual), liability = Some(reporterLiability))

      val taxpayers = IndexedSeq(models.taxpayer.Taxpayer("ID"))
      val submission =
        Submission(enrolmentID = "enrolmentID", disclosureDetails = disclosureDetails, reporterDetails = Some(reporterDetails), taxpayers = taxpayers)

      // break condition 3:
      submission.disclosureDetails.firstInitialDisclosureMA mustBe Some(true)
      submission.reporterDetails.exists(_.isIntermediary) mustBe true
      submission.taxpayers.isEmpty mustBe false

      submission.displayAssociatedEnterprises mustBe true
    }
  }
}
