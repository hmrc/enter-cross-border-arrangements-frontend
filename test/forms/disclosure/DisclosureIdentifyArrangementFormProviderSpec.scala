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

package forms.disclosure

import connectors.CrossBorderArrangementsConnector
import forms.behaviours.StringFieldBehaviours
import models.Country
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.FormError
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class DisclosureIdentifyArrangementFormProviderSpec extends StringFieldBehaviours with MockitoSugar {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val countriesSeq: Seq[Country] = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))
  val mockCrossBorderArrangementsConnector: CrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

  val requiredKey = "disclosureIdentifyArrangement.error.required"
  val lengthKey = "disclosureIdentifyArrangement.error.length"
  val maxLength = 20

  val form = new DisclosureIdentifyArrangementFormProvider()(countriesSeq, mockCrossBorderArrangementsConnector)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
