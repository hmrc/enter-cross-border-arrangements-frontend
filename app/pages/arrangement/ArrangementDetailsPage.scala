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

package pages.arrangement

import models.UserAnswers
import models.arrangement.ArrangementDetails
import pages.ModelPage
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

case object ArrangementDetailsPage extends ModelPage[ArrangementDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "arrangementDetails"

  override def restore(userAnswers: UserAnswers, id: Int, from: Option[ArrangementDetails]): Try[UserAnswers] =
    from.fold[Try[UserAnswers]](Success(userAnswers)) {
      arrangementDetails =>
        implicit val org: ArrangementDetails = implicitly(arrangementDetails)
        userAnswers
          .set(WhatIsThisArrangementCalledPage, id)
          .flatMap(_.set(WhatIsTheImplementationDatePage, id))
          .flatMap(_.set(WhyAreYouReportingThisArrangementNowPage, id))
          .flatMap(_.set(WhichExpectedInvolvedCountriesArrangementPage, id))
          .flatMap(_.set(WhatIsTheExpectedValueOfThisArrangementPage, id))
          .flatMap(_.set(WhichNationalProvisionsIsThisArrangementBasedOnPage, id))
          .flatMap(_.set(GiveDetailsOfThisArrangementPage, id))
          .flatMap(_.remove(ArrangementDetailsPage, id))
    }
}
