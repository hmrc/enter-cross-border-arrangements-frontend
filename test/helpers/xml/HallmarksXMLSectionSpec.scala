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

package helpers.xml

import base.SpecBase
import models.arrangement.{ArrangementDetails, ExpectedArrangementValue, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.hallmarks.{HallmarkD, HallmarkDetails}
import models.{UnsubmittedDisclosure, UserAnswers}
import pages.arrangement._
import pages.hallmarks.{HallmarkDPage, HallmarkDetailsPage}
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate

class HallmarksXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val hallmarksSection: HallmarksXMLSection = mock[HallmarksXMLSection]

  val today: LocalDate = LocalDate.now
  val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
    Seq(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom, WhichExpectedInvolvedCountriesArrangement.France).toSet

  val mockArrangementDetails: ArrangementDetails =
    ArrangementDetails(
      "name",
      today,
      Some("DAC6703"),
      List("GB", "FR"),
      ExpectedArrangementValue("GBP", 1000),
      "nationalProvisions",
      "arrangementDetails"
    )

  "HallmarksXMLSection" - {

    "buildHallmarks must build the full hallmarks section" in {

      val hallmarkDetails = HallmarkDetails(
        hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
        hallmarkContent = Some("Hallmark D1 other description")
      )

      val result = HallmarksXMLSection(hallmarkDetails).buildHallmarks

      val expected =
        """<Hallmarks>
          |    <ListHallmarks>
          |        <Hallmark>DAC6D1a</Hallmark>
          |        <Hallmark>DAC6D1Other</Hallmark>
          |        <Hallmark>DAC6D2</Hallmark>
          |    </ListHallmarks>
          |    <DAC6D1OtherInfo>Hallmark D1 other description</DAC6D1OtherInfo>
          |</Hallmarks>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildHallmarks must build the hallmarks section without the optional D1 other description" in {

      val hallmarkDetails = HallmarkDetails(
        hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
        None
      )

      val result = HallmarksXMLSection(hallmarkDetails).buildHallmarks

      val expected =
        """<Hallmarks>
          |    <ListHallmarks>
          |        <Hallmark>DAC6D1a</Hallmark>
          |        <Hallmark>DAC6D1Other</Hallmark>
          |        <Hallmark>DAC6D2</Hallmark>
          |    </ListHallmarks>
          |</Hallmarks>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    // TODO fix
//    "buildHallmarks must throw an exception if HallmarkD is missing" in {
//      assertThrows[Exception] {
//        val result = HallmarksXMLSection(hallmarkDetails).buildHallmarks
//      }
//    }

    // TODO fix
//    "buildHallmarks must throw an exception if HallmarkD1 is missing and it was selected" in {
//      val userAnswers = UserAnswers(userAnswersId)
//        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
//        .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("DAC6D1").toSet).success.value
//
//      assertThrows[Exception] {
//        HallmarksXMLSection.buildHallmarks(userAnswers, 0)
//      }
//    }

  }
}
