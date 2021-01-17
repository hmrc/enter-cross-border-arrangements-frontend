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

import helpers.xml.{DisclosingXMLSection, DisclosureInformationXMLSection, RelevantTaxPayersXMLSection}
import models.UserAnswers
import models.requests.DataRequest
import org.joda.time.DateTime
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import play.api.mvc.AnyContent

import javax.inject.Inject
import scala.xml.Elem

class XMLGenerationService @Inject()() {

  private[services] def buildHeader(userAnswers: UserAnswers)
                                   (implicit request: DataRequest[AnyContent]): Elem = {
    val mandatoryMessageRefId = userAnswers.get(DisclosureNamePage) match {
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

  private[services] def buildDisclosureImportInstruction(userAnswers: UserAnswers): Elem = {
    userAnswers.get(DisclosureTypePage) match {
      case Some(value) => <DisclosureImportInstruction>{value.toString.toUpperCase}</DisclosureImportInstruction>
      case None => throw new Exception("Missing disclosure type answer")
    }
  }

  private[services] def buildInitialDisclosureMA(userAnswers: UserAnswers): Elem = {
    userAnswers.get(DisclosureMarketablePage) match {
      case Some(value) => <InitialDisclosureMA>{value}</InitialDisclosureMA>
      case None => throw new Exception("Missing InitialDisclosureMA answer")
    }
  }

  def createXmlSubmission(userAnswers: UserAnswers)
                         (implicit request: DataRequest[AnyContent]): Elem = {

    <DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
      {buildHeader(userAnswers)}
      <DAC6Disclosures>
        {buildDisclosureImportInstruction(userAnswers)}
        {DisclosingXMLSection.toXml(userAnswers)}
        {buildInitialDisclosureMA(userAnswers)}
        {RelevantTaxPayersXMLSection.toXml(userAnswers)}
        {DisclosureInformationXMLSection.toXml(userAnswers)}
      </DAC6Disclosures>
    </DAC6_Arrangement>
  }

}
