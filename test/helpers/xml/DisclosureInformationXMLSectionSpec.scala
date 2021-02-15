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

import java.time.LocalDate

import base.SpecBase
import models.arrangement.{ArrangementDetails, ExpectedArrangementValue, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.hallmarks.{HallmarkD, HallmarkDetails}
import models.{UnsubmittedDisclosure, UserAnswers}
import pages.GiveDetailsOfThisArrangementPage
import pages.arrangement._
import pages.hallmarks.{HallmarkDPage, HallmarkDetailsPage}
import pages.unsubmitted.UnsubmittedDisclosurePage

class DisclosureInformationXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

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

  "DisclosureInformationXMLSection" - {

    "buildImplementingDate must build the implementing date Elem" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, mockArrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildImplementingDate(userAnswers, 0)

      val expected = s"<ImplementingDate>$today</ImplementingDate>"

      prettyPrinter.format(result) mustBe expected
    }

    "buildImplementingDate must throw an exception if date is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildImplementingDate(
          UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value,
          0
        )
      }
    }

    "buildReason must build the optional reason section if reason is known" in {

      val arrangementDetails = mockArrangementDetails.copy(
        reportingReason = Some(WhyAreYouReportingThisArrangementNow.Dac6703.toString)
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, arrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildReason(userAnswers, 0)

      val expected = "<Reason>DAC6703</Reason>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildReason must not build the optional reason section" in {

      val arrangementDetails = mockArrangementDetails.copy(
        reportingReason = None
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, arrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildReason(userAnswers, 0)

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildDisclosureInformationSummary must build the full summary section" in {

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, mockArrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildDisclosureInformationSummary(userAnswers, 0)

      val expected =
      """<Summary>
        |    <Disclosure_Name>name</Disclosure_Name>
        |    <Disclosure_Description>arrangementDetails</Disclosure_Description>
        |</Summary>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildDisclosureInformationSummary must throw an exception if arrangement name is missing" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(GiveDetailsOfThisArrangementPage, 0, "Some description").success.value

      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildDisclosureInformationSummary(userAnswers, 0)
      }
    }

    "buildDisclosureInformationSummary must throw an exception if disclosure description is missing" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(WhatIsThisArrangementCalledPage, 0, "Arrangement name").success.value

      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildDisclosureInformationSummary(userAnswers, 0)
      }
    }

    "buildNationalProvision must build the national provision section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, mockArrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildNationalProvision(userAnswers, 0)

      val expected = "<NationalProvision>nationalProvisions</NationalProvision>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildNationalProvision must throw an exception if national provision is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildNationalProvision(
          UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value,
          0
        )
      }
    }

    "buildAmountType must build the national provision section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, mockArrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildAmountType(userAnswers, 0)

      val expected = """<Amount currCode="GBP">1000</Amount>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildAmountType must throw an exception if national provision is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildAmountType(
          UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value,
          0
        )
      }
    }

    "buildConcernedMS must build the full ConcernedMS section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ArrangementDetailsPage, 0, mockArrangementDetails).success.value

      val result = DisclosureInformationXMLSection.buildConcernedMS(userAnswers, 0)

      val expected =
        """<ConcernedMSs>
          |    <ConcernedMS>GB</ConcernedMS>
          |    <ConcernedMS>FR</ConcernedMS>
          |</ConcernedMSs>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildConcernedMS must throw an exception if countries are missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildConcernedMS(
          UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value,
          0
        )
      }
    }

    "buildHallmarks must build the full hallmarks section" in {

      val hallmarkDetails = HallmarkDetails(
        hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
        hallmarkContent = Some("Hallmark D1 other description")
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(HallmarkDetailsPage, 0, hallmarkDetails).success.value

      val result = DisclosureInformationXMLSection.buildHallmarks(userAnswers, 0)

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

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(HallmarkDetailsPage, 0, hallmarkDetails).success.value

      val result = DisclosureInformationXMLSection.buildHallmarks(userAnswers, 0)

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

    "buildHallmarks must throw an exception if HallmarkD is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildHallmarks(
          UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value,
          0
        )
      }
    }

    "buildHallmarks must throw an exception if HallmarkD1 is missing and it was selected" in {
      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(HallmarkDPage, 0, HallmarkD.enumerable.withName("D1").toSet).success.value

      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildHallmarks(userAnswers, 0)
      }
    }

    "toXml must build the full DisclosureInformation Elem" in {

      val hallmarkDetails = HallmarkDetails(
        hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
        hallmarkContent = Some("Hallmark D1 other description")
      )

      val userAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(HallmarkDetailsPage, 0, hallmarkDetails).success.value
        .set(ArrangementDetailsPage, 0, mockArrangementDetails).success.value

      val expected =
        s"""<DisclosureInformation>
          |    <ImplementingDate>$today</ImplementingDate>
          |    <Reason>DAC6703</Reason>
          |    <Summary>
          |        <Disclosure_Name>name</Disclosure_Name>
          |        <Disclosure_Description>arrangementDetails</Disclosure_Description>
          |    </Summary>
          |    <NationalProvision>nationalProvisions</NationalProvision>
          |    <Amount currCode="GBP">1000</Amount>
          |    <ConcernedMSs>
          |        <ConcernedMS>GB</ConcernedMS>
          |        <ConcernedMS>FR</ConcernedMS>
          |    </ConcernedMSs>
          |    <MainBenefitTest1>false</MainBenefitTest1>
          |    <Hallmarks>
          |        <ListHallmarks>
          |            <Hallmark>DAC6D1a</Hallmark>
          |            <Hallmark>DAC6D1Other</Hallmark>
          |            <Hallmark>DAC6D2</Hallmark>
          |        </ListHallmarks>
          |        <DAC6D1OtherInfo>Hallmark D1 other description</DAC6D1OtherInfo>
          |    </Hallmarks>
          |</DisclosureInformation>""".stripMargin

      DisclosureInformationXMLSection.toXml(userAnswers, 0).map { result =>

        prettyPrinter.format(result) mustBe expected
      }
    }

  }
}
