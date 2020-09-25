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

package utils

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, HallmarkA, HallmarkB, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def mainBenefitTest: Option[Row] = userAnswers.get(MainBenefitTestPage) map {
    answer =>
      Row(
        key     = Key(msg"mainBenefitTest.checkYourAnswersLabel"),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.MainBenefitTestController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"mainBenefitTest.checkYourAnswersLabel"))
          )
        )
      )
  }

  def hallmarkCategories: Option[Row] = userAnswers.get(HallmarkCategoriesPage) map {
    answer =>
      Row(
        key     = Key(msg"hallmarkCategories.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-third")),
        value   = Value(Html(answer.map(a => msg"hallmarkCategories.$a".resolve).mkString(",<br>"))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.HallmarkCategoriesController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"hallmarkCategories.checkYourAnswersLabel"))
          )
        )
      )
  }

  def buildHallmarksRow(ua: UserAnswers): Row = {

    val hallmarkPages = Seq(
      ua.get(HallmarkAPage),
      ua.get(HallmarkBPage)
    )

    val selectedHallmarkParts = hallmarkPages.collect{ case Some(value) => value }

    val hallmarksList = for {
      selectedHallmark <- selectedHallmarkParts
    } yield {
      selectedHallmark.map(hallmark => msg"$hallmark".resolve).mkString(", ")
    }

    Row(
      key     = Key(msg"checkYourAnswers.selectedHallmarks.label"),
      value   = Value(msg"${hallmarksList.mkString(", ")}"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.HallmarkCategoriesController.onPageLoad(CheckMode).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"checkYourAnswers.selectedHallmarks.label"))
        )
      )
    )

  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }
}

object CheckYourAnswersHelper {
  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}