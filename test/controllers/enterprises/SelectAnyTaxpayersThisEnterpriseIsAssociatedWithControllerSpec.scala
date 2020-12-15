/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.enterprises

import base.SpecBase
import controllers.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.taxpayers
import forms.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider
import matchers.JsonMatchers
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{Checkboxes, NunjucksSupport}
import uk.gov.hmrc.viewmodels.Text.Literal

import scala.concurrent.Future

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute = controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(NormalMode).url

  val formProvider = new SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider()
  val form = formProvider()
  // TODO substitute when actual values are available
  val taxpayers = SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.taxpayers
  val items: Seq[Checkboxes.Checkbox] = taxpayers.map { taxpayer =>
    Checkboxes.Checkbox(label = Literal(taxpayer.nameAsString), value = s"${taxpayer.taxpayerId}")
  }

  val checkboxes = Checkboxes.set(field = form("value"), items = items)

  "SelectAnyTaxpayersThisEnterpriseIsAssociatedWith Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> checkboxes
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      val jc = jsonCaptor.getValue
      println("XXX\n\n")
      println(jc.toString.diff(expectedJson.toString))
      jc must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId).set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, List("1")).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(List("1"))
      val filledCheckboxes = Checkboxes.set(field = filledForm("value"), items = items)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "checkboxes" -> filledCheckboxes
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
          .withFormUrlEncodedBody(("value[0]", "1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute).withFormUrlEncodedBody(("value[0]", "1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
