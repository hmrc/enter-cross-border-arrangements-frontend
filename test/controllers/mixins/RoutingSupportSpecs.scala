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

import base.ModelSpecBase
import models.{CheckMode, NormalMode, SelectType, UnsubmittedDisclosure, UserAnswers}
import org.scalatest.TryValues
import pages.enterprises.AssociatedEnterpriseTypePage
import pages.intermediaries.IntermediariesTypePage
import pages.taxpayer.TaxpayerSelectTypePage
import pages.unsubmitted.UnsubmittedDisclosurePage

class RoutingSupportSpecs extends ModelSpecBase with TryValues {

  val userAnswersId = "id"

  "Routing support" - {

    "must continue to AssociatedEnterprisesRouting(NormalMode) if in NormalMode called from associated enterprises route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(AssociatedEnterpriseTypePage, 0, SelectType.values.head)
          .success
          .value

        toCheckRoute(NormalMode, userAnswers, 0) mustBe AssociatedEnterprisesRouting(NormalMode)
      }
    }

    "must continue to TaxpayerRouting(NormalMode) if in NormalMode called from taxpayer route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(TaxpayerSelectTypePage, 0, SelectType.values.head)
          .success
          .value

        toCheckRoute(NormalMode, userAnswers, 0) mustBe TaxpayersRouting(NormalMode)
      }
    }

    "must continue to IntermediariesRouting(NormalMode) if in NormalMode called from Intermediaries route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(IntermediariesTypePage, 0, SelectType.values.head)
          .success
          .value

        toCheckRoute(NormalMode, userAnswers, 0) mustBe IntermediariesRouting(NormalMode)
      }
    }

    "must route to AssociatedEnterpriseRouting(CheckMode) if in CheckMode called from associated enterprises route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(AssociatedEnterpriseTypePage, 0, SelectType.values.head)
          .success
          .value

        toCheckRoute(CheckMode, userAnswers, 0) mustBe AssociatedEnterprisesRouting(CheckMode)
      }
    }

    "must route to TaxpayerRouting(CheckMode) if in NormalMode called from taxpayer route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(TaxpayerSelectTypePage, 0, SelectType.values.head)
          .success
          .value

        toCheckRoute(CheckMode, userAnswers, 0) mustBe TaxpayersRouting(CheckMode)
      }
    }

    "must route to IntermediariesRouting(CheckMode) if in NormalMode called from Intermediaries route" in {

      new RoutingSupport {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(IntermediariesTypePage, 0, SelectType.values.head)
          .success
          .value

        toCheckRoute(CheckMode, userAnswers, 0) mustBe IntermediariesRouting(CheckMode)
      }
    }

  }
}
