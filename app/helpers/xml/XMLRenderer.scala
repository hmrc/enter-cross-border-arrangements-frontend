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

import models.UserAnswers
import models.requests.DataRequest
import org.joda.time.DateTime
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import play.api.mvc.AnyContent

import javax.inject.Inject
import scala.xml.Elem

class XMLRenderer @Inject()() {

  private[xml] def buildHeader(userAnswers: UserAnswers)
                                   (implicit request: DataRequest[AnyContent]): Elem = {
    val mandatoryMessageRefId = userAnswers.get(DisclosureNamePage) match {
      case Some(disclosureName) => "GB" + request.internalId + disclosureName
      case None => ""
    }

    //XML DateTime format e.g. 2021-01-06T12:25:14
    val mandatoryTimestamp = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")

    <Header>
      <MessageRefId>{mandatoryMessageRefId}</MessageRefId>
      <Timestamp>{mandatoryTimestamp}</Timestamp>
    </Header>
  }

  def renderXML(userAnswers: UserAnswers)
               (implicit request: DataRequest[AnyContent]): Elem = {
    val mandatoryDisclosureImportInstruction = userAnswers.get(DisclosureTypePage) match {
      case Some(value) => value.toString.toUpperCase
      case None => ""
    }

    val mandatoryInitialDisclosureMA = userAnswers.get(DisclosureMarketablePage) match {//TODO Is this the right page?
      case Some(value) => value
      case None => false
    }

    val xml =
      <DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
        {buildHeader(userAnswers)}
        <DAC6Disclosures>
          <DisclosureImportInstruction>{mandatoryDisclosureImportInstruction}</DisclosureImportInstruction>
          {DisclosingXMLSection.toXml(userAnswers)}
          <InitialDisclosureMA>{mandatoryInitialDisclosureMA}</InitialDisclosureMA>
          {RelevantTaxPayersXMLSection.toXml(userAnswers)}
          {DisclosureInformationXMLSection.toXml(userAnswers)}
        </DAC6Disclosures>
      </DAC6_Arrangement>

    val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

    prettyPrinter.format(xml)
    xml

  }
}

