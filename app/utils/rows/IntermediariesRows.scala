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
import models.intermediaries.ExemptCountries
import pages.intermediaries.{ExemptCountriesPage, IntermediariesTypePage, IsExemptionCountryKnownPage, IsExemptionKnownPage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}

trait IntermediariesRows extends RowBuilder {


  def intermediariesType(id: Int): Option[Row] = userAnswers.get(IntermediariesTypePage, id) map {
    answer =>
      Row(
        key     = Key(msg"intermediariesType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"intermediariesType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"intermediariesType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isExemptionKnown(id: Int): Option[Row] = userAnswers.get(IsExemptionKnownPage, id) map {
    answer =>
      Row(
        key     = Key(msg"isExemptionKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"isExemptionKnown.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isExemptionKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isExemptionCountryKnown(id: Int): Option[Row] = userAnswers.get(IsExemptionCountryKnownPage, id) map {
    answer =>
      Row(
        key     = Key(msg"isExemptionCountryKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isExemptionCountryKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def gbSort(exemptCountries : List[String]) : List[String] = {

    val gbMessage = msg"countriesListCheckboxes.GB".resolve

    if (exemptCountries.contains(gbMessage)) {
      List(gbMessage) ++ exemptCountries.filter(_ != gbMessage).sorted
    } else {
      exemptCountries.sorted
    }
  }

  def exemptCountries(id: Int): Option[Row] = userAnswers.get(ExemptCountriesPage, id) map {
    answer =>

      Row(
        key     = Key(msg"exemptCountries.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(Html(formatExemptCountriesList(answer, answer.tail.isEmpty))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"exemptCountries.checkYourAnswersLabel"))
          )
        )
      )
  }

  private def formatExemptCountriesList(selectedCountries: Set[ExemptCountries], singleItem: Boolean) = {

    val getCountryName = selectedCountries.map(_.toString).toSeq.map(
      countryCode => msg"countriesListCheckboxes.$countryCode".resolve).sorted

    if (singleItem) {
      getCountryName.head
    } else {
      s"<ul class='govuk-list govuk-list--bullet'>${getCountryName.foldLeft("")((a, b) => s"$a<li>$b</li>")}</ul>"
    }
  }

  import pages.intermediaries.WhatTypeofIntermediaryPage
  def whatTypeofIntermediary(id: Int): Option[Row] = userAnswers.get(WhatTypeofIntermediaryPage, id) map {
    answer =>
      Row(
        key     = Key(msg"whatTypeofIntermediary.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"whatTypeofIntermediary.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatTypeofIntermediary.checkYourAnswersLabel"))
          )
        )
      )
  }

  def youHaveNotAddedAnyIntermediaries(id: Int): Option[Row] = userAnswers.get(pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage, id) map {
    answer =>
      toRow(
        msgKey  = "youHaveNotAddedAnyIntermediaries",
        content = msg"youHaveNotAddedAnyIntermediaries.$answer",
        href    = controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(id, CheckMode).url
      )
  }
}
