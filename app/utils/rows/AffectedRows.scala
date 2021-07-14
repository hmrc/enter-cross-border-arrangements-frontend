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
import pages.affected.{AffectedTypePage, YouHaveNotAddedAnyAffectedPage}
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.Row

trait AffectedRows extends RowBuilder {

  def affectedType(id: Int): Option[Row] = userAnswers.getOrThrow(AffectedTypePage, id) map {
    answer =>
      toRow(
        msgKey = "affectedType",
        content = msg"affectedType.$answer",
        href = controllers.affected.routes.AffectedTypeController.onPageLoad(id, CheckMode).url
      )
  }

  def youHaveNotAddedAnyAffected(id: Int): Option[Row] = userAnswers.get(YouHaveNotAddedAnyAffectedPage, id) map {
    answer =>
      toRow(
        msgKey = "youHaveNotAddedAnyAffected",
        content = msg"youHaveNotAddedAnyAffected.$answer",
        href = controllers.affected.routes.YouHaveNotAddedAnyAffectedController.onPageLoad(id).url
      )
  }

}
