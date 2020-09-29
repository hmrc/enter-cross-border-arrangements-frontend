/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import base.SpecBase
import models.{HallmarkA, HallmarkB, HallmarkD1, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.{HallmarkAPage, HallmarkBPage, HallmarkD1OtherPage, HallmarkD1Page, MainBenefitTestPage}
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET if there's only one hallmark selected" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(HallmarkAPage, HallmarkA.enumerable.withName("A1").toSet)
        .success
        .value
        .set(MainBenefitTestPage, true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "check-your-answers.njk"

      application.stop()
    }

    "must return OK and the correct view for a GET if there's more than one hallmark selected" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(HallmarkAPage, HallmarkA.enumerable.withName("A1").toSet)
        .success
        .value
        .set(HallmarkBPage, HallmarkB.enumerable.withName("B2").toSet)
        .success
        .value
        .set(MainBenefitTestPage, true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val json = jsonCaptor.getValue
      val list = (json \ "list").toString

      templateCaptor.getValue mustEqual "check-your-answers.njk"
      list.contains("A1, B2") mustBe true

      application.stop()
    }

    "must include 'Parts of hallmark D1 that apply to this arrangement' if D1 Other selected" in {
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(HallmarkD1Page, HallmarkD1.enumerable.withName("D1other").toSet)
        .success
        .value
        .set(HallmarkD1OtherPage, "Other page text")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val json = jsonCaptor.getValue

      val list = (json \ "list").toString

      templateCaptor.getValue mustEqual "check-your-answers.njk"
      list.contains("D1other") mustBe true
      list.contains("Other page text") mustBe true

      application.stop()

    }

    "must not include 'Parts of hallmark D1 that apply to this arrangement' if D1 Other is not selected" in {
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(HallmarkD1Page, HallmarkD1.enumerable.withName("D1a").toSet)
        .success
        .value
        .set(HallmarkD1OtherPage, "Other page text")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val json = jsonCaptor.getValue

      val list = (json \ "list").toString

      templateCaptor.getValue mustEqual "check-your-answers.njk"
      list.contains("D1a") mustBe true
      list.contains("Other page text") mustBe false

      application.stop()

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
