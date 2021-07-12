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
import controllers.actions.{ContactRetrievalAction, FakeContactRetrievalAction}
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.subscription.ContactDetails
import models.{GeneratedIDs, UnsubmittedDisclosure, UserAnswers}
import org.mockito.Mockito
import pages.GeneratedIDPage
import pages.disclosure.DisclosureDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FileTypeGatewayControllerSpec extends SpecBase with ControllerMockFixtures {

  val mockContactRetrievalAction: ContactRetrievalAction = mock[ContactRetrievalAction]

  override def beforeEach {
    Mockito.reset(mockContactRetrievalAction)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[ContactRetrievalAction].toInstance(mockContactRetrievalAction))

  "FileTypeGateway Controller" - {

    "redirect to NEW disclosure received when disclosure type is NEW" in {

      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        disclosureType = DisclosureType.Dac6new,
        initialDisclosureMA = true
      )

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success
        .value
        .set(GeneratedIDPage, 0, GeneratedIDs(Some("arrangementID"), Some("disclosureID"), Some("messageRefID")))
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val fakeDataRetrieval =
        new FakeContactRetrievalAction(userAnswers,
                                       Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com")))
        )

      when(mockContactRetrievalAction.apply).thenReturn(fakeDataRetrieval)

      val request = FakeRequest(GET, routes.FileTypeGatewayController.onRouting(0).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/disclosure-received/0"
    }

    "redirect to ADDED disclosure received when disclosure type is ADDED" in {
      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        disclosureType = DisclosureType.Dac6add,
        initialDisclosureMA = true
      )

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success
        .value
        .set(GeneratedIDPage, 0, GeneratedIDs(None, Some("disclosureID"), Some("messageRefID")))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val fakeDataRetrieval =
        new FakeContactRetrievalAction(userAnswers,
                                       Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com")))
        )

      when(mockContactRetrievalAction.apply).thenReturn(fakeDataRetrieval)
      val request = FakeRequest(GET, routes.FileTypeGatewayController.onRouting(0).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/addition-received/0"
    }

    "redirect to REPLACEMENT disclosure received when disclosure type is Dac6rep" in {
      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        disclosureType = DisclosureType.Dac6rep,
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID")
      )

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("0", "My First")))
        .success
        .value
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success
        .value
        .set(GeneratedIDPage, 0, GeneratedIDs(None, None, Some("messageRefID")))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val fakeDataRetrieval =
        new FakeContactRetrievalAction(userAnswers,
                                       Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com")))
        )

      when(mockContactRetrievalAction.apply).thenReturn(fakeDataRetrieval)

      val request = FakeRequest(GET, routes.FileTypeGatewayController.onRouting(0).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/replacement-received/0"
    }
  }
}
