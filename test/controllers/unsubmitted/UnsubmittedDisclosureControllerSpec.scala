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

package controllers.unsubmitted

import base.{ControllerMockFixtures, SpecBase}
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, status, _}
import play.twirl.api.Html

import scala.concurrent.Future

class UnsubmittedDisclosureControllerSpec extends SpecBase with ControllerMockFixtures {
  "Unsubmitted Disclosure Controller" - {
    "must display the unsubmitted disclosures when some are present" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val unsubmittedDisclosures = Seq(UnsubmittedDisclosure("1", "My First Disclosure"), UnsubmittedDisclosure("2", "The Revenge"))

      val userAnswers = UserAnswers(userAnswersId).setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures).success.value
      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, controllers.unsubmitted.routes.UnsubmittedDisclosureController.onPageLoad().url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

      templateCaptor.getValue mustEqual "unsubmitted/unsubmitted.njk"

    }

    "must redirect first time users to the disclosures homepage" in {
      retrieveNoData()
      val request = FakeRequest(GET, controllers.unsubmitted.routes.UnsubmittedDisclosureController.onPageLoad().url)
      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
    }

    "must redirect users who have no unsubmitted disclosures to the no unsubmitted disclosures page" in {
      val unsubmittedDisclosures = Seq.empty[UnsubmittedDisclosure]
      val userAnswers = UserAnswers(userAnswersId).setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures).success.value
      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, controllers.unsubmitted.routes.UnsubmittedDisclosureController.onPageLoad().url)
      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
    }
  }
}
