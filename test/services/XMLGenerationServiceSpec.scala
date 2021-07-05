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

package services

import base.{MockServiceApp, SpecBase}
import helpers.data.ValidUserAnswersForSubmission.{userAnswersForIndividual, userAnswersForOrganisation}
import helpers.xml.GeneratedXMLExamples
import models.Submission

class XMLGenerationServiceSpec extends SpecBase with MockServiceApp {

  val xmlGenerationService: XMLGenerationService = app.injector.instanceOf[XMLGenerationService]

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  "XMLGenerationService" - {

    "must build the full XML for a reporter that is an ORGANISATION" in {

      xmlGenerationService.createXmlSubmission(Submission(userAnswersForOrganisation, 0, "XADAC0001122345")) map { result =>

        prettyPrinter.format(result) mustBe GeneratedXMLExamples.xmlForOrganisation
      }
    }

    "must build the full XML for a reporter that is an INDIVIDUAL" in {

      xmlGenerationService.createXmlSubmission(Submission(userAnswersForIndividual, 0, "XADAC0001122345")) map { result =>

        prettyPrinter.format(result) mustBe GeneratedXMLExamples.xmlForIndividual
      }
    }

  }
}
