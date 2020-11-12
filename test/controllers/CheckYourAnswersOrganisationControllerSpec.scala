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

package controllers

import base.SpecBase
import models.{Address, Country, OrganisationLoopDetails, TaxReferenceNumbers, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages._
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class CheckYourAnswersOrganisationControllerSpec extends SpecBase with BeforeAndAfterEach {

  "Check your answers Organisation controller" - {

    val organisationAddress =
      Address(Some("addressLine1"),
      Some("addressLine2"),
      Some("addressLine3"),
      "city",
      Some("postcode"),
      Country("state", "code", "description"))

    val referencesUK = TaxReferenceNumbers("UTR12345678", Some("UTR12345678"), Some("UTR12345678"))
    val referencesNonUK = TaxReferenceNumbers("TIN000000", Some("TIN000000"), Some("TIN000000"))

    val selectedUK: Country = Country("valid", "GB", "United Kingdom")
    val selectedNonUK: Country = Country("valid", "FR", "France")

    val organisationLoop: IndexedSeq[OrganisationLoopDetails] = IndexedSeq(OrganisationLoopDetails(
      Some(true), Some(selectedUK), Some(true), Some(referencesUK)
    ), OrganisationLoopDetails(
      Some(true), Some(selectedNonUK), Some(true), Some(referencesNonUK)
    ))


    "must return OK and the correct view for a GET when organisation name is provided" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "CheckYourAnswers Ltd")
        .success
        .value
        .set(IsOrganisationAddressKnownPage, false)
        .success
        .value
        .set(EmailAddressQuestionForOrganisationPage, false)
        .success
        .value
        .set(OrganisationLoopPage, organisationLoop)
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, selectedUK)
        .success
        .value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.CheckYourAnswersOrganisationController.onPageLoad().url)
      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val organisationDetails = (json \ "organisationSummary").toString

      templateCaptor.getValue mustEqual "check-your-answers-organisation.njk"
      organisationDetails.contains("What is the name of the organisation?") mustBe true
      organisationDetails.contains("Do you know their address?") mustBe true
      organisationDetails.contains("Do you know their email address?") mustBe true

      application.stop()
    }

    "must return OK and the correct view for a GET when all organisation details are provided" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "CheckYourAnswers Ltd")
        .success
        .value
        .set(IsOrganisationAddressKnownPage, true)
        .success
        .value
        .set(OrganisationAddressPage, organisationAddress)
        .success
        .value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success
        .value
        .set(EmailAddressForOrganisationPage, "test@test.com")
        .success
        .value
        .set(OrganisationLoopPage, organisationLoop)
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, selectedUK)
        .success
        .value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.CheckYourAnswersOrganisationController.onPageLoad().url)
      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val organisationDetails = (json \ "organisationSummary").toString

      templateCaptor.getValue mustEqual "check-your-answers-organisation.njk"
      organisationDetails.contains("What is the name of the organisation?") mustBe true
      organisationDetails.contains("Do you know their address?") mustBe true
      organisationDetails.contains("What is the organisation’s main address?") mustBe true
      organisationDetails.contains("Do you know their email address?") mustBe true
      organisationDetails.contains("Email address") mustBe true

      application.stop()
    }

    "must return OK and the correct view for a GET when all organisation details are provided " +
      "and a country for tax residency for tax purposes is selected but no tax references provided" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "CheckYourAnswers Ltd")
        .success
        .value
        .set(IsOrganisationAddressKnownPage, true)
        .success
        .value
        .set(OrganisationAddressPage, organisationAddress)
        .success
        .value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success
        .value
        .set(EmailAddressForOrganisationPage, "test@test.com")
        .success
        .value
        .set(OrganisationLoopPage, organisationLoop)
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, selectedUK)
        .success
        .value
        .set(DoYouKnowAnyTINForUKOrganisationPage, false)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.CheckYourAnswersOrganisationController.onPageLoad().url)
      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val organisationDetails = (json \ "organisationSummary").toString
      val residentCountryDetails = (json \ "countrySummary").toString

      templateCaptor.getValue mustEqual "check-your-answers-organisation.njk"
      organisationDetails.contains("What is the name of the organisation?") mustBe true
      organisationDetails.contains("Do you know their address?") mustBe true
      organisationDetails.contains("What is the organisation’s main address?") mustBe true
      organisationDetails.contains("Do you know their email address?") mustBe true
      organisationDetails.contains("Email address") mustBe true
      residentCountryDetails.contains("Tax resident countries") mustBe true
      residentCountryDetails.contains("Country 1") mustBe true

      application.stop()
    }

    "must return OK and the correct view for a GET when all organisation details are provided " +
      "and United Kingdom for tax residency for tax purposes is selected with UTR's provided" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "CheckYourAnswers Ltd")
        .success
        .value
        .set(IsOrganisationAddressKnownPage, true)
        .success
        .value
        .set(OrganisationAddressPage, organisationAddress)
        .success
        .value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success
        .value
        .set(EmailAddressForOrganisationPage, "test@test.com")
        .success
        .value
        .set(OrganisationLoopPage, organisationLoop)
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, selectedUK)
        .success
        .value
        .set(DoYouKnowAnyTINForUKOrganisationPage, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForUKOrganisationPage, referencesUK)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.CheckYourAnswersOrganisationController.onPageLoad().url)
      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val organisationDetails = (json \ "organisationSummary").toString
      val residentCountryDetails = (json \ "countrySummary").toString

      templateCaptor.getValue mustEqual "check-your-answers-organisation.njk"
      organisationDetails.contains("What is the name of the organisation?") mustBe true
      organisationDetails.contains("Do you know their address?") mustBe true
      organisationDetails.contains("What is the organisation’s main address?") mustBe true
      organisationDetails.contains("Do you know their email address?") mustBe true
      organisationDetails.contains("Email address") mustBe true
      residentCountryDetails.contains("Tax resident countries") mustBe true
      residentCountryDetails.contains("Country 1") mustBe true
      residentCountryDetails.contains("Unique Taxpayer Reference (UTR)") mustBe true

      application.stop()
    }

    "must return OK and the correct view for a GET when all organisation details are provided " +
      "and United Kingdom for tax residency for tax purposes is selected with UTR's provided" +
      "and is resident for tax purposes in another country with TIN's provided" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "CheckYourAnswers Ltd")
        .success
        .value
        .set(IsOrganisationAddressKnownPage, true)
        .success
        .value
        .set(OrganisationAddressPage, organisationAddress)
        .success
        .value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success
        .value
        .set(EmailAddressForOrganisationPage, "test@test.com")
        .success
        .value
        .set(OrganisationLoopPage, organisationLoop)
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, selectedUK)
        .success
        .value
        .set(DoYouKnowAnyTINForUKOrganisationPage, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForUKOrganisationPage, referencesUK)
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, selectedNonUK)
        .success
        .value
        .set(DoYouKnowTINForNonUKOrganisationPage, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForNonUKOrganisationPage, referencesUK)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.CheckYourAnswersOrganisationController.onPageLoad().url)
      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val organisationDetails = (json \ "organisationSummary").toString
      val residentCountryDetails = (json \ "countrySummary").toString

      templateCaptor.getValue mustEqual "check-your-answers-organisation.njk"
      organisationDetails.contains("What is the name of the organisation?") mustBe true
      organisationDetails.contains("Do you know their address?") mustBe true
      organisationDetails.contains("What is the organisation’s main address?") mustBe true
      organisationDetails.contains("Do you know their email address?") mustBe true
      organisationDetails.contains("Email address") mustBe true
      residentCountryDetails.contains("Tax resident countries") mustBe true
      residentCountryDetails.contains("Country 1") mustBe true
      residentCountryDetails.contains("Unique Taxpayer Reference (UTR)") mustBe true
      residentCountryDetails.contains("Country 2") mustBe true
      residentCountryDetails.contains(s"Tax identification number for ${selectedNonUK.description}") mustBe true

      application.stop()
    }
  }
}
