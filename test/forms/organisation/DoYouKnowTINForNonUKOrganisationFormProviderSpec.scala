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

package forms.organisation

import forms.behaviours.BooleanFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.{Form, FormError}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DoYouKnowTINForNonUKOrganisationFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  val requiredMessage = "Select yes if you know the tax identification numbers for the organisation in United Kingdom"
  val invalidKey = "error.boolean"

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val defaultMessagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  val messages: Messages = defaultMessagesApi.preferred(request)

  val formProvider = new DoYouKnowTINForNonUKOrganisationFormProvider()
  val form: Form[Boolean] = formProvider("United Kingdom")(messages)

  ".confirm" - {

    val fieldName = "confirm"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredMessage)
    )
  }
}