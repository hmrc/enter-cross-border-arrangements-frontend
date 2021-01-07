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

package models.intermediaries

import base.SpecBase
import models.IsExemptionKnown.Yes
import models.individual.Individual
import models.intermediaries.WhatTypeofIntermediary.Promoter
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Address, Country, IsExemptionKnown, LoopDetails, Name, SelectType, TaxReferenceNumbers, UserAnswers}
import pages.individual._
import pages.intermediaries._
import pages.organisation._

import java.time.{LocalDate, LocalDateTime}

class IntermediarySpec extends SpecBase {

  val country: Country = Country("valid", "GB", "United Kingdom")
  val taxRefNumbers: TaxReferenceNumbers = TaxReferenceNumbers("utr", None, None)
  val address: Address = Address(None, None, None, "", None, country)
  val loopDetails: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(None, Some(country), None,None, None, Some(taxRefNumbers)))
  val exemptCountries: Set[ExemptCountries] = ExemptCountries.enumerable.withName("ES").toSet

  "Intermediary" - {

    "either must be created from an individual" in {



      val userAnswers: UserAnswers = new UserAnswers("1").set(IndividualNamePage, Name("John", "Smith"))
        .success.value
        .set(IndividualDateOfBirthPage, LocalDate.now())
        .success.value
        .set(WhichCountryTaxForIndividualPage, country)
        .success.value
        .set(DoYouKnowAnyTINForUKIndividualPage, true)
        .success.value
        .set(WhatAreTheTaxNumbersForUKIndividualPage, taxRefNumbers)
        .success.value
        .set(IsIndividualResidentForTaxOtherCountriesPage, true)
        .success.value
        .set(DoYouKnowTINForNonUKIndividualPage, true)
        .success.value
        .set(WhatAreTheTaxNumbersForUKIndividualPage, taxRefNumbers)
        .success.value
        .set(IntermediariesTypePage, SelectType.Individual)
        .success.value
        .set(IndividualLoopPage, loopDetails)
        .success.value
        .set(WhatTypeofIntermediaryPage, WhatTypeofIntermediary.Promoter)
        .success.value
        .set(IsExemptionKnownPage, IsExemptionKnown.Yes)
        .success.value
        .set(IsExemptionCountryKnownPage, true)
        .success.value
        .set(ExemptCountriesPage, exemptCountries)
        .success.value

      val intermediary = Intermediary.buildIntermediaryDetails(userAnswers)

      val individual = Individual(
        individualName = Name("John", "Smith"),
        birthDate =  LocalDateTime.now().toLocalDate,
        None, None, None, Seq(TaxResidency(Some(country),Some(taxRefNumbers))).toIndexedSeq
      )



      intermediary.individual.get mustEqual individual
      intermediary.whatTypeofIntermediary mustEqual Promoter
      intermediary.isExemptionKnown mustEqual  Yes
      intermediary.isExemptionCountryKnown mustBe Some(true)
      intermediary.exemptCountries mustBe Some(exemptCountries)
    }

    "or must be created from an organisation" in {

      val userAnswers: UserAnswers = new UserAnswers("1")
        .set(OrganisationNamePage, "Organisation name")
        .success.value
        .set(IsOrganisationAddressKnownPage, true)
        .success.value
        .set(IsOrganisationAddressUkPage, true)
        .success.value
        .set(SelectAddressPage, "Some address")
        .success.value
        .set(PostcodePage, "ZZ1 1ZZ")
        .success.value
        .set(OrganisationAddressPage, address)
        .success.value
        .set(EmailAddressQuestionForOrganisationPage, true)
        .success.value
        .set(EmailAddressForOrganisationPage, "email@email.com")
        .success.value
        .set(WhichCountryTaxForOrganisationPage, country)
        .success.value
        .set(DoYouKnowAnyTINForUKOrganisationPage, true)
        .success.value
        .set(WhatAreTheTaxNumbersForUKOrganisationPage, taxRefNumbers)
        .success.value
        .set(IsOrganisationResidentForTaxOtherCountriesPage, true)
        .success.value
        .set(DoYouKnowTINForNonUKOrganisationPage, true)
        .success.value
        .set(WhatAreTheTaxNumbersForNonUKOrganisationPage, taxRefNumbers)
        .success.value
        .set(OrganisationLoopPage, loopDetails)
        .success.value
        .set(IntermediariesTypePage, SelectType.Organisation)
        .success.value
        .set(WhatTypeofIntermediaryPage, WhatTypeofIntermediary.Promoter)
        .success.value
        .set(IsExemptionKnownPage, IsExemptionKnown.Yes)
        .success.value
        .set(IsExemptionCountryKnownPage, true)
        .success.value
        .set(ExemptCountriesPage, exemptCountries)
        .success.value

      val organisation = Organisation(
        organisationName = "Organisation name",Some(address),Some("email@email.com"), Seq(TaxResidency(Some(country),Some(taxRefNumbers))).toIndexedSeq)

      val intermediary = Intermediary.buildIntermediaryDetails(userAnswers)

      intermediary.organisation.get mustEqual organisation
      intermediary.whatTypeofIntermediary mustEqual Promoter
      intermediary.isExemptionKnown mustEqual  Yes
      intermediary.isExemptionCountryKnown mustBe Some(true)
      intermediary.exemptCountries mustBe Some(exemptCountries)
    }
  }
}
