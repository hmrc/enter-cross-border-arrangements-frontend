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

import models.{CheckMode, NormalMode, SelectType, UserAnswers}
import org.scalatest.{FreeSpec, MustMatchers, TryValues}
import pages.enterprises.AssociatedEnterpriseTypePage
import pages.intermediaries.IntermediariesTypePage
import pages.taxpayer.TaxpayerSelectTypePage

class RoutingSupportSpecs extends FreeSpec with MustMatchers with TryValues {

  val userAnswersId = "id"

  "Routing support" - {

    "must continue to AssociatedEnterprisesRouting(NormalMode) if in NormalMode called from associated enterprises route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(AssociatedEnterpriseTypePage, SelectType.values.head)
          .success
          .value

        toCheckRoute(NormalMode, userAnswers) mustBe AssociatedEnterprisesRouting(NormalMode)
      }
    }

    "must continue to TaxpayerRouting(NormalMode) if in NormalMode called from taxpayer route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(TaxpayerSelectTypePage, SelectType.values.head)
          .success
          .value

        toCheckRoute(NormalMode, userAnswers) mustBe TaxpayersRouting(NormalMode)
      }
    }

    "must continue to IntermediariesRouting(NormalMode) if in NormalMode called from Intermediaries route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(IntermediariesTypePage, SelectType.values.head)
          .success
          .value

        toCheckRoute(NormalMode, userAnswers) mustBe IntermediariesRouting(NormalMode)
      }
    }

    "must route to AssociatedEnterpriseRouting(CheckMode) if in CheckMode called from associated enterprises route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(AssociatedEnterpriseTypePage, SelectType.values.head)
          .success
          .value

        toCheckRoute(CheckMode, userAnswers) mustBe AssociatedEnterprisesRouting(CheckMode)
      }
    }

    "must route to TaxpayerRouting(CheckMode) if in NormalMode called from taxpayer route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(TaxpayerSelectTypePage, SelectType.values.head)
          .success
          .value

        toCheckRoute(CheckMode, userAnswers) mustBe TaxpayersRouting(CheckMode)
      }
    }

    "must route to IntermediariesRouting(CheckMode) if in NormalMode called from Intermediaries route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .set(IntermediariesTypePage, SelectType.values.head)
          .success
          .value

        toCheckRoute(CheckMode, userAnswers) mustBe IntermediariesRouting(CheckMode)
      }
    }

  }
}
