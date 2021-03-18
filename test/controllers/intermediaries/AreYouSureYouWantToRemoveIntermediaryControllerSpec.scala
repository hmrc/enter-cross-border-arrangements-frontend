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

package controllers.intermediaries

import base.SpecBase
import forms.intermediaries.AreYouSureYouWantToRemoveIntermediaryFormProvider
import helpers.data.ValidUserAnswersForSubmission.{validIndividual, validOrganisation}
import matchers.JsonMatchers
import models.intermediaries.Intermediary
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.intermediaries.IntermediaryLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AreYouSureYouWantToRemoveIntermediaryControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AreYouSureYouWantToRemoveIntermediaryFormProvider()
  val form = formProvider()

  lazy val areYouSureYouWantToRemoveIntermediaryRoute = controllers.intermediaries.routes.AreYouSureYouWantToRemoveIntermediaryController.onPageLoad(0, "itemId").url

  lazy val intermediaryLoop: IndexedSeq[Intermediary] = IndexedSeq(
    Intermediary("1", None, Some(validOrganisation))
    , Intermediary("2", Some(validIndividual), None)
  )

  val userAnswers = UserAnswers(userAnswersId)
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
    .set(IntermediaryLoopPage, 0, intermediaryLoop).success.value

  val mockSessionRepository = mock[SessionRepository]

  override def afterEach() = {
    reset(mockSessionRepository)
  }

  "AreYouSureYouWantToRemoveIntermediary Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, areYouSureYouWantToRemoveIntermediaryRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val preparedForm = form.bind(Map("value" -> "false"))
      val expectedJson = Json.obj(
        "form"   -> preparedForm,
        "id"     -> 0,
        "itemId" -> "itemId",
        "name"   -> "",
        "radios" -> Radios.yesNo(preparedForm("value"))
      )

      templateCaptor.getValue mustEqual "intermediaries/areYouSureYouWantToRemoveIntermediary.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must not delete and return to the update page whe the answers is 'No' " in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
        bind[SessionRepository].toInstance(mockSessionRepository)
      ).build()

      val request = FakeRequest(POST, areYouSureYouWantToRemoveIntermediaryRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(0).url

      verify(mockSessionRepository, times(0)).set(any())

      application.stop()
    }

    "must delete the required item and redirect to the next page when 'Yes' is submitted" in {

      val userAnswersCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val postRoute = controllers.intermediaries.routes.AreYouSureYouWantToRemoveIntermediaryController.onSubmit(0, "1").url

      val request =
        FakeRequest(POST, postRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(0).url

      userAnswersCaptor.getValue.get(IntermediaryLoopPage, 0).map { loop =>
        loop mustBe(intermediaryLoop.filterNot(_.intermediaryId == "1"))
      }

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, areYouSureYouWantToRemoveIntermediaryRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "id"     -> 0,
        "itemId" -> "itemId",
        "name"   -> "",
        "radios" -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "intermediaries/areYouSureYouWantToRemoveIntermediary.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, areYouSureYouWantToRemoveIntermediaryRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, areYouSureYouWantToRemoveIntermediaryRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
