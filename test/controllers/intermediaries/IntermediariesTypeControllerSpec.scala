/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.intermediaries.IntermediariesTypeFormProvider
import matchers.JsonMatchers
import models.{CheckMode, NormalMode, SelectType, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.intermediaries.IntermediariesTypePage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.Form
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class IntermediariesTypeControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  private val formProvider           = new IntermediariesTypeFormProvider()
  private val form: Form[SelectType] = formProvider()

  private lazy val associatedEnterpriseTypeRoute: String = routes.IntermediariesTypeController.onPageLoad(0, NormalMode).url

  "AssociatedEnterpriseType Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(GET, associatedEnterpriseTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "radios" -> SelectType.radios(form)
      )

      templateCaptor.getValue mustEqual "intermediaries/intermediariesType.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(IntermediariesTypePage, 0, SelectType.values.head)
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, associatedEnterpriseTypeRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("selectType" -> SelectType.values.head.toString))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> SelectType.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "intermediaries/intermediariesType.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      retrieveUserAnswersData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, associatedEnterpriseTypeRoute)
          .withFormUrlEncodedBody(("selectType", SelectType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/name/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(POST, associatedEnterpriseTypeRoute).withFormUrlEncodedBody(("selectType", ""))
      val boundForm      = form.bind(Map("selectType" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> SelectType.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "intermediaries/intermediariesType.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the Check your answers page if user doesn't change their answer in CheckMode" in {
      val associatedEnterpriseTypeRoute: String = routes.IntermediariesTypeController.onPageLoad(0, CheckMode).url
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(IntermediariesTypePage, 0, SelectType.values.head)
        .success
        .value
      retrieveUserAnswersData(userAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, associatedEnterpriseTypeRoute)
          .withFormUrlEncodedBody(("selectType", SelectType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/intermediaries/check-answers/0"
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, associatedEnterpriseTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, associatedEnterpriseTypeRoute)
          .withFormUrlEncodedBody(("selectType", SelectType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
