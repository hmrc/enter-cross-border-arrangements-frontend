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

package utils.controllers

import models.{Mode, UserAnswers}
import pages.QuestionPage
import play.api.data.Form
import play.api.libs.json.{JsObject, Json, Reads}
import play.api.mvc.{Action, AnyContent}

trait OnPageLoadIndexMixIn[A] extends PageIndexController[A] with PageControllerMixIn[A] {

  def getPage(userAnswers: UserAnswers): Option[IndexedSeq[A]]

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val userAnswers = getUserAnswers(request)
      val preparedForm = userAnswers.flatMap(getPage) match {
        case None => form
        case Some(list) =>
          list.lift(index).fold(form) { fill }
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "index" -> index
      )

      renderer.render(template, pageData(preparedForm, userAnswers) ++ json).map(Ok(_))
  }

}
