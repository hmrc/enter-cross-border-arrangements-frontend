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
import forms.intermediaries.YouHaveNotAddedAnyIntermediariesFormProvider
import helpers.data.ValidUserAnswersForSubmission.validOrganisation
import matchers.JsonMatchers
import models.intermediaries.{Intermediary, WhatTypeofIntermediary, YouHaveNotAddedAnyIntermediaries}
import models.{IsExemptionKnown, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.intermediaries.{IntermediaryLoopPage, YouHaveNotAddedAnyIntermediariesPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class YouHaveNotAddedAnyIntermediariesControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  lazy val youHaveNotAddedAnyIntermediariesRoute = routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(0).url

  val formProvider = new YouHaveNotAddedAnyIntermediariesFormProvider()
  val form         = formProvider()

  "YouHaveNotAddedAnyIntermediaries Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))
      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, youHaveNotAddedAnyIntermediariesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"             -> form,
        "mode"             -> NormalMode,
        "intermediaryList" -> Json.arr(),
        "radios"           -> YouHaveNotAddedAnyIntermediaries.radios(form)
      )

      templateCaptor.getValue mustEqual "intermediaries/youHaveNotAddedAnyIntermediaries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return OK and the correct view with the list of all intermediaries for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val intermediariesLoop =
        IndexedSeq(Intermediary("id", None, Some(validOrganisation), WhatTypeofIntermediary.Promoter, IsExemptionKnown.No, None, None))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(IntermediaryLoopPage, 0, intermediariesLoop)
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, youHaveNotAddedAnyIntermediariesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])
      val controller     = app.injector.instanceOf[YouHaveNotAddedAnyIntermediariesController]

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedList = Json.toJson(controller.toItemList(userAnswers, 0))

      val expectedJson = Json.obj(
        "form"             -> form,
        "mode"             -> NormalMode,
        "intermediaryList" -> expectedList,
        "radios"           -> YouHaveNotAddedAnyIntermediaries.radios(form)
      )

      templateCaptor.getValue mustEqual "intermediaries/youHaveNotAddedAnyIntermediaries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(YouHaveNotAddedAnyIntermediariesPage, 0, YouHaveNotAddedAnyIntermediaries.values.head)
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, youHaveNotAddedAnyIntermediariesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(YouHaveNotAddedAnyIntermediaries.values.head)

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> YouHaveNotAddedAnyIntermediaries.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "intermediaries/youHaveNotAddedAnyIntermediaries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      retrieveUserAnswersData(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, youHaveNotAddedAnyIntermediariesRoute)
          .withFormUrlEncodedBody(("value", YouHaveNotAddedAnyIntermediaries.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/intermediaries/type/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(POST, youHaveNotAddedAnyIntermediariesRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> YouHaveNotAddedAnyIntermediaries.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "intermediaries/youHaveNotAddedAnyIntermediaries.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }
  }
}
