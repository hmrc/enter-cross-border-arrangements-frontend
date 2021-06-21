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

import base.SpecBase
import forms.individual.WhatAreTheTaxNumbersForNonUKIndividualFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, Name, NormalMode, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import navigation.{FakeIndividualNavigator, NavigatorForIndividual}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.individual.{IndividualLoopPage, IndividualNamePage}
import pages.organisation.WhatAreTheTaxNumbersForNonUKOrganisationPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class WhatAreTheTaxNumbersForNonUKIndividualControllerSpec extends SpecBase with NunjucksSupport with JsonMatchers {

  val formProvider = new WhatAreTheTaxNumbersForNonUKIndividualFormProvider
  val form = formProvider()
  val index: Int = 0

  val taxNumber: String = "123ABC"
  val taxReferenceNumbers: TaxReferenceNumbers = TaxReferenceNumbers(taxNumber, None, None)
  val selectedCountry: Country = Country("valid", "FR", "France")

  lazy val whatAreTheTaxNumbersForNonUKIndividualRoute = controllers.individual.routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(0, NormalMode, index).url

  "WhatAreTheTaxNumbersForNonUKIndividual Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val updatedUserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualNamePage, 0, Name("firstName", "lastName")).success.value
      val application = applicationBuilder(userAnswers = Some(updatedUserAnswers)).build()
      val request = FakeRequest(GET, whatAreTheTaxNumbersForNonUKIndividualRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "individualName" -> "firstName lastName",
        "country" -> "the country",
        "index" -> index
      )

      templateCaptor.getValue mustEqual "individual/whatAreTheTaxNumbersForNonUKIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhatAreTheTaxNumbersForNonUKOrganisationPage, 0, taxReferenceNumbers)
        .success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(None, Some(selectedCountry), None, Some(taxReferenceNumbers), None, None)))
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, whatAreTheTaxNumbersForNonUKIndividualRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

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
        "individualName" -> "the individual",
        "country" -> "France",
        "index" -> index
      )

      templateCaptor.getValue mustEqual "individual/whatAreTheTaxNumbersForNonUKIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[NavigatorForIndividual].toInstance(FakeIndividualNavigator()),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForNonUKIndividualRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", taxNumber), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/individual/tax-resident-countries-1/0"

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, whatAreTheTaxNumbersForNonUKIndividualRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "individual/whatAreTheTaxNumbersForNonUKIndividual.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, whatAreTheTaxNumbersForNonUKIndividualRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, whatAreTheTaxNumbersForNonUKIndividualRoute)
          .withFormUrlEncodedBody(("firstTaxNumber", taxNumber), ("secondTaxNumber", ""), ("thirdTaxNumber", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
