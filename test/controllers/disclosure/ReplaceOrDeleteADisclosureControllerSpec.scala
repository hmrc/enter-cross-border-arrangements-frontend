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

package controllers.disclosure

import base.{MockServiceApp, SpecBase}
import connectors.{CrossBorderArrangementsConnector, HistoryConnector}
import forms.disclosure.ReplaceOrDeleteADisclosureFormProvider
import matchers.JsonMatchers
import models.disclosure.{DisclosureType, IDVerificationStatus, ReplaceOrDeleteADisclosure}
import models.{Country, NormalMode, SubmissionDetails, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.disclosure.{DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import play.api.data.FormError
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.LocalDateTime
import scala.concurrent.Future

class ReplaceOrDeleteADisclosureControllerSpec extends SpecBase with MockServiceApp with NunjucksSupport with JsonMatchers {



  val formProvider = new ReplaceOrDeleteADisclosureFormProvider()
  val countries = List(Country("valid","GB","United Kingdom"))
  val form = formProvider(countries)

  lazy val replaceOrDeleteADisclosureRoute = routes.ReplaceOrDeleteADisclosureController.onPageLoad(NormalMode).url

  val arrangementID = "GBA20210101ABC123"
  val disclosureID = "GBD20210101ABC123"

  override val emptyUserAnswers =  UserAnswers(userAnswersId)
     .setBase(DisclosureTypePage, DisclosureType.Dac6rep).success.value

  val submissionDetails = SubmissionDetails("id",LocalDateTime.now(),"test.xml",Some(arrangementID),Some(disclosureID),"",true,"xxx")

  val userAnswers = emptyUserAnswers
    .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure(arrangementID, disclosureID)).success.value

  "ReplaceOrDeleteADisclosure Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, replaceOrDeleteADisclosureRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, replaceOrDeleteADisclosureRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "arrangementID" -> arrangementID,
          "disclosureID" -> disclosureID
        )
      )

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]
      val mockHistoryConnector = mock[HistoryConnector]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockCrossBorderArrangementsConnector.verifyDisclosureIDs(any(),any(), any())(any()))
        .thenReturn(Future.successful(IDVerificationStatus(isValid = true, IDVerificationStatus.IDsFound)))

      when(mockHistoryConnector.getSubmissionDetailForDisclosure(any())(any())).thenReturn(Future.successful(submissionDetails))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector),
            bind[HistoryConnector].toInstance(mockHistoryConnector)
          )
          .build()


      val request =
        FakeRequest(POST, replaceOrDeleteADisclosureRoute)
          .withFormUrlEncodedBody(("arrangementID", arrangementID), ("disclosureID", disclosureID))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad().url

      application.stop()
    }

    "must display id validation errors if arrangement ID is not found" in {

      val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCrossBorderArrangementsConnector.verifyDisclosureIDs(any(),any(), any())(any()))
        .thenReturn(Future.successful(IDVerificationStatus(isValid = false, IDVerificationStatus.ArrangementIDNotFound)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector)
          )
          .build()

      val request = FakeRequest(POST, replaceOrDeleteADisclosureRoute)
        .withFormUrlEncodedBody(("arrangementID", arrangementID), ("disclosureID", disclosureID))

      val boundForm =
        form.bind(Map("arrangementID" -> arrangementID, "disclosureID" -> disclosureID))
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.arrangementID.notFound")))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must return a Bad Request and display id validation errors if disclosure ID is not found for an enrolment ID" in {

      val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCrossBorderArrangementsConnector.verifyDisclosureIDs(any(),any(), any())(any()))
        .thenReturn(Future.successful(IDVerificationStatus(isValid = false, IDVerificationStatus.DisclosureIDNotFound)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector)
          )
          .build()

      val request = FakeRequest(POST, replaceOrDeleteADisclosureRoute)
        .withFormUrlEncodedBody(("arrangementID", arrangementID), ("disclosureID", disclosureID))

      val boundForm =
        form.bind(Map("arrangementID" -> arrangementID, "disclosureID" -> disclosureID))
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must display id validation errors if ids aren't from the same submission" in {

      val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCrossBorderArrangementsConnector.verifyDisclosureIDs(any(),any(), any())(any()))
        .thenReturn(Future.successful(IDVerificationStatus(isValid = false, IDVerificationStatus.IDsDoNotMatch)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector)
          )
          .build()

      val request = FakeRequest(POST, replaceOrDeleteADisclosureRoute)
        .withFormUrlEncodedBody(("arrangementID", arrangementID), ("disclosureID", disclosureID))

      val boundForm =
        form.bind(Map("arrangementID" -> arrangementID, "disclosureID" -> disclosureID))
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.mismatch")))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must display id validation errors if ids do not exist" in {

      val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCrossBorderArrangementsConnector.verifyDisclosureIDs(any(),any(), any())(any()))
        .thenReturn(Future.successful(IDVerificationStatus(isValid = false, IDVerificationStatus.IDsNotFound)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector)
          )
          .build()

      val request = FakeRequest(POST, replaceOrDeleteADisclosureRoute)
        .withFormUrlEncodedBody(("arrangementID", arrangementID), ("disclosureID", disclosureID))

      val boundForm =
        form.bind(Map("arrangementID" -> arrangementID, "disclosureID" -> disclosureID))
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.arrangementID.notFound")))
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, replaceOrDeleteADisclosureRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode
      )

      templateCaptor.getValue mustEqual "disclosure/replaceOrDeleteADisclosure.njk"
      jsonCaptor.getValue must containJson(expectedJson)

       application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, replaceOrDeleteADisclosureRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, replaceOrDeleteADisclosureRoute)
          .withFormUrlEncodedBody(("arrangementID", "value 1"), ("disclosureID", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must return RuntimeException if disclosure type is missing or not replace or delete" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId))).build()

      val request =
        FakeRequest(POST, replaceOrDeleteADisclosureRoute)
          .withFormUrlEncodedBody(("arrangementID", "value 1"), ("disclosureID", "value 2"))

      intercept[RuntimeException] {
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }
      application.stop()
    }
  }
}
