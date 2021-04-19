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

package controllers.reporter.organisation

import base.SpecBase
import config.FrontendAppConfig
import connectors.AddressLookupConnector
import forms.PostcodeFormProvider
import matchers.JsonMatchers
import models.{AddressLookup, NormalMode, UnsubmittedDisclosure, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.reporter.organisation.ReporterOrganisationPostcodePage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.FormError
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ReporterOrganisationPostcodeControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PostcodeFormProvider()
  val form = formProvider()

  val mockSessionRepository = mock[SessionRepository]
  val mockFrontendAppConfig = mock[FrontendAppConfig]
  val mockAddressLookupConnector = mock[AddressLookupConnector]

  lazy val reporterOrganisationPostcodeRoute = routes.ReporterOrganisationPostcodeController.onPageLoad(0, NormalMode).url

  "ReporterOrganisationPostcode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, reporterOrganisationPostcodeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ReporterOrganisationPostcodePage, 0, "ZZ1 1ZZ").success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, reporterOrganisationPostcodeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("postcode" -> "ZZ1 1ZZ"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val addresses: Seq[AddressLookup] = Seq(
        AddressLookup(Some("1 Address line 1"), None, None, None, "Town", None, "ZZ1 1ZZ"),
        AddressLookup(Some("2 Address line 1"), None, None, None, "Town", None, "ZZ1 1ZZ")
      )

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockAddressLookupConnector.addressLookupByPostcode(any())(any(), any()))
        .thenReturn(Future.successful(addresses))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
          )
          .build()

      val request =
        FakeRequest(POST, reporterOrganisationPostcodeRoute)
          .withFormUrlEncodedBody(("postcode", "AA1 1AA"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/reporter/organisation/select-address/0"

      reset(mockAddressLookupConnector)
      application.stop()
    }

    "must return a Bad Request and error when postcode is not matched" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockAddressLookupConnector.addressLookupByPostcode(any())(any(), any()))
        .thenReturn(Future.successful(Seq()))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)).build()
      val request = FakeRequest(POST, reporterOrganisationPostcodeRoute).withFormUrlEncodedBody(("postcode", "AA1 1AA"))
      val boundForm = form.bind(Map("postcode" -> "AA1 1AA"))
        .withError(FormError("postcode", List("Address not found - enter a different postcode or enter the address manually")))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockAddressLookupConnector, times(1)).addressLookupByPostcode(any())(any(), any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      reset(mockAddressLookupConnector)
      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, reporterOrganisationPostcodeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "postcode.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, reporterOrganisationPostcodeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, reporterOrganisationPostcodeRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
