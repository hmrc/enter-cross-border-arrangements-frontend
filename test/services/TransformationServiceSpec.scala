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

package services

import base.SpecBase
import helpers.Submissions

class TransformationServiceSpec extends SpecBase {

  "TransformationService" - {
    "must take a valid file and replace the messageRefID" in {
      val service = app.injector.instanceOf[TransformationService]
      val transformedFile = service.rewriteMessageRefID(
        Submissions.validSubmission,
        "GB0000000YYY"
      )
      transformedFile mustBe Submissions.updatedSubmission
    }
  }
}
