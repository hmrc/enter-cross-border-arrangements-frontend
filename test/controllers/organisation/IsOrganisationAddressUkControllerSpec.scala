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

import base.{ControllerMockFixtures, SpecBase}
import forms.organisation.IsOrganisationAddressUkFormProvider
import matchers.JsonMatchers
import models.{NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.organisation.IsOrganisationAddressUkPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class IsOrganisationAddressUkControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider = new IsOrganisationAddressUkFormProvider()
  val form         = formProvider()

  lazy val isOrganisationAddressUkRoute = controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(0, NormalMode).url

  "IsOrganisationAddressUk Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(GET, isOrganisationAddressUkRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "radios" -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "organisation/isOrganisationAddressUk.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(IsOrganisationAddressUkPage, 0, true)
        .success
        .value
      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, isOrganisationAddressUkRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "true"))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> Radios.yesNo(filledForm("value"))
      )

      templateCaptor.getValue mustEqual "organisation/isOrganisationAddressUk.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when yes is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, isOrganisationAddressUkRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/postcode/0"
    }

    "must redirect to the next page when no is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, isOrganisationAddressUkRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/address/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)

      val request        = FakeRequest(POST, isOrganisationAddressUkRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "organisation/isOrganisationAddressUk.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      retrieveNoData()
      val request = FakeRequest(GET, isOrganisationAddressUkRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, isOrganisationAddressUkRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
