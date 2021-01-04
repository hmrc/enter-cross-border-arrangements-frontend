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

package controllers.organisation

import base.SpecBase
import forms.organisation.DoYouKnowAnyTINForUKOrganisationFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, NormalMode, UserAnswers}
import navigation.NavigatorForOrganisation
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.organisation.{DoYouKnowAnyTINForUKOrganisationPage, OrganisationLoopPage, OrganisationNamePage}
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class DoYouKnowAnyTINForUKOrganisationControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider = new DoYouKnowAnyTINForUKOrganisationFormProvider()
  val form = formProvider()
  val index = 0
  val selectedCountry: Option[Country] = Some(Country("", "GB", "United Kingdom"))

  lazy val doYouKnowAnyTINForUKOrganisationRoute: String = controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(NormalMode, index).url

  "DoYouKnowAnyTINForUKOrganisation Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedUserAnswers = UserAnswers(userAnswersId).set(OrganisationNamePage, "Paper Org").success.value
      val application = applicationBuilder(userAnswers = Some(updatedUserAnswers)).build()
      val request = FakeRequest(GET, doYouKnowAnyTINForUKOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "radios" -> Radios.yesNo(form("confirm"))
      )

      templateCaptor.getValue mustEqual "organisation/doYouKnowAnyTINForUKOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .set(DoYouKnowAnyTINForUKOrganisationPage, true)
        .success
        .value
        .set(OrganisationLoopPage, IndexedSeq(
          LoopDetails(None, selectedCountry, None,None, Some(true), None))
        )
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, doYouKnowAnyTINForUKOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("confirm" -> "true"))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> Radios.yesNo(filledForm("confirm")),
        "index" -> index
      )

      templateCaptor.getValue mustEqual "organisation/doYouKnowAnyTINForUKOrganisation.njk"
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

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, doYouKnowAnyTINForUKOrganisationRoute)
          .withFormUrlEncodedBody(("confirm", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/organisation/uk-tax-numbers-0"

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, doYouKnowAnyTINForUKOrganisationRoute).withFormUrlEncodedBody(("confirm", ""))
      val boundForm = form.bind(Map("confirm" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> Radios.yesNo(boundForm("confirm"))
      )

      templateCaptor.getValue mustEqual "organisation/doYouKnowAnyTINForUKOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, doYouKnowAnyTINForUKOrganisationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, doYouKnowAnyTINForUKOrganisationRoute)
          .withFormUrlEncodedBody(("confirm", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
