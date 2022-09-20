/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import base.{MockServiceApp, SpecBase}
import helpers.Submissions
import helpers.xml.GeneratedXMLExamples

class TransformationServiceSpec extends SpecBase with MockServiceApp {

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)
  val service       = app.injector.instanceOf[TransformationService]

  "TransformationService" - {

    "must take a valid file and replace the messageRefID" in {
      val transformedFile = service.rewriteMessageRefID(
        Submissions.validSubmission,
        "GB0000000YYY"
      )
      transformedFile mustBe Some(Submissions.updatedSubmission)
    }

    "must construct the correct submission" in {
      val fileName    = "file.xml"
      val enrolmentID = "1234"

      val testSubmission =
        <submission>
          <fileName>{fileName}</fileName>
          <enrolmentID>{enrolmentID}</enrolmentID>
          <file></file>
        </submission>

      val document = scala.xml.XML.loadString(GeneratedXMLExamples.xmlForOrganisation)

      val correctSubmission = service.constructSubmission(fileName, enrolmentID, document)

      correctSubmission.isDefined mustBe true
      val doc = correctSubmission.get \ "file" \ "DAC6_Arrangement"

      doc.head mustBe document
    }
  }
}
