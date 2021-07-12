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

import base.{ControllerMockFixtures, SpecBase}
import forms.affected.AreYouSureYouWantToRemoveAffectedFormProvider
import helpers.data.ValidUserAnswersForSubmission.{validIndividual, validOrganisation}
import matchers.JsonMatchers
import models.affected.Affected
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.affected.AffectedLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class AreYouSureYouWantToRemoveAffectedControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider = new AreYouSureYouWantToRemoveAffectedFormProvider()
  val form         = formProvider()

  lazy val areYouSureYouWantToRemoveAffectedRoute = controllers.affected.routes.AreYouSureYouWantToRemoveAffectedController.onPageLoad(0, "itemId").url

  lazy val affectedLoop: IndexedSeq[Affected] = IndexedSeq(
    Affected("1", None, Some(validOrganisation)),
    Affected("2", Some(validIndividual), None)
  )

  val userAnswers = UserAnswers(userAnswersId)
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
    .success
    .value
    .set(AffectedLoopPage, 0, affectedLoop)
    .success
    .value

  "AreYouSureYouWantToRemoveAffected Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(GET, areYouSureYouWantToRemoveAffectedRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val preparedForm = form
      val expectedJson = Json.obj(
        "form"   -> preparedForm,
        "id"     -> 0,
        "itemId" -> "itemId",
        "name"   -> "",
        "radios" -> Radios.yesNo(preparedForm("value"))
      )

      templateCaptor.getValue mustEqual "affected/areYouSureYouWantToRemoveAffected.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must not delete and return to the update page whe the answers is 'No' " in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      retrieveUserAnswersData(emptyUserAnswers)

      val request = FakeRequest(POST, areYouSureYouWantToRemoveAffectedRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.affected.routes.YouHaveNotAddedAnyAffectedController.onPageLoad(0).url

      verify(mockSessionRepository, times(0)).set(any())
    }

    "must delete the required item and redirect to the next page when 'Yes' is submitted" in {

      val userAnswersCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      retrieveUserAnswersData(userAnswers)

      val postRoute = controllers.affected.routes.AreYouSureYouWantToRemoveAffectedController.onSubmit(0, "1").url

      val request =
        FakeRequest(POST, postRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.affected.routes.YouHaveNotAddedAnyAffectedController.onPageLoad(0).url

      userAnswersCaptor.getValue.get(AffectedLoopPage, 0).map {
        loop =>
          loop mustBe (affectedLoop.filterNot(_.affectedId == "1"))
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(POST, areYouSureYouWantToRemoveAffectedRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "id"     -> 0,
        "itemId" -> "itemId",
        "name"   -> "",
        "radios" -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "affected/areYouSureYouWantToRemoveAffected.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()
      val request = FakeRequest(GET, areYouSureYouWantToRemoveAffectedRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, areYouSureYouWantToRemoveAffectedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
