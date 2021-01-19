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

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object DisclosureInformationXMLSection extends XMLBuilder {

  private[xml] def buildImplementingDate(userAnswers: UserAnswers, id: Int): Elem = {

    userAnswers.get(WhatIsTheImplementationDatePage, id) match {
      case Some(date) => <ImplementingDate>{date}</ImplementingDate>
      case None => throw new Exception("Missing disclosure information implementing date")
    }
  }

  private[xml] def buildReason(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(DoYouKnowTheReasonToReportArrangementNowPage, id) match {
      case Some(true) =>
        userAnswers.get(WhyAreYouReportingThisArrangementNowPage, id)
          .fold(NodeSeq.Empty)(reason => <Reason>{reason.toString.toUpperCase}</Reason>)
      case _ => NodeSeq.Empty
    }
  }

  private[xml] def buildDisclosureInformationSummary(userAnswers: UserAnswers, id: Int): Elem = {
    val mandatoryDisclosureName: Elem = userAnswers.get(WhatIsThisArrangementCalledPage, id) match {
      case Some(name) => <Disclosure_Name>{name}</Disclosure_Name>
      case None => throw new Exception("Missing arrangement name when building DisclosureInformationSummary")
    }

    val mandatoryDisclosureDescription: NodeSeq = userAnswers.get(GiveDetailsOfThisArrangementPage, id) match {
      case Some(description) =>
        val splitString = description.grouped(4000).toList

        splitString.map(string =>
          <Disclosure_Description>{string}</Disclosure_Description>
        )
      case None => throw new Exception("Missing disclosure description when building DisclosureInformationSummary")
    }

    <Summary>
      {mandatoryDisclosureName}
      {mandatoryDisclosureDescription}
    </Summary>
  }

  private[xml] def buildNationalProvision(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage, id) match {
      case Some(nationalProvisions) =>
        val splitString = nationalProvisions.grouped(4000).toList

        splitString.map { string =>
          <NationalProvision>{string}</NationalProvision>
        }
      case None => throw new Exception("Missing national provision in disclosure information")
    }
  }

  private[xml] def buildAmountType(userAnswers: UserAnswers, id: Int): Elem = {
    userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage, id) match {
      case Some(value) => <Amount currCode={value.currency}>{value.amount}</Amount>
      case None => throw new Exception("Missing amount type in disclosure information")
    }
  }

  private[xml] def buildConcernedMS(userAnswers: UserAnswers, id: Int): Elem = {
    val mandatoryConcernedMS: Set[Elem] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage, id) match {
      case Some(countries) =>
        countries.map {
          country =>
            <ConcernedMS>{country.toString}</ConcernedMS>
        }
      case None => throw new Exception("Missing countries when building ConcernedMS")
    }

    <ConcernedMSs>{mandatoryConcernedMS}</ConcernedMSs>
  }

  private[xml] def buildHallmarks(userAnswers: UserAnswers, id: Int): Elem = {

    val mandatoryHallmarks: Set[Elem] = {
      userAnswers.get(HallmarkDPage, id) match {
        case Some(hallmarkDSet) =>
          hallmarkDSet.flatMap {
            hallmark =>
              if (hallmark == D1) {
                userAnswers.get(HallmarkD1Page, id) match {
                  case Some(hallmarkSet) =>
                    hallmarkSet.map(hallmark =>
                      <Hallmark>{hallmark.toString}</Hallmark>
                    )
                  case None => throw new Exception("Missing D1 hallmarks when building the section")
                }
              } else {
                Set(<Hallmark>{"DAC6D2"}</Hallmark>)
              }
          }
        case _ => throw new Exception("Missing hallmarks when building the section")
      }
    }

    val dac6D1OtherInfo: NodeSeq = userAnswers.get(HallmarkD1Page, id: Int) match {
      case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
        userAnswers.get(HallmarkD1OtherPage, id) match {
          case Some(description) =>
            val splitString = description.grouped(4000).toList

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

  override def toXml(userAnswers: UserAnswers, id: Int): Either[Throwable, Elem] = {
    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    Try {
      <DisclosureInformation>
        {buildImplementingDate(userAnswers, id)}
        {buildReason(userAnswers, id)}
        {buildDisclosureInformationSummary(userAnswers, id)}
        {buildNationalProvision(userAnswers, id)}
        {buildAmountType(userAnswers, id)}
        {buildConcernedMS(userAnswers, id)}
        <MainBenefitTest1>false</MainBenefitTest1>
        {buildHallmarks(userAnswers, id)}
      </DisclosureInformation>
    }.toEither
  }
}
