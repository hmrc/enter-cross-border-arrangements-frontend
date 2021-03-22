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

package controllers.reporter

import base.SpecBase
import models.ReporterOrganisationOrIndividual.{Individual, Organisation}
import models.YesNoDoNotKnowRadios.Yes
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.reporter.intermediary.IntermediaryRole.Promoter
import models.reporter.intermediary.IntermediaryWhyReportInUK.TaxResidentUK
import models.reporter.taxpayer.TaxpayerWhyReportArrangement.NoIntermediaries
import models.reporter.taxpayer.TaxpayerWhyReportInUK.UkTaxResident
import models.{AddressLookup, CountriesListEUCheckboxes, Country, LoopDetails, Name, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import java.time.LocalDate
import scala.concurrent.Future

class ReporterCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  val addressLookup = AddressLookup(
    Some("addressLine 1"),
    Some("addressLine 2"),
    Some("addressLine 3"),
    Some("addressLine 4"),
    "town",
    Some("county"),
    "postcode")

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: String => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.reporter.routes.ReporterCheckYourAnswersController.onPageLoad(0).url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    val reporterDetails = (json \ "reporterDetails").toString
    val residentCountryDetails = (json \ "residentCountryDetails").toString
    val roleDetails = (json \ "roleDetails").toString

    templateCaptor.getValue mustEqual "reporter/reporterCheckYourAnswers.njk"
    assertFunction(reporterDetails + residentCountryDetails + roleDetails)

    application.stop()

    reset(
      mockRenderer
    )
  }


  "ReporterCheckYourAnswers Controller" - {

    "must return rows for a reporter who is an ORGANISATION with UK TAX RESIDENCY & A UTR " +
      "with an INTERMEDIARY role of type PROMOTER & EXEMPT from a country" in {

      val unitedKingdom: Country = Country("valid", "GB", "United Kingdom")
      val utr: TaxReferenceNumbers = TaxReferenceNumbers("UTR12341234", None, None)
      val loopDetailsUK: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(Some(false), Some(unitedKingdom), Some(false), None, Some(true), Some(utr)))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ReporterOrganisationOrIndividualPage, 0, Organisation)
        .success.value
        .set(ReporterOrganisationNamePage, 0, "Name")
        .success.value
        .set(ReporterOrganisationIsAddressUkPage, 0, true)
        .success.value
        .set(ReporterSelectedAddressLookupPage, 0, addressLookup)
        .success.value
        .set(ReporterOrganisationEmailAddressQuestionPage, 0, true)
        .success.value
        .set(ReporterOrganisationEmailAddressPage, 0, "email@email.com")
        .success.value
        .set(ReporterTaxResidencyLoopPage, 0, loopDetailsUK)
        .success.value
        .set(RoleInArrangementPage, 0, Intermediary)
        .success.value
        .set(IntermediaryWhyReportInUKPage, 0, TaxResidentUK)
        .success.value
        .set(IntermediaryRolePage, 0, Promoter)
        .success.value
        .set(IntermediaryExemptionInEUPage, 0, Yes)
        .success.value
        .set(IntermediaryDoYouKnowExemptionsPage, 0, true)
        .success.value
        .set(IntermediaryWhichCountriesExemptPage, 0, CountriesListEUCheckboxes.enumerable.withName("FR").toSet)
        .success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Organisation"}""") mustBe true
        rows.contains("""{"key":{"text":"Reporter’s name","classes":"govuk-!-width-one-half"},"value":{"text":"Name"}""") mustBe true
        rows.contains("""{"key":{"text":"Address","classes":"govuk-!-width-one-half"},"value":{"html":"\n        addressLine 1<br>\n        addressLine 2<br>\n        addressLine 3<br>\n        addressLine 4<br>\n        town<br>\n        county<br>\n        postcode\n     "}""") mustBe true
        rows.contains("""{"key":{"text":"Do you want to provide an additional email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true

        rows.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        rows.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"United Kingdom"}}""") mustBe true
        rows.contains("""{"key":{"text":"UK tax number","classes":"govuk-!-width-one-half"},"value":{"text":"UTR12341234"}""") mustBe true

        rows.contains("""{"key":{"text":"Role in the arrangement","classes":"govuk-!-width-one-half"},"value":{"text":"Intermediary"}""") mustBe true
        rows.contains("""{"key":{"text":"Reason for reporting in the UK","classes":"govuk-!-width-one-half"},"value":{"text":"You are tax resident in the UK"}""") mustBe true
        rows.contains("""{"key":{"text":"Type of intermediary","classes":"govuk-!-width-one-half"},"value":{"text":"Promoter"}""") mustBe true
        rows.contains("""{"key":{"text":"Are you exempt from reporting anywhere?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know where?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Exempt Countries","classes":"govuk-!-width-one-half"},"value":{"html":"France"}""") mustBe true
      }
    }

    "must return rows for a reporter who is an INDIVIDUAL with a NON UK TAX RESIDENCY & multiple TIN's " +
      "with a TAXPAYER role & arrangement is MARKETABLE" in {

      val france: Country = Country("valid", "FR", "FRANCE")
      val tins: TaxReferenceNumbers = TaxReferenceNumbers("TIN123123", Some("TIN123123"), Some("TIN123123"))
      val loopDetailsNonUK: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(Some(false), Some(france), Some(true), Some(tins),Some(false), None))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
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

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Individual"}""") mustBe true
        rows.contains("""{"key":{"text":"Reporter’s name","classes":"govuk-!-width-one-half"},"value":{"text":"Firstname surname"}""") mustBe true
        rows.contains("""{"key":{"text":"Date of birth","classes":"govuk-!-width-one-half"},"value":{"text":"1 January 1990"}""") mustBe true
        rows.contains("""{"key":{"text":"Place of birth","classes":"govuk-!-width-one-half"},"value":{"text":"Place of Birth"}""") mustBe true
        rows.contains("""{"key":{"text":"Address","classes":"govuk-!-width-one-half"},"value":{"html":"\n        addressLine 1<br>\n        addressLine 2<br>\n        addressLine 3<br>\n        addressLine 4<br>\n        town<br>\n        county<br>\n        postcode\n     "}""") mustBe true
        rows.contains("""{"key":{"text":"Do you want to provide an additional email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true

        rows.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        rows.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"FRANCE"}}""") mustBe true
        rows.contains("""{"key":{"text":"Tax identification numbers for FRANCE","classes":"govuk-!-width-one-half"},"value":{"text":"TIN123123, TIN123123, TIN123123"}}""") mustBe true

        rows.contains("""{"key":{"text":"Role in the arrangement","classes":"govuk-!-width-one-half"},"value":{"text":"Taxpayer"}""") mustBe true
        rows.contains("""{"key":{"text":"Reason for reporting in the UK","classes":"govuk-!-width-one-half"},"value":{"text":"You are tax resident in the UK"}""") mustBe true
        rows.contains("""{"key":{"text":"Why are you reporting as a taxpayer?","classes":"govuk-!-width-one-half"},"value":{"text":"There are no intermediaries involved"}""") mustBe true

        rows.contains("""{"key":{"text":"Taxpayer implementing date","classes":"govuk-!-width-one-half"},"value":{"text":"1 January 2020"}""") mustBe true
      }
    }
  }
}
