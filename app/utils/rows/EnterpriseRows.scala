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
import pages.enterprises.YouHaveNotAddedAnyAssociatedEnterprisesPage
import pages.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage}
import pages.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait EnterpriseRows extends RowBuilder {

  def youHaveNotAddedAnyAssociatedEnterprises: Option[Row] = userAnswers.get(YouHaveNotAddedAnyAssociatedEnterprisesPage) map { answer =>

    toRow(
      msgKey  = "youHaveNotAddedAnyAssociatedEnterprises",
      content = msg"youHaveNotAddedAnyAssociatedEnterprises.$answer",
      href    = controllers.enterprises.routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(CheckMode).url
    )
  }

  def associatedEnterpriseType: Option[Row] = userAnswers.get(AssociatedEnterpriseTypePage) map {
    answer =>
      toRow(
        msgKey = "associatedEnterpriseType",
        content = msg"selectType.$answer",
        href = controllers.routes.AssociatedEnterpriseTypeController.onPageLoad(CheckMode).url
      )
  }

  def isAssociatedEnterpriseAffected: Option[Row] = userAnswers.get(IsAssociatedEnterpriseAffectedPage) map {
    answer =>
      toRow(
        msgKey = "isAssociatedEnterpriseAffected",
        content = yesOrNo(answer),
        href = controllers.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(CheckMode).url
      )
  }

  def selectAnyTaxpayersThisEnterpriseIsAssociatedWith: Option[Row] = userAnswers.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage) map { answer =>

    toRow(
      msgKey  = "selectAnyTaxpayersThisEnterpriseIsAssociatedWith",
      content = msg"selectAnyTaxpayersThisEnterpriseIsAssociatedWith.$answer",
      href    = controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(CheckMode).url

    )
  }

  def associatedEnterpriseType: Option[Row] = userAnswers.get(AssociatedEnterpriseTypePage) map {
    answer =>
      toRow(
        msgKey = "associatedEnterpriseType",
        content = msg"selectType.$answer",
        href = controllers.enterprises.routes.AssociatedEnterpriseTypeController.onPageLoad(CheckMode).url
      )
  }

  def isAssociatedEnterpriseAffected: Option[Row] = userAnswers.get(IsAssociatedEnterpriseAffectedPage) map {
    answer =>
      toRow(
        msgKey = "isAssociatedEnterpriseAffected",
        content = yesOrNo(answer),
        href = controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(CheckMode).url
      )
  }

}
