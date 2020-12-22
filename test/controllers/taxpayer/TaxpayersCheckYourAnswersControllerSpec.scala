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

package controllers.taxpayer

import java.time.LocalDate

import base.SpecBase
import models.organisation.Organisation
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, Country, LoopDetails, Name, SelectType, TaxReferenceNumbers, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.individual._
import pages.organisation._
import pages.taxpayer.{TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository

import scala.concurrent.Future

class TaxpayersCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: String => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad().url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    val taxpayersSummaryRows = (json \ "taxpayersSummary").toString

    templateCaptor.getValue mustEqual "taxpayer/check-your-answers-taxpayers.njk"
    assertFunction(taxpayersSummaryRows)

    application.stop()

    reset(
      mockRenderer
    )
  }

  val address: Address = Address(Some(""), Some(""), Some(""), "Newcastle", Some("NE1"), Country("", "GB", "United Kingdom"))
  val email = "email@email.com"
  val taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))
  val taxpayers = IndexedSeq(Taxpayer("123", None, Some(Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)), None))

  "TaxpayersCheckYourAnswers Controller - onPageload" - {

    "must return rows for a taxpayer who is an organisation" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(TaxpayerSelectTypePage, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, "Name")
        .success.value
        .set(IsOrganisationAddressKnownPage, false)
        .success.value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success.value
        .set(EmailAddressForOrganisationPage, "email@email.com")
        .success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Organisation"}""") mustBe true
        rows.contains("""{"key":{"text":"What is the name of the organisation?","classes":"govuk-!-width-one-half"},"value":{"text":"Name"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
      }
    }

    "must return rows for an taxpayer who is an individual" in {
      val dob = LocalDate.of(2020, 1, 1)

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(TaxpayerSelectTypePage, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, Name("First", "Last"))
        .success.value
        .set(IndividualDateOfBirthPage, dob)
        .success.value
        .set(IsIndividualPlaceOfBirthKnownPage, false)
        .success.value
        .set(EmailAddressQuestionForIndividualPage, true)
        .success.value
        .set(EmailAddressForIndividualPage, "email@email.com")
        .success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Individual"}""") mustBe true
        rows.contains("""{"key":{"text":"Name","classes":"govuk-!-width-one-half"},"value":{"text":"First Last"}""") mustBe true
        rows.contains("""{"key":{"text":"Date of birth","classes":"govuk-!-width-one-half"},"value":{"text":"1 January 2020"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know where they were born?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
      }
    }
  }

    "must return an implementing date an organisation" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(TaxpayerSelectTypePage, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, "Name")
        .success.value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage,
          LocalDate.of(2002,1,1))
        .success.value

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad().url)
      val result = route(application, request).value
      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val implementingDateRow  = (json \ "implementingDateSummary").toString

      templateCaptor.getValue mustEqual "taxpayer/check-your-answers-taxpayers.njk"
      implementingDateRow.contains("Implementing date") mustBe true

      application.stop()
    }

    "must return an implementing date individual" in {
      val dob = LocalDate.of(2020, 1, 1)

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(TaxpayerSelectTypePage, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, Name("First", "Last"))
        .success.value
        .set(IndividualDateOfBirthPage, dob)
        .success.value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage,
          LocalDate.of(2002,1,1))
        .success.value

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad().url)
      val result = route(application, request).value
      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val json = jsonCaptor.getValue
      val implementingDateRow  = (json \ "implementingDateSummary").toString

      templateCaptor.getValue mustEqual "taxpayer/check-your-answers-taxpayers.njk"
      implementingDateRow.contains("Implementing date") mustBe true

      application.stop()
    }

  "TaxpayersCheckYourAnswersController - onSubmit" - {

    lazy val taxpayersCheckYourAnswersRoute: String = controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad().url

    "must redirect to the taxpayers update page when valid data is submitted for an organisation taxpayer" in {

      val onwardRoute: Call = Call("GET", "/enter-cross-border-arrangements/taxpayers/update")

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(TaxpayerSelectTypePage, SelectType.Organisation)
        .success
        .value
        .set(OrganisationNamePage, "CheckYourAnswers Ltd")
        .success
        .value
        .set(OrganisationLoopPage, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None)))
        .success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, taxpayersCheckYourAnswersRoute)
          .withFormUrlEncodedBody(("", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must redirect to the taxpayers update page when valid data is submitted for an individual taxpayer" in {

      val onwardRoute: Call = Call("GET", "/enter-cross-border-arrangements/taxpayers/update")

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(TaxpayerSelectTypePage, SelectType.Individual)
        .success
        .value
        .set(IndividualNamePage, Name("Check", "YourAnswers"))
        .success
        .value
        .set(IndividualDateOfBirthPage, LocalDate.now())
        .success
        .value
        .set(IndividualLoopPage, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None)))
        .success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, taxpayersCheckYourAnswersRoute)
          .withFormUrlEncodedBody(("", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }
  }

}

