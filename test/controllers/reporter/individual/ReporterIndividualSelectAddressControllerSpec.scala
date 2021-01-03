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

package controllers.reporter.individual

import base.SpecBase
import connectors.AddressLookupConnector
import forms.SelectAddressFormProvider
import matchers.JsonMatchers
import models.{AddressLookup, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.reporter.individual.{ReporterIndividualPostcodePage, ReporterIndividualSelectAddressPage}
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class ReporterIndividualSelectAddressControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  val mockAddressLookupConnector: AddressLookupConnector = mock[AddressLookupConnector]

  def onwardRoute = Call("GET", "/foo")

  lazy val reporterIndividualSelectAddressRoute = routes.ReporterIndividualSelectAddressController.onPageLoad(NormalMode).url
  lazy val manualAddressURL = routes.ReporterIndividualAddressController.onPageLoad(NormalMode).url

  val formProvider = new SelectAddressFormProvider()
  val form = formProvider()

  val selectedAddress = "1 Address line 1, Town, ZZ1 1ZZ"
  val addresses: Seq[AddressLookup] = Seq(
    AddressLookup(Some("1 Address line 1"), None, None, None, "Town", None, "ZZ1 1ZZ"),
    AddressLookup(Some("2 Address line 1"), None, None, None, "Town", None, "ZZ1 1ZZ")
  )
  val addressRadios: Seq[Radios.Radio] = Seq(
    Radios.Radio(label = msg"1 Address line 1, Town, ZZ1 1ZZ", value = s"1 Address line 1, Town, ZZ1 1ZZ"),
    Radios.Radio(label = msg"2 Address line 1, Town, ZZ1 1ZZ", value = s"2 Address line 1, Town, ZZ1 1ZZ")
  )

  override def beforeEach: Unit = {
    reset(
      mockRenderer, mockAddressLookupConnector
    )

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))
    when(mockAddressLookupConnector.addressLookupByPostcode(any())(any(), any()))
      .thenReturn(Future.successful(addresses))
  }

  "ReporterIndividualSelectAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterIndividualPostcodePage, "ZZ1 1ZZ")
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
        ).build()

      val request = FakeRequest(GET, reporterIndividualSelectAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "manualAddressURL" -> manualAddressURL,
        "radios" -> Radios(field = form("value"), items = addressRadios)
      )

      templateCaptor.getValue mustEqual "reporter/individual/reporterIndividualSelectAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterIndividualSelectAddressPage, selectedAddress)
        .success.value
        .set(ReporterIndividualPostcodePage, "ZZ1 1ZZ")
        .success.value


      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
        ).build()

      val request = FakeRequest(GET, reporterIndividualSelectAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> selectedAddress))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "manualAddressURL" -> manualAddressURL,
        "radios" -> Radios(field = filledForm("value"), items = addressRadios)
      )

      templateCaptor.getValue mustEqual "reporter/individual/reporterIndividualSelectAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterIndividualPostcodePage, "ZZ1 1ZZ")
        .success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
          )
          .build()

      val request =
        FakeRequest(POST, reporterIndividualSelectAddressRoute)
          .withFormUrlEncodedBody(("value", selectedAddress))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements"

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterIndividualPostcodePage, "ZZ1 1ZZ")
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
        ).build()

      val request = FakeRequest(POST, reporterIndividualSelectAddressRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "manualAddressURL" -> manualAddressURL,
        "radios" -> Radios(field = boundForm("value"), items = addressRadios)
      )

      templateCaptor.getValue mustEqual "reporter/individual/reporterIndividualSelectAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, reporterIndividualSelectAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, reporterIndividualSelectAddressRoute)
          .withFormUrlEncodedBody(("value", selectedAddress))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
