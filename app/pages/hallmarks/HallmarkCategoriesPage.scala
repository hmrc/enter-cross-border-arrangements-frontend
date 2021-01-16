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

package pages.hallmarks

import models.UserAnswers
import models.hallmarks.HallmarkCategories
import models.hallmarks.HallmarkCategories.{CategoryA, CategoryB, CategoryC, CategoryD, CategoryE}
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.annotation.tailrec
import scala.util.Try

case object HallmarkCategoriesPage extends QuestionPage[Set[HallmarkCategories]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "hallmarkCategories"

  def cleanup(value: Option[Set[HallmarkCategories]], userAnswers: UserAnswers, id: Int): Try[UserAnswers] = {
    value match {
      case Some(selected) =>
       val unselectedHallmarks = HallmarkCategories.values.filterNot(selected)

        super.cleanup(value, cleanupPages(unselectedHallmarks, userAnswers, id))
      case _ => super.cleanup(value, userAnswers)
    }
  }

  private def cleanupPages(unselectedHallmarks: Seq[HallmarkCategories], userAnswers: UserAnswers, id: Int): UserAnswers = {
    @tailrec
    def recursiveRemove(hallmarks: Seq[HallmarkCategories], userAnswers: UserAnswers, id: Int): UserAnswers = {
      hallmarks match {
        case Nil => userAnswers
        case head :: tail =>
          val updatedUserAnswers = head match {
            case CategoryA if !unselectedHallmarks.contains(CategoryB) =>
              userAnswers.remove(HallmarkAPage, id)
                .flatMap(_.remove(MainBenefitTestPage, id))
            case CategoryA => userAnswers.remove(HallmarkAPage, id)
            case CategoryB if !unselectedHallmarks.contains(CategoryA) =>
              userAnswers.remove(HallmarkBPage, id)
                .flatMap(_.remove(MainBenefitTestPage, id))
            case CategoryB => userAnswers.remove(HallmarkBPage, id)
            case CategoryC => userAnswers.remove(HallmarkCPage, id).flatMap(_.remove(HallmarkC1Page, id))
            case CategoryD => userAnswers.remove(HallmarkDPage, id).flatMap(_.remove(HallmarkD1Page, id)).flatMap(_.remove(HallmarkD1OtherPage, id))
            case CategoryE => userAnswers.remove(HallmarkEPage, id)
          }

          recursiveRemove(tail, updatedUserAnswers.getOrElse(userAnswers), id)
      }
    }
    recursiveRemove(unselectedHallmarks, userAnswers, id)
  }

}
