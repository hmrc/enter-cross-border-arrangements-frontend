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

package controllers

import base.{ControllerMockFixtures, SpecBase}
import models.{NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with ControllerMockFixtures {

  "Index Controller" - {

    "must redirect to unsubmitted disclosures when at least one is in progress" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("foo")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get mustEqual controllers.unsubmitted.routes.UnsubmittedDisclosureController.onPageLoad.url
    }

    "must redirect to start a disclosure if all disclosures have been deleted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("foo")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First", deleted = true)))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get mustEqual controllers.disclosure.routes.DisclosureNameController.onPageLoad(NormalMode).url
    }

    "must redirect to start a disclosure if all disclosures have been submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("foo")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First", submitted = true)))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get mustEqual controllers.disclosure.routes.DisclosureNameController.onPageLoad(NormalMode).url
    }

    "must redirect to start a disclosure when none is in progress" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("foo")))

      retrieveNoData()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get mustEqual controllers.disclosure.routes.DisclosureNameController.onPageLoad(NormalMode).url
    }
  }
}
