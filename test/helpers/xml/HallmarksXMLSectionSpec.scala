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
import helpers.data.ValidUserAnswersForSubmission.validDisclosureDetails
import models.Submission
import models.hallmarks.HallmarkDetails

class HallmarksXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val hallmarksSection: HallmarksXMLSection = mock[HallmarksXMLSection]

  "HallmarksXMLSection" - {

    "buildHallmarks must build the full hallmarks section" in {

      val hallmarkDetails = HallmarkDetails(
        hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
        hallmarkContent = Some("Hallmark D1 other description")
      )

      val submission = Submission("id", validDisclosureDetails).copy(hallmarkDetails = Some(hallmarkDetails))

      val result = HallmarksXMLSection(submission).buildHallmarks

      val expected =
        """<Hallmarks>
          |    <ListHallmarks>
          |        <Hallmark>DAC6D1a</Hallmark>
          |        <Hallmark>DAC6D1Other</Hallmark>
          |        <Hallmark>DAC6D2</Hallmark>
          |    </ListHallmarks>
          |    <DAC6D1OtherInfo>Hallmark D1 other description</DAC6D1OtherInfo>
          |</Hallmarks>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildHallmarks must build the hallmarks section without the optional D1 other description" in {

      val hallmarkDetails = HallmarkDetails(
        hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
        None
      )

      val submission = Submission("id", validDisclosureDetails).copy(hallmarkDetails = Some(hallmarkDetails))

      val result = HallmarksXMLSection(submission).buildHallmarks

      val expected =
        """<Hallmarks>
          |    <ListHallmarks>
          |        <Hallmark>DAC6D1a</Hallmark>
          |        <Hallmark>DAC6D1Other</Hallmark>
          |        <Hallmark>DAC6D2</Hallmark>
          |    </ListHallmarks>
          |</Hallmarks>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }
  }
}
