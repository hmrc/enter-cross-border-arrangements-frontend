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

package helpers

import models.UserAnswers
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted}
import pages.QuestionPage
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.Html

object TaskListHelper {

  def restrictiveTaskListHtmlProvider(url: String, status: String, linkContent: String, id: String, ariaLabel: String)(implicit messages: Messages): Html = {
    Html(s"<li class='app-task-list__item'><a class='app-task-list__task-name' href='$url' aria-describedby='$ariaLabel'> ${messages(linkContent)}</a>" +
      s"<strong class='govuk-tag app-task-list__task-completed' id='$id'>$status</strong> </li>")
  }

  def taskListHtmlProvider(url: String, status: String, linkContent: String, id: String, ariaLabel: String)(implicit messages: Messages): Html = {
    Html(s"<li class='app-task-list__item'><a class='app-task-list__task-name' href='$url' aria-describedby='$ariaLabel'> ${messages(linkContent)}</a>" +
      s"<strong class='govuk-tag app-task-list__task-completed' id='$id'>$status</strong> </li>")
  }

  def statusRetriever(ua: UserAnswers, page: QuestionPage[JourneyStatus],
                      url: String, linkContent: String, id: String, ariaLabel: String)(implicit messages: Messages): Html = {

    ua.get(page) match {
      case Some(Completed) => taskListHtmlProvider(url, Completed.toString, linkContent, s"$id-completed", ariaLabel)
      case Some(InProgress) => taskListHtmlProvider(url, InProgress.toString, linkContent, s"$id-inProgress", ariaLabel)
      case _ => taskListHtmlProvider(url, NotStarted.toString, linkContent, s"$id-notStarted", ariaLabel)
    }

  }
}
