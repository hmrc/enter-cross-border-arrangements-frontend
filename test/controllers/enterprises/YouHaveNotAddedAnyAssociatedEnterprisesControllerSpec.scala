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

package controllers.enterprises

import base.{ControllerMockFixtures, SpecBase}
import forms.enterprises.YouHaveNotAddedAnyAssociatedEnterprisesFormProvider
import matchers.JsonMatchers
import models.enterprises.{AssociatedEnterprise, YouHaveNotAddedAnyAssociatedEnterprises}
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Country, NormalMode, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.enterprises.{AssociatedEnterpriseLoopPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class YouHaveNotAddedAnyAssociatedEnterprisesControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  lazy private val youHaveNotAddedAnyAssociatedEnterprisesRoute = routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(0, NormalMode).url

  private val formProvider = new YouHaveNotAddedAnyAssociatedEnterprisesFormProvider()
  private val form = formProvider()

  "YouHaveNotAddedAnyAssociatedEnterprises Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request = FakeRequest(GET, youHaveNotAddedAnyAssociatedEnterprisesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "mode"   -> NormalMode,
        "radios" -> YouHaveNotAddedAnyAssociatedEnterprises.radios(form),
        "associatedEnterpriseList" -> Json.arr()
      )

      templateCaptor.getValue mustEqual "enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view with a list of enterprises correctly on a GET when the question has previously been answered" in {

      val organisation = Organisation(
        organisationName = "Organisation Ltd.",
        address = None,
        emailAddress = None,
        taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), None))
      )

      val enterpriseLoop = IndexedSeq(
        AssociatedEnterprise("id", None, Some(organisation), List("Associated Enterprise"), isAffectedBy = false))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(AssociatedEnterpriseLoopPage, 0, enterpriseLoop).success.value
        .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, 0, YouHaveNotAddedAnyAssociatedEnterprises.values.head).success.value

      retrieveUserAnswersData(userAnswers)
      val request = FakeRequest(GET, youHaveNotAddedAnyAssociatedEnterprisesRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])
      val controller = app.injector.instanceOf[YouHaveNotAddedAnyAssociatedEnterprisesController]

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> YouHaveNotAddedAnyAssociatedEnterprises.values.head.toString))

      val expectedList = Json.toJson(controller.toItemList(userAnswers, 0))

      val expectedJson = Json.obj(
        "form"   -> filledForm,
        "mode"   -> NormalMode,
        "radios" -> YouHaveNotAddedAnyAssociatedEnterprises.radios(filledForm),
        "associatedEnterpriseList" -> expectedList
      )

      templateCaptor.getValue mustEqual "enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      retrieveUserAnswersData(emptyUserAnswers)

      val request =
        FakeRequest(POST, youHaveNotAddedAnyAssociatedEnterprisesRoute)
          .withFormUrlEncodedBody(("value", YouHaveNotAddedAnyAssociatedEnterprises.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/associated-enterprises/taxpayers/0"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      retrieveUserAnswersData(emptyUserAnswers)
      val request = FakeRequest(POST, youHaveNotAddedAnyAssociatedEnterprisesRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "mode"   -> NormalMode,
        "radios" -> YouHaveNotAddedAnyAssociatedEnterprises.radios(boundForm)
      )

      templateCaptor.getValue mustEqual "enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

  }
}
