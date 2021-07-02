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
import forms.organisation.WhatAreTheTaxNumbersForNonUKOrganisationFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, NormalMode, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage, WhatAreTheTaxNumbersForNonUKOrganisationPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class WhatAreTheTaxNumbersForNonUKOrganisationControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val formProvider = new WhatAreTheTaxNumbersForNonUKOrganisationFormProvider()
  val form = formProvider()
  val index: Int = 0

  val taxNumber: String = "123ABC"
  val taxReferenceNumbers: TaxReferenceNumbers = TaxReferenceNumbers(taxNumber, None, None)
  val selectedCountry: Country = Country("valid", "FR", "France")

  lazy val whatAreTheTaxNumbersForNonUKOrganisationRoute = controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(0, NormalMode, index).url

  "WhatAreTheTaxNumbersForNonUKOrganisation Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedUserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(OrganisationNamePage, 0, "Paper Org").success.value

      retrieveUserAnswersData(updatedUserAnswers)

      val request = FakeRequest(GET, whatAreTheTaxNumbersForNonUKOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "organisationName" -> "Paper Org",
        "country" -> "the country",
        "index" -> index
      )

      templateCaptor.getValue mustEqual "organisation/whatAreTheTaxNumbersForNonUKOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhatAreTheTaxNumbersForNonUKOrganisationPage, 0, taxReferenceNumbers)
        .success.value
        .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(selectedCountry), None, Some(taxReferenceNumbers), None, None)))
        .success.value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, whatAreTheTaxNumbersForNonUKOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map(
        "firstTaxNumber" -> taxNumber,
        "secondTaxNumber" -> "",
        "thirdTaxNumber" -> ""
      ))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode,
        "organisationName" -> "the organisation",
        "country" -> "France",
        "index" -> index
      )

      templateCaptor.getValue mustEqual "organisation/whatAreTheTaxNumbersForNonUKOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForNonUKOrganisationRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", taxNumber), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/tax-resident-countries-1/0"
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, whatAreTheTaxNumbersForNonUKOrganisationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForNonUKOrganisationRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", taxNumber), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
