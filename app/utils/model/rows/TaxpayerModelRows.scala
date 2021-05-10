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

import models.SelectType
import models.taxpayer.Taxpayer
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.Text.Literal
import utils.SummaryListDisplay.DisplayRow

trait TaxpayerModelRows extends DisplayRowBuilder {

  def taxpayerSelectType(taxpayer: Taxpayer)(implicit messages: Messages): DisplayRow = {
    val selectType = (taxpayer.individual, taxpayer.organisation) match {
      case (Some(_), None) => SelectType.Individual
      case (None, Some(_)) => SelectType.Organisation
    }

    toDisplayRow(
        msgKey = "selectType",
        content = msg"selectType.$selectType"
      )
  }

  def whatIsTaxpayersStartDateForImplementingArrangement(taxpayer: Taxpayer)(implicit messages: Messages): Option[DisplayRow] =
    taxpayer.implementingDate map { implementingDate =>
      toDisplayRow(
        msgKey = "whatIsTaxpayersStartDateForImplementingArrangement",
        content = Literal(implementingDate.format(dateFormatter))
      )
    }
}
