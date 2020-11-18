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

package pages.hallmarks

import models.HallmarkCategories.{CategoryA, CategoryB, CategoryC, CategoryD, CategoryE}
import models.{HallmarkCategories, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.annotation.tailrec
import scala.util.Try

case object HallmarkCategoriesPage extends QuestionPage[Set[HallmarkCategories]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "hallmarkCategories"

  override def cleanup(value: Option[Set[HallmarkCategories]], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(selected) =>
       val unselectedHallmarks = HallmarkCategories.values.filterNot(selected)

        super.cleanup(value, cleanupPages(unselectedHallmarks, userAnswers))
      case _ => super.cleanup(value, userAnswers)
    }
  }

  private def cleanupPages(unselectedHallmarks: Seq[HallmarkCategories], userAnswers: UserAnswers): UserAnswers = {
    @tailrec
    def recursiveRemove(hallmarks: Seq[HallmarkCategories], userAnswers: UserAnswers): UserAnswers = {
      hallmarks match {
        case Nil => userAnswers
        case head :: tail =>
          val updatedUserAnswers = head match {
            case CategoryA if !unselectedHallmarks.contains(CategoryB) =>
              userAnswers.remove(HallmarkAPage)
                .flatMap(_.remove(MainBenefitTestPage))
            case CategoryA => userAnswers.remove(HallmarkAPage)
            case CategoryB if !unselectedHallmarks.contains(CategoryA) =>
              userAnswers.remove(HallmarkBPage)
                .flatMap(_.remove(MainBenefitTestPage))
            case CategoryB => userAnswers.remove(HallmarkBPage)
            case CategoryC => userAnswers.remove(HallmarkCPage).flatMap(_.remove(HallmarkC1Page))
            case CategoryD => userAnswers.remove(HallmarkDPage).flatMap(_.remove(HallmarkD1Page)).flatMap(_.remove(HallmarkD1OtherPage))
            case CategoryE => userAnswers.remove(HallmarkEPage)
          }

          recursiveRemove(tail, updatedUserAnswers.getOrElse(userAnswers))
      }
    }
    recursiveRemove(unselectedHallmarks, userAnswers)
  }

}
