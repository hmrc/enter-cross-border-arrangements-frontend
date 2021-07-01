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

package controllers

import base.{ControllerMockFixtures, SpecBase}
import connectors.{CrossBorderArrangementsConnector, HistoryConnector, ValidationConnector}
import controllers.actions.{ContactRetrievalAction, FakeContactRetrievalProvider}
import helpers.data.ValidUserAnswersForSubmission.{userAnswersForOrganisation, userAnswersModelsForOrganisation}
import matchers.JsonMatchers
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.requests.DataRequest
import models.subscription.ContactDetails
import models.{GeneratedIDs, Submission, SubmissionDetails, SubmissionHistory, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.disclosure.DisclosureDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, AnyContentAsEmpty}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.XMLGenerationService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import java.time.LocalDateTime
import scala.concurrent.Future

class DisclosureDetailsControllerSpec extends SpecBase with ControllerMockFixtures with NunjucksSupport with JsonMatchers {

  private val mockValidationConnector = mock[ValidationConnector]
  private val mockXMLGenerationService = mock[XMLGenerationService]
  private val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]
  private val mockHistoryConnector = mock[HistoryConnector]

  override def beforeEach: Unit = {
    reset(mockCrossBorderArrangementsConnector,
      mockXMLGenerationService,
      mockValidationConnector,
      mockHistoryConnector)
    super.beforeEach()
  }

  val fakeDataRetrieval = new FakeContactRetrievalProvider(userAnswersForOrganisation,
    Some(ContactDetails(Some("Test Testing"), Some("test@test.com"), Some("Test Testing"), Some("test@test.com"))))

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super.guiceApplicationBuilder()
      .overrides(bind[XMLGenerationService].toInstance(mockXMLGenerationService),
        bind[ValidationConnector].toInstance(mockValidationConnector),
        bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector),
        bind[HistoryConnector].toInstance(mockHistoryConnector),
        bind[ContactRetrievalAction].toInstance(fakeDataRetrieval))

  "DisclosureDetails Controller" - {

    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val firstDisclosureSubmissionDetails = SubmissionDetails("id", LocalDateTime.now(), "test.xml",
        Some("arrangementID"), Some("disclosureID"), "New", initialDisclosureMA = true, "messageRefID")

      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        arrangementID = Some("arrangement"),
        disclosureType = DisclosureType.Dac6new,
        initialDisclosureMA = true
      )

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosureSubmissionDetails))

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"), UnsubmittedDisclosure("2", "The Revenge"))).success.value
        .set(DisclosureDetailsPage, 1, disclosureDetails)
        .success.value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.DisclosureDetailsController.onPageLoad(1).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())

      templateCaptor.getValue mustEqual "disclosure/disclosureDetails.njk"
    }

    "return OK and the correct view for a GET if it's a replacement disclosure" in {

      val firstDisclosureSubmissionDetails = SubmissionDetails("id", LocalDateTime.now(), "test.xml",
        Some("arrangementID"), Some("disclosureID"), "New", initialDisclosureMA = true, "messageRefID")

      val submissionHistory = SubmissionHistory(Seq(firstDisclosureSubmissionDetails.copy(importInstruction = "Add", initialDisclosureMA = false)))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockHistoryConnector.retrieveFirstDisclosureForArrangementID(any())(any()))
        .thenReturn(Future.successful(firstDisclosureSubmissionDetails))

      when(mockHistoryConnector.searchDisclosures(any())(any()))
        .thenReturn(Future.successful(submissionHistory))

      val disclosureDetails = DisclosureDetails(
        disclosureName = "",
        arrangementID = Some("arrangementID"),
        disclosureID = Some("disclosureID"),
        disclosureType = DisclosureType.Dac6rep
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("0", "My replacement"))).success.value
        .set(DisclosureDetailsPage, 0, disclosureDetails)
        .success.value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, routes.DisclosureDetailsController.onPageLoad(0).url)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())
      verify(mockHistoryConnector, times(1)).retrieveFirstDisclosureForArrangementID(any())(any())
      verify(mockHistoryConnector, times(1)).searchDisclosures(any())(any())

      templateCaptor.getValue mustEqual "disclosure/disclosureDetails.njk"
    }

    "must redirect to confirmation page when user submits a completed application" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      retrieveUserAnswersData(userAnswersForOrganisation)
      val submission = Submission(userAnswersForOrganisation, 0, "XADAC0001122345")
      val generatedIDs = GeneratedIDs(Some("XADAC0001122345"), Some("XADAC0001122345"))

      val postRequest = FakeRequest(POST, routes.DisclosureDetailsController.onSubmit(0).url)
      implicit val request: DataRequest[AnyContent] =
        DataRequest[AnyContent](fakeRequest, "internalID", "XADAC0001122345", userAnswersForOrganisation)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockXMLGenerationService.createAndValidateXmlSubmission(any())(any(), any()))
        .thenReturn(Future.successful(Right(generatedIDs)))
      when(mockCrossBorderArrangementsConnector.submitXML(any())(any()))
        .thenReturn(Future.successful(GeneratedIDs(None, None)))


      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.confirmation.routes.FileTypeGatewayController.onRouting(0).url

    }

    "must update the submitted flag" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val controller = injector.instanceOf[DisclosureDetailsController]
      val list = List(
        UnsubmittedDisclosure("0", "name_0"), UnsubmittedDisclosure("1", "name_1")
      )
      val expected = List(
        UnsubmittedDisclosure("0", "name_0"), UnsubmittedDisclosure("1", "name_1", submitted = true)
      )
      for {
        userAnswers    <- UserAnswers(userAnswersId).setBase(UnsubmittedDisclosurePage, list)
        updatedAnswers <- controller.updateFlags(userAnswers, 1)
        updatedList    <- updatedAnswers.getBase(UnsubmittedDisclosurePage)
      } updatedList       must contain theSameElementsAs expected
    }

    "must redirect to validation errors page when validation fails" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      retrieveUserAnswersData(userAnswersModelsForOrganisation)

      implicit val postRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, routes.DisclosureDetailsController.onSubmit(0).url)
      implicit val request: DataRequest[AnyContent] =
        DataRequest[AnyContent](fakeRequest, "internalID", "XADAC0001122345", userAnswersModelsForOrganisation)

      when(mockXMLGenerationService.createAndValidateXmlSubmission(any())(any(), any()))
        .thenReturn(Future.successful(Left(Seq("key1", "key2"))))

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.confirmation.routes.DisclosureValidationErrorsController.onPageLoad(0).url
    }
  }

}
