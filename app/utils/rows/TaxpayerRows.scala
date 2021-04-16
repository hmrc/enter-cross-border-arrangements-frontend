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
import pages.taxpayer.{TaxpayerSelectTypePage, UpdateTaxpayerPage}
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal

trait TaxpayerRows extends RowBuilder {

  import pages.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementPage

  def taxpayerSelectType(id: Int): Option[Row] = userAnswers.get(TaxpayerSelectTypePage, id) map {
    answer =>
      toRow(
        msgKey = "selectType",
        content = msg"selectType.$answer",
        href = controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(id, CheckMode).url
      )
  }

  def whatIsTaxpayersStartDateForImplementingArrangement(id: Int): Option[Row] =
    userAnswers.get(WhatIsTaxpayersStartDateForImplementingArrangementPage, id) map {
    answer =>
      toRow(
        msgKey  = "whatIsTaxpayersStartDateForImplementingArrangement",
        content = Literal(answer.format(dateFormatter)),
        href    = controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(id, CheckMode).url
      )
  }

  def updateTaxpayers(id: Int): Option[Row] = userAnswers.get(UpdateTaxpayerPage, id) map {
    answer =>
      toRow(
        msgKey  = "updateTaxpayer",
        content = msg"updateTaxpayer.$answer",
        href    = controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad(id).url
      )
  }
}
