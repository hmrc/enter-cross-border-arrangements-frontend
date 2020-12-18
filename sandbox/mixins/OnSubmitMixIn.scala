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

package utils.mixins

import models.Mode
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

trait OnSubmitMixIn[A] extends OnPageLoadMixIn[A] with OnSubmitSuccessMixIn[A] {

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      getUserAnswers(request) match {

        case Some(userAnswers) =>

          form.bindFromRequest().fold(
            formWithErrors => {

              val json = Json.obj(
                "form" -> formWithErrors,
                "mode" -> mode
              )
              renderer.render(template, pageData(formWithErrors, Some(userAnswers)) ++ json).map(BadRequest(_))
            },
            value => updateAnswers(userAnswers, value) { updatedAnswers =>

              val previousValue: Option[A] = getValue(userAnswers)
              val alternative = previousValue.exists(_ != value)
              success(mode, updatedAnswers, value, getIndex(request), alternative)
            }
          )
        case _ => Future.successful(Redirect(failOnSubmit))
      }
  }

}
