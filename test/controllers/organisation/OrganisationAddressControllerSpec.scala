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

package controllers.organisation

import base.{ControllerMockFixtures, SpecBase}
import config.FrontendAppConfig
import forms.AddressFormProvider
import matchers.JsonMatchers
import models.{Address, CheckMode, Country, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.organisation.OrganisationAddressPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.Future

class OrganisationAddressControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val mockCountryFactory: CountryListFactory   = mock[CountryListFactory]

  val formProvider        = new AddressFormProvider()
  val form: Form[Address] = formProvider(Seq(Country("valid", "FR", "France")))
  val address: Address    = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"), Country("valid", "FR", "France"))

  lazy val organisationAddressRoute: String  = controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, NormalMode).url
  lazy val organisationAddressCheckModeRoute = controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, CheckMode).url

  override def beforeEach: Unit = {
    reset(mockCountryFactory)
    super.beforeEach
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(bind[CountryListFactory].toInstance(mockCountryFactory))

  "OrganisationAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid", "FR", "France"))))
      when(mockCountryFactory.uk).thenReturn(Country("valid", "GB", "United Kingdom"))
      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, organisationAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "address.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid", "FR", "France"))))
      when(mockCountryFactory.uk).thenReturn(Country("valid", "GB", "United Kingdom"))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(OrganisationAddressPage, 0, address)
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, organisationAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "addressLine1" -> "value 1",
          "addressLine2" -> "value 2",
          "addressLine3" -> "value 3",
          "city"         -> "value 4",
          "postCode"     -> "XX9 9XX",
          "country"      -> "FR"
        )
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "address.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      retrieveUserAnswersData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid", "FR", "France"))))
      when(mockCountryFactory.uk).thenReturn(Country("valid", "GB", "United Kingdom"))

      val request =
        FakeRequest(POST, organisationAddressRoute)
          .withFormUrlEncodedBody(("addressLine1", "value 1"),
                                  ("addressLine2", "value 2"),
                                  ("addressLine3", "value 3"),
                                  ("city", "value 4"),
                                  ("postcode", "XX9 9XX"),
                                  ("country", "FR")
          )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/email-address/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid", "FR", "France"))))
      when(mockCountryFactory.uk).thenReturn(Country("valid", "GB", "United Kingdom"))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(POST, organisationAddressRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "address.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      retrieveNoData()
      val request = FakeRequest(GET, organisationAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, organisationAddressRoute)
          .withFormUrlEncodedBody(("addressLine1", "value 1"), ("addressLine2", "value 2"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
