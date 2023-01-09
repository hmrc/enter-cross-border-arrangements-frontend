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

package pages.taxpayer

import models.UserAnswers
import models.taxpayer.Taxpayer
import pages.{LoopPage, QuestionPage}
import play.api.libs.json.JsPath

case object TaxpayerLoopPage extends LoopPage[Taxpayer] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "taxpayerLoop"

  def updatedLoopList(userAnswers: UserAnswers, id: Int): IndexedSeq[Taxpayer] = {
    val buildTaxpayer: Taxpayer = Taxpayer(userAnswers, id)
    userAnswers.get(TaxpayerLoopPage, id) match {
      case Some(list) => // append to existing list without duplication
        list.filterNot(_.taxpayerId == buildTaxpayer.taxpayerId) :+ buildTaxpayer
      case None => // start new list
        IndexedSeq[Taxpayer](buildTaxpayer)
    }
  }

  override val cleanPages: Seq[QuestionPage[_]] = Seq(
    TaxpayerCheckYourAnswersPage,
    UpdateTaxpayerPage,
    TaxpayerSelectTypePage,
    WhatIsTaxpayersStartDateForImplementingArrangementPage
  )
}
