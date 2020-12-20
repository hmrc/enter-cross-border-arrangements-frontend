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
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait DisclosureRows extends RowBuilder {

  def disclosureNamePage: Option[Row] = userAnswers.get(DisclosureNamePage) map { answer =>

    toRow(
      msgKey  = "disclosureName",
      content = formatMaxChars(answer),
      href    = controllers.disclosure.routes.DisclosureNameController.onPageLoad(CheckMode).url
    )
  }

  def disclosureMarketablePage: Option[Row] = userAnswers.get(DisclosureMarketablePage) map { answer =>
    toRow(
      msgKey  = "disclosureMarketable",
      content = yesOrNo(answer),
      href    = controllers.disclosure.routes.DisclosureMarketableController.onPageLoad(CheckMode).url
    )
  }

  def disclosureTypePage: Option[Row] = userAnswers.get(DisclosureTypePage) map { answer =>

    toRow(
      msgKey  = "disclosureType",
      content = msg"disclosureType.$answer",
      href    = controllers.disclosure.routes.DisclosureTypeController.onPageLoad(CheckMode).url
    )
  }
}
