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
import models.arrangement.ArrangementDetails
import pages.arrangement._
import pages.hallmarks.HallmarkDetailsPage

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object DisclosureInformationXMLSection extends XMLBuilder {

  private[xml] def buildReason(arrangementDetails: ArrangementDetails): NodeSeq = {
    arrangementDetails.reportingReason.fold(NodeSeq.Empty)(
      reason =>  <Reason>{reason.toUpperCase}</Reason>
    )
  }

  private[xml] def buildDisclosureInformationSummary(arrangementDetails: ArrangementDetails): Elem = {
    <Summary>
      <Disclosure_Name>{arrangementDetails.arrangementName}</Disclosure_Name>
      {arrangementDetails.arrangementDetails.grouped(4000).toList.map(string =>
      <Disclosure_Description>{string}</Disclosure_Description>)}
    </Summary>
  }

  private[xml] def buildNationalProvision(arrangementDetails: ArrangementDetails): NodeSeq = {
    arrangementDetails.nationalProvisionDetails.grouped(4000).toList.map { string =>
      <NationalProvision>{string}</NationalProvision>
    }
  }

  private[xml] def buildConcernedMS(arrangementDetails: ArrangementDetails): Elem = {
    <ConcernedMSs>
      {arrangementDetails.countriesInvolved.map(country => <ConcernedMS>{country}</ConcernedMS>)}
    </ConcernedMSs>
  }

  private[xml] def buildArrangementDetails(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(ArrangementDetailsPage, id) match {
      case Some(arrangementDetails) =>
        val nodeBuffer = new xml.NodeBuffer
        nodeBuffer ++
          <ImplementingDate>{arrangementDetails.implementationDate}</ImplementingDate> ++
          buildReason(arrangementDetails) ++
          buildDisclosureInformationSummary(arrangementDetails) ++
          buildNationalProvision(arrangementDetails) ++
          <Amount currCode={arrangementDetails.expectedValue.currency}>{arrangementDetails.expectedValue.amount}</Amount> ++
          buildConcernedMS(arrangementDetails)
      case None => throw new Exception("Unable to construct XML from arrangement details")
    }
  }

  private[xml] def buildHallmarks(userAnswers: UserAnswers, id: Int): Elem = {

    userAnswers.get(HallmarkDetailsPage, id) match {
      case Some(hallmarkDetails) =>

        val hallmarkContent = hallmarkDetails.hallmarkContent.fold(NodeSeq.Empty)(content =>
        content.grouped(4000).toList.map(string => <DAC6D1OtherInfo>{string}</DAC6D1OtherInfo>))

        <Hallmarks>
          <ListHallmarks>
            {hallmarkDetails.hallmarkType.map(hallmark => <Hallmark>{hallmark}</Hallmark>)}
          </ListHallmarks>
          {hallmarkContent}
        </Hallmarks>

      case _ => throw new Exception("Unable to construct hallmarks XML from hallmark details")
    }
  }

  override def toXml(userAnswers: UserAnswers, id: Int): Either[Throwable, Elem] = {
    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    Try {
      <DisclosureInformation>
        {buildArrangementDetails(userAnswers, id)}
        <MainBenefitTest1>false</MainBenefitTest1>
        {buildHallmarks(userAnswers, id)}
      </DisclosureInformation>
    }.toEither
  }
}
