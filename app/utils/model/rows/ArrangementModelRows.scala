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

package utils.model.rows

import models.CountryList
import models.arrangement.ArrangementDetails
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.SummaryListDisplay.DisplayRow

trait ArrangementModelRows extends DisplayRowBuilder {

  def whatIsThisArrangementCalledPage(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    Some(
      toDisplayRow(
        msgKey = "whatIsThisArrangementCalled",
        content = formatMaxChars(arrangementDetails.arrangementName)
      )
    )

  def whatIsTheImplementationDatePage(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    Some(
      toDisplayRow(
        msgKey = "whatIsTheImplementationDate",
        content = Literal(arrangementDetails.implementationDate.format(dateFormatter))
      )
    )

  def buildWhyAreYouReportingThisArrangementNow(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    arrangementDetails.reportingReason map {
      answer =>
        toDisplayRow(
          msgKey = "whyAreYouReportingThisArrangementNow",
          content = msg"whyAreYouReportingThisArrangementNow.${answer.substring(0, 1).toLowerCase + answer.substring(1)}"
        )
    }

  private def formatCountries(countries: List[CountryList])(implicit messages: Messages): Html = {
    val list: String = if (countries.size > 1) {
      s"""<ul>
        |${countries.sorted
        .map(
          a => s"<li>${msg"whichExpectedInvolvedCountriesArrangement.${a.toString}".resolve}</li>"
        )
        .mkString("\n")}
        |</ul>""".stripMargin
    } else {
      countries
        .map(
          a => msg"whichExpectedInvolvedCountriesArrangement.${a.toString}".resolve
        )
        .mkString
    }
    Html(list)
  }

  def whichExpectedInvolvedCountriesArrangement(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    Some(
      toDisplayRow(
        msgKey = "whichExpectedInvolvedCountriesArrangement",
        content = formatCountries(arrangementDetails.countriesInvolved)
      )
    )

  def whatIsTheExpectedValueOfThisArrangement(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    Some(
      toDisplayRow(
        msgKey = "whatIsTheExpectedValueOfThisArrangement",
        content = lit"${arrangementDetails.expectedValue.currency} ${arrangementDetails.expectedValue.amount}"
      )
    )

  def whichNationalProvisionsIsThisArrangementBasedOn(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    Some(
      toDisplayRow(
        msgKey = "whichNationalProvisionsIsThisArrangementBasedOn",
        content = lit"${arrangementDetails.nationalProvisionDetails}"
      )
    )

  def giveDetailsOfThisArrangement(arrangementDetails: ArrangementDetails)(implicit messages: Messages): Option[DisplayRow] =
    Some(
      toDisplayRow(
        msgKey = "giveDetailsOfThisArrangement",
        content = lit"${arrangementDetails.arrangementDetails}"
      )
    )

}
