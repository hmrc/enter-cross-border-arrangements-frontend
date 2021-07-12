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
import models.{Address, Country, CountryList, IsExemptionKnown, LoopDetails, Name, SelectType, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import pages.individual._
import pages.intermediaries._
import pages.organisation._
import pages.unsubmitted.UnsubmittedDisclosurePage
import java.time.{LocalDate, LocalDateTime}

class IntermediarySpec extends SpecBase {

  val country: Country                     = Country("valid", "GB", "United Kingdom")
  val taxRefNumbers: TaxReferenceNumbers   = TaxReferenceNumbers("utr", None, None)
  val address: Address                     = Address(None, None, None, "", None, country)
  val loopDetails: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(None, Some(country), None, None, Some(true), Some(taxRefNumbers)))
  val exemptCountries: Set[CountryList]    = CountryList.enumerable.withName("ES").toSet

  "Intermediary" - {

    "either must be created from an individual" in {

      val userAnswers: UserAnswers = new UserAnswers("1")
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(IntermediariesTypePage, 0, SelectType.Individual)
        .success
        .value
        .set(IndividualNamePage, 0, Name("John", "Smith"))
        .success
        .value
        .set(IndividualDateOfBirthPage, 0, LocalDate.now())
        .success
        .value
        .set(WhichCountryTaxForIndividualPage, 0, country)
        .success
        .value
        .set(DoYouKnowAnyTINForUKIndividualPage, 0, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForUKIndividualPage, 0, taxRefNumbers)
        .success
        .value
        .set(IsIndividualResidentForTaxOtherCountriesPage, 0, true)
        .success
        .value
        .set(DoYouKnowTINForNonUKIndividualPage, 0, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForUKIndividualPage, 0, taxRefNumbers)
        .success
        .value
        .set(IndividualLoopPage, 0, loopDetails)
        .success
        .value
        .set(WhatTypeofIntermediaryPage, 0, WhatTypeofIntermediary.Promoter)
        .success
        .value
        .set(IsExemptionKnownPage, 0, IsExemptionKnown.Yes)
        .success
        .value
        .set(IsExemptionCountryKnownPage, 0, true)
        .success
        .value
        .set(ExemptCountriesPage, 0, exemptCountries)
        .success
        .value

      val intermediary = Intermediary(userAnswers, 0)

      val individual = Individual(
        individualName = Name("John", "Smith"),
        birthDate = Some(LocalDateTime.now().toLocalDate),
        None,
        None,
        None,
        Seq(TaxResidency(Some(country), Some(taxRefNumbers))).toIndexedSeq
      )

      intermediary.individual.get mustEqual individual
      intermediary.whatTypeofIntermediary mustEqual Promoter
      intermediary.isExemptionKnown mustEqual Yes
      intermediary.isExemptionCountryKnown mustBe Some(true)
      intermediary.exemptCountries mustBe Some(exemptCountries)
    }

    "or must be created from an organisation" in {

      val userAnswers: UserAnswers = new UserAnswers("1")
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
        .success
        .value
        .set(IntermediariesTypePage, 0, SelectType.Organisation)
        .success
        .value
        .set(OrganisationNamePage, 0, "Organisation name")
        .success
        .value
        .set(IsOrganisationAddressKnownPage, 0, true)
        .success
        .value
        .set(IsOrganisationAddressUkPage, 0, true)
        .success
        .value
        .set(SelectAddressPage, 0, "Some address")
        .success
        .value
        .set(PostcodePage, 0, "ZZ1 1ZZ")
        .success
        .value
        .set(OrganisationAddressPage, 0, address)
        .success
        .value
        .set(EmailAddressQuestionForOrganisationPage, 0, true)
        .success
        .value
        .set(EmailAddressForOrganisationPage, 0, "email@email.com")
        .success
        .value
        .set(WhichCountryTaxForOrganisationPage, 0, country)
        .success
        .value
        .set(DoYouKnowAnyTINForUKOrganisationPage, 0, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForUKOrganisationPage, 0, taxRefNumbers)
        .success
        .value
        .set(IsOrganisationResidentForTaxOtherCountriesPage, 0, true)
        .success
        .value
        .set(DoYouKnowTINForNonUKOrganisationPage, 0, true)
        .success
        .value
        .set(WhatAreTheTaxNumbersForNonUKOrganisationPage, 0, taxRefNumbers)
        .success
        .value
        .set(OrganisationLoopPage, 0, loopDetails)
        .success
        .value
        .set(WhatTypeofIntermediaryPage, 0, WhatTypeofIntermediary.Promoter)
        .success
        .value
        .set(IsExemptionKnownPage, 0, IsExemptionKnown.Yes)
        .success
        .value
        .set(IsExemptionCountryKnownPage, 0, true)
        .success
        .value
        .set(ExemptCountriesPage, 0, exemptCountries)
        .success
        .value

      val organisation = Organisation(organisationName = "Organisation name",
                                      Some(address),
                                      Some("email@email.com"),
                                      Seq(TaxResidency(Some(country), Some(taxRefNumbers))).toIndexedSeq
      )

      val intermediary = Intermediary(userAnswers, 0)

      intermediary.organisation.get mustEqual organisation
      intermediary.whatTypeofIntermediary mustEqual Promoter
      intermediary.isExemptionKnown mustEqual Yes
      intermediary.isExemptionCountryKnown mustBe Some(true)
      intermediary.exemptCountries mustBe Some(exemptCountries)
    }
  }
}
