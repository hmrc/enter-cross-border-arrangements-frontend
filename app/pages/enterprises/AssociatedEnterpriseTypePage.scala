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

package pages.enterprises

import models.enterprises.AssociatedEnterprise
import models.{SelectType, UserAnswers}
import pages.{CleanUpSelectTypePage, DetailsPage}
import play.api.libs.json.JsPath

import scala.util.Try

case object AssociatedEnterpriseTypePage extends DetailsPage[SelectType, AssociatedEnterprise] with CleanUpSelectTypePage {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "associatedEnterpriseType"

  override def cleanup(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    userAnswers.remove(IsAssociatedEnterpriseAffectedPage, id)

  override def getFromModel(model: AssociatedEnterprise): Option[SelectType] = model.selectType.toOption
}
