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
import matchers.JsonMatchers
import models.disclosure.DisclosureDetails
import models.disclosure.DisclosureType._
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.disclosure.DisclosureDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class DisclosureAlreadySentControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  "DisclosureAlreadySent Controller" - {

    "return OK and the correct view for a GET when Dac6new" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("0", "My replacement")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, DisclosureDetails("name", Dac6new))
        .success
        .value

      val application    = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request        = FakeRequest(GET, routes.DisclosureAlreadySentController.onSent(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val expectedJson = Json.obj(
        "option"      -> "sent",
        "backLinkUrl" -> "/disclose-cross-border-arrangements/manual/disclosure-received/0"
      )

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "informationSent.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "return OK and the correct view for a GET when Dac6add" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("0", "My replacement")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, DisclosureDetails("name", Dac6add))
        .success
        .value

      val application    = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request        = FakeRequest(GET, routes.DisclosureAlreadySentController.onSent(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val expectedJson = Json.obj(
        "option"      -> "sent",
        "backLinkUrl" -> "/disclose-cross-border-arrangements/manual/addition-received/0"
      )

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "informationSent.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "return OK and the correct view for a GET when Dac6repl" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("0", "My replacement")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, DisclosureDetails("name", Dac6rep))
        .success
        .value

      val application    = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request        = FakeRequest(GET, routes.DisclosureAlreadySentController.onSent(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val expectedJson = Json.obj(
        "option"      -> "sent",
        "backLinkUrl" -> "/disclose-cross-border-arrangements/manual/replacement-received/0"
      )

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "informationSent.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "return OK and the correct view for a GET when Dac6del" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("0", "My replacement")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, DisclosureDetails("name", Dac6del))
        .success
        .value

      val application    = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request        = FakeRequest(GET, routes.DisclosureAlreadySentController.onDeleted().url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val expectedJson = Json.obj(
        "option"      -> "deleted",
        "backLinkUrl" -> "/disclose-cross-border-arrangements/manual/disclosure/disclosure-has-been-deleted"
      )

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "informationSent.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

  }
}
