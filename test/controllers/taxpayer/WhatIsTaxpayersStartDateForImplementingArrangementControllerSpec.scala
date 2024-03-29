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

package controllers.taxpayer

import base.{ControllerMockFixtures, SpecBase}
import forms.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementFormProvider
import matchers.JsonMatchers
import models.SelectType.Organisation
import models.{CheckMode, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.organisation.OrganisationNamePage
import pages.taxpayer.{TaxpayerSelectTypePage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.DateInput

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class WhatIsTaxpayersStartDateForImplementingArrangementControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider = new WhatIsTaxpayersStartDateForImplementingArrangementFormProvider()
  private def form = formProvider()

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val whatIsTaxpayersStartDateForImplementingArrangementRoute =
    routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(0, NormalMode).url

  lazy val whatIsTaxpayersStartDateForImplementingArrangementCheckRoute =
    routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(0, CheckMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId)
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
    .success
    .value
    .set(OrganisationNamePage, 0, "validAnswer")
    .success
    .value

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, whatIsTaxpayersStartDateForImplementingArrangementRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, whatIsTaxpayersStartDateForImplementingArrangementRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "WhatIsTaxpayersStartDateForImplementingArrangement Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(OrganisationNamePage, 0, "validAnswer")
        .success
        .value
        .set(TaxpayerSelectTypePage, 0, Organisation)
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateInput.localDate(form("value"))

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "date" -> viewModel
      )

      templateCaptor.getValue mustEqual "implementingArrangementDate.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when in check mode and the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, 0, validAnswer)
        .success
        .value
        .set(TaxpayerSelectTypePage, 0, Organisation)
        .success
        .value
        .set(OrganisationNamePage, 0, "validAnswer")
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val request = FakeRequest("GET", whatIsTaxpayersStartDateForImplementingArrangementCheckRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value.day"   -> validAnswer.getDayOfMonth.toString,
          "value.month" -> validAnswer.getMonthValue.toString,
          "value.year"  -> validAnswer.getYear.toString
        )
      )

      val viewModel = DateInput.localDate(filledForm("value"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> CheckMode,
        "date" -> viewModel
      )

      templateCaptor.getValue mustEqual "implementingArrangementDate.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(POST, whatIsTaxpayersStartDateForImplementingArrangementRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateInput.localDate(boundForm("value"))

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode,
        "date" -> viewModel
      )

      templateCaptor.getValue mustEqual "implementingArrangementDate.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      retrieveNoData()

      val result = route(app, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      retrieveNoData()

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
