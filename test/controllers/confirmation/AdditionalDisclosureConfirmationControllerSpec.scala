/*
 * Copyright 2022 HM Revenue & Customs
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
import org.mockito.ArgumentMatchers.any
import pages.disclosure.DisclosureDetailsPage
import org.mockito.{ArgumentCaptor, Mockito}
import pages.unsubmitted.UnsubmittedDisclosurePage
import pages.{GeneratedIDPage, MessageRefIDPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class AdditionalDisclosureConfirmationControllerSpec extends SpecBase with ControllerMockFixtures {

  val mockContactRetrievalAction: ContactRetrievalAction = mock[ContactRetrievalAction]

  override def beforeEach {
    Mockito.reset(mockContactRetrievalAction)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[ContactRetrievalAction].toInstance(mockContactRetrievalAction))

  "AdditionalDisclosureConfirmation Controller" - {

    val disclosureDetails = DisclosureDetails(
      disclosureName = "",
      arrangementID = Some("arrangement"),
      disclosureType = DisclosureType.Dac6new,
      initialDisclosureMA = true
    )

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(GeneratedIDPage, 0, GeneratedIDs(Some(""), Some(""), Some("")))
        .success
        .value
        .set(MessageRefIDPage, 0, "")
        .success
        .value
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success
        .value

      val fakeDataRetrieval =
        new FakeContactRetrievalAction(userAnswers,
                                       Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com")))
        )

      retrieveUserAnswersData(userAnswers)
      when(mockContactRetrievalAction.apply).thenReturn(fakeDataRetrieval)

      val request        = FakeRequest(GET, routes.AdditionalDisclosureConfirmationController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

      templateCaptor.getValue mustEqual "confirmation/disclosureConfirmation.njk"
    }

    "throw an error then display technical error page if no MessageRefID is present" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(GeneratedIDPage, 0, GeneratedIDs(Some(""), Some("")))
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request        = FakeRequest(GET, routes.AdditionalDisclosureConfirmationController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, request).value

      an[Exception] mustBe thrownBy {
        status(result) mustEqual OK

        verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

        templateCaptor.getValue mustEqual "internalServerError.njk"
      }
    }

    "throw an error then display technical error page if no disclosureID is present" in {
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(GeneratedIDPage, 0, GeneratedIDs(None, None))
        .success
        .value
        .set(MessageRefIDPage, 0, "")
        .success
        .value

      retrieveUserAnswersData(userAnswers)
      val request        = FakeRequest(GET, routes.AdditionalDisclosureConfirmationController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, request).value

      an[Exception] mustBe thrownBy {
        status(result) mustEqual OK

        verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

        templateCaptor.getValue mustEqual "internalServerError.njk"
      }
    }
  }
}
