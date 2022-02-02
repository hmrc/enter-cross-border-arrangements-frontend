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

package utils.model.rows

import models.{CountryList, SelectType}
import models.intermediaries.Intermediary
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}
import utils.SummaryListDisplay.DisplayRow

trait IntermediariesModelRows extends DisplayRowBuilder {

  def intermediariesType(intermediary: Intermediary)(implicit messages: Messages): DisplayRow = {
    val selectType = (intermediary.individual, intermediary.organisation) match {
      case (Some(_), None) => SelectType.Individual
      case (None, Some(_)) => SelectType.Organisation
    }

    DisplayRow(
      key = Key(msg"intermediariesType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
      value = Value(msg"intermediariesType.$selectType")
    )
  }

  def isExemptionKnown(intermediary: Intermediary)(implicit messages: Messages): DisplayRow =
    DisplayRow(
      key = Key(msg"isExemptionKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
      value = Value(msg"isExemptionKnown.${intermediary.isExemptionKnown}")
    )

  def isExemptionCountryKnown(intermediary: Intermediary)(implicit messages: Messages): Option[DisplayRow] =
    intermediary.isExemptionCountryKnown map {
      answer =>
        DisplayRow(
          key = Key(msg"isExemptionCountryKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(yesOrNo(answer))
        )
    }

  def exemptCountries(intermediary: Intermediary)(implicit messages: Messages): Option[DisplayRow] = intermediary.exemptCountries map {
    answer =>
      DisplayRow(
        key = Key(msg"exemptCountries.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Html(formatExemptCountriesList(answer, answer.tail.isEmpty)))
      )
  }

  private def formatExemptCountriesList(selectedCountries: Set[CountryList], singleItem: Boolean)(implicit messages: Messages) = {

    val getCountryName = selectedCountries.toSeq.sorted.map(_.toString).map {
      countryCode =>
        msg"countriesListCheckboxes.$countryCode".resolve
    }

    if (singleItem) {
      getCountryName.head
    } else {
      s"<ul class='govuk-list govuk-list--bullet'>${getCountryName.foldLeft("")(
        (a, b) => s"$a<li>$b</li>"
      )}</ul>"
    }
  }

  def whatTypeofIntermediary(intermediary: Intermediary)(implicit messages: Messages): DisplayRow =
    DisplayRow(
      key = Key(msg"whatTypeofIntermediary.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
      value = Value(msg"whatTypeofIntermediary.${intermediary.whatTypeofIntermediary}")
    )

}
