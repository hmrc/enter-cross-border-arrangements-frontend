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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import base.SpecBase
import models.{Address, Country, Name, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages._
import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import scala.concurrent.Future

class IndividualCheckYourAnswersControllerSpec extends SpecBase with BeforeAndAfterEach {

  val address: Address = Address(
      addressLine1 = Some("value 1")
    , addressLine2 = Some("value 2")
    , addressLine3 = Some("value 3")
    , city = "Newcastle upon Tyne"
    , postCode = Some("XX9 9XX")
    , country = Country("valid","GB","United Kingdom")
  )
  val name: Name = Name(
      firstName = "FirstName"
    , secondName  = "LastName"
  )
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

    val request = FakeRequest(GET, routes.IndividualCheckYourAnswersController.onPageLoad().url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    import RowJsonReads._
    val list = (json \ "list" ).get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "individual/check-your-answers.njk"
    assertFunction(list)

    application.stop()

  }

  def assertAction(href: String, text: Option[Any] = Some(Literal("Change")), visuallyHiddenText: Option[Any] = None)(action: Action): Unit = {
    action.href mustBe href
    action.text mustBe text
    action.visuallyHiddenText mustBe visuallyHiddenText
  }

  def assertName(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Name"))
    row.value.text mustBe Some(Literal("FirstName LastName"))
    assertAction("/enter-cross-border-arrangements/individual/change-name")(row.actions.head)
  }

  def assertDateOfBirth(birthDateAsString: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Date of birth"))
    row.value.text mustBe Some(Literal(birthDateAsString))
    assertAction("/enter-cross-border-arrangements/individual/change-date-of-birth"
      , text = Some(Literal("Change")))(row.actions.head)
  }

  def assertBirthPlaceKnown(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Do you know where they were born?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction(href = "/enter-cross-border-arrangements/individual/change-do-you-know-birthplace")(row.actions.head)
  }

  def assertBirthPlace(birthplace: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Birthplace"))
    row.value.text mustBe Some(Literal(birthplace))
    assertAction(href = "/enter-cross-border-arrangements/individual/change-birthplace")(row.actions.head)
  }

  def assertAddressKnown(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Do you know their address?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction(href = "/enter-cross-border-arrangements/individual/change-do-you-know-address")(row.actions.head)
  }

  def assertAddress(address: Address)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Address"))
    row.value.html.map { html =>
      html.value mustBe
        Html("""
               |        value 1<br>
               |        value 2<br>
               |        value 3<br>
               |        XX9 9XX<br>
               |        United Kingdom
               |     """.stripMargin)
    }
    assertAction(href = "/enter-cross-border-arrangements/individual/change-live-in-uk")(row.actions.head)
  }

  def assertEmailKnown(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Do you know their email address?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction(href = "/enter-cross-border-arrangements/individual/change-email-address")(row.actions.head)
  }

  def assertEmail(email: String)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Email address"))
    row.value.text mustBe Some(Literal(email))
    assertAction(href = "/enter-cross-border-arrangements/individual/change-what-is-email-address")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return name row" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(IndividualNamePage, name)
        .success.value
      verifyList(userAnswers) { list =>
        assertName(list.head)
        assertBirthPlaceKnown(yesOrNo = false)(list(1))
        assertAddressKnown(yesOrNo = false)(list(2))
        assertEmailKnown(yesOrNo = false)(list(3))
        list.size mustBe(4)
      }
    }

    "must return date of birth row" in {

      val dateOfBirth = LocalDate.now()
      val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(IndividualDateOfBirthPage, LocalDate.now())
        .success.value
      verifyList(userAnswers) { list =>
        assertDateOfBirth(dateFormatter.format(dateOfBirth))(list.head)
        assertBirthPlaceKnown(yesOrNo = false)(list(1))
        assertAddressKnown(yesOrNo = false)(list(2))
        assertEmailKnown(yesOrNo = false)(list(3))
        list.size mustBe(4)
      }
    }

    "must return birth place rows, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(IsIndividualPlaceOfBirthKnownPage, true)
        .success.value
        .set(IndividualPlaceOfBirthPage, "BIRTHPLACE")
        .success.value
      verifyList(userAnswers) { list =>
        assertBirthPlaceKnown(yesOrNo = true)(list.head)
        assertBirthPlace("BIRTHPLACE")(list(1))
        assertAddressKnown(yesOrNo = false)(list(2))
        assertEmailKnown(yesOrNo = false)(list(3))
        list.size mustBe(4)
      }
    }

    "must return address rows, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(IsIndividualAddressKnownPage, true)
        .success.value
        .set(IndividualAddressPage, address)
        .success.value
      verifyList(userAnswers) { list =>
        assertBirthPlaceKnown(yesOrNo = false)(list.head)
        assertAddressKnown(yesOrNo = true)(list(1))
        assertAddress(address)(list(2))
        assertEmailKnown(yesOrNo = false)(list(3))
        list.size mustBe(4)
      }
    }

    "must return e-mail rows, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(EmailAddressQuestionForIndividualPage, true)
        .success.value
        .set(EmailAddressForIndividualPage, "email@email.org")
        .success.value
      verifyList(userAnswers) { list =>
        assertBirthPlaceKnown(yesOrNo = false)(list.head)
        assertAddressKnown(yesOrNo = false)(list(1))
        assertEmailKnown(yesOrNo = true)(list(2))
        assertEmail("email@email.org")(list(3))
        list.size mustBe(4)
      }
    }

  }
}

