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

package models.arrangement

import models.UserAnswers
import pages.WhatIsTheExpectedValueOfThisArrangementPage
import play.api.libs.json._

case class ExpectedArrangementValue(currency: String, amount: Int)

object ExpectedArrangementValue {
  implicit val format = Json.format[ExpectedArrangementValue]

  def buildExpectedArrangementValue(ua: UserAnswers, id: Int): ExpectedArrangementValue = {
    ua.get(WhatIsTheExpectedValueOfThisArrangementPage, id) match {
      case Some(countryWithValue) =>
        new ExpectedArrangementValue(
          currency = countryWithValue.currency,
          amount = countryWithValue.amount
        )
      case _ => throw new Exception("Unable to build ExpectedArrangementValue as missing mandatory answers")
    }
  }
}
