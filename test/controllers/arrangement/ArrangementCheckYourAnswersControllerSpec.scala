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

package controllers.arrangement

import base.SpecBase
import controllers.RowJsonReads
import generators.ModelGenerators
import models.UserAnswers
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.arrangement._
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}
import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.Future

class ArrangementCheckYourAnswersControllerSpec extends SpecBase with BeforeAndAfterEach with ModelGenerators {

  val oneHundredCharacters: String = "123456789 " * 10
  val textTuples = Seq(
    (oneHundredCharacters, oneHundredCharacters)
    , (s"$oneHundredCharacters longer than 100 chars", s"$oneHundredCharacters...")
  )

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: Seq[Row] => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad().url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json = jsonCaptor.getValue
    import RowJsonReads._
    val list = (json \ "list" ).get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "arrangement/check-your-answers-arrangement.njk"
    assertFunction(list)

    application.stop()

    reset(
      mockRenderer
    )
  }

  def assertAction(href: String, text: Option[Any] = Some(Literal("Change")), visuallyHiddenText: Option[Any] = None)(action: Action): Unit = {
    action.href mustBe href
    action.text mustBe text
    action.visuallyHiddenText mustBe visuallyHiddenText
  }

  def assertReasonToReportKnown(yesOrNo: Boolean)(row: Row): Unit = {
    row.key.text mustBe Some(Literal("Reason for reporting known?"))
    row.value.text mustBe Some(Literal(if (yesOrNo) "Yes" else "No"))
    assertAction(href = "/enter-cross-border-arrangements/arrangement/change-reporting-reason-known")(row.actions.head)
  }

  def assertReasonToReport(reasonAsString: String)(row: Row): Unit = {
    import uk.gov.hmrc.viewmodels._
    val msg: String = msg"whyAreYouReportingThisArrangementNow.$reasonAsString".resolve
    row.key.text mustBe Some(Literal("Reason for reporting"))
    row.value.text mustBe Some(Literal(msg))
    assertAction(href = "/enter-cross-border-arrangements/arrangement/change-reporting-reason")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return name row" in {

      def assertName(name: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Name of arrangement"))
        row.value.text mustBe Some(Literal(name))
        assertAction("/enter-cross-border-arrangements/arrangement/change-name")(row.actions.head)
      }

      textTuples.foreach { case (given, expected) =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(WhatIsThisArrangementCalledPage, given)
          .success.value
        verifyList(userAnswers) { list =>
          assertName(expected)(list.head)
          assertReasonToReportKnown(yesOrNo = false)(list(1))
          list.size mustBe(2)
        }
      }
    }

    "must return implementing date row" in {

      def assertImplementationDate(implementationDateAsString: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Implementing date"))
        row.value.text mustBe Some(Literal(implementationDateAsString))
        assertAction("/enter-cross-border-arrangements/arrangement/change-implementation-date"
          , text = Some(Literal("Change")))(row.actions.head)
      }

      val implementationDate = LocalDate.now()
      val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTheImplementationDatePage, LocalDate.now())
        .success.value
      verifyList(userAnswers) { list =>
        assertImplementationDate(dateFormatter.format(implementationDate))(list.head)
        assertReasonToReportKnown(yesOrNo = false)(list(1))
        list.size mustBe(2)
      }
    }

    "must return reason to report rows, if known" in {

      WhyAreYouReportingThisArrangementNow.values.map { reportReason =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(DoYouKnowTheReasonToReportArrangementNowPage, true)
          .success.value
          .set(WhyAreYouReportingThisArrangementNowPage, reportReason)
          .success.value
        verifyList(userAnswers) { list =>
          assertReasonToReportKnown(yesOrNo = true)(list.head)
          assertReasonToReport(reportReason.toString)(list(1))
          list.size mustBe(2)
        }
      }
    }

    "must return one country row without bullets" in {

      val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
        Seq(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom).toSet

      def assertCountries(html: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Countries involved"))
        row.value.html.map { html =>
          html.value mustBe Html("United Kingdom")
        }
        assertAction("/enter-cross-border-arrangements/arrangement/change-choose-countries-involved"
          , text = Some(Literal("Change")))(row.actions.head)
      }

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries)
        .success.value
      verifyList(userAnswers) { list =>
        assertReasonToReportKnown(yesOrNo = false)(list.head)
        assertCountries("United Kingdom")(list(1))
        list.size mustBe(2)
      }
    }

    "must return multiple country rows with bullets" in {

      val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
        Set(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom
        , WhichExpectedInvolvedCountriesArrangement.Sweden)

      def assertCountries(html: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Countries involved"))
        row.value.html.map { html =>
          html.value mustBe Html(
            """<ul>
              |<li>United Kingdom</li>
              |<li>Sweden</li>
              |</ul>""".stripMargin)
        }
        assertAction("/enter-cross-border-arrangements/arrangement/change-choose-countries-involved"
          , text = Some(Literal("Change")))(row.actions.head)
      }

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries)
        .success.value
      verifyList(userAnswers) { list =>
        assertReasonToReportKnown(yesOrNo = false)(list.head)
        assertCountries("<ul></ul>")(list(1))
        list.size mustBe(2)
      }
    }

    "must return the expected value " in {

      val expectedValue: WhatIsTheExpectedValueOfThisArrangement = WhatIsTheExpectedValueOfThisArrangement(
        currency = "CURRENCY"
        , amount = Int.MaxValue
      )

      def assertExpectedValue(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Total value"))
        row.value.html.map { html =>
          html.value mustBe Html("CURRENCY 1")
        }
        assertAction(href = "/enter-cross-border-arrangements/arrangement/change-value")(row.actions.head)
      }

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTheExpectedValueOfThisArrangementPage, expectedValue)
        .success.value
      verifyList(userAnswers) { list =>
        assertReasonToReportKnown(yesOrNo = false)(list.head)
        assertExpectedValue(list(1))
        list.size mustBe(2)
      }
    }

    "must return the national provisions " in {

      def assertNationalProvisions(content: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("National provisions"))
        row.value.text mustBe Some(Literal(content))
        assertAction(href = "/enter-cross-border-arrangements/arrangement/change-national-provisions")(row.actions.head)
      }

      textTuples.foreach { case (given, expected) =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, given)
          .success.value
        verifyList(userAnswers) { list =>
          assertReasonToReportKnown(yesOrNo = false)(list.head)
          assertNationalProvisions(expected)(list(1))
          list.size mustBe(2)
        }
      }
    }

    "must return the details " in {

      def assertNationalProvisions(content: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Description"))
        row.value.text mustBe Some(Literal(content))
        assertAction(href = "/enter-cross-border-arrangements/arrangement/change-details")(row.actions.head)
      }

      textTuples.foreach { case (given, expected) =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(GiveDetailsOfThisArrangementPage, given)
          .success.value
        verifyList(userAnswers) { list =>
          assertReasonToReportKnown(yesOrNo = false)(list.head)
          assertNationalProvisions(expected)(list(1))
          list.size mustBe(2)
        }
      }
    }

  }
}

