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

package forms.disclosure

import forms.behaviours.StringFieldBehaviours
import models.Country
import org.mockito.MockitoSugar
import play.api.data.FormError
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class DisclosureIdentifyArrangementFormProviderSpec extends StringFieldBehaviours with MockitoSugar {

  implicit val hc: HeaderCarrier                  = HeaderCarrier()
  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val countriesSeq: Seq[Country]                  = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))

  val requiredKey = "disclosureIdentifyArrangement.error.required"
  val invalidKey  = "disclosureIdentifyArrangement.error.invalid"

  val form = new DisclosureIdentifyArrangementFormProvider()(countriesSeq)

  ".arrangementID" - {

    val fieldName = "arrangementID"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validArrangementID
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      invalidString = "ZZA20210101ABC123",
      error = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
