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
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

trait OnPageLoadIndexMixIn[A, D] extends FrontendBaseController with I18nSupport with NunjucksSupport with PageControllerMixIn[A] {

  val  getLoopPage: UserAnswers => Option[IndexedSeq[D]]

  val toValue: D => A

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val userAnswers = getUserAnswers(request)
      val preparedForm = userAnswers.flatMap(getLoopPage) match {
        case None => form
        case Some(list) =>
          list.lift(index).map(toValue).fold(form) { fill }
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "index" -> index
      )

      renderer.render(template, pageData(preparedForm, userAnswers) ++ json).map(Ok(_))
  }

}
