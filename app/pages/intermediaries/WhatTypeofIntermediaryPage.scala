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

package pages.intermediaries

import models.UserAnswers
import models.intermediaries.WhatTypeofIntermediary
import models.intermediaries.WhatTypeofIntermediary.{IDoNotKnow, Serviceprovider}
import pages._
import play.api.libs.json.JsPath

import scala.util.Try

case object WhatTypeofIntermediaryPage extends QuestionPage[WhatTypeofIntermediary] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whatTypeofIntermediary"

  override def cleanup(value: Option[WhatTypeofIntermediary], userAnswers: UserAnswers): Try[UserAnswers] = {
    //Clear following answers
    value match {
      case Some(Serviceprovider | IDoNotKnow) => userAnswers.remove(IsExemptionKnownPage)
        .flatMap(_.remove(IsExemptionCountryKnownPage))
        .flatMap(_.remove(ExemptCountriesPage))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}
