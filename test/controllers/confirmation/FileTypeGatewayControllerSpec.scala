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

import base.SpecBase
import models.UserAnswers
import models.disclosure.DisclosureType
import org.scalatestplus.mockito.MockitoSugar
import pages.disclosure.DisclosureTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FileTypeGatewayControllerSpec extends SpecBase with MockitoSugar {

  "FileTypeGateway Controller" - {

    "redirect to NEW disclosure received when disclosure type is NEW" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(DisclosureTypePage, DisclosureType.Dac6new)
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.FileTypeGatewayController.onRouting().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/manual/disclosure-received"

      application.stop()
    }

    "redirect to ADDED disclosure received when disclosure type is ADDED" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(DisclosureTypePage, DisclosureType.Dac6add)
        .success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.FileTypeGatewayController.onRouting().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/manual/addition-received"

      application.stop()
    }
  }
}
