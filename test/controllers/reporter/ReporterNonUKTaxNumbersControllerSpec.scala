/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.reporter.ReporterNonUKTaxNumbersFormProvider
import matchers.JsonMatchers
import models.ReporterOrganisationOrIndividual.{Individual, Organisation}
import models.{Country, LoopDetails, NormalMode, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.reporter.{ReporterNonUKTaxNumbersPage, ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class ReporterNonUKTaxNumbersControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  override def onwardRoute = Call("GET", "/disclose-cross-border-arrangements/manual/reporter/tax-resident-countries-1/0")

  val formProvider = new ReporterNonUKTaxNumbersFormProvider()
  val index        = 0

  val taxNumber: String                        = "123ABC"
  val taxReferenceNumbers: TaxReferenceNumbers = TaxReferenceNumbers(taxNumber, None, None)
  val selectedCountry: Country                 = Country("valid", "FR", "France")
  val reporterIndividualKey                    = "reporterIndividual"
  val reporterOrganisationKey                  = "reporterOrganisation"

  lazy val reporterNonUKTaxNumbersRoute = routes.ReporterNonUKTaxNumbersController.onPageLoad(0, NormalMode, index).url

  "ReporterNonUKTaxNumbers Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val form = formProvider(reporterIndividualKey)
      retrieveUserAnswersData(emptyUserAnswers)
      val request        = FakeRequest(GET, reporterNonUKTaxNumbersRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "reporter/reporterNonUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(ReporterNonUKTaxNumbersPage, 0, taxReferenceNumbers)
        .success
        .value
        .set(ReporterTaxResidencyLoopPage, 0, IndexedSeq(LoopDetails(None, Some(selectedCountry), None, Some(taxReferenceNumbers), None, None)))
        .success
        .value

      val form = formProvider(reporterIndividualKey)

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, reporterNonUKTaxNumbersRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "firstTaxNumber"  -> taxNumber,
          "secondTaxNumber" -> "",
          "thirdTaxNumber"  -> ""
        )
      )
      val expectedJson = Json.obj(
        "form"    -> filledForm,
        "mode"    -> NormalMode,
        "country" -> "France",
        "index"   -> index
      )

      templateCaptor.getValue mustEqual "reporter/reporterNonUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)
      val request =
        FakeRequest(POST, reporterNonUKTaxNumbersRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", taxNumber), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

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
      val request        = FakeRequest(POST, reporterNonUKTaxNumbersRoute).withFormUrlEncodedBody(("value", ""))
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

      templateCaptor.getValue mustEqual "reporter/reporterNonUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return a Bad Request and errors when invalid data is submitted for reporter as Organisation" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val form = formProvider(reporterOrganisationKey)
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(ReporterOrganisationOrIndividualPage, 0, Organisation)
        .success
        .value
      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(POST, reporterNonUKTaxNumbersRoute).withFormUrlEncodedBody(("value", ""))
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

      templateCaptor.getValue mustEqual "reporter/reporterNonUKTaxNumbers.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      retrieveNoData()
      val request = FakeRequest(GET, reporterNonUKTaxNumbersRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      retrieveNoData()
      val request =
        FakeRequest(POST, reporterNonUKTaxNumbersRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
