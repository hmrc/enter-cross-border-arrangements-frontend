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

package controllers.individual

import base.{ControllerMockFixtures, SpecBase}
import forms.individual.WhichCountryTaxForIndividualFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.individual.{IndividualLoopPage, WhichCountryTaxForIndividualPage}
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

class WhichCountryTaxForIndividualControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider                           = new WhichCountryTaxForIndividualFormProvider()
  val form: Form[Country]                    = formProvider(Seq(Country("valid", "GB", "United Kingdom")))
  val country: Country                       = Country("valid", "GB", "United Kingdom")
  val nonUKCountry: Country                  = Country("valid", "FR", "France")
  val mockCountryFactory: CountryListFactory = mock[CountryListFactory]
  val index: Int                             = 0

  lazy val whichCountryTaxForIndividualRoute: String = controllers.individual.routes.WhichCountryTaxForIndividualController.onPageLoad(0, NormalMode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(bind[CountryListFactory].toInstance(mockCountryFactory))

  "WhichCountryTaxForIndividual Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(country, nonUKCountry)))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, whichCountryTaxForIndividualRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "individual/whichCountryTaxForIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(Country("valid", "GB", "United Kingdom"))))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(WhichCountryTaxForIndividualPage, 0, country)
        .success
        .value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(None, Some(country), None, None, None, None)))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request        = FakeRequest(GET, whichCountryTaxForIndividualRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "country" -> "GB"
        )
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "individual/whichCountryTaxForIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when UK is submitted" in {

      retrieveUserAnswersData(emptyUserAnswers)

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(country, nonUKCountry)))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      val request =
        FakeRequest(POST, whichCountryTaxForIndividualRoute)
          .withFormUrlEncodedBody(("country", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/individual/uk-tin-known-0/0"
    }

    "must redirect to the next page when non UK is submitted" in {

      retrieveUserAnswersData(emptyUserAnswers)

      when(mockCountryFactory.getCountryList()).thenReturn(Some(Seq(country, nonUKCountry)))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, whichCountryTaxForIndividualRoute)
          .withFormUrlEncodedBody(("country", "FR"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/individual/resident-country-tin-0/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(POST, whichCountryTaxForIndividualRoute).withFormUrlEncodedBody(("country", ""))
      val boundForm      = form.bind(Map("country" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "individual/whichCountryTaxForIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, whichCountryTaxForIndividualRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, whichCountryTaxForIndividualRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
