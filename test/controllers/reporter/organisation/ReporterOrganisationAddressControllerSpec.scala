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
import forms.AddressFormProvider
import matchers.JsonMatchers
import models.{Address, CheckMode, Country, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.reporter.organisation.ReporterOrganisationAddressPage
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.Future

class ReporterOrganisationAddressControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val mockCountryFactory: CountryListFactory = mock[CountryListFactory]

  val formProvider = new AddressFormProvider()
  val form: Form[Address] = formProvider(Seq(Country("valid","FR","France")))
  val address: Address = Address(Some("value 1"),Some("value 2"),Some("value 3"),"value 4",Some("XX9 9XX"),
    Country("valid","FR","France"))

  lazy val organisationAddressRoute: String = controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(NormalMode).url
  lazy val organisationAddressCheckModeRoute = controllers.reporter.organisation.routes.ReporterOrganisationAddressController.onPageLoad(CheckMode).url

  "ReporterOrganisationAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid","FR","France"))))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(
        bind[CountryListFactory].toInstance(mockCountryFactory)).build()

      val request = FakeRequest(GET, organisationAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "address.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid","FR","France"))))

      val userAnswers = UserAnswers(userAnswersId).set(ReporterOrganisationAddressPage, address).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[CountryListFactory].toInstance(mockCountryFactory)).build()
      val request = FakeRequest(GET, organisationAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "addressLine1" -> "value 1",
          "addressLine2" -> "value 2",
          "addressLine3" -> "value 3",
          "city" -> "value 4",
          "postCode" -> "XX9 9XX",
          "country" -> "FR"
        )
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "address.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, organisationAddressRoute)
          .withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"),("addressLine3", "value 3"), ("city", "value 4"),
            ("postcode", "XX9 9XX"),("country", "FR"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

//      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/reporter/organisation/email-address" // TODO - uncomment when Reporter Organisation email page built

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, organisationAddressRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode
      )

      templateCaptor.getValue mustEqual "address.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, organisationAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, organisationAddressRoute)
          .withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
