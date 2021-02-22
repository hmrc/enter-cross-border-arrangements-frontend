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
import helpers.data.ValidUserAnswersForSubmission.{validArrangementDetails, validDisclosureDetails, validToday}
import models.Submission
import models.arrangement.WhyAreYouReportingThisArrangementNow

import scala.xml.Elem

class DisclosureInformationXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val hallmarksSection: HallmarksXMLSection = mock[HallmarksXMLSection]

  "DisclosureInformationXMLSection" - {

    "buildReason must build the optional reason section if reason is known" in {

      val arrangementDetails = validArrangementDetails.copy(
        reportingReason = Some(WhyAreYouReportingThisArrangementNow.Dac6703.toString)
      )

      val submission = Submission("id", validDisclosureDetails).copy(arrangementDetails = Some(arrangementDetails))

      val result = DisclosureInformationXMLSection(submission).buildReason

      val expected = "<Reason>DAC6703</Reason>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildReason must not build the optional reason section" in {

      val arrangementDetails = validArrangementDetails.copy(
        reportingReason = None
      )

      val submission = Submission("id", validDisclosureDetails).copy(arrangementDetails = Some(arrangementDetails))

      val result = DisclosureInformationXMLSection(submission).buildReason

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildDisclosureInformationSummary must build the full summary section" in {

      val submission = Submission("id", validDisclosureDetails).copy(arrangementDetails = Some(validArrangementDetails))

      val result = DisclosureInformationXMLSection(submission).buildDisclosureInformationSummary

      val expected =
      """<Summary>
        |    <Disclosure_Name>name</Disclosure_Name>
        |    <Disclosure_Description>arrangementDetails</Disclosure_Description>
        |</Summary>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildNationalProvision must build the national provision section" in {

      val submission = Submission("id", validDisclosureDetails).copy(arrangementDetails = Some(validArrangementDetails))

      val result = DisclosureInformationXMLSection(submission).buildNationalProvision

      val expected = "<NationalProvision>nationalProvisions</NationalProvision>"

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildConcernedMS must build the full ConcernedMS section" in {

      val submission = Submission("id", validDisclosureDetails).copy(arrangementDetails = Some(validArrangementDetails))

      val result = DisclosureInformationXMLSection(submission).buildConcernedMS

      val expected =
        """<ConcernedMSs>
          |    <ConcernedMS>GB</ConcernedMS>
          |    <ConcernedMS>FR</ConcernedMS>
          |</ConcernedMSs>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildArrangementDetails must build the sections from the ArrangementDetails model" in {

      val submission = Submission("id", validDisclosureDetails).copy(arrangementDetails = Some(validArrangementDetails))

      val result = DisclosureInformationXMLSection(submission).buildArrangementDetails

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

    // TODO fix
    "buildArrangementDetails must throw an exception if ArrangementDetails is missing" in {
      val submission = Submission("id", validDisclosureDetails)//.copy(arrangementDetails = Some(arrangementDetails))

      assertThrows[Exception] {
        DisclosureInformationXMLSection(submission).buildArrangementDetails
      }
    }

    "buildDisclosureInformation must build the full DisclosureInformation Elem" in {

      val submission = Submission("id", validDisclosureDetails)//.copy(arrangementDetails = Some(arrangementDetails))

      val hallmarksSection = HallmarksXMLSection(submission)

      val result: Either[Throwable, Elem] = DisclosureInformationXMLSection(submission).buildDisclosureInformation

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

      result.map { result =>

        prettyPrinter.format(result) mustBe expected
      }
    }

  }
}
