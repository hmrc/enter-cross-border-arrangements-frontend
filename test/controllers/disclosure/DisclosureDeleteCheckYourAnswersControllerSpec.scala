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

package controllers.disclosure

import base.{ControllerMockFixtures, SpecBase}
import config.FrontendAppConfig
import connectors.{CrossBorderArrangementsConnector, EmailConnector, SubscriptionConnector}
import controllers.RowJsonReads
import controllers.exceptions.DiscloseDetailsAlreadyDeletedException
import models.disclosure.{DisclosureType, ReplaceOrDeleteADisclosure}
import models.{Country, Currency, GeneratedIDs, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.disclosure.{DisclosureNamePage, DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.{EmailService, XMLGenerationService}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal
import utils.{CountryListFactory, CurrencyListFactory}

import scala.concurrent.Future

class DisclosureDeleteCheckYourAnswersControllerSpec extends SpecBase with ControllerMockFixtures {

  lazy val disclosureCheckYourAnswersLoadRoute: String     = controllers.disclosure.routes.DisclosureDeleteCheckYourAnswersController.onPageLoad().url
  lazy val disclosureCheckYourAnswersContinueRoute: String = controllers.disclosure.routes.DisclosureDeleteCheckYourAnswersController.onPageLoad().url

  val mockEmailConnector: EmailConnector                                     = mock[EmailConnector]
  val mockEmailService: EmailService                                         = mock[EmailService]
  val mockCurrencyList                                                       = mock[CurrencyListFactory]
  val mockCountryFactory: CountryListFactory                                 = mock[CountryListFactory]
  val mockCrossBorderArrangementsConnector: CrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]
  val mockSubscriptionConnector: SubscriptionConnector                       = mock[SubscriptionConnector]
  val countriesSeq: Seq[Country]                                             = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))
  val mockXMLGenerationService: XMLGenerationService                         = mock[XMLGenerationService]

  override def beforeEach: Unit = {
    when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
    when(mockSubscriptionConnector.displaySubscriptionDetails(any())(any(), any())).thenReturn(Future.successful(None))
    when(mockEmailService.sendEmail(any(), any(), any(), any())(any()))
      .thenReturn(Future.successful(Some(HttpResponse(ACCEPTED, ""))))
    when(mockCurrencyList.getCurrencyList).thenReturn(Some(Seq(Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)"))))
    when(mockCountryFactory.getCountryList()).thenReturn(Some(countriesSeq))

    reset(mockEmailService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(
      bind[EmailService].toInstance(mockEmailService),
      bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector),
      bind[SubscriptionConnector].toInstance(mockSubscriptionConnector),
      bind[FrontendAppConfig].toInstance(mockAppConfig),
      bind[CountryListFactory].toInstance(mockCountryFactory),
      bind[CurrencyListFactory].toInstance(mockCurrencyList)
    )

  def verifyList(userAnswers: UserAnswers)(assertFunction: Seq[Row] => Unit): Unit = {
    retrieveUserAnswersData(userAnswers)
    val request = FakeRequest(GET, disclosureCheckYourAnswersLoadRoute)

    val result = route(app, request).value

    status(result) mustEqual OK

    val templateCaptor                       = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor: ArgumentCaptor[JsObject] = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json: JsObject = jsonCaptor.getValue
    import RowJsonReads._
    val list = (json \ "disclosureSummary").get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "disclosure/check-your-answers-delete-disclosure.njk"
    assertFunction(list)

    verify(mockEmailService, times(0)).sendEmail(any(), any(), any(), any())(any())

  }

  private def assertAction(href: String, text: Option[Any] = Some(Literal("Change")), visuallyHiddenText: Option[Any] = None)(action: Action): Unit = {
    action.href mustBe href
    action.text mustBe text
    action.visuallyHiddenText mustBe visuallyHiddenText
  }

  private def assertDisclosureName(name: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Disclosure name"))
    row.value.text mustBe Some(Literal(name))
    assertAction("/disclose-cross-border-arrangements/manual/disclosure/change-name")(row.actions.head)
  }

  private def assertArrangementID(arrangementID: String, href: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Arrangement ID"))
    row.value.text mustBe Some(Literal(arrangementID))
    assertAction(href)(row.actions.head)
  }

  private def assertDisclosureID(disclosureID: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Disclosure ID"))
    row.value.text mustBe Some(Literal(disclosureID))
    assertAction("/disclose-cross-border-arrangements/manual/disclosure/change-identify")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return correct rows for a deletion" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6del)
        .success
        .value
        .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA20210101ABC123", "GBD20210101ABC123"))
        .success
        .value
      verifyList(userAnswers) {
        list =>
          assertDisclosureName("My arrangement")(list.head)
          assertArrangementID("GBA20210101ABC123", "/disclose-cross-border-arrangements/manual/disclosure/change-identify")(list(1))
          assertDisclosureID("GBD20210101ABC123")(list(2))
          list.size mustBe 3
      }
    }

    "must redirect to your disclosure has been deleted" in {
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6del)
        .success
        .value
        .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA20210101ABC123", "GBD20210101ABC123"))
        .success
        .value

      when(mockXMLGenerationService.createAndValidateXmlSubmission(any())(any(), any()))
        .thenReturn(Future.successful(Right(GeneratedIDs(None, Some("disclosureID"), Some("messageRefID")))))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector),
          bind[SubscriptionConnector].toInstance(mockSubscriptionConnector),
          bind[FrontendAppConfig].toInstance(mockAppConfig),
          bind[CountryListFactory].toInstance(mockCountryFactory),
          bind[CurrencyListFactory].toInstance(mockCurrencyList),
          bind[XMLGenerationService].toInstance(mockXMLGenerationService)
        )
        .build()

      val request = FakeRequest(POST, disclosureCheckYourAnswersContinueRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/disclosure/disclosure-has-been-deleted"
    }

    "fail with DiscloseDetailsAlreadyDeletedException if ReplaceOrDeleteADisclosurePage is empty " in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6del)
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(GET, disclosureCheckYourAnswersLoadRoute)

      val result = route(app, request).value

      an[DiscloseDetailsAlreadyDeletedException] mustBe thrownBy {
        status(result) mustEqual OK
      }
    }
  }

}
