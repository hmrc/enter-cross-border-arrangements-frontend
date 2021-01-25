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
import matchers.JsonMatchers.containJson
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ValidationErrorsPage
import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class DisclosureValidationErrorsControllerSpec extends SpecBase with MockitoSugar {

  val errors = Seq("Error 1", "Error 2")
  lazy val disclosureValidationErrorsRoute = controllers.confirmation.routes.DisclosureValidationErrorsController.onPageLoad(0).url

  "DisclosureValidationErrors Controller" - {

    "map keys to errors" in {

      val controller = injector.instanceOf[DisclosureValidationErrorsController]

      val keysToErrors = Map[String, Option[String]](
        "businessrules.initialDisclosure.needRelevantTaxPayer" ->
          Some("""As this arrangement is not marketable, it must have at least one relevant taxpayer.
            |If you are a relevant taxpayer, confirm this in your reporter’s details.
            |If you are not, add at least one relevant taxpayer.""".stripMargin)
        , "businessrules.initialDisclosureMA.missingRelevantTaxPayerDates" ->
          Some("""As this arrangement is marketable, all relevant taxpayers disclosed must have implementing dates.""")
        , "businessrules.initialDisclosureMA.firstDisclosureHasInitialDisclosureMAAsTrue" ->
          Some("""As this arrangement is marketable, all relevant taxpayers disclosed must have implementing dates."""  )
        , "any.other.key" -> None
      )

      keysToErrors.keys.foreach { key =>
        controller.keyMapper(key) must be (keysToErrors.get(key).flatten)
      }
    }

    "map keys to table rows" in {

      val controller = injector.instanceOf[DisclosureValidationErrorsController]

      val rows: Seq[String] = controller.toTableRows(errors, Option(_)).flatten.map(_.toString)

      rows must contain("""{"text":"Relevant taxpayers or reporter’s details","classes":"govuk-table__cell","attributes":{"id":"lineNumber_0"}}""")
      rows must contain("""{"html":"Error 1","classes":"govuk-table__cell","attributes":{"id":"errorMessage_0"}}""")
      rows must contain("""{"text":"Relevant taxpayers or reporter’s details","classes":"govuk-table__cell","attributes":{"id":"lineNumber_1"}}""")
      rows must contain("""{"html":"Error 2","classes":"govuk-table__cell","attributes":{"id":"errorMessage_1"}}""")
    }

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(ValidationErrorsPage, 0, errors)
        .success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, disclosureValidationErrorsRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "errorRows" -> injector.instanceOf[DisclosureValidationErrorsController].toTableRows(errors)
      )

      templateCaptor.getValue mustEqual "confirmation/validationErrors.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }
  }
}
