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
import helpers.DateHelper.formatXMLTimeStamp
import models.Submission
import models.disclosure.{DisclosureDetails, DisclosureType}

import java.time.LocalDateTime

class DisclosureDetailsXMLSectionSpec extends SpecBase {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  "XMLGenerationService" - {

    "buildHeader" - {

      "buildHeader must build the XML header" in {

        val disclosureDetails = DisclosureDetails(
          disclosureName = "DisclosureName",
          disclosureType = DisclosureType.Dac6new,
          initialDisclosureMA = true
        )

        val submission = Submission("id", disclosureDetails)
        val timestamp  = formatXMLTimeStamp(LocalDateTime.of(2020, 1, 1, 1, 1, 1))
        val result     = DisclosureDetailsXMLSection(submission).buildHeader("XADAC0001122345", timestamp)

        val expected =
          s"""<Header>
             |    <MessageRefId>GBXADAC0001122345DisclosureName</MessageRefId>
             |    <Timestamp>2020-01-01T01:01:01</Timestamp>
             |</Header>""".stripMargin

        prettyPrinter.format(result) mustBe expected
      }
    }

    "buildDisclosureImportInstruction" - {

      "buildDisclosureImportInstruction must build the import instruction section if additional arrangement" in {

        val disclosureDetails = DisclosureDetails(
          disclosureName = "not empty",
          disclosureType = DisclosureType.Dac6add,
          initialDisclosureMA = true
        )

        val submission = Submission("id", disclosureDetails)

        val result = DisclosureDetailsXMLSection(submission).buildDisclosureImportInstruction

        val expected = "<DisclosureImportInstruction>DAC6ADD</DisclosureImportInstruction>"

        prettyPrinter.format(result) mustBe expected
      }

      "buildDisclosureImportInstruction must build the import instruction section if new arrangement" in {

        val disclosureDetails = DisclosureDetails(
          disclosureName = "not empty",
          disclosureType = DisclosureType.Dac6new
        )

        val submission = Submission("id", disclosureDetails)

        val result = DisclosureDetailsXMLSection(submission).buildDisclosureImportInstruction

        val expected = "<DisclosureImportInstruction>DAC6NEW</DisclosureImportInstruction>"

        prettyPrinter.format(result) mustBe expected
      }

      "buildDisclosureImportInstruction" - {

        "buildInitialDisclosureMA must build the disclosure MA section when arrangement is marketable" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6new,
            initialDisclosureMA = true
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildInitialDisclosureMA

          val expected = "<InitialDisclosureMA>true</InitialDisclosureMA>"

          prettyPrinter.format(result) mustBe expected
        }

        "buildInitialDisclosureMA must build the disclosure MA section when arrangement is not marketable" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6new
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildInitialDisclosureMA

          val expected = "<InitialDisclosureMA>false</InitialDisclosureMA>"

          prettyPrinter.format(result) mustBe expected
        }

        "buildInitialDisclosureMA must build the disclosure MA section as false if arrangement is an additional" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "My Second",
            disclosureType = DisclosureType.Dac6add
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildInitialDisclosureMA

          val expected = "<InitialDisclosureMA>false</InitialDisclosureMA>"

          prettyPrinter.format(result) mustBe expected
        }

      }

      "buildArrangementID" - {

        "must build arrangement ID when user Selects DAC6ADD & enters a valid arrangementID" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6add,
            arrangementID = Some("GBA20210120FOK5BT")
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildArrangementID

          val expected = "<ArrangementID>GBA20210120FOK5BT</ArrangementID>"

          prettyPrinter.formatNodes(result) mustBe expected
        }

        "must NOT build arrangement ID when user Selects DAC6NEW" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6new
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildArrangementID

          val expected = ""

          prettyPrinter.formatNodes(result) mustBe expected
        }

      }

      "buildDisclosureID" - {

        "must build disclosure ID when user Selects DAC6REP & enters a valid disclosureID" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6rep,
            disclosureID = Some("GBA20210120FOK5BT")
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildDisclosureID

          val expected = "<DisclosureID>GBA20210120FOK5BT</DisclosureID>"

          prettyPrinter.formatNodes(result) mustBe expected
        }

        "must build disclosure ID when user Selects DAC6DEL & enters a valid disclosureID" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6rep,
            disclosureID = Some("GBA20210120FOK5BT")
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildDisclosureID

          val expected = "<DisclosureID>GBA20210120FOK5BT</DisclosureID>"

          prettyPrinter.formatNodes(result) mustBe expected
        }

        "must NOT build disclosure ID when user Selects DAC6ADD" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6add
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildDisclosureID

          val expected = ""

          prettyPrinter.formatNodes(result) mustBe expected
        }

        "must NOT build disclosure ID when user Selects DAC6NEW" in {

          val disclosureDetails = DisclosureDetails(
            disclosureName = "not empty",
            disclosureType = DisclosureType.Dac6new
          )

          val submission = Submission("id", disclosureDetails)

          val result = DisclosureDetailsXMLSection(submission).buildDisclosureID

          val expected = ""

          prettyPrinter.formatNodes(result) mustBe expected
        }

      }

    }
  }
}
