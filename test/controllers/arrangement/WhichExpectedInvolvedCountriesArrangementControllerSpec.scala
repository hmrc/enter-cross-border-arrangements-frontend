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

package controllers.arrangement

import base.SpecBase
import controllers.routes
import forms.arrangement.WhichExpectedInvolvedCountriesArrangementFormProvider
import matchers.JsonMatchers
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.arrangement.WhichExpectedInvolvedCountriesArrangementPage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport
import models.arrangement.WhichExpectedInvolvedCountriesArrangement
import scala.concurrent.Future

class WhichExpectedInvolvedCountriesArrangementControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichExpectedInvolvedCountriesArrangementRoute = controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(NormalMode).url

  val formProvider = new WhichExpectedInvolvedCountriesArrangementFormProvider()
  val form = formProvider()

  "WhichExpectedInvolvedCountriesArrangement Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, whichExpectedInvolvedCountriesArrangementRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> WhichExpectedInvolvedCountriesArrangement.checkboxes(form)
      )

      templateCaptor.getValue mustEqual "arrangement/whichExpectedInvolvedCountriesArrangement.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId).set(WhichExpectedInvolvedCountriesArrangementPage, WhichExpectedInvolvedCountriesArrangement.values.toSet).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, whichExpectedInvolvedCountriesArrangementRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(WhichExpectedInvolvedCountriesArrangement.values.toSet)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "checkboxes" -> WhichExpectedInvolvedCountriesArrangement.checkboxes(filledForm)
      )

      templateCaptor.getValue mustEqual "arrangement/whichExpectedInvolvedCountriesArrangement.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, whichExpectedInvolvedCountriesArrangementRoute)
          .withFormUrlEncodedBody(("value[0]", WhichExpectedInvolvedCountriesArrangement.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request =  FakeRequest(POST, whichExpectedInvolvedCountriesArrangementRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> boundForm,
        "mode"       -> NormalMode,
        "checkboxes" -> WhichExpectedInvolvedCountriesArrangement.checkboxes(boundForm)
      )

      templateCaptor.getValue mustEqual "arrangement/whichExpectedInvolvedCountriesArrangement.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(GET, whichExpectedInvolvedCountriesArrangementRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(POST, whichExpectedInvolvedCountriesArrangementRoute).withFormUrlEncodedBody(("value[0]", WhichExpectedInvolvedCountriesArrangement.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
