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

package controllers.taxpayer

import base.SpecBase
import models.organisation.Organisation
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, Country, LoopDetails, Name, SelectType, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.individual._
import pages.organisation._
import pages.taxpayer.{TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository

import java.time.LocalDate
import scala.concurrent.Future

class TaxpayersCheckYourAnswersControllerSpec extends SpecBase {

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: String => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None).url)

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
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerSelectTypePage, 0, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, 0, "Name")
        .success.value
        .set(IsOrganisationAddressKnownPage, 0, false)
        .success.value
        .set(EmailAddressQuestionForOrganisationPage, 0, true)
        .success.value
        .set(EmailAddressForOrganisationPage, 0, "email@email.com")
        .success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Organisation"}""") mustBe true
        rows.contains("""{"key":{"text":"What is the name of the organisation?","classes":"govuk-!-width-one-half"},"value":{"text":"Name"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you want to provide an email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
      }
    }

    "must return rows for an taxpayer who is an individual" in {
      val dob = LocalDate.of(2020, 1, 1)

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerSelectTypePage, 0, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, 0, Name("First", "Last"))
        .success.value
        .set(IsIndividualDateOfBirthKnownPage, 0, true)
        .success.value
        .set(IndividualDateOfBirthPage, 0, dob)
        .success.value
        .set(IsIndividualPlaceOfBirthKnownPage, 0, false)
        .success.value
        .set(EmailAddressQuestionForIndividualPage, 0, true)
        .success.value
        .set(EmailAddressForIndividualPage, 0, "email@email.com")
        .success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Individual"}""") mustBe true
        rows.contains("""{"key":{"text":"Name","classes":"govuk-!-width-one-half"},"value":{"text":"First Last"}""") mustBe true
        rows.contains("""{"key":{"text":"Date of birth","classes":"govuk-!-width-one-half"},"value":{"text":"1 January 2020"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their place of birth?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you want to provide an email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
      }
    }
  }

    "must return an implementing date an organisation" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerSelectTypePage, 0, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, 0, "Name")
        .success.value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0,
          LocalDate.of(2002,1,1))
        .success.value

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None).url)
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
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerSelectTypePage, 0, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, 0, Name("First", "Last"))
        .success.value
        .set(IndividualDateOfBirthPage, 0, dob)
        .success.value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0,
          LocalDate.of(2002,1,1))
        .success.value

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None).url)
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

    lazy val taxpayersCheckYourAnswersRoute: String = controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None).url

    "must redirect to the taxpayers update page when valid data is submitted for an organisation taxpayer" in {

      val onwardRoute: Call = Call("GET", "/disclose-cross-border-arrangements/manual/taxpayers/update")

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerSelectTypePage, 0, SelectType.Organisation)
        .success
        .value
        .set(OrganisationNamePage, 0, "CheckYourAnswers Ltd")
        .success
        .value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now())
        .success
        .value
        .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None)))
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

      val onwardRoute: Call = Call("GET", "/disclose-cross-border-arrangements/manual/taxpayers/update")

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerSelectTypePage, 0, SelectType.Individual)
        .success
        .value
        .set(IndividualNamePage, 0, Name("Check", "YourAnswers"))
        .success
        .value
        .set(IndividualDateOfBirthPage, 0, LocalDate.now())
        .success
        .value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now())
        .success
        .value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None)))
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

