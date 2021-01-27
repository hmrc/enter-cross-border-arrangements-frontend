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
import models.disclosure.ReplaceOrDeleteADisclosure
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.{Form, FormError}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ReplaceOrDeleteADisclosureFormProviderSpec extends StringFieldBehaviours  with GuiceOneAppPerSuite {

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val countries = List(Country("valid","GB","United Kingdom"))
  val formProvider = new ReplaceOrDeleteADisclosureFormProvider()
  val form: Form[ReplaceOrDeleteADisclosure] = formProvider(countries)

  ".arrangementID" - {

    val fieldName = "arrangementID"
    val requiredKey = "replaceOrDeleteADisclosure.error.arrangementID.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validArrangementID
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".disclosureID" - {

    val fieldName = "disclosureID"
    val requiredKey = "replaceOrDeleteADisclosure.error.disclosureID.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDisclosureID
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
