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
import models.disclosure.DisclosureType.{Dac6add, Dac6del, Dac6new, Dac6rep}
import models.disclosure.{DisclosureDetails, ReplaceOrDeleteADisclosure}
import pages.{MessageRefIDPage, ModelPage, QuestionPage}
import play.api.libs.json.JsPath

import scala.concurrent.Future
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
    ).foldLeft(Try(userAnswers)) { case (ua, page) => ua.flatMap(_.removeBase(page.asInstanceOf[QuestionPage[_]])) }

  def restore(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    userAnswers.get(DisclosureDetailsPage, id)
      .fold[Try[UserAnswers]](Success(userAnswers)) { disclosureDetails =>
        userAnswers.set(DisclosureNamePage, id, DisclosureNamePage.getFromModel(disclosureDetails))
          .flatMap(_.set(DisclosureTypePage, id, DisclosureTypePage.getFromModel(disclosureDetails)))
          .flatMap(_.set(DisclosureMarketablePage, id, DisclosureMarketablePage.getFromModel(disclosureDetails)))
          .flatMap(_.set(DisclosureIdentifyArrangementPage, id, DisclosureIdentifyArrangementPage.getFromModel(disclosureDetails)))
          .flatMap(_.remove(DisclosureDetailsPage, id))
      }

  def build(userAnswers: UserAnswers): DisclosureDetails = {

    def getDisclosureDetails = userAnswers.getBase(DisclosureDetailsPage)
      .orElse(Some(DisclosureDetails("")))
    def getDisclosureName = userAnswers.getBase(DisclosureNamePage)
    def getDisclosureType = userAnswers.getBase(DisclosureTypePage)
    def getDisclosureMarketable = userAnswers.getBase(DisclosureMarketablePage).orElse(Some(false))
    def getDisclosureIdentifyArrangement = userAnswers.getBase(DisclosureIdentifyArrangementPage)
      .orElse(throw new UnsupportedOperationException(s"Additional Arrangement must be identified"))
    def getReplaceOrDeleteDisclosure: Option[ReplaceOrDeleteADisclosure] = userAnswers.getBase(ReplaceOrDeleteADisclosurePage)
    def getInitialDisclosureMA: Boolean = userAnswers.getBase(InitialDisclosureMAPage).getOrElse(false)
    def getMessageRefId = userAnswers.getBase(MessageRefIDPage).orElse(None)

    getDisclosureDetails
      .flatMap { details =>
        getDisclosureName.map { disclosureName => details.copy(disclosureName = disclosureName) }
      }
      .flatMap { details =>
        getDisclosureType.flatMap {
          case Dac6new =>
            getDisclosureMarketable.map { initialDisclosureMA =>
              details.copy(disclosureType = Dac6new, initialDisclosureMA = initialDisclosureMA)
            }
          case Dac6add =>
            getDisclosureIdentifyArrangement.flatMap { arrangementID =>
              getDisclosureMarketable.map { initialDisclosureMA =>
                details.copy(disclosureType = Dac6add, arrangementID = Some(arrangementID), initialDisclosureMA = initialDisclosureMA)
              }
            }
          case repOrDel =>
            getReplaceOrDeleteDisclosure.map { ids =>
              details.copy(
                disclosureType = repOrDel,
                arrangementID = Some(ids.arrangementID),
                disclosureID = Some(ids.disclosureID),
                initialDisclosureMA = getInitialDisclosureMA)
            }
        }
      }
      .map { details =>
        details.copy(messageRefId = getMessageRefId)
      }
      .getOrElse(throw new IllegalStateException("Unable to build disclose details"))
  }
}