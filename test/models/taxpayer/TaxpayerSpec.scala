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

package models.taxpayer

import models.individual.Individual
import models.organisation.Organisation
import models.{Name, SelectType}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.time.LocalDateTime

class TaxpayerSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "Taxpayer" - {

    "either must be created from an individual" in {

      val individual = Individual(
        individualName = Name("John", "Smith"),
        birthDate =  LocalDateTime.now()
      )

      val taxpayer = Taxpayer(individual)

      taxpayer.taxpayerId.isEmpty mustBe false
      taxpayer.taxpayerId.length mustBe 36
      taxpayer.selectType mustEqual SelectType.Individual
      taxpayer.individual.get mustEqual individual
    }

    "or must be created from an organisation" in {

      val organisation = Organisation(
        organisationName = "My organisation"
      )

      val taxpayer = Taxpayer(organisation)

      taxpayer.taxpayerId.isEmpty mustBe false
      taxpayer.taxpayerId.length mustBe 36
      taxpayer.selectType mustEqual SelectType.Organisation
      taxpayer.organisation.get mustEqual organisation
    }

  }
}