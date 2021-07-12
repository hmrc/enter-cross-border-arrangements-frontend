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

package controllers.reporter.taxpayer

import base.{ControllerMockFixtures, SpecBase}
import forms.reporter.taxpayer.TaxpayerWhyReportInUKFormProvider
import matchers.JsonMatchers
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.{NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.reporter.taxpayer.TaxpayerWhyReportInUKPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class TaxpayerWhyReportInUKControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  lazy val taxpayerWhyReportInUKRoute = controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(0, NormalMode).url

  val formProvider = new TaxpayerWhyReportInUKFormProvider()
  val form         = formProvider()

  "TaxpayerWhyReportInUK Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, taxpayerWhyReportInUKRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "radios" -> TaxpayerWhyReportInUK.radios(form)
      )

      templateCaptor.getValue mustEqual "reporter/taxpayer/taxpayerWhyReportInUK.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.values.head)
        .success
        .value
      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, taxpayerWhyReportInUKRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> TaxpayerWhyReportInUK.values.head.toString))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> TaxpayerWhyReportInUK.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "reporter/taxpayer/taxpayerWhyReportInUK.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)
      val request =
        FakeRequest(POST, taxpayerWhyReportInUKRoute)
          .withFormUrlEncodedBody(("value", TaxpayerWhyReportInUK.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(POST, taxpayerWhyReportInUKRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> TaxpayerWhyReportInUK.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "reporter/taxpayer/taxpayerWhyReportInUK.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }
  }
}
