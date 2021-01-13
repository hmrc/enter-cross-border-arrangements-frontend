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

package models

import base.SpecBase
import play.api.libs.json.{JsSuccess, Json}

class ManualSubmissionResultSpec extends SpecBase {

  "ManualSubmissionResult" - {
    "must be able to read ManualSubmissionValidationSuccess from the trait" in {
      val jsonPayload =
        """
          |{
          | "messageRefId": "message"
          |}""".stripMargin

      Json.parse(jsonPayload).validate[ManualSubmissionValidationResult] mustBe
        JsSuccess(ManualSubmissionValidationSuccess("message"))
    }

    "must be able to read ManualSubmissionValidationFailure from the trait" in {
      val jsonPayload =
        """
          |{
          | "errors": ["message1", "message2"]
          |}""".stripMargin

      Json.parse(jsonPayload).validate[ManualSubmissionValidationResult] mustBe
        JsSuccess(ManualSubmissionValidationFailure(Seq("message1", "message2")))
    }
  }

}
