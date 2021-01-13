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

import base.SpecBase
import connectors.{CrossBorderArrangementsConnector, ValidationConnector}
import helpers.Submissions
import models.{GeneratedIDs, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, route, status, _}
import play.twirl.api.Html
import services.XMLGenerationService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class TaskListControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with BeforeAndAfterEach {
  val mockValidationConnector = mock[ValidationConnector]
  val mockXMLGenerationService = mock[XMLGenerationService]
  val mockCrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

  override def beforeEach(): Unit = {
    reset(mockCrossBorderArrangementsConnector)
  }

  "TaskListController" - {
    "must redirect to confirmation page when user submits a completed application" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId)))
        .overrides(
      bind[XMLGenerationService].toInstance(mockXMLGenerationService),
      bind[ValidationConnector].toInstance(mockValidationConnector),
      bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector)
      )
        .build()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockXMLGenerationService.createXmlSubmission(any()))
        .thenReturn(Submissions.validSubmission)
      when(mockValidationConnector.sendForValidation(any())(any(), any()))
        .thenReturn(Future.successful(Right("GBABC-123")))
      when(mockCrossBorderArrangementsConnector.submitXML(any())(any()))
        .thenReturn(Future.successful(GeneratedIDs(None, None)))

      val postRequest = FakeRequest(POST, routes.TaskListController.onSubmit.url)

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(controllers.confirmation.routes.FileTypeGatewayController.onRouting().url)

      verify(mockCrossBorderArrangementsConnector, times(1)).submitXML(any())(any())
    }

    "must redirect to validation errors page when validation fails" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId)))
        .overrides(
          bind[XMLGenerationService].toInstance(mockXMLGenerationService),
          bind[ValidationConnector].toInstance(mockValidationConnector)
        )
        .build()

      when(mockXMLGenerationService.createXmlSubmission(any()))
        .thenReturn(Submissions.validSubmission)
      when(mockValidationConnector.sendForValidation(any())(any(), any()))
        .thenReturn(Future.successful(Left(Seq("key1", "key2"))))

      val postRequest = FakeRequest(POST, routes.TaskListController.onSubmit.url)

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(controllers.confirmation.routes.DisclosureValidationErrorsController.onPageLoad().url)
    }
  }

}
