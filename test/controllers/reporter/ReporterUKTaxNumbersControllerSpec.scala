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

package controllers.reporter

import base.{ControllerMockFixtures, SpecBase}
import config.FrontendAppConfig
import forms.reporter.ReporterUKTaxNumbersFormProvider
import matchers.JsonMatchers
import models.ReporterOrganisationOrIndividual.{Individual, Organisation}
import models.{Country, LoopDetails, NormalMode, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, ReporterUKTaxNumbersPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ReporterUKTaxNumbersControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  override def onwardRoute = Call("GET", "/disclose-cross-border-arrangements/manual/reporter/tax-resident-countries-1/0")

  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val formProvider                             = new ReporterUKTaxNumbersFormProvider()
  val index                                    = 0
  val reporterIndividualKey                    = "reporterIndividual"
  val reporterOrganisationKey                  = "reporterOrganisation"

  val utr: String                      = "1234567890"
  val selectedCountry: Option[Country] = Some(Country("", "GB", "United Kingdom"))

  lazy val reporterUKTaxNumbersRoute = routes.ReporterUKTaxNumbersController.onPageLoad(0, NormalMode, index).url

  "ReporterUKTaxNumbers Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val form = formProvider(reporterIndividualKey)

      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, reporterUKTaxNumbersRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "reporter/reporterUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val taxReferenceNumbers = TaxReferenceNumbers(utr, None, None)

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(ReporterUKTaxNumbersPage, 0, taxReferenceNumbers)
        .success
        .value
        .set(ReporterTaxResidencyLoopPage, 0, IndexedSeq(LoopDetails(None, selectedCountry, None, None, Some(true), Some(taxReferenceNumbers))))
        .success
        .value

      val form = formProvider(reporterIndividualKey)

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, reporterUKTaxNumbersRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "firstTaxNumber"  -> utr,
          "secondTaxNumber" -> "",
          "thirdTaxNumber"  -> ""
        )
      )

      val expectedJson = Json.obj(
        "form"  -> filledForm,
        "mode"  -> NormalMode,
        "index" -> index
      )

      templateCaptor.getValue mustEqual "reporter/reporterUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, reporterUKTaxNumbersRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", utr), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted for reporter as Individual" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(ReporterOrganisationOrIndividualPage, 0, Individual)
        .success
        .value

      val form = formProvider(reporterIndividualKey)
      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(POST, reporterUKTaxNumbersRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "reporter/reporterUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return a Bad Request and errors when invalid data is submitted for reporter as Organisation" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(ReporterOrganisationOrIndividualPage, 0, Organisation)
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val form           = formProvider(reporterOrganisationKey)
      val request        = FakeRequest(POST, reporterUKTaxNumbersRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "reporter/reporterUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, reporterUKTaxNumbersRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      retrieveNoData()
      val request =
        FakeRequest(POST, reporterUKTaxNumbersRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url
    }
  }
}
