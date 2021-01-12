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

import java.time.LocalDate

import models.{CountriesListEUCheckboxes, UserAnswers}
import models.YesNoDoNotKnowRadios.Yes
import models.reporter.RoleInArrangement
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.reporter.intermediary.IntermediaryRole.Promoter
import models.reporter.intermediary.IntermediaryWhyReportInUK.TaxResidentUK
import models.reporter.taxpayer.TaxpayerWhyReportArrangement.NoIntermediaries
import models.reporter.taxpayer.TaxpayerWhyReportInUK.UkTaxResident
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhichCountriesExemptPage, IntermediaryWhyReportInUKPage}
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}

class RoleInArrangementPageSpec extends PageBehaviours {

  "RoleInArrangementPage" - {

    beRetrievable[RoleInArrangement](RoleInArrangementPage)

    beSettable[RoleInArrangement](RoleInArrangementPage)

    beRemovable[RoleInArrangement](RoleInArrangementPage)

    "must remove taxpayer details if reporter selects intermediary" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .set(TaxpayerWhyReportInUKPage, UkTaxResident)
            .success.value
            .set(TaxpayerWhyReportArrangementPage, NoIntermediaries)
            .success.value
            .set(ReporterTaxpayersStartDateForImplementingArrangementPage, LocalDate.now())
            .success.value
            .set(RoleInArrangementPage, Intermediary)
            .success.value

          result.get(TaxpayerWhyReportInUKPage) must not be defined
          result.get(TaxpayerWhyReportArrangementPage) must not be defined
          result.get(ReporterTaxpayersStartDateForImplementingArrangementPage) must not be defined

      }
    }

    "must remove intermediary details if reporter selects taxpayer" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .set(IntermediaryWhyReportInUKPage, TaxResidentUK)
            .success.value
            .set(IntermediaryRolePage, Promoter)
            .success.value
            .set(IntermediaryExemptionInEUPage, Yes)
            .success.value
            .set(IntermediaryDoYouKnowExemptionsPage, true)
            .success.value
            .set(IntermediaryWhichCountriesExemptPage, CountriesListEUCheckboxes.enumerable.withName("Austria").toSet)
            .success.value
            .set(RoleInArrangementPage, Taxpayer)
            .success.value

          result.get(IntermediaryWhyReportInUKPage) must not be defined
          result.get(IntermediaryRolePage) must not be defined
          result.get(IntermediaryExemptionInEUPage) must not be defined
          result.get(IntermediaryDoYouKnowExemptionsPage) must not be defined
          result.get(IntermediaryWhichCountriesExemptPage) must not be defined

      }
    }
  }
}
