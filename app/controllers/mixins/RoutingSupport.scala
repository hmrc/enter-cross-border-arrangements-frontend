/*
 * Copyright 2020 HM Revenue & Customs
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

import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.enterprises.AssociatedEnterpriseTypePage
import pages.intermediaries.IntermediariesTypePage
import pages.taxpayer.TaxpayerSelectTypePage

trait RoutingSupport {

  def toCheckRoute(mode: Mode, userAnswers: UserAnswers): CheckRoute =
    (userAnswers.get(AssociatedEnterpriseTypePage)
      , userAnswers.get(TaxpayerSelectTypePage)
      , userAnswers.get(IntermediariesTypePage)) match {
      case (Some(_), _, _) => AssociatedEnterprisesRouting(mode)
      case (_, Some(_), _) => TaxpayersRouting(mode)
      case (_, _, Some(_)) => IntermediariesRouting(mode)
      case _ => DefaultRouting(mode)
    }

}