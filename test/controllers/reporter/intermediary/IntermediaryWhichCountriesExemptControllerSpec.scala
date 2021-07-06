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

package controllers.reporter.intermediary

import base.{ControllerMockFixtures, SpecBase}
import forms.IntermediaryWhichCountriesExemptFormProvider
import matchers.JsonMatchers
import models.{CountryList, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.reporter.intermediary.IntermediaryWhichCountriesExemptPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class IntermediaryWhichCountriesExemptControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  lazy val intermediaryWhichCountriesExemptRoute = routes.IntermediaryWhichCountriesExemptController.onPageLoad(0, NormalMode).url

  val formProvider = new IntermediaryWhichCountriesExemptFormProvider()
  val form = formProvider()

  "IntermediaryWhichCountriesExempt Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))
      retrieveUserAnswersData(emptyUserAnswers)

      val request = FakeRequest(GET, intermediaryWhichCountriesExemptRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> CountryList.nonGBCheckboxes(form)
      )

      templateCaptor.getValue mustEqual "reporter/intermediary/intermediaryWhichCountriesExempt.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IntermediaryWhichCountriesExemptPage, 0, CountryList.values.tail.toSet).success.value
      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, intermediaryWhichCountriesExemptRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(CountryList.values.tail.toSet)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "checkboxes" -> CountryList.nonGBCheckboxes(filledForm)
      )

      templateCaptor.getValue mustEqual "reporter/intermediary/intermediaryWhichCountriesExempt.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      retrieveUserAnswersData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, intermediaryWhichCountriesExemptRoute)
          .withFormUrlEncodedBody(("value[0]", CountryList.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      retrieveUserAnswersData(emptyUserAnswers)

      val request =  FakeRequest(POST, intermediaryWhichCountriesExemptRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> boundForm,
        "mode"       -> NormalMode,
        "checkboxes" -> CountryList.nonGBCheckboxes(boundForm)
      )

      templateCaptor.getValue mustEqual "reporter/intermediary/intermediaryWhichCountriesExempt.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }
  }
}
