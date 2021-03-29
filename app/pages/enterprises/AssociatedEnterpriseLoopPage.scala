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

package pages.enterprises

import models.enterprises.AssociatedEnterprise
import pages.{LoopPage, QuestionPage}
import play.api.libs.json.JsPath

case object AssociatedEnterpriseLoopPage extends LoopPage[IndexedSeq[AssociatedEnterprise]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "associatedEnterpriseLoop"

  override val cleanPages: Seq[QuestionPage[_]] = Seq(
    YouHaveNotAddedAnyAssociatedEnterprisesPage
    , SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
    , AssociatedEnterpriseTypePage
    , IsAssociatedEnterpriseAffectedPage
  )
}
