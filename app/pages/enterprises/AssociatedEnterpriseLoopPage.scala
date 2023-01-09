/*
 * Copyright 2023 HM Revenue & Customs
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

import models.UserAnswers
import models.enterprises.AssociatedEnterprise
import pages.{LoopPage, QuestionPage}
import play.api.libs.json.JsPath

case object AssociatedEnterpriseLoopPage extends LoopPage[AssociatedEnterprise] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "associatedEnterpriseLoop"

  def updatedLoopList(userAnswers: UserAnswers, id: Int): IndexedSeq[AssociatedEnterprise] = {
    val associatedEnterprise: AssociatedEnterprise = AssociatedEnterprise(userAnswers, id)
    userAnswers.get(AssociatedEnterpriseLoopPage, id) match {
      case Some(list) => // append to existing list without duplication
        list.filterNot(_.enterpriseId == associatedEnterprise.enterpriseId) :+ associatedEnterprise
      case None => // start new list
        IndexedSeq[AssociatedEnterprise](associatedEnterprise)
    }
  }

  override val cleanPages: Seq[QuestionPage[_]] = Seq(
    AssociatedEnterpriseCheckYourAnswersPage,
    YouHaveNotAddedAnyAssociatedEnterprisesPage,
    SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage,
    AssociatedEnterpriseTypePage,
    IsAssociatedEnterpriseAffectedPage
  )
}
