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

package controllers.affected

import base.SpecBase
import forms.affected.YouHaveNotAddedAnyAffectedFormProvider
import matchers.JsonMatchers
import models.affected.{Affected, YouHaveNotAddedAnyAffected}
import models.individual.Individual
import models.taxpayer.TaxResidency
import models.{Country, Name, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.affected.{AffectedLoopPage, YouHaveNotAddedAnyAffectedPage}
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.LocalDate
import scala.concurrent.Future

class YouHaveNotAddedAnyAffectedControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val youHaveNotAddedAnyAffectedRoute = controllers.affected.routes.YouHaveNotAddedAnyAffectedController.onPageLoad().url

  val formProvider = new YouHaveNotAddedAnyAffectedFormProvider()
  val form = formProvider()

  "YouHaveNotAddedAnyAffected Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, youHaveNotAddedAnyAffectedRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "affectedList" -> Json.arr(),
        "radios" -> YouHaveNotAddedAnyAffected.radios(form)
      )

      templateCaptor.getValue mustEqual "affected/youHaveNotAddedAnyAffected.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    //TODO Include test for change and remove links if needed
    "must return OK and the correct view with the list of all affected persons for a GET" ignore {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val individual = Individual(
        individualName = Name("John", "Smith"),
        birthDate =  LocalDate.now(), None, None,
        taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), None))
      )

      val affectedLoop = IndexedSeq(Affected("id", Some(individual)))
      val userAnswers = UserAnswers(userAnswersId).set(AffectedLoopPage, affectedLoop).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, youHaveNotAddedAnyAffectedRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedList = Json.arr(Json.obj("name" -> "John Smith", "changeUrl" -> "#", "removeUrl" -> "#"))

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "affectedList" -> expectedList,
        "radios" -> YouHaveNotAddedAnyAffected.radios(form)
      )

      templateCaptor.getValue mustEqual "affected/youHaveNotAddedAnyAffected.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId).set(YouHaveNotAddedAnyAffectedPage, YouHaveNotAddedAnyAffected.values.head).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, youHaveNotAddedAnyAffectedRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(YouHaveNotAddedAnyAffected.values.head)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "radios" -> YouHaveNotAddedAnyAffected.radios(filledForm)
      )

      templateCaptor.getValue mustEqual "affected/youHaveNotAddedAnyAffected.njk"
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
        FakeRequest(POST, youHaveNotAddedAnyAffectedRoute)
          .withFormUrlEncodedBody(("value", YouHaveNotAddedAnyAffected.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/others-affected/type"

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request =  FakeRequest(POST, youHaveNotAddedAnyAffectedRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> boundForm,
        "mode"       -> NormalMode,
        "radios" -> YouHaveNotAddedAnyAffected.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "affected/youHaveNotAddedAnyAffected.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }
  }
}
