/*
 * Copyright 2023 HM Revenue & Customs
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
import helpers.data.ValidUserAnswersForSubmission.{validArrangementDetails, validDisclosureDetails, validHallmarkDetails, validToday}
import models.Submission
import models.arrangement.WhyAreYouReportingThisArrangementNow

import scala.xml.NodeSeq

class DisclosureInformationXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val hallmarksSection: HallmarksXMLSection = mock[HallmarksXMLSection]

  val submission = Submission("id", validDisclosureDetails)
    .copy(arrangementDetails = Some(validArrangementDetails), hallmarkDetails = Some(validHallmarkDetails))

  "DisclosureInformationXMLSection" - {

    "buildReason must build the optional reason section if reason is known" in {

      val arrangementDetails = validArrangementDetails.copy(
        reportingReason = Some(WhyAreYouReportingThisArrangementNow.Dac6703.toString)
      )

      val submission = Submission("id", validDisclosureDetails)
        .copy(arrangementDetails = Some(arrangementDetails), hallmarkDetails = Some(validHallmarkDetails))

      val result = DisclosureInformationXMLSection(submission).buildReason(arrangementDetails)

      val expected = "<Reason>DAC6703</Reason>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildReason must not build the optional reason section" in {

      val arrangementDetails = validArrangementDetails.copy(
        reportingReason = None
      )

      val submission = Submission("id", validDisclosureDetails)
        .copy(arrangementDetails = Some(arrangementDetails), hallmarkDetails = Some(validHallmarkDetails))

      val result = DisclosureInformationXMLSection(submission).buildReason(arrangementDetails)

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildDisclosureInformationSummary must build the full summary section" in {

      val result = DisclosureInformationXMLSection(submission).buildDisclosureInformationSummary(validArrangementDetails)

      val expected =
        """<Summary>
        |    <Disclosure_Name>name</Disclosure_Name>
        |    <Disclosure_Description>arrangementDetails</Disclosure_Description>
        |</Summary>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildNationalProvision must build the national provision section" in {

      val result = DisclosureInformationXMLSection(submission).buildNationalProvision(validArrangementDetails)

      val expected = "<NationalProvision>nationalProvisions</NationalProvision>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildConcernedMS must build the full ConcernedMS section" in {

      val result = DisclosureInformationXMLSection(submission).buildConcernedMS(validArrangementDetails)

      val expected =
        """<ConcernedMSs>
          |    <ConcernedMS>GB</ConcernedMS>
          |    <ConcernedMS>FR</ConcernedMS>
          |</ConcernedMSs>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildArrangementDetails must build the sections from the ArrangementDetails model" in {

      val result = DisclosureInformationXMLSection(submission).buildArrangementDetails(validArrangementDetails)

      val expected =
        s"""<ImplementingDate>$validToday</ImplementingDate><Reason>DAC6703</Reason><Summary>
          |    <Disclosure_Name>name</Disclosure_Name>
          |    <Disclosure_Description>arrangementDetails</Disclosure_Description>
          |</Summary><NationalProvision>nationalProvisions</NationalProvision><Amount currCode="GBP">1000</Amount><ConcernedMSs>
          |    <ConcernedMS>GB</ConcernedMS>
          |    <ConcernedMS>FR</ConcernedMS>
          |</ConcernedMSs>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildDisclosureInformation must build the full DisclosureInformation Elem" in {

      val result: Either[Throwable, NodeSeq] = Right(DisclosureInformationXMLSection(submission).buildDisclosureInformation)

      val expected =
        s"""<DisclosureInformation>
          |    <ImplementingDate>$validToday</ImplementingDate>
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

      result.map {
        result =>
          prettyPrinter.formatNodes(result) mustBe expected
      }
    }

  }
}
