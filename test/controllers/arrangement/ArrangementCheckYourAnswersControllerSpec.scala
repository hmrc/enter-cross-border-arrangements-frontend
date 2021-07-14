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

import base.{ControllerMockFixtures, SpecBase}
import generators.ModelGenerators
import models.arrangement.WhyAreYouReportingThisArrangementNow.Dac6701
import models.arrangement.{ArrangementDetails, ExpectedArrangementValue}
import models.{CountryList, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.arrangement._
import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text.Literal

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.Future

class ArrangementCheckYourAnswersControllerSpec extends SpecBase with ControllerMockFixtures with BeforeAndAfterEach with ModelGenerators {

  val oneHundredCharacters: String = "123456789 " * 10

  val textTuples = Seq(
    (oneHundredCharacters, oneHundredCharacters),
    (s"$oneHundredCharacters longer than 100 chars", s"$oneHundredCharacters...")
  )

  val countries: List[CountryList] = List(CountryList.UnitedKingdom)

  val arrangementDetails = ArrangementDetails(
    arrangementName = "arrangement",
    implementationDate = LocalDate.now(),
    reportingReason = Some(Dac6701.toString),
    countriesInvolved = countries,
    expectedValue = ExpectedArrangementValue.zero,
    nationalProvisionDetails = "National Provisions",
    arrangementDetails = "given"
  )

  def verifyList(userAnswers: UserAnswers, nrOfInvocations: Int = 1)(assertFunction: Seq[Row] => Unit): Unit = {

    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

    retrieveUserAnswersData(userAnswers)

    val request = FakeRequest(GET, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onPageLoad(0).url)

    val result = route(app, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(nrOfInvocations)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json: JsObject = jsonCaptor.getValue
    import controllers.RowJsonReads._
    val list = (json \ "list").get.as[Seq[Row]]

    templateCaptor.getValue mustEqual "arrangement/check-your-answers-arrangement.njk"
    assertFunction(list)

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

      textTuples.foreach {
        case (given, expected) =>
          val userAnswers: UserAnswers =
            ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails.copy(arrangementName = given))).success.value

          verifyList(userAnswers) {
            list =>
              assertName(expected)(list.head)
          }
      }
    }

    "must return implementing date row" in {

      def assertImplementationDate(implementationDateAsString: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Implementing date"))
        row.value.text mustBe Some(Literal(implementationDateAsString))
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-implementation-date/0", text = Some(Literal("Change")))(row.actions.head)
      }

      val implementationDate               = LocalDate.now()
      val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      val userAnswers: UserAnswers =
        ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails.copy(implementationDate = implementationDate))).success.value
      verifyList(userAnswers) {
        list =>
          assertImplementationDate(dateFormatter.format(implementationDate))(list(1))
      }
    }

    "must return one country row without bullets" in {

      def assertCountries(html: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Countries involved"))
        row.value.html.map {
          html =>
            html.value mustBe Html("United Kingdom")
        }
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-choose-countries-involved/0", text = Some(Literal("Change")))(
          row.actions.head
        )
      }

      val userAnswers: UserAnswers = ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails)).success.value
      verifyList(userAnswers) {
        list =>
          assertCountries("United Kingdom")(list(3))
      }
    }

    "must return multiple country rows with bullets" in {

      val countries: List[CountryList] = List(CountryList.UnitedKingdom, CountryList.Sweden)

      def assertCountries(html: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Countries involved"))
        row.value.html.map {
          html =>
            html.value mustBe Html("""<ul>
              |<li>United Kingdom</li>
              |<li>Sweden</li>
              |</ul>""".stripMargin)
        }
        assertAction("/disclose-cross-border-arrangements/manual/arrangement/change-choose-countries-involved/0", text = Some(Literal("Change")))(
          row.actions.head
        )
      }

      val userAnswers: UserAnswers =
        ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails.copy(countriesInvolved = countries))).success.value
      verifyList(userAnswers) {
        list =>
          assertCountries("<ul></ul>")(list(3))
      }
    }

    "must return the expected value " in {

      val expectedValue: ExpectedArrangementValue = ExpectedArrangementValue(
        currency = "CURRENCY",
        amount = Int.MaxValue
      )

      def assertExpectedValue(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Total value"))
        row.value.html.map {
          html =>
            html.value mustBe Html("CURRENCY 1")
        }
        assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-value/0")(row.actions.head)
      }

      val userAnswers = ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails.copy(expectedValue = expectedValue))).success.value
      verifyList(userAnswers) {
        list =>
          assertExpectedValue(list(4))
      }
    }

    "must return the national provisions " in {

      def assertNationalProvisions(content: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("National provisions"))
        row.value.text mustBe Some(Literal(content))
        assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-national-provisions/0")(row.actions.head)
      }

      textTuples.foreach {
        case (given, expected) =>
          val userAnswers = ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails.copy(nationalProvisionDetails = given))).success.value
          verifyList(userAnswers) {
            list =>
              assertNationalProvisions(expected)(list(5))
          }
      }
    }

    "must return the details " in {

      def assertNationalProvisions(content: String)(row: Row): Unit = {
        row.key.text mustBe Some(Literal("Description"))
        row.value.text mustBe Some(Literal(content))
        assertAction(href = "/disclose-cross-border-arrangements/manual/arrangement/change-details/0")(row.actions.head)
      }

      textTuples.foreach {
        case (given, expected) =>
          val userAnswers = ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails.copy(arrangementDetails = given))).success.value
          verifyList(userAnswers) {
            list =>
              assertNationalProvisions(expected)(list(6))
          }
      }
    }

    "must redirect to next page on submit" in {

      val userAnswers: UserAnswers = ArrangementDetailsPage.restore(emptyUserAnswers, 0, Some(arrangementDetails)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, controllers.arrangement.routes.ArrangementCheckYourAnswersController.onSubmit(0).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/your-disclosure-details/0"
    }
  }
}
