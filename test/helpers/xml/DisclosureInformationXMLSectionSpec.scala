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
import models.UserAnswers
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.hallmarks.{HallmarkD, HallmarkD1}
import pages.arrangement._
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}

import java.time.LocalDate

class DisclosureInformationXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val today: LocalDate = LocalDate.now
  val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
    Seq(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom, WhichExpectedInvolvedCountriesArrangement.France).toSet


  "DisclosureInformationXMLSection" - {

    "buildImplementingDate must build the implementing date Elem" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTheImplementationDatePage, today).success.value

      val result = DisclosureInformationXMLSection.buildImplementingDate(userAnswers)

      val expected = s"<ImplementingDate>$today</ImplementingDate>"

      prettyPrinter.format(result) mustBe expected
    }

    "buildImplementingDate must throw an exception if date is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildImplementingDate(UserAnswers(userAnswersId))
      }
    }

    "buildReason must build the optional reason section if reason is known" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(DoYouKnowTheReasonToReportArrangementNowPage, true).success.value
        .set(WhyAreYouReportingThisArrangementNowPage, WhyAreYouReportingThisArrangementNow.Dac6703).success.value

      val result = DisclosureInformationXMLSection.buildReason(userAnswers)

      val expected = "<Reason>DAC6703</Reason>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildReason must not build the optional reason section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(DoYouKnowTheReasonToReportArrangementNowPage, false).success.value

      val result = DisclosureInformationXMLSection.buildReason(userAnswers)

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildDisclosureInformationSummary must build the full summary section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsThisArrangementCalledPage, "Arrangement name").success.value
        .set(GiveDetailsOfThisArrangementPage, "Some description").success.value

      val result = DisclosureInformationXMLSection.buildDisclosureInformationSummary(userAnswers)

      val expected =
      """<Summary>
        |    <Disclosure_Name>Arrangement name</Disclosure_Name>
        |    <Disclosure_Description>Some description</Disclosure_Description>
        |</Summary>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildDisclosureInformationSummary must throw an exception if arrangement name is missing" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(GiveDetailsOfThisArrangementPage, "Some description").success.value

      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildDisclosureInformationSummary(userAnswers)
      }
    }

    "buildDisclosureInformationSummary must throw an exception if disclosure description is missing" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsThisArrangementCalledPage, "Arrangement name").success.value

      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildDisclosureInformationSummary(userAnswers)
      }
    }

    "buildNationalProvision must build the national provision section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, "National provisions description").success.value

      val result = DisclosureInformationXMLSection.buildNationalProvision(userAnswers)

      val expected = "<NationalProvision>National provisions description</NationalProvision>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildNationalProvision must throw an exception if national provision is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildNationalProvision(UserAnswers(userAnswersId))
      }
    }

    "buildAmountType must build the national provision section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTheExpectedValueOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangement("GBP", 1000)).success.value

      val result = DisclosureInformationXMLSection.buildAmountType(userAnswers)

      val expected = """<Amount currCode="GBP">1000</Amount>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildAmountType must throw an exception if national provision is missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildAmountType(UserAnswers(userAnswersId))
      }
    }

    "buildConcernedMS must build the full ConcernedMS section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries).success.value

      val result = DisclosureInformationXMLSection.buildConcernedMS(userAnswers)

      val expected =
        """<ConcernedMSs>
          |    <ConcernedMS>GB</ConcernedMS>
          |    <ConcernedMS>FR</ConcernedMS>
          |</ConcernedMSs>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildConcernedMS must throw an exception if countries are missing" in {
      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildConcernedMS(UserAnswers(userAnswersId))
      }
    }

    "buildHallmarks must build the full hallmarks section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallmark D1 other description").success.value

      val result = DisclosureInformationXMLSection.buildHallmarks(userAnswers)

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
      val userAnswers = UserAnswers(userAnswersId)
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value

      val result = DisclosureInformationXMLSection.buildHallmarks(userAnswers)

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
        DisclosureInformationXMLSection.buildHallmarks(UserAnswers(userAnswersId))
      }
    }

    "buildHallmarks must throw an exception if HallmarkD1 is missing and it was selected" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(HallmarkDPage, HallmarkD.enumerable.withName("D1").toSet).success.value

      assertThrows[Exception] {
        DisclosureInformationXMLSection.buildHallmarks(userAnswers)
      }
    }

    "toXml must build the full DisclosureInformation Elem" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTheImplementationDatePage, today).success.value
        .set(DoYouKnowTheReasonToReportArrangementNowPage, true).success.value
        .set(WhyAreYouReportingThisArrangementNowPage, WhyAreYouReportingThisArrangementNow.Dac6703).success.value
        .set(WhatIsThisArrangementCalledPage, "Arrangement name").success.value
        .set(GiveDetailsOfThisArrangementPage, "Some description").success.value
        .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, "National provisions description").success.value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangement("GBP", 1000)).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries).success.value
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallmark D1 other description").success.value

      val expected =
        s"""<DisclosureInformation>
          |    <ImplementingDate>$today</ImplementingDate>
          |    <Reason>DAC6703</Reason>
          |    <Summary>
          |        <Disclosure_Name>Arrangement name</Disclosure_Name>
          |        <Disclosure_Description>Some description</Disclosure_Description>
          |    </Summary>
          |    <NationalProvision>National provisions description</NationalProvision>
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

      DisclosureInformationXMLSection.toXml(userAnswers).map { result =>

        prettyPrinter.format(result) mustBe expected
      }

    }
  }

}
