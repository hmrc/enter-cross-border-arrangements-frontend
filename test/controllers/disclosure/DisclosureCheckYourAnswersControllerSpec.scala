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

import base.SpecBase
import controllers.RowJsonReads
import models.{UnsubmittedDisclosure, UserAnswers}
import models.disclosure.DisclosureType
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import scala.concurrent.Future

class DisclosureCheckYourAnswersControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val disclosureCheckYourAnswersLoadRoute: String     = controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad.url

  lazy val disclosureCheckYourAnswersContinueRoute: String = controllers.disclosure.routes.DisclosureCheckYourAnswersController.onPageLoad.url

  override def beforeEach: Unit = {
    reset(
      mockRenderer
    )
    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))
  }

  def verifyList(userAnswers: UserAnswers)(assertFunction: Seq[Row] => Unit): Unit = {

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, disclosureCheckYourAnswersLoadRoute)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    import RowJsonReads._
    val list = (json \ "disclosureSummary" ).get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "disclosure/check-your-answers-disclosure.njk"
    assertFunction(list)

    application.stop()
  }

  private def assertAction(href: String, text: Option[Any] = Some(Literal("Change")), visuallyHiddenText: Option[Any] = None)(action: Action): Unit = {
    action.href mustBe href
    action.text mustBe text
    action.visuallyHiddenText mustBe visuallyHiddenText
  }

  private def assertDisclosureName(name: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Disclosure name"))
    row.value.text mustBe Some(Literal(name))
    assertAction("/enter-cross-border-arrangements/disclosure/change-name")(row.actions.head)
  }

  private def assertTypeDac6new()(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Type of disclosure"))
    row.value.text mustBe Some(Literal("A new arrangement"))
    assertAction("/enter-cross-border-arrangements/disclosure/change-type")(row.actions.head)
  }

  private def assertTypeDac6add()(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Type of disclosure"))
    row.value.text mustBe Some(Literal("An addition to an existing arrangement"))
    assertAction("/enter-cross-border-arrangements/disclosure/change-type")(row.actions.head)
  }

  private def assertArrangementID(arrangementID: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Arrangement ID"))
    row.value.text mustBe Some(Literal(arrangementID))
    assertAction("/enter-cross-border-arrangements/disclosure/change-identify")(row.actions.head)
  }

  private def assertMarketableArrangement(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Is this a marketable arrangement?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction("/enter-cross-border-arrangements/disclosure/change-marketable")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return correct rows for a new arrangement" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .setBase(DisclosureNamePage,"My arrangement")
        .success.value
        .setBase(DisclosureTypePage, DisclosureType.Dac6new)
        .success
        .value
        .setBase(DisclosureMarketablePage, false)
        .success
        .value
      verifyList(userAnswers) { list =>
        assertDisclosureName("My arrangement")(list.head)
        assertTypeDac6new()(list(1))
        assertMarketableArrangement(yesOrNo = false)(list(2))
        list.size mustBe 3
      }
    }

    "must return correct rows for an additional arrangement" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .setBase(DisclosureNamePage, "My arrangement")
        .success.value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA20210101ABC123")
        .success
        .value
      verifyList(userAnswers) { list =>
        assertDisclosureName("My arrangement")(list.head)
        assertTypeDac6add()(list(1))
        assertArrangementID("GBA20210101ABC123")(list(2))
        list.size mustBe 3
      }
    }

    "must be able to build disclosure details from user answers and redirect to task list" ignore {
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .setBase(DisclosureNamePage, "My arrangement")
        .success.value
        .setBase(DisclosureTypePage, DisclosureType.Dac6add)
        .success
        .value
        .setBase(DisclosureIdentifyArrangementPage, "GBA20210101ABC123")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, disclosureCheckYourAnswersContinueRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/enter-cross-border-arrangements/manual/your-disclosure-details"

      application.stop()
    }

  }

}
