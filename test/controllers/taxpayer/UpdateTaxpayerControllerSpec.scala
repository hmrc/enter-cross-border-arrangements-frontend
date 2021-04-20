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

import base.SpecBase
import config.FrontendAppConfig
import forms.taxpayer.UpdateTaxpayerFormProvider
import matchers.JsonMatchers
import models.individual.Individual
import models.taxpayer.{TaxResidency, Taxpayer, UpdateTaxpayer}
import models.{Country, Name, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.taxpayer.{TaxpayerLoopPage, UpdateTaxpayerPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.LocalDate
import scala.concurrent.Future

class UpdateTaxpayerControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  lazy val updateTaxpayerRoute = controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(0).url

  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val formProvider = new UpdateTaxpayerFormProvider()
  val form = formProvider()

  "UpdateTaxpayer Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, updateTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "taxpayerList" -> Json.arr(),
        "mode"   -> NormalMode,
        "radios" -> UpdateTaxpayer.radios(form)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must return OK and the correct view with the list of all taxpayers for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockFrontendAppConfig.changeLinkToggle).thenReturn(true)

      val taxpayerLoop = IndexedSeq(
        Taxpayer("id",
          Some(Individual(
            individualName = Name("John", "Smith"),
            birthDate = LocalDate.now(), None, None,
            taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), None)
            ))),
          organisation = None,
          implementingDate = None)
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerLoopPage, 0, taxpayerLoop).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, updateTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedList = Json.arr(Json.obj("name" -> "John Smith",
        "changeUrl" -> routes.TaxpayerSelectTypeController.onPageLoad(0, NormalMode).url,
        "removeUrl" -> routes.RemoveTaxpayerController.onPageLoad(0, taxpayerLoop.head.taxpayerId).url)
      )

      val expectedJson = Json.obj(
        "form"   -> form,
        "taxpayerList" -> expectedList,
        "mode"   -> NormalMode,
        "radios" -> UpdateTaxpayer.radios(form)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(UpdateTaxpayerPage, 0, UpdateTaxpayer.values.head).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, updateTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

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

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, updateTaxpayerRoute)
          .withFormUrlEncodedBody(("confirm", UpdateTaxpayer.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/taxpayers/choose-type/0"

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, updateTaxpayerRoute).withFormUrlEncodedBody(("confirm", "invalid value"))
      val boundForm = form.bind(Map("confirm" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> UpdateTaxpayer.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "taxpayer/updateTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }
  }
}
