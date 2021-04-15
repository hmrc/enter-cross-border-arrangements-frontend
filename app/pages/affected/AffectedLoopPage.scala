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

package pages.affected

import models.UserAnswers
import models.affected.Affected
import pages.{LoopPage, QuestionPage}
import play.api.libs.json.JsPath

case object AffectedLoopPage extends LoopPage[Affected] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "affectedLoop"

  def updatedLoopList(userAnswers: UserAnswers, id: Int): IndexedSeq[Affected] = {
    val affected: Affected = Affected(userAnswers, id)
    userAnswers.get(AffectedLoopPage, id) match {
      case Some(list) => // append to existing list without duplication
        list.filterNot(_.affectedId == affected.affectedId) :+ affected
      case None =>      // start new list
        IndexedSeq[Affected](affected)
    }
  }

  override val cleanPages: Seq[QuestionPage[_]] = Seq(AffectedCheckYourAnswersPage)
}
