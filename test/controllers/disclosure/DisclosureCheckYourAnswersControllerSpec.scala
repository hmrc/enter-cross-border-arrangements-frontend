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
import connectors.CrossBorderArrangementsConnector
import controllers.RowJsonReads
import models.disclosure.{DisclosureType, ReplaceOrDeleteADisclosure}
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.disclosure._
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import scala.concurrent.Future

class DisclosureCheckYourAnswersControllerSpec extends SpecBase with ControllerMockFixtures {

  lazy val disclosureCheckYourAnswersLoadRoute: String = controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad().url

  lazy val disclosureCheckYourAnswersContinueRoute: String = controllers.disclosure.routes.DisclosureCheckYourAnswersController.onContinue().url

  val mockCrossBorderArrangementsConnector: CrossBorderArrangementsConnector = mock[CrossBorderArrangementsConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CrossBorderArrangementsConnector].toInstance(mockCrossBorderArrangementsConnector))

  override def beforeEach: Unit = {
    reset(
      mockRenderer,
      mockCrossBorderArrangementsConnector
    )
    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))
  }

  def verifyList(userAnswers: UserAnswers)(assertFunction: Seq[Row] => Unit): Unit = {

    retrieveUserAnswersData(userAnswers)

    val request = FakeRequest(GET, disclosureCheckYourAnswersLoadRoute)

    val result = route(app, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json: JsObject = jsonCaptor.getValue
    import RowJsonReads._
    val list = (json \ "disclosureSummary").get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "disclosure/check-your-answers-disclosure.njk"
    assertFunction(list)
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

  private def assertTypeDac6new()(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Type of disclosure"))
    row.value.text mustBe Some(Literal("A new arrangement"))
    assertAction("/disclose-cross-border-arrangements/manual/disclosure/change-type")(row.actions.head)
  }

  private def assertTypeDac6add()(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Type of disclosure"))
    row.value.text mustBe Some(Literal("An addition to an existing arrangement"))
    assertAction("/disclose-cross-border-arrangements/manual/disclosure/change-type")(row.actions.head)
  }

  private def assertTypeDac6rep()(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Type of disclosure"))
    row.value.text mustBe Some(Literal("A replacement of an existing disclosure"))
    assertAction("/disclose-cross-border-arrangements/manual/disclosure/change-type")(row.actions.head)
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

  private def assertMarketableArrangement(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Is this a marketable arrangement?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction("/disclose-cross-border-arrangements/manual/disclosure/change-marketable")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return correct rows for a new arrangement" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6new)
        .success
        .value
        .setBase(DisclosureMarketablePage, false)
        .success
        .value
      verifyList(userAnswers) {
        list =>
          assertDisclosureName("My arrangement")(list.head)
          assertTypeDac6new()(list(1))
          assertMarketableArrangement(yesOrNo = false)(list(2))
          list.size mustBe 3
      }
    }

    "must return correct rows for an additional arrangement" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA20210101ABC123")
        .success
        .value
      verifyList(userAnswers) {
        list =>
          assertDisclosureName("My arrangement")(list.head)
          assertTypeDac6add()(list(1))
          assertArrangementID("GBA20210101ABC123", "/disclose-cross-border-arrangements/manual/disclosure/change-identify-arrangement")(list(2))
          list.size mustBe 3
      }
    }

    "must return correct rows for a replacement arrangement" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6rep)
        .success
        .value
        .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA20210101ABC123", "GBD20210101ABC123"))
        .success
        .value
      verifyList(userAnswers) {
        list =>
          assertDisclosureName("My arrangement")(list.head)
          assertTypeDac6rep()(list(1))
          assertArrangementID("GBA20210101ABC123", "/disclose-cross-border-arrangements/manual/disclosure/change-identify")(list(2))
          assertDisclosureID("GBD20210101ABC123")(list(3))
          list.size mustBe 4
      }
    }

    "must be able to build disclosure details from user answers and redirect to task list" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockCrossBorderArrangementsConnector.isMarketableArrangement(any())(any()))
        .thenReturn(Future.successful(false))

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .setBase(DisclosureNamePage, "My arrangement")
        .success
        .value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA20210101ABC123")
        .success
        .value

      retrieveUserAnswersData(userAnswers)

      val request = FakeRequest(POST, disclosureCheckYourAnswersContinueRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/your-disclosure-details/1"
    }

  }

}
