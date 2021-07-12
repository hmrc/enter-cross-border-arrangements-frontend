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

sealed trait SubmissionError {

  def errorKey: String
  def defaultMessage: String
}

case object DisclosureNameEmptyError extends SubmissionError {

  def errorKey: String       = "submission.disclosure.name.missing"
  def defaultMessage: String = "A disclosure must have a name"
}

case object DisclosureImportInstructionInvalidError extends SubmissionError {

  def errorKey: String       = "submission.disclosure.import.instruction.invalid"
  def defaultMessage: String = "A submission must have a valid import instruction"
}

case object DisclosureInitialMarketableArrangementInvalidError extends SubmissionError {

  def errorKey: String       = "submission.disclosure.marketable.arrangement.invalid"
  def defaultMessage: String = "A submission must have a marketable arrangement flag when not new"
}

case object ArrangementDetailsNotDefinedError extends SubmissionError {

  def errorKey: String       = "submission.arrangement.details.not.defined"
  def defaultMessage: String = "A submission must have arrangement details if not deleting"
}

case object ArrangementNameEmptyError extends SubmissionError {

  def errorKey: String       = "submission.arrangement.name.empty"
  def defaultMessage: String = "A submission must have a non-empty arrangement name"
}

case object ArrangementImplementingDateInvalidError extends SubmissionError {

  def errorKey: String       = "submission.arrangement.date.empty"
  def defaultMessage: String = "A submission must have a valid arrangement implementing date"
}

case object HallmarkDetailsNotDefinedError extends SubmissionError {

  def errorKey: String       = "submission.halmmark.details.not.defined"
  def defaultMessage: String = "A submission must have hallmark details if not deleting"
}

case object HallmarkDMissingError extends SubmissionError {

  def errorKey: String       = "submission.hallmark.d.missing"
  def defaultMessage: String = "A submission must have at least one hallmark D"
}
