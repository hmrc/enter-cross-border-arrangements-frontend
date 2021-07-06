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

import models.{CheckMode, CountryList}
import pages._
import pages.arrangement._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.CheckYourAnswersHelper

trait ArrangementRows extends RowBuilder { self: CheckYourAnswersHelper =>

  def whatIsThisArrangementCalledPage(id: Int): Option[Row] = userAnswers.get(WhatIsThisArrangementCalledPage, id) map {
    answer =>
      toRow(
        msgKey = "whatIsThisArrangementCalled",
        content = formatMaxChars(answer, maxVisibleChars),
        href = controllers.arrangement.routes.WhatIsThisArrangementCalledController.onPageLoad(id, CheckMode).url
      )
  }

  def whatIsTheImplementationDatePage(id: Int): Option[Row] = userAnswers.get(WhatIsTheImplementationDatePage, id) map {
    answer =>
      toRow(
        msgKey = "whatIsTheImplementationDate",
        content = Literal(answer.format(dateFormatter)),
        href = controllers.arrangement.routes.WhatIsTheImplementationDateController.onPageLoad(id, CheckMode).url
      )
  }

  def buildWhyAreYouReportingThisArrangementNow(id: Int): Option[Row] = userAnswers.get(WhyAreYouReportingThisArrangementNowPage, id) map {
    answer =>
      toRow(
        msgKey = "whyAreYouReportingThisArrangementNow",
        content = msg"whyAreYouReportingThisArrangementNow.$answer",
        href = controllers.arrangement.routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(id, CheckMode).url
      )
  }

  private def formatCountries(countries: Set[CountryList]): Html = {

    val list: String = if (countries.size > 1) {
      s"""<ul>
        |${countries.toSeq.sorted
        .map(
          a => s"<li>${msg"whichExpectedInvolvedCountriesArrangement.$a".resolve}</li>"
        )
        .mkString("\n")}
        |</ul>""".stripMargin
    } else {
      countries
        .map(
          a => msg"whichExpectedInvolvedCountriesArrangement.$a".resolve
        )
        .mkString
    }
    Html(list)
  }

  def whichExpectedInvolvedCountriesArrangement(id: Int): Option[Row] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage, id) map {
    answer =>
      toRow(
        msgKey = "whichExpectedInvolvedCountriesArrangement",
        content = formatCountries(answer),
        href = controllers.arrangement.routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(id, CheckMode).url
      )
  }

  def whatIsTheExpectedValueOfThisArrangement(id: Int): Option[Row] = userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage, id) map {
    answer =>
      toRow(
        msgKey = "whatIsTheExpectedValueOfThisArrangement",
        content = lit"${answer.currency} ${answer.amount}",
        href = controllers.arrangement.routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(id, CheckMode).url
      )
  }

  def whichNationalProvisionsIsThisArrangementBasedOn(id: Int): Option[Row] = userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage, id) map {
    answer =>
      toRow(
        msgKey = "whichNationalProvisionsIsThisArrangementBasedOn",
        content = formatMaxChars(answer, self.maxVisibleChars),
        href = controllers.arrangement.routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(id, CheckMode).url
      )
  }

  def giveDetailsOfThisArrangement(id: Int): Option[Row] = userAnswers.get(GiveDetailsOfThisArrangementPage, id) map {
    answer =>
      toRow(
        msgKey = "giveDetailsOfThisArrangement",
        content = formatMaxChars(answer, self.maxVisibleChars),
        href = controllers.arrangement.routes.GiveDetailsOfThisArrangementController.onPageLoad(id, CheckMode).url
      )
  }

}
