/*
 * Copyright 2020 HM Revenue & Customs
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

package forms.mappings

import java.time.LocalDate

import play.api.data.FieldMapping
import play.api.data.Forms.of
import models.Enumerable

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatter(errorKey))

  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric"): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey))

  protected def boolean(requiredKey: String = "error.required",
                        invalidKey: String = "error.boolean"): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey))

  protected def optionalText(): FieldMapping[Option[String]] =
    of(optionalStringFormatter)

  protected def enumerable[A](requiredKey: String = "error.required",
                              invalidKey: String = "error.invalid")(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(
                           invalidKey: String,
                           allRequiredKey: String,
                           twoRequiredKey: String,
                           requiredKey: String,
                           nonNumericKey: String,

                           args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, nonNumericKey, args))

  protected def validatedText(requiredKey: String, invalidKey: String, lengthKey: String, regex: String, maxLength: Int): FieldMapping[String] = {
    of(validatedTextFormatter(requiredKey, invalidKey, lengthKey, regex, maxLength))
  }

  protected def validatedTextMaxLength(requiredKey: String, lengthKey: String, maxLength: Int): FieldMapping[String] = {
    of(textMaxLengthFormatter(requiredKey, lengthKey, maxLength))
  }

  protected def validatedOptionalText(invalidKey: String, lengthKey: String, regex: String, length: Int): FieldMapping[Option[String]] = {
    of(validatedOptionalTextFormatter(invalidKey, lengthKey, regex, length))
  }

  protected def validatedOptionalTextMaxLength(lengthKey: String, length: Int): FieldMapping[Option[String]] = {
    of(validatedOptionalTextAndMaxLengthFormatter(lengthKey, length))
  }

  protected def requiredRegexOnlyText(requiredKey: String, invalidKey: String, regex: String): FieldMapping[String] = {
    of(requiredRegexOnly(requiredKey, invalidKey, regex))
  }
}
