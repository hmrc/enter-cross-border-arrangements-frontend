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

import models.affected.Affected
import models.{CheckMode, SelectType}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.Row

trait AffectedModelRows extends DisplayRowBuilder {

  def affectedType(id: Int, affected: Affected)(implicit messages: Messages): Row = {

    val selectType = (affected.individual, affected.organisation) match {
      case (Some(_), None) => SelectType.Individual
      case (None, Some(_)) => SelectType.Organisation
    }

    toRow(
      msgKey  = "affectedType",
      content = msg"affectedType.${selectType}",
      href    = controllers.affected.routes.AffectedTypeController.onPageLoad(id, CheckMode).url
    )
  }

}
