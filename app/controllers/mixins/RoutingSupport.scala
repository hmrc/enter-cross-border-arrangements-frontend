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

package controllers.mixins

import models.{Mode, UserAnswers}
import pages.affected.AffectedTypePage
import pages.enterprises.AssociatedEnterpriseTypePage
import pages.intermediaries.IntermediariesTypePage
import pages.taxpayer.TaxpayerSelectTypePage

trait RoutingSupport {

  def toCheckRoute(mode: Mode, userAnswers: UserAnswers): CheckRoute =
    DefaultRouting(mode)

  def toCheckRoute(mode: Mode, userAnswers: UserAnswers, id: Int): CheckRoute =
    (userAnswers.get(TaxpayerSelectTypePage, id),
     userAnswers.get(AssociatedEnterpriseTypePage, id),
     userAnswers.get(IntermediariesTypePage, id),
     userAnswers.get(AffectedTypePage, id)
    ) match {
      case (Some(_), None, _, _) => TaxpayersRouting(mode)
      case (_, Some(_), _, _)    => AssociatedEnterprisesRouting(mode)
      case (_, _, Some(_), _)    => IntermediariesRouting(mode)
      case (_, _, _, Some(_))    => AffectedRouting(mode)
      case _                     => DefaultRouting(mode)
    }

}
