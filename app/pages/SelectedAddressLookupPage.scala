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

package pages

import models.{AddressLookup, UserAnswers}
import pages.individual.IndividualAddressPage
import pages.organisation.OrganisationAddressPage
import play.api.libs.json.JsPath

import scala.util.Try

object SelectedAddressLookupPage  extends QuestionPage[AddressLookup] {
  override def path: JsPath = JsPath \ toString

  override def toString: String = "selectedAddressLookup"

  def cleanup(value: Option[AddressLookup], userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    value match {
      case Some(_) =>
        userAnswers
          .remove(OrganisationAddressPage, id)
          .flatMap(_.remove(IndividualAddressPage, id))
      case None => super.cleanup(value, userAnswers)
    }
}
