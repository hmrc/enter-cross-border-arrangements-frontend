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

package controllers.arrangement

import base.{ControllerMockFixtures, SpecBase}
import forms.arrangement.WhatIsTheExpectedValueOfThisArrangementFormProvider
import matchers.JsonMatchers
import models.arrangement.ExpectedArrangementValue
import models.{Currency, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, Mockito}
import pages.arrangement.WhatIsTheExpectedValueOfThisArrangementPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CurrencyListFactory

import scala.concurrent.Future

class WhatIsTheExpectedValueOfThisArrangementControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider     = new WhatIsTheExpectedValueOfThisArrangementFormProvider()
  val mockCurrencyList = mock[CurrencyListFactory]

  val form = formProvider(Seq(Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)")))

  override def beforeEach {
    Mockito.reset(
      mockCurrencyList
    )
    super.beforeEach()
  }

  lazy val whatIsTheExpectedValueOfThisArrangementRoute = routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(0, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[CurrencyListFactory].toInstance(mockCurrencyList)
      )

  "WhatIsTheExpectedValueOfThisArrangement Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      when(mockCurrencyList.getCurrencyList).thenReturn(Some(Seq(Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)"))))

      val request        = FakeRequest(GET, whatIsTheExpectedValueOfThisArrangementRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "arrangement/whatIsTheExpectedValueOfThisArrangement.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCurrencyList.getCurrencyList).thenReturn(Some(Seq(Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)"))))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("ALL", 0))
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, whatIsTheExpectedValueOfThisArrangementRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "currency" -> "ALL",
          "amount"   -> "0"
        )
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "arrangement/whatIsTheExpectedValueOfThisArrangement.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      retrieveUserAnswersData(emptyUserAnswers)

      when(mockCurrencyList.getCurrencyList).thenReturn(Some(Seq(Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)"))))

      val request =
        FakeRequest(POST, whatIsTheExpectedValueOfThisArrangementRoute)
          .withFormUrlEncodedBody(("currency", "ALL"), ("amount", "0"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCurrencyList.getCurrencyList).thenReturn(Some(Seq(Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)"))))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(POST, whatIsTheExpectedValueOfThisArrangementRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "arrangement/whatIsTheExpectedValueOfThisArrangement.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, whatIsTheExpectedValueOfThisArrangementRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, whatIsTheExpectedValueOfThisArrangementRoute)
          .withFormUrlEncodedBody(("currency", "ALL"), ("amount", "1"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
