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

package models

import play.api.libs.json._

sealed trait ManualSubmissionValidationResult

object ManualSubmissionValidationResult {

  implicit val validationWrites = new Format[ManualSubmissionValidationResult] {

    override def reads(json: JsValue): JsResult[ManualSubmissionValidationResult] =
      json
        .validate[ManualSubmissionValidationSuccess]
        .orElse(
          json.validate[ManualSubmissionValidationFailure]
        )

    override def writes(o: ManualSubmissionValidationResult): JsValue = o match {
      case m @ ManualSubmissionValidationSuccess(_) => ManualSubmissionValidationSuccess.format.writes(m)
      case m @ ManualSubmissionValidationFailure(_) => ManualSubmissionValidationFailure.format.writes(m)
    }
  }
}

case class ManualSubmissionValidationSuccess(messageRefId: String) extends ManualSubmissionValidationResult

object ManualSubmissionValidationSuccess {
  implicit val format: OFormat[ManualSubmissionValidationSuccess] = Json.format[ManualSubmissionValidationSuccess]
}

case class ManualSubmissionValidationFailure(errors: Seq[String]) extends ManualSubmissionValidationResult

object ManualSubmissionValidationFailure {
  implicit val format: OFormat[ManualSubmissionValidationFailure] = Json.format[ManualSubmissionValidationFailure]
}
