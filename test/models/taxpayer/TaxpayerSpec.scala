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

package models.taxpayer

import base.ModelSpecBase
import models.individual.Individual
import models.organisation.Organisation
import models.{Country, Name}

import java.time.LocalDate

class TaxpayerSpec extends ModelSpecBase {

  "Taxpayer" - {

    "either must be created from an individual" in {

      val individual = Individual(
        individualName = Name("John", "Smith"),
        birthDate = Some(LocalDate.now()),
        None,
        None,
        taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), None))
      )

      val taxpayer = Taxpayer("123456789012345678901234567890123456", Some(individual), None, Some(LocalDate.now()))

      taxpayer.taxpayerId.isEmpty mustBe false
      taxpayer.taxpayerId.length mustBe 36
      taxpayer.individual.get mustEqual individual
      taxpayer.implementingDate.get mustEqual LocalDate.now()
    }

    "or must be created from an organisation" in {

      val organisation = Organisation(
        organisationName = "My organisation",
        taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), None))
      )

      val taxpayer = Taxpayer("123456789012345678901234567890123456", None, Some(organisation), Some(LocalDate.now()))

      taxpayer.taxpayerId.isEmpty mustBe false
      taxpayer.taxpayerId.length mustBe 36
      taxpayer.organisation.get mustEqual organisation
      taxpayer.implementingDate.get mustEqual LocalDate.now()
    }
  }
}
