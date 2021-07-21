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

package models.arrangement

import controllers.exceptions.SomeInformationIsMissingException
import models.{ArrangementImplementingDateInvalidError, ArrangementNameEmptyError, CountryList, SubmissionError, UserAnswers}
import pages.GiveDetailsOfThisArrangementPage
import pages.arrangement._
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class ArrangementDetails(arrangementName: String,
                              implementationDate: LocalDate,
                              reportingReason: Option[String] = None,
                              countriesInvolved: List[CountryList],
                              expectedValue: ExpectedArrangementValue,
                              nationalProvisionDetails: String,
                              arrangementDetails: String
) {

  def validate: Either[SubmissionError, ArrangementDetails] =
    for {
      _ <- Either.cond(Option(arrangementName).exists(_.nonEmpty), arrangementName, ArrangementNameEmptyError)
      _ <- Either.cond(Option(implementationDate).isDefined, implementationDate, ArrangementImplementingDateInvalidError)
    } yield this
}

object ArrangementDetails {
  implicit val format: OFormat[ArrangementDetails] = Json.format[ArrangementDetails]

  private def getArrangementName(ua: UserAnswers, id: Int): String =
    ua.get(WhatIsThisArrangementCalledPage, id) match {
      case Some(arrangementName) => arrangementName
      case _                     => throw new SomeInformationIsMissingException(id, "Arrangement details must contain a name for Disclosure Information")
    }

  private def getImplementationDate(ua: UserAnswers, id: Int): LocalDate =
    ua.get(WhatIsTheImplementationDatePage, id) match {
      case Some(date) => date
      case _          => throw new SomeInformationIsMissingException(id, "Arrangement details must contain an expected implementation date for Disclosure Information")
    }

  private def getReportingReason(ua: UserAnswers, id: Int): Option[String] =
    ua.get(WhyAreYouReportingThisArrangementNowPage, id)
      .fold(
        throw new SomeInformationIsMissingException(id,
                                                    "Arrangement details must contain a reporting reason when 'yes' " +
                                                      "to 'do you know reporting reason' is selected"
        )
      )(
        reason => Some(reason.toString.toUpperCase)
      )

  private def getCountriesInvolved(ua: UserAnswers, id: Int): List[CountryList] =
    ua.get(WhichExpectedInvolvedCountriesArrangementPage, id) match {
      case Some(countries) => countries.toList.sorted
      case _ =>
        throw new SomeInformationIsMissingException(id, "Arrangement details must contain expected involved countries details for Disclosure Information")
    }

  private def getNationalProvisionDetails(ua: UserAnswers, id: Int): String =
    ua.get(WhichNationalProvisionsIsThisArrangementBasedOnPage, id) match {
      case Some(details) => details
      case _             => throw new SomeInformationIsMissingException(id, "Arrangement details must contain national provision details for Disclosure Information")
    }

  private def getArrangementDetails(ua: UserAnswers, id: Int): String =
    ua.get(GiveDetailsOfThisArrangementPage, id) match {
      case Some(details) => details
      case _             => throw new SomeInformationIsMissingException(id, "Arrangement details must contain details of arrangement for Disclosure Information")
    }

  def buildArrangementDetails(ua: UserAnswers, id: Int): ArrangementDetails =
    new ArrangementDetails(
      arrangementName = getArrangementName(ua, id),
      implementationDate = getImplementationDate(ua, id),
      reportingReason = getReportingReason(ua, id),
      countriesInvolved = getCountriesInvolved(ua, id),
      expectedValue = ExpectedArrangementValue.buildExpectedArrangementValue(ua, id),
      nationalProvisionDetails = getNationalProvisionDetails(ua, id),
      arrangementDetails = getArrangementDetails(ua, id)
    )
}
