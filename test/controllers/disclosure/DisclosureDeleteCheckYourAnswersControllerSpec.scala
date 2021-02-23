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
import models.disclosure.{DisclosureType, ReplaceOrDeleteADisclosure}
import models.{UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.disclosure.{DisclosureNamePage, DisclosureTypePage, ReplaceOrDeleteADisclosurePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import scala.concurrent.Future

class DisclosureDeleteCheckYourAnswersControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val disclosureCheckYourAnswersLoadRoute: String     = controllers.disclosure.routes.DisclosureDeleteCheckYourAnswersController.onPageLoad().url

  lazy val disclosureCheckYourAnswersContinueRoute: String = controllers.disclosure.routes.DisclosureDeleteCheckYourAnswersController.onPageLoad().url

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

    templateCaptor.getValue mustEqual "disclosure/check-your-answers-delete-disclosure.njk"
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
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .setBase(DisclosureNamePage, "My arrangement")
        .success.value
        .setBase(DisclosureTypePage, DisclosureType.Dac6del)
        .success
        .value
        .setBase(ReplaceOrDeleteADisclosurePage, ReplaceOrDeleteADisclosure("GBA20210101ABC123", "GBD20210101ABC123"))
        .success
        .value
      verifyList(userAnswers) { list =>
        assertDisclosureName("My arrangement")(list.head)
        assertArrangementID("GBA20210101ABC123",
          "/disclose-cross-border-arrangements/manual/disclosure/change-identify")(list(1))
        assertDisclosureID("GBD20210101ABC123")(list(2))
        list.size mustBe 3
      }
    }

  }

}
