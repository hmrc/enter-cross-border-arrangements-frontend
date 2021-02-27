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

import connectors.{CrossBorderArrangementsConnector, ValidationConnector}
import helpers.xml.{AffectedXMLSection, DisclosureInformationXMLSection, IntermediariesXMLSection, RelevantTaxPayersXMLSection, _}
import models.disclosure.DisclosureType
import models.{GeneratedIDs, Submission}
import org.slf4j.LoggerFactory
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}
import scala.xml.{Elem, NodeSeq}

class XMLGenerationService @Inject()(
  validationConnector: ValidationConnector,
  transformationService: TransformationService,
  crossBorderArrangementsConnector: CrossBorderArrangementsConnector
) {

  private val logger = LoggerFactory.getLogger(getClass)

  def createXmlSubmission(submission: Submission): Try[Elem] = {

    (for {
      disclosureSection            <- Option(submission).map(DisclosureDetailsXMLSection)
                                        .toRight(new RuntimeException("Could not generate XML from disclosure details."))
      reporterSection              =  ReporterXMLSection(submission)
      disclosureInformationSection =  DisclosureInformationXMLSection(submission)
      enrolmentID                  <- Option(submission).map(_.enrolmentID)
                                        .toRight(new RuntimeException("Could not get enrolment id from submission."))
    } yield {
      Try {
        <DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
          {disclosureSection.buildHeader(enrolmentID)}
          {disclosureSection.buildArrangementID}
          <DAC6Disclosures>
            {disclosureSection.buildDisclosureID}
            {disclosureSection.buildDisclosureImportInstruction}
            {reporterSection.buildDisclosureDetails}
            {disclosureSection.buildInitialDisclosureMA}
            {createPartiesSection(submission, Option(reporterSection))}
            {disclosureInformationSection.buildDisclosureInformation}
          </DAC6Disclosures>
        </DAC6_Arrangement>
      }
    }).fold(Failure(_), identity)

  }

  def createPartiesSection(submission: Submission, reporterSection: Option[ReporterXMLSection]): NodeSeq = {
    if (submission.getDisclosureType == DisclosureType.Dac6del) {
      NodeSeq.Empty
    }
    else {
      RelevantTaxPayersXMLSection(submission, reporterSection).buildRelevantTaxpayers ++
        IntermediariesXMLSection(submission, reporterSection).buildIntermediaries ++
        AffectedXMLSection(submission).buildAffectedPersons
    }
  }

  def createAndValidateXmlSubmission(submission: Submission)
              (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Either[Seq[String], GeneratedIDs]] = {

    createXmlSubmission(submission).fold (
      error => {
        // TODO today we rely on task list enforcement to avoid incomplete xml to be submitted; we could add an extra layer of validation here
        logger.error("""Xml generation failed before validation: """.stripMargin, error)
        throw error
      },
      xml => {
        //send it off to be validated and business rules
        validationConnector.sendForValidation(xml).flatMap {
          _.fold(
            //did it fail? oh my god - hand back to the user to fix
            errors => Future.successful(Left(errors)),
            //did it succeed - hand off to the backend to do it's generating thing
            messageRefId => {
              for {
                submissionXML <- Future.fromTry(transformationService.build(xml, messageRefId, submission.enrolmentID))
                ids           <- crossBorderArrangementsConnector.submitXML(submissionXML)
              } yield Right(ids.withMessageRefId(messageRefId).withXml(submissionXML.toString))
            }
          )
        }
      }
    )
  }
}
