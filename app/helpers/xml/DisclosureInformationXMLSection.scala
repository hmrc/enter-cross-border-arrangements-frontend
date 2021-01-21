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

import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import models.{CompletionState, InProgress, NotStarted, UserAnswers}
import pages.arrangement._
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}

import scala.xml.{Elem, NodeSeq}

object DisclosureInformationXMLSection extends XMLBuilder {

  private[xml] def buildImplementingDate(userAnswers: UserAnswers): Either[CompletionState, NodeSeq] =

    userAnswers.get(WhatIsTheImplementationDatePage).toRight(NotStarted) map { date => <ImplementingDate>{date}</ImplementingDate> }

  private[xml] def buildReason(userAnswers: UserAnswers): Either[CompletionState, NodeSeq] =

    userAnswers.get(DoYouKnowTheReasonToReportArrangementNowPage).toRight(InProgress) flatMap {
      case true =>
        userAnswers.get(WhyAreYouReportingThisArrangementNowPage)
          .toRight(InProgress).map(reason => <Reason>{reason.toString.toUpperCase}</Reason>)
    }

  private[xml] def buildDisclosureInformationSummary(userAnswers: UserAnswers): Elem = {
    val mandatoryDisclosureName: Elem = userAnswers.get(WhatIsThisArrangementCalledPage) match {
      case Some(name) => <Disclosure_Name>{name}</Disclosure_Name>
      case None       => throw new Exception("Missing arrangement name when building DisclosureInformationSummary")
    }

    val mandatoryDisclosureDescription: NodeSeq = userAnswers.get(GiveDetailsOfThisArrangementPage) match {
      case Some(description) =>
        val splitString = description.grouped(4000).toList

        splitString.map(string =>
          <Disclosure_Description>{string}</Disclosure_Description>
        )
      case None => throw new Exception("Missing disclosure description when building DisclosureInformationSummary")
    }

    <Summary>{mandatoryDisclosureName}{mandatoryDisclosureDescription}</Summary>
  }

  private[xml] def buildNationalProvision(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage) match {
      case Some(nationalProvisions) =>
        val splitString = nationalProvisions.grouped(4000).toList

        splitString.map { string =>
          <NationalProvision>{string}</NationalProvision>
        }
      case None => throw new Exception("Missing national provision in disclosure information")
    }
  }

  private[xml] def buildAmountType(userAnswers: UserAnswers): Elem = {
    userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage) match {
      case Some(value) => <Amount currCode={value.currency}>{value.amount}</Amount>
      case None        => throw new Exception("Missing amount type in disclosure information")
    }
  }

  private[xml] def buildConcernedMS(userAnswers: UserAnswers): Elem = {
    val mandatoryConcernedMS: Set[Elem] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage) match {
      case Some(countries) =>
        countries.map {
          country => <ConcernedMS>{country.toString}</ConcernedMS>
        }
      case None => throw new Exception("Missing countries when building ConcernedMS")
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
                  case None => throw new Exception("Missing D1 hallmarks when building the section")
                }
              } else {
                Set(<Hallmark>{"DAC6D2"}</Hallmark>)
              }
          }
        case _ => throw new Exception("Missing hallmarks when building the section")
      }
    }

    val dac6D1OtherInfo: NodeSeq = userAnswers.get(HallmarkD1Page) match {
      case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
        userAnswers.get(HallmarkD1OtherPage) match {
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
      <ListHallmarks>{mandatoryHallmarks}</ListHallmarks>{dac6D1OtherInfo}
    </Hallmarks>
  }

  override def toXml(userAnswers: UserAnswers): Either[CompletionState, Elem] = {

    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    val content: Either[CompletionState, NodeSeq] = for {
      implementingDate <- buildImplementingDate(userAnswers)
      reason <- buildReason(userAnswers)
      disclosureInformationSummary = buildDisclosureInformationSummary(userAnswers)
      nationalProvision = buildNationalProvision(userAnswers)
      amountType = buildAmountType(userAnswers)
      concernedMS = buildConcernedMS(userAnswers)
      mainBenefitTest1 = <MainBenefitTest1>false</MainBenefitTest1>
      hallmarks = buildHallmarks(userAnswers)
    } yield {
      (implementingDate
        ++ reason
        ++ disclosureInformationSummary
        ++ nationalProvision
        ++ amountType
        ++ concernedMS
        ++ mainBenefitTest1
        ++ hallmarks
        ).flatten
    }

    build(content) { nodes =>
      <DisclosureInformation>{nodes}</DisclosureInformation>
    }
  }
}
