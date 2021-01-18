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

package controllers.organisation

import base.SpecBase
import controllers.RowJsonReads
import models.{Address, Country, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.organisation._
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import scala.concurrent.Future

class OrganisationCheckYourAnswersControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val checkYourAnswersOrganisationRoute: String = controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0).url

  val address: Address = Address(
    addressLine1 = Some("value 1")
    , addressLine2 = Some("value 2")
    , addressLine3 = Some("value 3")
    , city = "Newcastle upon Tyne"
    , postCode = Some("XX9 9XX")
    , country = Country("valid","GB","United Kingdom")
  )
  val name: String = "Organisation"

  val email: String = "email@email.com"

  override def beforeEach: Unit = {
    reset(
      mockRenderer
    )
    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

  }

  def verifyList(userAnswers: UserAnswers)(assertFunction: Seq[Row] => Unit): Unit = {

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0).url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    import RowJsonReads._
    val list = (json \ "organisationSummary" ).get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "organisation/check-your-answers-organisation.njk"
    assertFunction(list)

    application.stop()

  }

  def assertAction(href: String, text: Option[Any] = Some(Literal("Change")), visuallyHiddenText: Option[Any] = None)(action: Action): Unit = {
    action.href mustBe href
    action.text mustBe text
    action.visuallyHiddenText mustBe visuallyHiddenText
  }

  def assertName(row: Row): Unit = {
    row.key.text mustBe Some(Literal("What is the name of the organisation?"))
    row.value.text mustBe Some(Literal("Organisation"))
    assertAction("/enter-cross-border-arrangements/organisation/change-name/0")(row.actions.head)
  }

  def assertAddressKnown(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Do you know their address?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction(href = "/enter-cross-border-arrangements/organisation/change-do-you-know-address/0")(row.actions.head)
  }

  def assertAddress(address: Address)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("What is the organisation’s main address?"))
    row.value.html.map { html =>
      html.value mustBe
        Html("""
               |        value 1<br>
               |        value 2<br>
               |        value 3<br>
               |        Newcastle upon Tyne<br>
               |        XX9 9XX<br>
               |        United Kingdom
               |     """.stripMargin)
    }
    assertAction(href = "/enter-cross-border-arrangements/organisation/change-main-address-in-uk/0")(row.actions.head)
  }

  def assertEmailKnown(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Do you know their email address?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction(href = "/enter-cross-border-arrangements/organisation/change-email-address/0")(row.actions.head)
  }

  def assertEmail(email: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Email address"))
    row.value.text mustBe Some(Literal(email))
    assertAction(href = "/enter-cross-border-arrangements/organisation/change-what-is-email-address/0")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return name row" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(OrganisationNamePage, 0, name)
        .success.value
      verifyList(userAnswers) { list =>
        assertName(list.head)
        assertAddressKnown(yesOrNo = false)(list(1))
        assertEmailKnown(yesOrNo = false)(list(2))
        list.size mustBe (3)
      }
    }

    "must return address rows, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IsOrganisationAddressKnownPage, 0, true)
        .success.value
        .set(OrganisationAddressPage, 0, address)
        .success.value
      verifyList(userAnswers) { list =>
        assertAddressKnown(yesOrNo = true)(list.head)
        assertAddress(address)(list(1))
        assertEmailKnown(yesOrNo = false)(list(2))
        list.size mustBe (3)
      }
    }

    "must return e-mail rows, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(EmailAddressQuestionForOrganisationPage, 0, true)
        .success.value
        .set(EmailAddressForOrganisationPage, 0, "email@email.org")
        .success.value
      verifyList(userAnswers) { list =>
        assertAddressKnown(yesOrNo = false)(list.head)
        assertEmailKnown(yesOrNo = true)(list(1))
        assertEmail("email@email.org")(list(2))
        list.size mustBe (3)
      }
    }
  }
}
