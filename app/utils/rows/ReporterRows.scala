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
import pages.reporter.RoleInArrangementPage
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels.Text.Literal

trait ReporterRows extends RowBuilder {

  def roleInArrangementPage: Option[Row] = userAnswers.get(RoleInArrangementPage) map { answer =>

    toRow(
      msgKey  = "roleInArrangement",
      content = Literal("roleInArrangement.$answer"),
      href    = controllers.reporter.routes.RoleInArrangementController.onPageLoad(CheckMode).url
    )
  }
}
