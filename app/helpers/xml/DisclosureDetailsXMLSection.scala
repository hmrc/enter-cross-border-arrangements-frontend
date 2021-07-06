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
import models.disclosure.DisclosureDetails
import models.disclosure.DisclosureType._

import scala.xml.{Elem, NodeSeq}

case class DisclosureDetailsXMLSection(submission: Submission) {

  val disclosure: DisclosureDetails = submission.disclosureDetails.validate.fold(e => throw new IllegalStateException(e.defaultMessage), identity)

  def buildHeader(enrolmentID: String, timeStamp: String): Elem = {
    val mandatoryMessageRefId = "GB" + enrolmentID + disclosure.disclosureName

    <Header>
      <MessageRefId>{mandatoryMessageRefId}</MessageRefId>
      <Timestamp>{timeStamp}</Timestamp>
    </Header>
  }

  def buildDisclosureImportInstruction: Elem =
    <DisclosureImportInstruction>{disclosure.disclosureType.toString.toUpperCase}</DisclosureImportInstruction>

  def buildInitialDisclosureMA: Elem =
    <InitialDisclosureMA>{disclosure.initialDisclosureMA}</InitialDisclosureMA>

  def buildArrangementID: NodeSeq =
    disclosure.disclosureType match {
      case Dac6new => NodeSeq.Empty
      case _ =>
        disclosure.arrangementID.fold(NodeSeq.Empty) {
          arrangementID =>
            <ArrangementID>{arrangementID}</ArrangementID>
        }
    }

  def buildDisclosureID: NodeSeq =
    disclosure.disclosureType match {
      case Dac6rep | Dac6del =>
        disclosure.disclosureID.fold(NodeSeq.Empty) {
          disclosureID =>
            <DisclosureID>{disclosureID}</DisclosureID>
        }
      case _ => NodeSeq.Empty
    }

}
