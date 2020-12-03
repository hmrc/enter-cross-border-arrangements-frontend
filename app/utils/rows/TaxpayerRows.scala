/*
 * Copyright 2020 HM Revenue & Customs
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
import pages.taxpayer.TaxpayerSelectTypePage
import uk.gov.hmrc.viewmodels.{MessageInterpolators, SummaryList}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal

trait TaxpayerRows extends RowBuilder {

  import pages.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementPage

  def taxpayerSelectType: Option[Row] = userAnswers.get(TaxpayerSelectTypePage) map {
    answer =>
      Row(
        key     = Key(msg"selectType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"selectType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def whatIsTaxpayersStartDateForImplementingArrangement: Option[Row] = userAnswers.get(WhatIsTaxpayersStartDateForImplementingArrangementPage) map {
    answer =>
      toRow(
        msgKey  = "whatIsTaxpayersStartDateForImplementingArrangement",
        content = Literal(answer.format(dateFormatter)),
        href    = controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(CheckMode).url
      )
  }
}
