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

package pages.disclosure

import models.UserAnswers
import models.disclosure.{DisclosureDetails, DisclosureType}
import pages.ModelPage
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

case object DisclosureDetailsPage extends ModelPage[DisclosureDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "disclosureMarketable"

  override def cleanup(value: Option[DisclosureDetails], userAnswers: UserAnswers): Try[UserAnswers] =
    List(
      DisclosureNamePage,
      DisclosureTypePage,
      DisclosureIdentifyArrangementPage,
      DisclosureMarketablePage
    ).foldLeft(Try(userAnswers)) { case (ua, page) => page.remove(ua) }

  def restore(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(DisclosureDetailsPage)
      .fold[Try[UserAnswers]](Success(userAnswers)) { disclosureDetails =>
        userAnswers.set(DisclosureNamePage, DisclosureNamePage.getFromModel(disclosureDetails))
          .flatMap(_.set(DisclosureTypePage, DisclosureTypePage.getFromModel(disclosureDetails)))
          .flatMap(_.set(DisclosureMarketablePage, DisclosureMarketablePage.getFromModel(disclosureDetails)))
          .flatMap(_.set(DisclosureIdentifyArrangementPage, DisclosureIdentifyArrangementPage.getFromModel(disclosureDetails)))
          .flatMap(_.remove(DisclosureDetailsPage))
      }

  def build(userAnswers: UserAnswers): DisclosureDetails = {

    def getDisclosureDetails = userAnswers.get(DisclosureDetailsPage)
      .orElse(Some(DisclosureDetails("")))
    def getDisclosureName = userAnswers.get(DisclosureNamePage)
    def getDisclosureType = userAnswers.get(DisclosureTypePage)
    def getDisclosureMarketable = userAnswers.get(DisclosureMarketablePage).orElse(Some(false))
    def getDisclosureIdentifyArrangement = userAnswers.get(DisclosureIdentifyArrangementPage)
      .orElse(throw new UnsupportedOperationException(s"Additional Arrangement must be identified"))

    getDisclosureDetails
      .flatMap { details =>
        getDisclosureName.map { disclosureName => details.copy(disclosureName = disclosureName) }
      }
      .flatMap { details =>
        getDisclosureType.flatMap {
          case disclosureType@DisclosureType.Dac6new =>
            getDisclosureMarketable.map { initialDisclosureMA =>
              details.copy(disclosureType = disclosureType, initialDisclosureMA = initialDisclosureMA)
            }
          case disclosureType@DisclosureType.Dac6add =>
            getDisclosureIdentifyArrangement.flatMap { arrangementID =>
              getDisclosureMarketable.map { initialDisclosureMA =>
                details.copy(disclosureType = disclosureType, arrangementID = Some(arrangementID), initialDisclosureMA = initialDisclosureMA)
              }
            }
          case disclosureType@(DisclosureType.Dac6rep | DisclosureType.Dac6del) => // TODO implement DisclosureType.Dac6rep | DisclosureType.Dac6del cases
            throw new UnsupportedOperationException(s"Not yet implemented: $disclosureType")
        }
      }
      .getOrElse(throw new IllegalStateException("Unable to build disclose details"))
  }

}