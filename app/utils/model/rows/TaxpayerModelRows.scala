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

import models.taxpayer.Taxpayer
import models.{CheckMode, SelectType}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal

trait TaxpayerModelRows extends DisplayRowBuilder {

  def taxpayerSelectType(id: Int, taxpayer: Taxpayer)(implicit messages: Messages): Row = {
    val selectType = (taxpayer.individual, taxpayer.organisation) match {
      case (Some(_), None) => SelectType.Individual
      case (None, Some(_)) => SelectType.Organisation
    }

    toRow(
        msgKey = "selectType",
        content = msg"selectType.$selectType",
        href = controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(id, CheckMode).url
      )
  }


  def whatIsTaxpayersStartDateForImplementingArrangement(id: Int, taxpayer: Taxpayer)(implicit messages: Messages): Option[Row] =
    taxpayer.implementingDate map { implementingDate =>
      toRow(
        msgKey = "whatIsTaxpayersStartDateForImplementingArrangement",
        content = Literal(implementingDate.format(dateFormatter)),
        href = controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(id, CheckMode).url
      )
    }
}
