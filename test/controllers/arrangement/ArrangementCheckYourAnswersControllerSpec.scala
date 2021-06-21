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

package controllers.arrangement

import base.SpecBase
import controllers.RowJsonReads
import generators.ModelGenerators
import models.arrangement.ExpectedArrangementValue
import models.{CountryList, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.arrangement._
import pages.unsubmitted.UnsubmittedDisclosurePage
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

    val request = FakeRequest(GET, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0).url)

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

  def assertReasonToReport(reasonAsString: String)(row: Row): Unit = {
    import uk.gov.hmrc.viewmodels._
    val msg: String = msg"whyAreYouReportingThisArrangementNow.$reasonAsString".resolve
    row.key.text mustBe Some(Literal("Reason for reporting"))
    row.value.text mustBe Some(Literal(msg))
    assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-reporting-reason/0")(row.actions.head)
  }

  "Check Your Answers Controller" - {

    "must return name row" in {

      def assertName(name: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Name of arrangement"))
        row.value.text mustBe Some(Literal(name))
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-name/0")(row.actions.head)
      }

      textTuples.foreach { case (given, expected) =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(WhatIsThisArrangementCalledPage,0, given)
          .success.value
        verifyList(userAnswers) { list =>
          assertName(expected)(list.head)
          list.size mustBe(1)
        }
      }
    }

    "must return implementing date row" in {

      def assertImplementationDate(implementationDateAsString: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Implementing date"))
        row.value.text mustBe Some(Literal(implementationDateAsString))
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-implementation-date/0"
          , text = Some(Literal("Change")))(row.actions.head)
      }

      val implementationDate = LocalDate.now()
      val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhatIsTheImplementationDatePage, 0, LocalDate.now())
        .success.value
      verifyList(userAnswers) { list =>
        assertImplementationDate(dateFormatter.format(implementationDate))(list.head)
        list.size mustBe(1)
      }
    }

    "must return one country row without bullets" in {

      val countries: Set[CountryList] =
        Seq(CountryList.UnitedKingdom).toSet

      def assertCountries(html: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Countries involved"))
        row.value.html.map { html =>
          html.value mustBe Html("United Kingdom")
        }
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-choose-countries-involved/0"
          , text = Some(Literal("Change")))(row.actions.head)
      }

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, 0, countries)
        .success.value
      verifyList(userAnswers) { list =>
        assertCountries("United Kingdom")(list.head)
        list.size mustBe(1)
      }
    }

    "must return multiple country rows with bullets" in {

      val countries: Set[CountryList] =
        Set(CountryList.UnitedKingdom
        , CountryList.Sweden)

      def assertCountries(html: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Countries involved"))
        row.value.html.map { html =>
          html.value mustBe Html(
            """<ul>
              |<li>United Kingdom</li>
              |<li>Sweden</li>
              |</ul>""".stripMargin)
        }
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-choose-countries-involved/0"
          , text = Some(Literal("Change")))(row.actions.head)
      }

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, 0, countries)
        .success.value
      verifyList(userAnswers) { list =>
        assertCountries("<ul></ul>")(list.head)
        list.size mustBe(1)
      }
    }

    "must return the expected value " in {

      val expectedValue: ExpectedArrangementValue = ExpectedArrangementValue(
        currency = "CURRENCY"
        , amount = Int.MaxValue
      )

      def assertExpectedValue(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Total value"))
        row.value.html.map { html =>
          html.value mustBe Html("CURRENCY 1")
        }
        assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-value/0")(row.actions.head)
      }

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, expectedValue)
        .success.value
      verifyList(userAnswers) { list =>
        assertExpectedValue(list.head)
        list.size mustBe(1)
      }
    }

    "must return the national provisions " in {

      def assertNationalProvisions(content: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("National provisions"))
        row.value.text mustBe Some(Literal(content))
        assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-national-provisions/0")(row.actions.head)
      }

      textTuples.foreach { case (given, expected) =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, given)
          .success.value
        verifyList(userAnswers) { list =>
          assertNationalProvisions(expected)(list.head)
          list.size mustBe(1)
        }
      }
    }

    "must return the details " in {

      def assertNationalProvisions(content: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Description"))
        row.value.text mustBe Some(Literal(content))
        assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-details/0")(row.actions.head)
      }

      textTuples.foreach { case (given, expected) =>
        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(GiveDetailsOfThisArrangementPage, 0, given)
          .success.value
        verifyList(userAnswers) { list =>
          assertNationalProvisions(expected)(list.head)
          list.size mustBe(1)
        }
      }
    }

  }
}

