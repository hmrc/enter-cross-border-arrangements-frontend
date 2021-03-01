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

import models.{ArrangementDetailsNotDefinedError, Submission}
import models.arrangement.ArrangementDetails

import scala.util.Try
import scala.xml.NodeSeq

case class DisclosureInformationXMLSection(submission: Submission) {

  val arrangementDetails: ArrangementDetails = submission.arrangementDetails
    .orElse(throw new IllegalStateException(ArrangementDetailsNotDefinedError.defaultMessage))
    .get.validate.fold(e => throw new IllegalStateException(e.defaultMessage), identity)

  val hallmarksSection: HallmarksXMLSection = HallmarksXMLSection(submission)

  val groupSize = 4000

  private[xml] def buildReason: NodeSeq =
    arrangementDetails.reportingReason.fold(NodeSeq.Empty)(reason =>
      <Reason>{reason.toUpperCase}</Reason>
    )

  private[xml] def buildDisclosureInformationSummary: NodeSeq =
    <Summary>
      <Disclosure_Name>{arrangementDetails.arrangementName}</Disclosure_Name>{arrangementDetails.arrangementDetails.grouped(groupSize).toList.map(string =>
      <Disclosure_Description>{string}</Disclosure_Description>)}
    </Summary>

  private[xml] def buildNationalProvision: NodeSeq =
    arrangementDetails.nationalProvisionDetails.grouped(groupSize).toList.map { string =>
      <NationalProvision>{string}</NationalProvision>
    }

  private[xml] def buildConcernedMS: NodeSeq =
    <ConcernedMSs>
      {arrangementDetails.countriesInvolved.map(country => <ConcernedMS>{country}</ConcernedMS>)}
    </ConcernedMSs>


  private[xml] def buildArrangementDetails: NodeSeq =
    new xml.NodeBuffer ++
      <ImplementingDate>{arrangementDetails.implementationDate}</ImplementingDate> ++
      buildReason ++
      buildDisclosureInformationSummary ++
      buildNationalProvision ++
      <Amount currCode={arrangementDetails.expectedValue.currency}>{arrangementDetails.expectedValue.amount}</Amount> ++
      buildConcernedMS

  def buildDisclosureInformation: NodeSeq = {
    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    Try {
      <DisclosureInformation>
        {buildArrangementDetails}
        <MainBenefitTest1>false</MainBenefitTest1>
        {hallmarksSection.buildHallmarks}
      </DisclosureInformation>
    }.getOrElse(NodeSeq.Empty)
  }
}
