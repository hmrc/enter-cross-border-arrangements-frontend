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

import controllers.actions.{DataRequiredAction, DataRequiredInitializingActionImpl}
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json.JsObject

trait PageControllerMixIn[A] extends PageControllerComponents {

  val template: String

  def pageData(form: Form[A], userAnswers: Option[UserAnswers])(implicit messages: Messages): JsObject

  val form: Form[A]

  val fill: A => Form[A] = form.fill

  def getUserAnswers(request: Any): Option[UserAnswers] = requireData match {
    case _: DataRequiredInitializingActionImpl =>
      request.asInstanceOf[OptionalDataRequest[A]].userAnswers
    case _: DataRequiredAction =>
      Some(request.asInstanceOf[DataRequest[A]].userAnswers)
    case _ =>
      Some(request.asInstanceOf[DataRequest[A]].userAnswers)
  }

}
