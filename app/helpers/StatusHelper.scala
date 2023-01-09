/*
 * Copyright 2023 HM Revenue & Customs
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

package helpers

import models.UserAnswers
import models.disclosure.DisclosureType
import models.disclosure.DisclosureType.{Dac6add, Dac6new, Dac6rep}
import models.hallmarks.JourneyStatus
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import pages.disclosure.DisclosureDetailsPage
import pages.reporter.RoleInArrangementPage
import pages.taxpayer.TaxpayerLoopPage

object StatusHelper {

  def checkTaxpayerStatusConditions(ua: UserAnswers, id: Int): JourneyStatus = {

    val oneRelevantTaxpayerAdded: Boolean = ua
      .get(TaxpayerLoopPage, id)
      .exists(
        list => list.nonEmpty
      )
    val getMarketableFlag: Boolean                = ua.get(DisclosureDetailsPage, id).exists(_.initialDisclosureMA)
    val getDisclosureType: Option[DisclosureType] = ua.get(DisclosureDetailsPage, id).map(_.disclosureType)

    (getDisclosureType, getMarketableFlag, ua.get(RoleInArrangementPage, id)) match {

      case (Some(Dac6new), true, _) => JourneyStatus.Completed //new & marketable

      case (Some(Dac6new), false, Some(Taxpayer)) => JourneyStatus.Completed //new & non marketable & Reporter is Taxpayer

      case (Some(Dac6add | Dac6rep), _, Some(Taxpayer)) => JourneyStatus.Completed // add | replace & Reporter is taxpayer

      case (Some(Dac6rep), true, Some(Intermediary)) => JourneyStatus.Completed // replace journey & reporter as Intermediary & Marketable Arrangement

      case (_, _, Some(Intermediary)) if oneRelevantTaxpayerAdded =>
        JourneyStatus.Completed //non marketable & Reporter is Intermediary but has added a taxpayer

      case _ => JourneyStatus.NotStarted
    }
  }
}
