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

package utils

import base.SpecBase
import models.disclosure.{DisclosureDetails, DisclosureType}
import CreateDisplayRows._
import models.Country
import models.affected.Affected
import models.arrangement.{ArrangementDetails, ExpectedArrangementValue}
import models.enterprises.AssociatedEnterprise
import models.organisation.Organisation
import models.taxpayer.{TaxResidency, Taxpayer}
import models.intermediaries.Intermediary

import java.time.LocalDate

class CreateDisplayRowsSpec extends SpecBase {

  val organisation = Organisation("name1", None, None, IndexedSeq(TaxResidency(Some(Country.UK), None)))

  "CreateDisplayRows" - {
    "must add createDisplayRow method to a disclosureDetails object and return the correct number of rows" in {
      val disclosure = DisclosureDetails("disclosure1", DisclosureType.Dac6new)
      disclosure.createDisplayRows.length mustBe 3
    }
    "must add createDisplayRow method to a ArrangementDetails object and return the correct number of rows"in {
      val arrangement = ArrangementDetails("arrangement1",
        LocalDate.now,
        Some("reason1"),
        List("GB"),
        ExpectedArrangementValue("USD",10), "Provision1", "Details1")
      arrangement.createDisplayRows.length mustBe 7
    }
    "must add createDisplayRow method to a TaxpayerDetails object and return the correct number of rows" in {
      val taxpayer = Taxpayer("1",None,Some(organisation), Some(LocalDate.now()))
      taxpayer.createDisplayRows.length mustBe 7
    }
    "must add createDisplayRow method to a AssociatedEnterprises object and return the correct number of rows" in {
      val enterprise = AssociatedEnterprise("1", None, Some(organisation), List("tax1"),true)
      enterprise.createDisplayRows.length mustBe 8
    }
    "must add createDisplayRow method to a Intermediary object and return the correct number of rows" in {
      val intermediary = Intermediary("1",None,Some(organisation))
      intermediary.createDisplayRows.length mustBe 8
    }
    "must add createDisplayRow method to a Affected object and return the correct number of rows" in {
      val affected = Affected("1", None, Some(organisation))
      affected.createDisplayRows.length mustBe 6
    }
  }
}

