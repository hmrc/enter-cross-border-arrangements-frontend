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
import config.FrontendAppConfig
import forms.organisation.WhatAreTheTaxNumbersForUKOrganisationFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, NormalMode, TaxReferenceNumbers, UserAnswers}
import navigation.NavigatorForOrganisation
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage, WhatAreTheTaxNumbersForUKOrganisationPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class WhatAreTheTaxNumbersForUKOrganisationControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  val formProvider = new WhatAreTheTaxNumbersForUKOrganisationFormProvider()
  val form: Form[TaxReferenceNumbers] = formProvider()

  val utr: String = "1234567890"
  val index = 0
  val selectedCountry: Option[Country] = Some(Country("", "GB", "United Kingdom"))

  lazy val whatAreTheTaxNumbersForUKOrganisationRoute: String = controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(NormalMode, index).url

  "WhatAreTheTaxNumbersForUKOrganisation Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedUserAnswers = UserAnswers(userAnswersId).set(OrganisationNamePage, "Paper Org").success.value
      val application = applicationBuilder(userAnswers = Some(updatedUserAnswers)).build()
      val request = FakeRequest(GET, whatAreTheTaxNumbersForUKOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "organisationName" -> "Paper Org"
      )

      templateCaptor.getValue mustEqual "organisation/whatAreTheTaxNumbersForUKOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val taxReferenceNumbers = TaxReferenceNumbers(utr, None, None)

      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatAreTheTaxNumbersForUKOrganisationPage, taxReferenceNumbers)
        .success
        .value
        .set(OrganisationLoopPage, IndexedSeq(
          LoopDetails(None, selectedCountry, None,None, Some(true), Some(taxReferenceNumbers)))
        )
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, whatAreTheTaxNumbersForUKOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

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

      templateCaptor.getValue mustEqual "organisation/whatAreTheTaxNumbersForUKOrganisation.njk"
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

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForUKOrganisationRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", utr), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/organisation/tax-resident-countries-1"

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, whatAreTheTaxNumbersForUKOrganisationRoute)
        .withFormUrlEncodedBody(("value", utr))
      val boundForm = form.bind(Map("value" -> utr))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "organisation/whatAreTheTaxNumbersForUKOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, whatAreTheTaxNumbersForUKOrganisationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForUKOrganisationRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", utr), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}