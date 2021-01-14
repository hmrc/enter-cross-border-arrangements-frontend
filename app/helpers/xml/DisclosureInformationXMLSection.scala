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
import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import pages.arrangement._
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}

import scala.xml.{Elem, NodeSeq}

object DisclosureInformationXMLSection extends XMLBuilder {

  private[xml] def buildDisclosureInformationSummary(userAnswers: UserAnswers): Elem = {
    val mandatoryDisclosureName = userAnswers.get(WhatIsThisArrangementCalledPage) match {
      case Some(name) => Seq(<Disclosure_Name>{name}</Disclosure_Name>)
      case None => NodeSeq.Empty
    }

    val mandatoryDisclosureDescription: NodeSeq = userAnswers.get(GiveDetailsOfThisArrangementPage) match {
      case Some(description) =>
        val splitString = description.grouped(10).toList

        splitString.map(string =>
          <Disclosure_Description>{string}</Disclosure_Description>
        )
      case None => NodeSeq.Empty
    }

    val nodeBuffer = new xml.NodeBuffer

    <Summary>
      {nodeBuffer ++
      mandatoryDisclosureName ++
      mandatoryDisclosureDescription
      }
    </Summary>
  }

  private[xml] def buildConcernedMS(userAnswers: UserAnswers): Elem = {
    val mandatoryConcernedMS: Set[Elem] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage) match {
      case Some(countries) =>
        countries.map {
          country =>
            <ConcernedMS>{country.toString}</ConcernedMS>
        }
      case None => Set.empty[Elem]
    }

    <ConcernedMSs>{mandatoryConcernedMS}</ConcernedMSs>
  }

  private[xml] def buildHallmarks(userAnswers: UserAnswers): Elem = {

    val mandatoryHallmarks: Set[Elem] = {
      userAnswers.get(HallmarkDPage) match {
        case Some(hallmarkDSet) =>
          hallmarkDSet.flatMap {
            hallmark =>
              if (hallmark == D1) {
                userAnswers.get(HallmarkD1Page) match {
                  case Some(hallmarkSet) =>
                    hallmarkSet.map(hallmark =>
                      <Hallmark>{hallmark.toString}</Hallmark>
                    )
                  case None => Set.empty[Elem]
                }
              } else {
                Set(<Hallmark>{"DAC6D2"}</Hallmark>)
              }
          }
        case _ => Set.empty[Elem]
      }
    }

    val dac6D1OtherInfo: NodeSeq = userAnswers.get(HallmarkD1Page) match {
      case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
        userAnswers.get(HallmarkD1OtherPage) match {
          case Some(description) =>
            val splitString = description.grouped(10).toList

            splitString.map(string =>
              <DAC6D1OtherInfo>{string}</DAC6D1OtherInfo>
            )
          case None => NodeSeq.Empty
        }
      case _ => NodeSeq.Empty
    }

    <Hallmarks>
      <ListHallmarks>
        {mandatoryHallmarks}
      </ListHallmarks>
      {dac6D1OtherInfo}
    </Hallmarks>
  }

  override def toXml(userAnswers: UserAnswers): Elem = {
    val mandatoryImplementingDate = userAnswers.get(WhatIsTheImplementationDatePage) match {
      case Some(date) => Seq(<ImplementingDate>{date}</ImplementingDate>)
      case None => NodeSeq.Empty
    }

    val reason: NodeSeq = userAnswers.get(DoYouKnowTheReasonToReportArrangementNowPage) match {
      case Some(reasonKnown) if reasonKnown =>
        userAnswers.get(WhyAreYouReportingThisArrangementNowPage)
          .fold(NodeSeq.Empty)(reason => <Reason>{reason.toString.toUpperCase}</Reason>)
      case _ => NodeSeq.Empty
    }

    val mandatoryNationalProvision: NodeSeq = userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage) match {
      case Some(nationalProvisions) =>
        val splitString = nationalProvisions.grouped(10).toList

        splitString.map(string =>
          <NationalProvision>{string}</NationalProvision>
        )
      case None => NodeSeq.Empty
    }

    val mandatoryAmountType: NodeSeq = userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage) match {
      case Some(value) => <Amount currCode={value.currency}>{value.amount}</Amount>
      case None => NodeSeq.Empty
    }

    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    <DisclosureInformation>
      {mandatoryImplementingDate}
      {reason}
      {buildDisclosureInformationSummary(userAnswers)}
      {mandatoryNationalProvision}
      {mandatoryAmountType}
      {buildConcernedMS(userAnswers)}
      <MainBenefitTest1>false</MainBenefitTest1>
      {buildHallmarks(userAnswers)}
    </DisclosureInformation>
  }
}
