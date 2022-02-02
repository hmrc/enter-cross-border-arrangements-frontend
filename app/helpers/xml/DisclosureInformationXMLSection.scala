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

package helpers.xml

import models.Submission
import models.arrangement.ArrangementDetails

import scala.util.Try
import scala.xml.NodeSeq

case class DisclosureInformationXMLSection(submission: Submission) {

  val arrangementDetails: Option[ArrangementDetails] = submission.arrangementDetails
    .map(_.validate.fold(e => throw new IllegalStateException(e.defaultMessage), identity))

  val hallmarksSection: HallmarksXMLSection = HallmarksXMLSection(submission)

  val groupSize = 4000

  private[xml] def buildReason(details: ArrangementDetails): NodeSeq =
    details.reportingReason.fold(NodeSeq.Empty) {
      reportingReason =>
        <Reason>{reportingReason.toUpperCase}</Reason>
    }

  private[xml] def buildDisclosureInformationSummary(details: ArrangementDetails): NodeSeq =
    <Summary>
      <Disclosure_Name>{details.arrangementName}</Disclosure_Name>{
      details.arrangementDetails
        .grouped(groupSize)
        .toList
        .map(
          string => <Disclosure_Description>{string}</Disclosure_Description>
        )
    }
    </Summary>

  private[xml] def buildNationalProvision(details: ArrangementDetails): NodeSeq =
    details.nationalProvisionDetails.grouped(groupSize).toList.map {
      string =>
        <NationalProvision>{string}</NationalProvision>
    }

  private[xml] def buildConcernedMS(details: ArrangementDetails): NodeSeq =
    <ConcernedMSs>{
      details.countriesInvolved.map(
        country => <ConcernedMS>{country}</ConcernedMS>
      )
    }</ConcernedMSs>

  private[xml] def buildArrangementDetails(details: ArrangementDetails): NodeSeq =
    new xml.NodeBuffer ++
      <ImplementingDate>{details.implementationDate}</ImplementingDate> ++
      buildReason(details) ++
      buildDisclosureInformationSummary(details) ++
      buildNationalProvision(details) ++
      <Amount currCode={details.expectedValue.currency}>{details.expectedValue.amount}</Amount> ++
      buildConcernedMS(details)

  def buildDisclosureInformation: NodeSeq =
    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    arrangementDetails.fold(NodeSeq.Empty) {
      details =>
        Try {
          <DisclosureInformation>
          {buildArrangementDetails(details)}
          <MainBenefitTest1>false</MainBenefitTest1>
          {hallmarksSection.buildHallmarks}
        </DisclosureInformation>
        }.getOrElse(NodeSeq.Empty)
    }

}

object DisclosureInformationXMLSection {

  val dummyDisclosureInformation: NodeSeq =
    <DisclosureInformation>
      <ImplementingDate>2018-06-25</ImplementingDate>
      <Summary>
        <Disclosure_Name>xxxxxx</Disclosure_Name>
        <Disclosure_Description>xxxxxxxxx</Disclosure_Description>
      </Summary>
      <NationalProvision>xxxxxxxxxx</NationalProvision>
      <Amount currCode="GBP">0</Amount>
      <ConcernedMSs>
        <ConcernedMS>GB</ConcernedMS>
      </ConcernedMSs>
      <MainBenefitTest1>false</MainBenefitTest1>
      <Hallmarks>
        <ListHallmarks>
          <Hallmark>DAC6D1Other</Hallmark>
        </ListHallmarks>
        <DAC6D1OtherInfo>xxxxx</DAC6D1OtherInfo>
      </Hallmarks>
    </DisclosureInformation>
}
