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

package utils.rows

import models.CheckMode
import models.arrangement.{WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import pages._
import pages.arrangement._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

trait ArrangementRows extends RowBuilder {

  def whatIsThisArrangementCalledPage: Option[Row] = userAnswers.get(WhatIsThisArrangementCalledPage) map { answer =>

    toRow(
      msgKey  = "whatIsThisArrangementCalled",
      content = formatMaxChars(answer),
      href    = controllers.arrangement.routes.WhatIsThisArrangementCalledController.onPageLoad(CheckMode).url
    )
  }

  def whatIsTheImplementationDatePage: Option[Row] = userAnswers.get(WhatIsTheImplementationDatePage) map { answer =>

    toRow(
      msgKey  = "whatIsTheImplementationDate",
      content = Literal(answer.format(dateFormatter)),
      href    = controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(CheckMode).url
    )
  }

  def buildReportingThisArrangement: Seq[Row] =
    (userAnswers.get(DoYouKnowTheReasonToReportArrangementNowPage), userAnswers.get(WhyAreYouReportingThisArrangementNowPage)) match {

      case (Some(true), Some(reason)) =>
        Seq(doYouKnowTheReasonToReportArrangementNow(true), whyAreYouReportingThisArrangementNow(reason))
      case _ =>
        Seq(doYouKnowTheReasonToReportArrangementNow(false))
    }

  private def doYouKnowTheReasonToReportArrangementNow(answer: Boolean): Row =

    toRow(
      msgKey  = "doYouKnowTheReasonToReportArrangementNow",
      content = yesOrNo(answer),
      href    = controllers.arrangement.routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(CheckMode).url
    )

  private def whyAreYouReportingThisArrangementNow(answer: WhyAreYouReportingThisArrangementNow): Row =

    toRow(
      msgKey  = "whyAreYouReportingThisArrangementNow",
      content = msg"whyAreYouReportingThisArrangementNow.$answer",
      href    = controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(CheckMode).url
    )

  private def formatCountries(countries: Set[WhichExpectedInvolvedCountriesArrangement]): Html = {

    val list: String = if (countries.size > 1) {
      s"""<ul>
        |${countries.map(a => s"<li>${msg"whichExpectedInvolvedCountriesArrangement.$a".resolve}</li>").mkString("\n")}
        |</ul>""".stripMargin
    } else {
      countries.map(a => msg"whichExpectedInvolvedCountriesArrangement.$a".resolve).mkString
    }
    Html(list)
  }

  def whichExpectedInvolvedCountriesArrangement: Option[Row] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage) map { answer =>

    toRow(
      msgKey  = "whichExpectedInvolvedCountriesArrangement",
      content = formatCountries(answer),
      href    = controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(CheckMode).url
    )
  }

  def whatIsTheExpectedValueOfThisArrangement: Option[Row] = userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage) map { answer =>

    toRow(
      msgKey  = "whatIsTheExpectedValueOfThisArrangement",
      content = lit"${answer.currency} ${answer.amount}",
      href    = controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(CheckMode).url
    )
  }

  def whichNationalProvisionsIsThisArrangementBasedOn: Option[Row] = userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage) map { answer =>

    toRow(
      msgKey  = "whichNationalProvisionsIsThisArrangementBasedOn",
      content = formatMaxChars(answer),
      href    = controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(CheckMode).url
    )
  }

  def giveDetailsOfThisArrangement: Option[Row] = userAnswers.get(GiveDetailsOfThisArrangementPage) map { answer =>

    toRow(
      msgKey  = "giveDetailsOfThisArrangement",
      content = formatMaxChars(answer),
      href    = controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(CheckMode).url
    )
  }

}
