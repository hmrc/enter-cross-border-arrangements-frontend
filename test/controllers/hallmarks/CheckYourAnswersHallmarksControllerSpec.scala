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

package controllers.hallmarks

import base.{ControllerMockFixtures, SpecBase}
import models.hallmarks.{HallmarkD, HallmarkD1}
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.hallmarks._
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class CheckYourAnswersHallmarksControllerSpec extends SpecBase with ControllerMockFixtures {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET if there's only one hallmark selected" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
        .success
        .value
        .set(HallmarkD1Page, 0, HallmarkD1.enumerable.withName("D1").toSet)
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.CheckYourAnswersHallmarksController.onPageLoad(0).url)

      val result = route(app, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "hallmarks/check-your-answers-hallmarks.njk"
    }

    "must include 'Parts of hallmark D1 that apply to this arrangement' if D1 Other selected" in {
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
        .success
        .value
        .set(HallmarkD1Page, 0, HallmarkD1.enumerable.withName("DAC6D1Other").toSet)
        .success
        .value
        .set(HallmarkD1OtherPage, 0, "Other page text")
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.CheckYourAnswersHallmarksController.onPageLoad(0).url)

      val result = route(app, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val json = jsonCaptor.getValue

      val list = (json \ "list").toString

      templateCaptor.getValue mustEqual "hallmarks/check-your-answers-hallmarks.njk"
      list.contains("D1Other") mustBe true
      list.contains("Other page text") mustBe true
    }

    "must not include 'Parts of hallmark D1 that apply to this arrangement' if D1 Other is not selected" in {
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet)
        .success
        .value
        .set(HallmarkD1Page, 0, HallmarkD1.enumerable.withName("DAC6D1a").toSet)
        .success
        .value
        .set(HallmarkD1OtherPage, 0, "Other page text")
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.CheckYourAnswersHallmarksController.onPageLoad(0).url)

      val result = route(app, request).value

      status(result) mustEqual OK

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      val json = jsonCaptor.getValue

      val list = (json \ "list").toString

      templateCaptor.getValue mustEqual "hallmarks/check-your-answers-hallmarks.njk"
      list.contains("D1a") mustBe true
      list.contains("Other page text") mustBe false
    }

    "must redirect to task list page on submit" in {
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D2").toSet)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, routes.CheckYourAnswersHallmarksController.onPageLoad(0).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/your-disclosure-details/0"
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, routes.CheckYourAnswersHallmarksController.onPageLoad(0).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
