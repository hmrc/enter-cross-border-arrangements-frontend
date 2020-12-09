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

package controllers

import base.SpecBase
import models.{Name, SelectType, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.individual._
import pages.organisation._
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage}
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import java.time.LocalDate
import scala.concurrent.Future

class AssociatedEnterpriseCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: String => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.organisation.routes.CheckYourAnswersOrganisationController.associatedEnterpriseCheckAnswers().url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    val summaryRows = (json \ "summaryRows").toString
    val countrySummary = (json \ "countrySummary").toString
    val isEnterpriseAffected = (json \ "isEnterpriseAffected").toString

    templateCaptor.getValue mustEqual "associatedEnterpriseCheckYourAnswers.njk"
    assertFunction(summaryRows + countrySummary + isEnterpriseAffected)

    application.stop()

    reset(
      mockRenderer
    )
  }


  "AssociatedEnterpriseCheckYourAnswers Controller" - {

    "must return rows for an associated enterprise who is an organisation" in {
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(AssociatedEnterpriseTypePage, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, "Name")
        .success.value
        .set(IsOrganisationAddressKnownPage, false)
        .success.value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success.value
        .set(EmailAddressForOrganisationPage, "email@email.com")
        .success.value
        .set(IsAssociatedEnterpriseAffectedPage, true).success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Organisation or individual","classes":"govuk-!-width-one-half"},"value":{"text":"Organisation"}""") mustBe true
        rows.contains("""{"key":{"text":"What is the name of the organisation?","classes":"govuk-!-width-one-half"},"value":{"text":"Name"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
        rows.contains("""{"key":{"text":"Are they affected by the arrangement?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
      }
    }

    "must return rows for an associated enterprise who is an individual" in {
      val dob = LocalDate.of(2020, 1, 1)

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(AssociatedEnterpriseTypePage, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, Name("First", "Last"))
        .success.value
        .set(IndividualDateOfBirthPage, dob)
        .success.value
        .set(IsIndividualPlaceOfBirthKnownPage, false)
        .success.value
        .set(EmailAddressQuestionForIndividualPage, true)
        .success.value
        .set(EmailAddressForIndividualPage, "email@email.com")
        .success.value
        .set(IsAssociatedEnterpriseAffectedPage, false).success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Name","classes":"govuk-!-width-one-half"},"value":{"text":"First Last"}""") mustBe true
        rows.contains("""{"key":{"text":"Date of birth","classes":"govuk-!-width-one-half"},"value":{"text":"1 January 2020"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know where they were born?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
        rows.contains("""{"key":{"text":"Are they affected by the arrangement?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
      }
    }
  }
}
