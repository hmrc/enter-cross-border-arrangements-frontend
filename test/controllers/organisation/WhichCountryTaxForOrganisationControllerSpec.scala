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
import forms.organisation.WhichCountryTaxForOrganisationFormProvider
import matchers.JsonMatchers
import models.{Country, LoopDetails, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage, WhichCountryTaxForOrganisationPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.Future

class WhichCountryTaxForOrganisationControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  val mockCountryFactory: CountryListFactory = mock[CountryListFactory]

  val formProvider = new WhichCountryTaxForOrganisationFormProvider()
  val countriesSeq: Seq[Country] = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))
  val form: Form[Country] = formProvider(countriesSeq)
  val selectedCountry: Country = Country("valid", "FR", "France")
  val index: Int = 0

  lazy val whichCountryTaxForOrganisationRoute: String = controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, NormalMode, index).url

  override def beforeEach: Unit = {
    reset(mockCountryFactory)
    super.beforeEach
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(
      bind[CountryListFactory].toInstance(mockCountryFactory)
    )

  "WhichCountryTaxForOrganisation Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(countriesSeq))

      val updatedUserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(OrganisationNamePage, 0, "Paper Org").success.value

      retrieveUserAnswersData(updatedUserAnswers)

      val request = FakeRequest(GET, whichCountryTaxForOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "organisation/whichCountryTaxForOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountryFactory.getCountryList()).thenReturn(Some(countriesSeq))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhichCountryTaxForOrganisationPage, 0, selectedCountry)
        .success.value
        .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(selectedCountry), None, None, None, None)))
        .success.value

      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, whichCountryTaxForOrganisationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("country" -> "FR"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "organisation/whichCountryTaxForOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCountryFactory.getCountryList()).thenReturn(Some(countriesSeq))

      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, whichCountryTaxForOrganisationRoute)
          .withFormUrlEncodedBody(("country", "FR"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/organisation/resident-country-tin-0/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      when(mockCountryFactory.getCountryList()).thenReturn(Some(countriesSeq))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request = FakeRequest(POST, whichCountryTaxForOrganisationRoute).withFormUrlEncodedBody(("country", ""))
      val boundForm = form.bind(Map("country" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "organisation/whichCountryTaxForOrganisation.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      retrieveNoData()

      val request = FakeRequest(GET, whichCountryTaxForOrganisationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      retrieveNoData()

      val request =
        FakeRequest(POST, whichCountryTaxForOrganisationRoute)
          .withFormUrlEncodedBody(("value", "FR"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
