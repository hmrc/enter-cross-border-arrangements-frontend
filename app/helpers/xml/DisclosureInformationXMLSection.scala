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

import scala.util.Try
import scala.xml.NodeSeq

case class DisclosureInformationXMLSection(submission: Submission) {

  val arrangementDetails = submission.arrangementDetails

  val hallmarksSection = HallmarksXMLSection(submission)

  val groupSize = 4000

  private[xml] def buildReason: NodeSeq =
    arrangementDetails.flatMap(_.reportingReason).fold(NodeSeq.Empty)(
      reason =>  <Reason>{reason.toUpperCase}</Reason>
    )

  private[xml] def buildDisclosureInformationSummary: NodeSeq =
    arrangementDetails.fold(NodeSeq.Empty) { arrangementDetails =>
      <Summary>
        <Disclosure_Name>{arrangementDetails.arrangementName}</Disclosure_Name>{arrangementDetails.arrangementDetails.grouped(groupSize).toList.map(string =>
        <Disclosure_Description>{string}</Disclosure_Description>)}
      </Summary>
    }

  private[xml] def buildNationalProvision: NodeSeq =
    arrangementDetails.fold(NodeSeq.Empty) { arrangementDetails =>
      arrangementDetails.nationalProvisionDetails.grouped(groupSize).toList.map { string =>
        <NationalProvision>{string}</NationalProvision>
      }
    }

  private[xml] def buildConcernedMS: NodeSeq =
    arrangementDetails.fold(NodeSeq.Empty) { arrangementDetails =>
      <ConcernedMSs>
        {arrangementDetails.countriesInvolved.map(country => <ConcernedMS>{country}</ConcernedMS>)}
      </ConcernedMSs>
    }

  private[xml] def buildArrangementDetails: NodeSeq =
    arrangementDetails match {
      case Some(arrangementDetails) =>
        val nodeBuffer = new xml.NodeBuffer
        nodeBuffer ++
          <ImplementingDate>{arrangementDetails.implementationDate}</ImplementingDate> ++
          buildReason ++
          buildDisclosureInformationSummary ++
          buildNationalProvision ++
          <Amount currCode={arrangementDetails.expectedValue.currency}>{arrangementDetails.expectedValue.amount}</Amount> ++
          buildConcernedMS
      case None => throw new Exception("Unable to construct XML from arrangement details")
    }

  def buildDisclosureInformation: NodeSeq =
    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    Try {
      <DisclosureInformation>
        {buildArrangementDetails}
        <MainBenefitTest1>false</MainBenefitTest1>
        {hallmarksSection.buildHallmarks}
      </DisclosureInformation>
    }.getOrElse(NodeSeq.Empty)
}
