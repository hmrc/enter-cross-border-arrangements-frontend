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

package controllers.taxpayer

import base.{ControllerMockFixtures, SpecBase}
import config.FrontendAppConfig
import forms.taxpayer.UpdateTaxpayerFormProvider
import helpers.data.ValidUserAnswersForSubmission.validIndividual
import matchers.JsonMatchers
import models.taxpayer.{Taxpayer, UpdateTaxpayer}
import models.{NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.taxpayer.{TaxpayerLoopPage, UpdateTaxpayerPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class UpdateTaxpayerControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  lazy val updateTaxpayerRoute = controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(0).url

  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val formProvider                             = new UpdateTaxpayerFormProvider()
  val form                                     = formProvider()

  "UpdateTaxpayer Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, updateTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"         -> form,
        "taxpayerList" -> Json.arr(),
        "mode"         -> NormalMode,
        "radios"       -> UpdateTaxpayer.radios(form)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return OK and the correct view with the list of all taxpayers for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val taxpayerLoop = IndexedSeq(
        Taxpayer("id", Some(validIndividual), organisation = None, implementingDate = None)
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(TaxpayerLoopPage, 0, taxpayerLoop)
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request        = FakeRequest(GET, updateTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])
      val controller     = app.injector.instanceOf[UpdateTaxpayerController]

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedList = Json.toJson(controller.toItemList(userAnswers, 0))

      val expectedJson = Json.obj(
        "form"         -> form,
        "taxpayerList" -> expectedList,
        "mode"         -> NormalMode,
        "radios"       -> UpdateTaxpayer.radios(form)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(UpdateTaxpayerPage, 0, UpdateTaxpayer.values.head)
        .success
        .value
      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, updateTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("confirm" -> UpdateTaxpayer.values.head.toString))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> UpdateTaxpayer.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, updateTaxpayerRoute)
          .withFormUrlEncodedBody(("confirm", UpdateTaxpayer.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/choose-type/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(POST, updateTaxpayerRoute).withFormUrlEncodedBody(("confirm", "invalid value"))
      val boundForm      = form.bind(Map("confirm" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> UpdateTaxpayer.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }
  }
}
