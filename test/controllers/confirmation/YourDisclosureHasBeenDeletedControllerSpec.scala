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

package controllers.confirmation

import base.{ControllerMockFixtures, SpecBase}
import connectors.SubscriptionConnector
import controllers.actions.{ContactRetrievalAction, FakeContactRetrievalAction}
import helpers.JsonFixtures.displaySubscriptionPayloadNoSecondary
import matchers.JsonMatchers.containJson
import models.subscription.{ContactDetails, DisplaySubscriptionForDACResponse}
import models.{GeneratedIDs, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.disclosure.DisclosureDeleteCheckYourAnswersPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class YourDisclosureHasBeenDeletedControllerSpec extends SpecBase with ControllerMockFixtures {

  val arrangementID = "GBA20210101ABC123"
  val disclosureID = "GBD20210101ABC123"
  val messageRefID = "GBXADAC0001122345Name"
  val generatedIDs = GeneratedIDs(Some(arrangementID), Some(disclosureID), Some(messageRefID))
  val mockContactRetrievalAction: ContactRetrievalAction = mock[ContactRetrievalAction]
  val userAnswers = UserAnswers(userAnswersId)
    .setBase(DisclosureDeleteCheckYourAnswersPage, generatedIDs).success.value

  val mockSubscriptionConnector: SubscriptionConnector = mock[SubscriptionConnector]

  override def beforeEach: Unit = {
    reset(mockContactRetrievalAction, mockSubscriptionConnector)
    super.beforeEach
  }
  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super.guiceApplicationBuilder()
    .overrides(bind[SubscriptionConnector].toInstance(mockSubscriptionConnector),
    bind[ContactRetrievalAction].toInstance(mockContactRetrievalAction))

  val jsonPayload: String = displaySubscriptionPayloadNoSecondary(
    JsString("id"), JsString("FirstName"), JsString("LastName"), JsString("test@test.com"), JsString("0191 111 2222"))
  val displaySubscriptionDetails: DisplaySubscriptionForDACResponse = Json.parse(jsonPayload).as[DisplaySubscriptionForDACResponse]

  "YourDisclosureHasBeenDeleted Controller" - {

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockSubscriptionConnector.displaySubscriptionDetails(any())(any(), any()))
        .thenReturn(Future.successful(Some(displaySubscriptionDetails)))

      val fakeDataRetrieval = new FakeContactRetrievalAction(userAnswers, Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com"))))
      when(mockContactRetrievalAction.apply).thenReturn(fakeDataRetrieval)
      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.YourDisclosureHasBeenDeletedController.onPageLoad().url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      val expectedJson = Json.obj(
        "arrangementID" -> arrangementID,
        "disclosureID"         -> disclosureID,
        "messageRefid"         -> messageRefID
      )

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual "confirmation/yourDisclosureHasBeenDeleted.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "return Internal Server Error if arrangement details are not in user answers" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockSubscriptionConnector.displaySubscriptionDetails(any())(any(), any()))
        .thenReturn(Future.successful(Some(displaySubscriptionDetails)))
      val fakeDataRetrieval = new FakeContactRetrievalAction(emptyUserAnswers, Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com"))))
      when(mockContactRetrievalAction.apply).thenReturn(fakeDataRetrieval)

      retrieveUserAnswersData(emptyUserAnswers)
      val request = FakeRequest(GET, routes.YourDisclosureHasBeenDeletedController.onPageLoad().url)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }
  }
}
