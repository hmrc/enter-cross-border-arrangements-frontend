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
import models.{CountriesListEUCheckboxes, UnsubmittedDisclosure, UserAnswers}
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
import pages.unsubmitted.UnsubmittedDisclosurePage

class RoleInArrangementPageSpec extends PageBehaviours {

  "RoleInArrangementPage" - {

    beRetrievable[RoleInArrangement](RoleInArrangementPage)

    beSettable[RoleInArrangement](RoleInArrangementPage)

    beRemovable[RoleInArrangement](RoleInArrangementPage)

    "must remove taxpayer details if reporter selects intermediary" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .set(TaxpayerWhyReportInUKPage, 0, UkTaxResident)
            .success.value
            .set(TaxpayerWhyReportArrangementPage, 0, NoIntermediaries)
            .success.value
            .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now())
            .success.value
            .set(RoleInArrangementPage, 0, Intermediary)
            .success.value

          result.get(TaxpayerWhyReportInUKPage, 0) must not be defined
          result.get(TaxpayerWhyReportArrangementPage, 0) must not be defined
          result.get(ReporterTaxpayersStartDateForImplementingArrangementPage, 0) must not be defined

      }
    }

    "must remove intermediary details if reporter selects taxpayer" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
            .set(IntermediaryWhyReportInUKPage, 0, TaxResidentUK)
            .success.value
            .set(IntermediaryRolePage, 0, Promoter)
            .success.value
            .set(IntermediaryExemptionInEUPage, 0, Yes)
            .success.value
            .set(IntermediaryDoYouKnowExemptionsPage, 0, true)
            .success.value
            .set(IntermediaryWhichCountriesExemptPage, 0, CountriesListEUCheckboxes.enumerable.withName("Austria").toSet)
            .success.value
            .set(RoleInArrangementPage, 0, Taxpayer)
            .success.value

          result.get(IntermediaryWhyReportInUKPage, 0) must not be defined
          result.get(IntermediaryRolePage, 0) must not be defined
          result.get(IntermediaryExemptionInEUPage, 0) must not be defined
          result.get(IntermediaryDoYouKnowExemptionsPage, 0) must not be defined
          result.get(IntermediaryWhichCountriesExemptPage, 0) must not be defined

      }
    }
  }
}
