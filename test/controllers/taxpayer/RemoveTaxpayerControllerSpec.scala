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

package controllers.taxpayer

import base.SpecBase
import forms.taxpayer.RemoveTaxpayerFormProvider
import helpers.data.ValidUserAnswersForSubmission.{validIndividual, validOrganisation}
import matchers.JsonMatchers
import models.taxpayer.Taxpayer
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.taxpayer.TaxpayerLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class RemoveTaxpayerControllerSpec extends SpecBase with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new RemoveTaxpayerFormProvider()
  val form = formProvider()

  lazy val removeTaxpayerRoute = controllers.taxpayer.routes.RemoveTaxpayerController.onPageLoad(0, "itemId").url

  lazy val taxpayerLoop: IndexedSeq[Taxpayer] = IndexedSeq(
    Taxpayer("1", None, Some(validOrganisation))
    , Taxpayer("2", Some(validIndividual), None)
  )

  val userAnswers = UserAnswers(userAnswersId)
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
    .set(TaxpayerLoopPage, 0, taxpayerLoop).success.value

  val mockSessionRepository = mock[SessionRepository]

  override def afterEach() = {
    reset(mockSessionRepository)
  }

  "RemoveTaxpayer Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, removeTaxpayerRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> form,
        "id"     -> 0,
        "itemId" -> "itemId",
        "name"   -> "",
        "radios" -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "taxpayer/removeTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must delete the required taxpayer and redirect to the next page when 'Yes' is submitted" in {

      val userAnswersCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, controllers.taxpayer.routes.RemoveTaxpayerController.onSubmit(0, "1").url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(0).url

      userAnswersCaptor.getValue.get(TaxpayerLoopPage, 0).map { loop =>
        loop mustBe(taxpayerLoop.filterNot(_.taxpayerId == "1"))
      }

      application.stop()
    }

    "must not delete taxpayer and return to the update page when 'No' is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
        bind[SessionRepository].toInstance(mockSessionRepository)
      ).build()

      val request = FakeRequest(POST, removeTaxpayerRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(0).url

      verify(mockSessionRepository, times(0)).set(any())

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, removeTaxpayerRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"   -> boundForm,
        "id"     -> 0,
        "itemId" -> "itemId",
        "name"   -> "",
        "radios" -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "taxpayer/removeTaxpayer.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }
  }
}
