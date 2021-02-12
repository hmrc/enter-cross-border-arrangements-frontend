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
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.{UnsubmittedDisclosure, UserAnswers}
import org.joda.time.DateTime
import pages.disclosure.DisclosureDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage

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

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildHeader("XADAC0001122345")

        val expected =
          s"""<Header>
             |    <MessageRefId>GBXADAC0001122345DisclosureName</MessageRefId>
             |    <Timestamp>${DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")}</Timestamp>
             |</Header>""".stripMargin

        prettyPrinter.format(result) mustBe expected
      }

      // TODO fix
//      "buildHeader must throw an exception if disclosure name is missing" in {
//        assertThrows[Exception] {
//          val result = DisclosureDetailsXMLSection(disclosureDetails).buildHeader("XADAC0001122345")
//        }
//      }

    }

    "buildDisclosureImportInstruction" - {

      "buildDisclosureImportInstruction must build the import instruction section if additional arrangement" in {

        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6add,
          initialDisclosureMA = true
        )

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildDisclosureImportInstruction

        val expected = "<DisclosureImportInstruction>DAC6ADD</DisclosureImportInstruction>"

        prettyPrinter.format(result) mustBe expected
      }

      "buildDisclosureImportInstruction must build the import instruction section if new arrangement" in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6new
        )

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildDisclosureImportInstruction

        val expected = "<DisclosureImportInstruction>DAC6NEW</DisclosureImportInstruction>"

        prettyPrinter.format(result) mustBe expected
      }

      // TODO fix
//      "buildDisclosureImportInstruction must throw an exception if import instruction is missing" in {
//        assertThrows[Exception] {
//          val result = DisclosureDetailsXMLSection(disclosureDetails).buildDisclosureImportInstruction
//        }
      }
    }

    "buildDisclosureImportInstruction" - {

      "buildInitialDisclosureMA must build the disclosure MA section when arrangement is marketable" in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6new,
          initialDisclosureMA = true
        )

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(DisclosureDetailsPage, 0, disclosureDetails)
          .success.value

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildInitialDisclosureMA

        val expected = "<InitialDisclosureMA>true</InitialDisclosureMA>"

        prettyPrinter.format(result) mustBe expected
      }

      "buildInitialDisclosureMA must build the disclosure MA section when arrangement is not marketable" in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6new,
          initialDisclosureMA = false
        )

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildInitialDisclosureMA

        val expected = "<InitialDisclosureMA>false</InitialDisclosureMA>"

        prettyPrinter.format(result) mustBe expected
      }

      "buildInitialDisclosureMA must build the disclosure MA section as false if arrangement is an additional" in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "My Second",
          disclosureType = DisclosureType.Dac6add
        )

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildInitialDisclosureMA

        val expected = "<InitialDisclosureMA>false</InitialDisclosureMA>"

        prettyPrinter.format(result) mustBe expected
      }

      // TODO fix
//      "buildInitialDisclosureMA must throw an exception if disclosure MA is missing" in {
//        assertThrows[Exception] {
//          DisclosureDetailsXMLSection(disclosureDetails).buildInitialDisclosureMA
//        }
//      }
    }

    "buildArrangementID" - {

      "must build arrangement ID when user Selects DAC6ADD & enters a valid arrangementID" in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6add,
          arrangementID = Some("GBA20210120FOK5BT")
        )

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildArrangementID

        val expected = "<ArrangementID>GBA20210120FOK5BT</ArrangementID>"

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must NOT build arrangement ID when user Selects DAC6NEW" in {
        val disclosureDetails = DisclosureDetails(
          disclosureName = "",
          disclosureType = DisclosureType.Dac6new
        )

        val result = DisclosureDetailsXMLSection(disclosureDetails).buildArrangementID

        val expected = ""

        prettyPrinter.formatNodes(result) mustBe expected
      }

    }

  }
