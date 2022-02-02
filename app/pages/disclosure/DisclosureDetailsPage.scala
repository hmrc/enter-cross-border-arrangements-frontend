/*
 * Copyright 2022 HM Revenue & Customs
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

package pages.disclosure

import models.UserAnswers
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import models.disclosure.{DisclosureDetails, ReplaceOrDeleteADisclosure}
import pages.{MessageRefIDPage, ModelPage, QuestionPage}
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

case object DisclosureDetailsPage extends ModelPage[DisclosureDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "disclosureDetails"

  override def cleanup(value: Option[DisclosureDetails], userAnswers: UserAnswers, index: Int): Try[UserAnswers] =
    List(
      DisclosureNamePage,
      DisclosureTypePage,
      DisclosureIdentifyArrangementPage,
      ReplaceOrDeleteADisclosurePage,
      DisclosureMarketablePage,
      InitialDisclosureMAPage
    ).foldLeft(Try(userAnswers)) {
      case (ua, page) => ua.flatMap(_.removeBase(page.asInstanceOf[QuestionPage[_]]))
    }

  def restore(userAnswers: UserAnswers, id: Int, from: Option[DisclosureDetails]): Try[UserAnswers] =
    from.fold[Try[UserAnswers]](Success(userAnswers)) {
      disclosureDetails =>
        implicit val org: DisclosureDetails = implicitly(disclosureDetails)
        userAnswers
          .set(DisclosureNamePage, id)
          .flatMap(_.set(DisclosureTypePage, id))
          .flatMap(_.set(DisclosureMarketablePage, id))
          .flatMap(_.set(DisclosureIdentifyArrangementPage, id))
          .flatMap(_.remove(DisclosureDetailsPage, id))
    }

}
