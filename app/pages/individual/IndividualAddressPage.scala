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

package pages.individual

import models.individual.Individual
import models.{Address, UserAnswers}
import pages.organisation.PostcodePage
import pages.{DetailsPage, SelectedAddressLookupPage}
import play.api.libs.json.JsPath

import scala.util.Try

case object IndividualAddressPage extends DetailsPage[Address, Individual] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "individualAddress"

  override def cleanup(value: Option[Address], userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    value match {
      case Some(_) =>
        userAnswers
          .remove(SelectedAddressLookupPage, id)
          .flatMap(_.remove(PostcodePage, id))
      case None => super.cleanup(value, userAnswers, id)
    }

  override def getFromModel(model: Individual): Option[Address] = model.address

}
