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

package controllers.intermediaries

import base.SpecBase
import models.intermediaries.{Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Address, Country, IsExemptionKnown, LoopDetails, SelectType, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.scalatestplus.mockito.MockitoSugar
import pages.intermediaries.{IntermediariesTypePage, IntermediaryLoopPage, IsExemptionKnownPage, WhatTypeofIntermediaryPage}
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage}
import pages.unsubmitted.UnsubmittedDisclosurePage

class IntermediariesCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  "must ensure the correct updated loop list" - {

    val address: Address = Address(Some(""), Some(""), Some(""), "Newcastle", Some("NE1"), Country("", "GB", "United Kingdom"))
    val email = "email@email.com"
    val taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))

    def buildUserAnswers(list: IndexedSeq[Intermediary]): UserAnswers = UserAnswers(userAnswersId)
      .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
      .set(IntermediaryLoopPage, 0, list).success.value
      .set(IntermediariesTypePage, 0, SelectType.Organisation).success.value
      .set(OrganisationNamePage, 0, "Intermediary Ltd").success.value
      .set(WhatTypeofIntermediaryPage, 0, WhatTypeofIntermediary.IDoNotKnow).success.value
      .set(IsExemptionKnownPage,0, IsExemptionKnown.Unknown).success.value
      .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None))).success.value

    val controller: IntermediariesCheckYourAnswersController = injector.instanceOf[IntermediariesCheckYourAnswersController]

    def organisation(name: String) = Organisation(name, Some(address), Some(email), taxResidencies)

    def buildIntermediary(id: String, name: String) =
      Intermediary(id, None, Some(organisation(name)), WhatTypeofIntermediary.IDoNotKnow, IsExemptionKnown.Unknown)

  }

}
