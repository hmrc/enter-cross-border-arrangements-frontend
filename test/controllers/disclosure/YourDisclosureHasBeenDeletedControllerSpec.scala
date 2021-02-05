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

package controllers.disclosure

import base.SpecBase
import connectors.SubscriptionConnector
import helpers.JsonFixtures.displaySubscriptionPayloadNoSecondary
import matchers.JsonMatchers.containJson
import models.UserAnswers
import models.subscription.DisplaySubscriptionForDACResponse
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.disclosure.DeletedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class YourDisclosureHasBeenDeletedControllerSpec extends SpecBase with MockitoSugar {

  val arrangementID = "GBA20210101ABC123"
  val disclosureID = "GBD20210101ABC123"
  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      DeletedDisclosurePage.toString -> Json.obj(
        "arrangementID" -> arrangementID,
        "disclosureID" -> disclosureID
      )
    )
  )

  val mockSubscriptionConnector = mock[SubscriptionConnector]

  val jsonPayload: String = displaySubscriptionPayloadNoSecondary(
    JsString("id"), JsString("FirstName"), JsString("LastName"), JsString("test@test.com"), JsString("0191 111 2222"))
  val displaySubscriptionDetails: DisplaySubscriptionForDACResponse = Json.parse(jsonPayload).as[DisplaySubscriptionForDACResponse]


  "YourDisclosureHasBeenDeleted Controller" - {

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))


      when(mockSubscriptionConnector.displaySubscriptionDetails(any())(any(), any()))
        .thenReturn(Future.successful(Some(displaySubscriptionDetails)))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SubscriptionConnector].toInstance(mockSubscriptionConnector)).build()

      val request = FakeRequest(GET, routes.YourDisclosureHasBeenDeletedController.onPageLoad().url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      val expectedJson = Json.obj(
        "arrangementID" -> "GBA20210101ABC123",
        "disclosureID" -> "GBD20210101ABC123",
        "messageRefid" -> "messageID"
      )

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "disclosure/yourDisclosureHasBeenDeleted.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "return Internal Server Error if arrangement details are not in user answers" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))


      when(mockSubscriptionConnector.displaySubscriptionDetails(any())(any(), any()))
        .thenReturn(Future.successful(Some(displaySubscriptionDetails)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SubscriptionConnector].toInstance(mockSubscriptionConnector)).build()

      val request = FakeRequest(GET, routes.YourDisclosureHasBeenDeletedController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }
  }
}
