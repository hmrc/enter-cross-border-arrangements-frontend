/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.organisation

import base.{ControllerMockFixtures, SpecBase}
import config.FrontendAppConfig
import connectors.AddressLookupConnector
import forms.PostcodeFormProvider
import matchers.JsonMatchers
import models.{AddressLookup, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.organisation.PostcodePage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.FormError
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class OrganisationPostcodeControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider = new PostcodeFormProvider()
  val form         = formProvider()

  val mockFrontendAppConfig: FrontendAppConfig           = mock[FrontendAppConfig]
  val mockAddressLookupConnector: AddressLookupConnector = mock[AddressLookupConnector]

  lazy val postcodeRoute = controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(0, NormalMode).url

  override def beforeEach: Unit = {
    reset(mockAddressLookupConnector)
    super.beforeEach
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(
      bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
    )

  "Postcode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(GET, postcodeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(PostcodePage, 0, "ZZ1 1ZZ")
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, postcodeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("postcode" -> "ZZ1 1ZZ"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      val addresses: Seq[AddressLookup] = Seq(
        AddressLookup(Some("1 Address line 1"), None, None, None, "Town", None, "ZZ1 1ZZ"),
        AddressLookup(Some("2 Address line 1"), None, None, None, "Town", None, "ZZ1 1ZZ")
      )

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockAddressLookupConnector.addressLookupByPostcode(any())(any(), any()))
        .thenReturn(Future.successful(addresses))

      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, postcodeRoute)
          .withFormUrlEncodedBody(("postcode", "AA1 1AA"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/select-address/0"
    }

    "must return a Bad Request and error when postcode is not matched" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockAddressLookupConnector.addressLookupByPostcode(any())(any(), any()))
        .thenReturn(Future.successful(Seq()))
      retrieveUserAnswersData(emptyUserAnswers)

      val request = FakeRequest(POST, postcodeRoute).withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val boundForm = form
        .bind(Map("postcode" -> "AA1 1AA"))
        .withError(FormError("postcode", List("Address not found - enter a different postcode or enter the address manually")))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockAddressLookupConnector, times(1)).addressLookupByPostcode(any())(any(), any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      retrieveUserAnswersData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      val request        = FakeRequest(POST, postcodeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, postcodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, postcodeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
