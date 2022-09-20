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

package controllers.intermediaries

import base.{ControllerMockFixtures, SpecBase}
import models.intermediaries.{Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Address, Country, IsExemptionKnown, LoopDetails, SelectType, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.intermediaries.{IntermediariesTypePage, IntermediaryLoopPage, IsExemptionKnownPage, WhatTypeofIntermediaryPage}
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, status, _}
import play.twirl.api.Html

import scala.concurrent.Future

class IntermediariesCheckYourAnswersControllerSpec extends SpecBase with ControllerMockFixtures {

  val address: Address = Address(Some(""), Some(""), Some(""), "Newcastle", Some("NE1"), Country("", "GB", "United Kingdom"))
  val email            = "email@email.com"
  val taxResidencies   = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))

  def buildUserAnswers(list: IndexedSeq[Intermediary]): UserAnswers = UserAnswers(userAnswersId)
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
    .success
    .value
    .set(IntermediaryLoopPage, 0, list)
    .success
    .value
    .set(IntermediariesTypePage, 0, SelectType.Organisation)
    .success
    .value
    .set(OrganisationNamePage, 0, "Intermediary Ltd")
    .success
    .value
    .set(WhatTypeofIntermediaryPage, 0, WhatTypeofIntermediary.IDoNotKnow)
    .success
    .value
    .set(IsExemptionKnownPage, 0, IsExemptionKnown.Unknown)
    .success
    .value
    .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("", "GB", "United Kingdom")), None, None, None, None)))
    .success
    .value

  val controller: IntermediariesCheckYourAnswersController = app.injector.instanceOf[IntermediariesCheckYourAnswersController]

  def organisation(name: String) = Organisation(name, Some(address), Some(email), taxResidencies)

  def buildIntermediary(id: String, name: String) =
    Intermediary(id, None, Some(organisation(name)), WhatTypeofIntermediary.IDoNotKnow, IsExemptionKnown.Unknown)

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: String => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    retrieveUserAnswersData(userAnswers)

    val request = FakeRequest(GET, controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None).url)

    val result = route(app, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json: JsObject = jsonCaptor.getValue
    val intermediary   = (json \ "intermediarySummary").toString
    val tinSummary     = (json \ "tinCountrySummary").toString
    val intermediary2  = (json \ "intermediarySummary2").toString

    templateCaptor.getValue mustEqual "intermediaries/intermediariesCheckYourAnswers.njk"
    assertFunction(intermediary + tinSummary + intermediary2)
    reset(
      mockRenderer
    )
  }

  "must ensure the correct updated loop list" - {
    "must have intermediary type" in {

      verifyList(buildUserAnswers(List(buildIntermediary("id", "inter")).toIndexedSeq)) {
        rows =>
          rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Organisation"}""") mustBe true
          rows.contains("""{"key":{"text":"What is the name of the organisation?","classes":"govuk-!-width-one-half"}""") mustBe true
          rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"}""") mustBe true
          rows.contains("""{"key":{"text":"Do you want to provide an email address?","classes":"govuk-!-width-one-half"}""") mustBe true
          rows.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"}""") mustBe true
          rows.contains(""""key":{"text":"Reporting exemption known?","classes":"govuk-!-width-one-half"""") mustBe true
      }

    }

    "must redirect to task page on successful submission" in {
      val application = applicationBuilder(userAnswers = Some(buildUserAnswers(List(buildIntermediary("id", "inter")).toIndexedSeq))).build()

      val request = FakeRequest(POST, controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onSubmit(0).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/intermediaries/update/0"
    }
  }

}
