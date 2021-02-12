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

import helpers.xml.{AffectedXMLSection, DisclosingXMLSection, DisclosureInformationXMLSection, IntermediariesXMLSection, RelevantTaxPayersXMLSection}
import models.UserAnswers
import models.disclosure.DisclosureType.Dac6add
import models.requests.DataRequest
import org.joda.time.DateTime
import pages.disclosure.DisclosureDetailsPage
import play.api.mvc.AnyContent

import javax.inject.Inject
import scala.util.Try
import scala.xml.{Elem, NodeSeq}

class XMLGenerationService @Inject()() {

  private[services] def buildHeader(userAnswers: UserAnswers, id: Int)
                                   (implicit request: DataRequest[AnyContent]): Elem = {
    val mandatoryMessageRefId = userAnswers.get(DisclosureDetailsPage, id).map(_.disclosureName) match {
      case Some(disclosureName) => "GB" + request.enrolmentID + disclosureName
      case None => throw new Exception("Unable to build MessageRefID due to missing disclosure name")
    }

    //XML DateTime format e.g. 2021-01-06T12:25:14
    val mandatoryTimestamp = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")
    <Header>
      <MessageRefId>{mandatoryMessageRefId}</MessageRefId>
      <Timestamp>{mandatoryTimestamp}</Timestamp>
    </Header>
  }

  private[services] def buildDisclosureImportInstruction(userAnswers: UserAnswers, id: Int): Elem = {
    userAnswers.get(DisclosureDetailsPage, id).map(_.disclosureType) match {
      case Some(value) => <DisclosureImportInstruction>{value.toString.toUpperCase}</DisclosureImportInstruction>
      case None => throw new Exception("Missing disclosure type answer")
    }
  }

  private[services] def buildInitialDisclosureMA(userAnswers: UserAnswers, id: Int): Elem = {
    userAnswers.get(DisclosureDetailsPage, id).map(_.disclosureType) match {
      case Some(Dac6add) =>
        <InitialDisclosureMA>false</InitialDisclosureMA>
      case _ =>
        userAnswers.get(DisclosureDetailsPage, id).map(_.initialDisclosureMA)  match {
          case Some(value) => <InitialDisclosureMA>{value}</InitialDisclosureMA>
          case _ => throw new Exception("Missing InitialDisclosureMA flag")
      }
    }
  }

  private[services] def buildArrangementID(userAnswers: UserAnswers, id: Int): NodeSeq = { //TODO - update method as we add DAC6DEL & DAC6REPLACE
    userAnswers.get(DisclosureDetailsPage, id).map(_.disclosureType) match {
      case Some(Dac6add) =>
        userAnswers.get(DisclosureDetailsPage, id).flatMap(_.arrangementID).fold(NodeSeq.Empty){
          arrangementID =>
            <ArrangementID>{arrangementID}</ArrangementID>
        }
      case _ => NodeSeq.Empty
    }
  }

  def createXmlSubmission(userAnswers: UserAnswers, id: Int)
                         (implicit request: DataRequest[AnyContent]): Try[Elem] = {

    Try {
      <DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
        {buildHeader(userAnswers, id)}
        {buildArrangementID(userAnswers, id)}
        <DAC6Disclosures>
          {buildDisclosureImportInstruction(userAnswers, id)}
          {DisclosingXMLSection.toXml(userAnswers, id).getOrElse(NodeSeq.Empty)}
          {buildInitialDisclosureMA(userAnswers, id)}
          {RelevantTaxPayersXMLSection.toXml(userAnswers, id).getOrElse(NodeSeq.Empty)}
          {IntermediariesXMLSection.toXml(userAnswers, id).getOrElse(NodeSeq.Empty)}
          {AffectedXMLSection.toXml(userAnswers, id).getOrElse(NodeSeq.Empty)}
          {DisclosureInformationXMLSection.toXml(userAnswers, id).getOrElse(NodeSeq.Empty)}
        </DAC6Disclosures>
      </DAC6_Arrangement>
    }
  }
}
