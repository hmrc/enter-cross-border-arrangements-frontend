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

import models.Submission
import models.disclosure.DisclosureType.{Dac6add, Dac6del, Dac6new, Dac6rep}
import org.joda.time.DateTime

import scala.xml.{Elem, NodeSeq}

case class DisclosureDetailsXMLSection(submission: Submission) {

  val disclosure = Option(submission.disclosureDetails)

  def buildHeader(enrolmentID: String): Elem = {
    val mandatoryMessageRefId = disclosure.map(_.disclosureName) match {
      case Some(disclosureName) => "GB" + enrolmentID + disclosureName
      case None => throw new Exception("Unable to build MessageRefID due to missing disclosure name")
    }

    //XML DateTime format e.g. 2021-01-06T12:25:14
    val mandatoryTimestamp = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")
    <Header>
      <MessageRefId>{mandatoryMessageRefId}</MessageRefId>
      <Timestamp>{mandatoryTimestamp}</Timestamp>
    </Header>
  }

  def buildDisclosureImportInstruction: Elem =
    disclosure.map(_.disclosureType) match {
      case Some(value) => <DisclosureImportInstruction>{value.toString.toUpperCase}</DisclosureImportInstruction>
      case None => throw new Exception("Missing disclosure type answer")
    }

  def buildInitialDisclosureMA: Elem =
    disclosure.map(_.initialDisclosureMA) match {
      case Some(value) => <InitialDisclosureMA>{value}</InitialDisclosureMA>
      case _ => throw new Exception("Missing InitialDisclosureMA flag")
    }

  def buildArrangementID: NodeSeq =
    disclosure.map(_.disclosureType) match {
      case Some(Dac6new) => NodeSeq.Empty
      case _ =>
        disclosure.flatMap(_.arrangementID).fold(NodeSeq.Empty) { arrangementID =>
          <ArrangementID>{arrangementID}</ArrangementID>
        }
    }

  def buildDisclosureID: NodeSeq =
    disclosure.map(_.disclosureType) match {
      case Some(Dac6rep) | Some(Dac6del) =>
        disclosure.flatMap(_.disclosureID).fold(NodeSeq.Empty) { disclosureID =>
          <DisclosureID>{disclosureID}</DisclosureID>
        }
      case _ => NodeSeq.Empty
    }

}
