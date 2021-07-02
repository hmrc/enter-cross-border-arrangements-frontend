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

package controllers.intermediaries

import base.{ControllerMockFixtures, SpecBase}
import forms.intermediaries.ExemptCountriesFormProvider
import matchers.JsonMatchers
import models.{CountryList, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.intermediaries.ExemptCountriesPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class CountryListControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  lazy val exemptCountriesRoute = routes.ExemptCountriesController.onPageLoad(0, NormalMode).url

  val formProvider = new ExemptCountriesFormProvider()
  val form = formProvider()

  "ExemptCountries Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))
      retrieveUserAnswersData(emptyUserAnswers)

      val request = FakeRequest(GET, exemptCountriesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> CountryList.checkboxes(form)
      )

      templateCaptor.getValue mustEqual "intermediaries/exemptCountries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ExemptCountriesPage, 0, CountryList.values.toSet).success.value

      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, exemptCountriesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(CountryList.values.toSet)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "checkboxes" -> CountryList.checkboxes(filledForm)
      )

      templateCaptor.getValue mustEqual "intermediaries/exemptCountries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      retrieveUserAnswersData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, exemptCountriesRoute)
          .withFormUrlEncodedBody(("value[0]", CountryList.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/intermediaries/check-answers/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request =  FakeRequest(POST, exemptCountriesRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> boundForm,
        "mode"       -> NormalMode,
        "checkboxes" -> CountryList.checkboxes(boundForm)
      )

      templateCaptor.getValue mustEqual "intermediaries/exemptCountries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()
      val request = FakeRequest(GET, exemptCountriesRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()
      val request = FakeRequest(POST, exemptCountriesRoute).withFormUrlEncodedBody(("value[0]", CountryList.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
