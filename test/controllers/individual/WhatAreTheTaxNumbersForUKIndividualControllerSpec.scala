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

package controllers.individual

import base.{ControllerMockFixtures, SpecBase}
import forms.individual.WhatAreTheTaxNumbersForUKIndividualFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, Name, NormalMode, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.individual.{IndividualLoopPage, IndividualNamePage, WhatAreTheTaxNumbersForUKIndividualPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.Form
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class WhatAreTheTaxNumbersForUKIndividualControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider = new WhatAreTheTaxNumbersForUKIndividualFormProvider()
  val form: Form[TaxReferenceNumbers] = formProvider()

  val utr: String = "1234567890"
  val index = 0
  val selectedCountry: Option[Country] = Some(Country("", "GB", "United Kingdom"))


  lazy val whatAreTheTaxNumbersForUKIndividualRoute: String = controllers.individual.routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(0, NormalMode, index).url

  "WhatAreTheTaxNumbersForUKIndividual Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedUserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualNamePage, 0, Name("First", "Last")).success.value

      retrieveUserAnswersData(updatedUserAnswers)
      val request = FakeRequest(GET, whatAreTheTaxNumbersForUKIndividualRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "name" -> "First Last’s"
      )

      templateCaptor.getValue mustEqual "individual/whatAreTheTaxNumbersForUKIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val taxReferenceNumbers = TaxReferenceNumbers(utr, None, None)

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhatAreTheTaxNumbersForUKIndividualPage, 0, taxReferenceNumbers).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(
          LoopDetails(None, selectedCountry, None,None, Some(true), Some(taxReferenceNumbers)))
        )
        .success
        .value
      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, whatAreTheTaxNumbersForUKIndividualRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "firstTaxNumber" -> utr,
          "secondTaxNumber" -> "",
          "thirdTaxNumber" -> ""
        ))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode,
        "index" -> index
      )

      templateCaptor.getValue mustEqual "individual/whatAreTheTaxNumbersForUKIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      retrieveUserAnswersData(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForUKIndividualRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", utr), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/individual/tax-resident-countries-1/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      retrieveUserAnswersData(emptyUserAnswers)
      val request = FakeRequest(POST, whatAreTheTaxNumbersForUKIndividualRoute)
        .withFormUrlEncodedBody(("value", utr))
      val boundForm = form.bind(Map("value" -> utr))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "individual/whatAreTheTaxNumbersForUKIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, whatAreTheTaxNumbersForUKIndividualRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForUKIndividualRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", utr), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}