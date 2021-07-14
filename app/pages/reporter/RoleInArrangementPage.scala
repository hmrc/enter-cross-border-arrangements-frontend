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
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.reporter.{ReporterDetails, RoleInArrangement}
import pages.DetailsPage
import pages.reporter.intermediary._
import pages.reporter.taxpayer._
import pages.taxpayer.RelevantTaxpayerStatusPage
import play.api.libs.json.JsPath

import scala.util.Try

case object RoleInArrangementPage extends DetailsPage[RoleInArrangement, ReporterDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "roleInArrangement"

  override def cleanup(value: Option[RoleInArrangement], userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    value match {
      case Some(Intermediary) =>
        userAnswers
          .remove(TaxpayerWhyReportInUKPage, id)
          .flatMap(_.remove(TaxpayerWhyReportArrangementPage, id))
          .flatMap(_.remove(ReporterTaxpayersStartDateForImplementingArrangementPage, id))
          .flatMap(_.remove(RelevantTaxpayerStatusPage, id))

      case Some(Taxpayer) =>
        userAnswers
          .remove(IntermediaryWhyReportInUKPage, id)
          .flatMap(_.remove(IntermediaryRolePage, id))
          .flatMap(_.remove(IntermediaryExemptionInEUPage, id))
          .flatMap(_.remove(IntermediaryDoYouKnowExemptionsPage, id))
          .flatMap(_.remove(IntermediaryWhichCountriesExemptPage, id))

      case _ => super.cleanup(value, userAnswers, id)
    }

  override def getFromModel(model: ReporterDetails): Option[RoleInArrangement] =
    model match {
      case m if m.isTaxpayer     => Some(RoleInArrangement.Taxpayer)
      case m if m.isIntermediary => Some(RoleInArrangement.Intermediary)
      case _                     => None
    }
}
