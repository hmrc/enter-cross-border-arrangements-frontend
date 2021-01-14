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

package pages.reporter

import models.UserAnswers
import models.reporter.RoleInArrangement
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import pages.QuestionPage
import pages.reporter.intermediary._
import pages.reporter.taxpayer._
import play.api.libs.json.JsPath

import scala.util.Try

case object RoleInArrangementPage extends QuestionPage[RoleInArrangement] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "roleInArrangement"

  override def cleanup(value: Option[RoleInArrangement], userAnswers: UserAnswers): Try[UserAnswers] = {

    value match {
      case Some(Intermediary) =>
        userAnswers.remove(TaxpayerWhyReportInUKPage)
          .flatMap(_.remove(TaxpayerWhyReportArrangementPage))
          .flatMap(_.remove(ReporterTaxpayersStartDateForImplementingArrangementPage))

      case Some(Taxpayer) =>
        userAnswers.remove(IntermediaryWhyReportInUKPage)
          .flatMap(_.remove(IntermediaryRolePage))
          .flatMap(_.remove(IntermediaryExemptionInEUPage))
          .flatMap(_.remove(IntermediaryDoYouKnowExemptionsPage))
          .flatMap(_.remove(IntermediaryWhichCountriesExemptPage))

      case _ => super.cleanup(value, userAnswers)
    }
  }
}
