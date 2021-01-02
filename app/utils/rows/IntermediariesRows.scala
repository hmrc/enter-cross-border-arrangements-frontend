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
import pages.intermediaries.{ExemptCountriesPage, IntermediariesTypePage, IsExemptionCountryKnownPage, IsExemptionKnownPage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}

trait IntermediariesRows extends RowBuilder {


  def intermediariesType: Option[Row] = userAnswers.get(IntermediariesTypePage) map {
    answer =>
      Row(
        key     = Key(msg"intermediariesType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"intermediariesType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"intermediariesType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isExemptionKnown: Option[Row] = userAnswers.get(IsExemptionKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isExemptionKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"isExemptionKnown.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isExemptionKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isExemptionCountryKnown: Option[Row] = userAnswers.get(IsExemptionCountryKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isExemptionCountryKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isExemptionCountryKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def exemptCountries: Option[Row] = userAnswers.get(ExemptCountriesPage) map {
    answer =>
      Row(
        key     = Key(msg"exemptCountries.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(Html(answer.map(a => msg"countriesListCheckboxes.$a".resolve).mkString(",<br>"))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"exemptCountries.checkYourAnswersLabel"))
          )
        )
      )
  }

  import pages.intermediaries.WhatTypeofIntermediaryPage
  def whatTypeofIntermediary: Option[Row] = userAnswers.get(WhatTypeofIntermediaryPage) map {
    answer =>
      Row(
        key     = Key(msg"whatTypeofIntermediary.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"whatTypeofIntermediary.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatTypeofIntermediary.checkYourAnswersLabel"))
          )
        )
      )
  }

  def youHaveNotAddedAnyIntermediaries: Option[Row] = userAnswers.get(pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage) map {
    answer =>
      toRow(
        msgKey  = "youHaveNotAddedAnyIntermediaries",
        content = msg"youHaveNotAddedAnyIntermediaries.$answer",
        href    = controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(CheckMode).url
      )
  }
}
