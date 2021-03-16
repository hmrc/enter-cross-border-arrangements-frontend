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

package controllers.enterprises

import base.SpecBase
import models.enterprises.{AssociatedEnterprise, YouHaveNotAddedAnyAssociatedEnterprises}
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Address, Country, LoopDetails, Name, SelectType, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage, _}
import pages.individual._
import pages.organisation._
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository

import java.time.LocalDate
import scala.concurrent.Future

class AssociatedEnterpriseCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val onwardRoute: Call = Call("GET", "/disclose-cross-border-arrangements/manual/associated-enterprises/update/0")

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: String => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0).url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    val summaryRows = (json \ "summaryRows").toString
    val countrySummary = (json \ "countrySummary").toString
    val isEnterpriseAffected = (json \ "isEnterpriseAffected").toString

    templateCaptor.getValue mustEqual "enterprises/associatedEnterpriseCheckYourAnswers.njk"
    assertFunction(summaryRows + countrySummary + isEnterpriseAffected)

    application.stop()

    reset(
      mockRenderer
    )
  }


  "AssociatedEnterpriseCheckYourAnswers Controller" - {

    "must return rows for an associated enterprise who is an organisation" in {
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Taxpayer One"))
        .success.value
        .set(AssociatedEnterpriseTypePage, 0, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, 0, "Name")
        .success.value
        .set(IsOrganisationAddressKnownPage, 0, false)
        .success.value
        .set(EmailAddressQuestionForOrganisationPage, 0, true)
        .success.value
        .set(EmailAddressForOrganisationPage, 0, "email@email.com")
        .success.value
        .set(IsAssociatedEnterpriseAffectedPage, 0, true).success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Taxpayers associated with","classes":"govuk-!-width-one-half"},"value":{"html":"Taxpayer One"}""") mustBe true
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
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Taxpayer One"))
        .success.value
        .set(AssociatedEnterpriseTypePage, 0, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, 0, Name("First", "Last"))
        .success.value
        .set(IsIndividualDateOfBirthKnownPage, 0, true)
        .success.value
        .set(IndividualDateOfBirthPage, 0, dob)
        .success.value
        .set(IsIndividualPlaceOfBirthKnownPage, 0, false)
        .success.value
        .set(EmailAddressQuestionForIndividualPage, 0, true)
        .success.value
        .set(EmailAddressForIndividualPage, 0, "email@email.com")
        .success.value
        .set(IsAssociatedEnterpriseAffectedPage, 0, false).success.value

      verifyList(userAnswers) { rows =>
        rows.contains("""{"key":{"text":"Taxpayers associated with","classes":"govuk-!-width-one-half"},"value":{"html":"Taxpayer One"}""") mustBe true
        rows.contains("""{"key":{"text":"Name","classes":"govuk-!-width-one-half"},"value":{"text":"First Last"}""") mustBe true
        rows.contains("""{"key":{"text":"Date of birth","classes":"govuk-!-width-one-half"},"value":{"text":"1 January 2020"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know where they were born?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their address?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
        rows.contains("""{"key":{"text":"Do you know their email address?","classes":"govuk-!-width-one-half"},"value":{"text":"Yes"}""") mustBe true
        rows.contains("""{"key":{"text":"Email address","classes":"govuk-!-width-one-half"},"value":{"text":"email@email.com"}""") mustBe true
        rows.contains("""{"key":{"text":"Are they affected by the arrangement?","classes":"govuk-!-width-one-half"},"value":{"text":"No"}""") mustBe true
      }
    }

    "must redirect to the associated enterprise update page when valid data is submitted for an organisation" in {
      val checkYourAnswersRoute: String = controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onSubmit(0).url
      val loopDetails = IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, 0, YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)
        .success.value
        .set(AssociatedEnterpriseTypePage, 0, SelectType.Organisation)
        .success.value
        .set(OrganisationNamePage, 0, "Organisation name")
        .success.value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Associated taxpayer"))
        .success.value
        .set(IsAssociatedEnterpriseAffectedPage, 0, false)
        .success.value
        .set(OrganisationLoopPage, 0, loopDetails)
        .success.value

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request = FakeRequest(POST, checkYourAnswersRoute).withFormUrlEncodedBody(("", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must redirect to the associated enterprise update page when valid data is submitted for an individual" in {
      val checkYourAnswersRoute: String = controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onSubmit(0).url
      val loopDetails = IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, 0, YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)
        .success.value
        .set(AssociatedEnterpriseTypePage, 0, SelectType.Individual)
        .success.value
        .set(IndividualNamePage, 0, Name("Name", "Name"))
        .success.value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Associated taxpayer"))
        .success.value
        .set(IsAssociatedEnterpriseAffectedPage, 0, false)
        .success.value
        .set(IndividualLoopPage, 0, loopDetails)
        .success.value

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request = FakeRequest(POST, checkYourAnswersRoute).withFormUrlEncodedBody(("", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must ensure the correct updated loop list" - {

      val address: Address = Address(Some(""), Some(""), Some(""), "Newcastle", Some("NE1"), Country("", "GB", "United Kingdom"))
      val email = "email@email.com"
      val taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))

      def buildUserAnswers(list: IndexedSeq[AssociatedEnterprise]): UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(AssociatedEnterpriseTypePage, 0, SelectType.Organisation).success.value
        .set(OrganisationNamePage, 0, "Associated Ltd").success.value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Associated taxpayer")).success.value
        .set(IsAssociatedEnterpriseAffectedPage, 0, false).success.value
        .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None))).success.value
        .set(AssociatedEnterpriseLoopPage, 0, list).success.value

      def organisation(name: String) = Organisation(name, Some(address), Some(email), taxResidencies)

      val controller: AssociatedEnterpriseCheckYourAnswersController = injector.instanceOf[AssociatedEnterpriseCheckYourAnswersController]

      "if ids are not duplicated" in {

        val list: IndexedSeq[AssociatedEnterprise] = IndexedSeq(
          AssociatedEnterprise("ID1", None, Some(organisation("First Ltd")), List(), false), AssociatedEnterprise("ID2", None, Some(organisation("Second Ltd")), List(), false)
        )

        controller.updatedLoopList(buildUserAnswers(list), 0).map(_.nameAsString) must contain theSameElementsAs(list).map(_.nameAsString) :+ "Associated Ltd"
      }

      "if ids are duplicated" in {

        val list: IndexedSeq[AssociatedEnterprise] = IndexedSeq(
          AssociatedEnterprise("ID1", None, Some(organisation("Associated Ltd")), List(), false), AssociatedEnterprise("ID2", None, Some(organisation("Other")), List(), false)
        )

        controller.updatedLoopList(buildUserAnswers(list), 0).map(_.nameAsString) must contain theSameElementsAs(list).map(_.nameAsString)
      }
    }

  }
}
