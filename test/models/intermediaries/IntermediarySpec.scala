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
import models.{Address, Country, LoopDetails, Name, SelectType, TaxReferenceNumbers, UserAnswers}
import models.individual.Individual
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import pages.individual.{DoYouKnowAnyTINForUKIndividualPage, DoYouKnowTINForNonUKIndividualPage, EmailAddressForIndividualPage, EmailAddressQuestionForIndividualPage, IndividualAddressPage, IndividualDateOfBirthPage, IndividualLoopPage, IndividualNamePage, IndividualPlaceOfBirthPage, IndividualSelectAddressPage, IndividualUkPostcodePage, IsIndividualAddressKnownPage, IsIndividualAddressUkPage, IsIndividualPlaceOfBirthKnownPage, IsIndividualResidentForTaxOtherCountriesPage, WhatAreTheTaxNumbersForNonUKIndividualPage, WhatAreTheTaxNumbersForUKIndividualPage, WhichCountryTaxForIndividualPage}
import pages.intermediaries.IntermediariesTypePage
import pages.organisation.{DoYouKnowAnyTINForUKOrganisationPage, DoYouKnowTINForNonUKOrganisationPage, EmailAddressForOrganisationPage, EmailAddressQuestionForOrganisationPage, IsOrganisationAddressKnownPage, IsOrganisationAddressUkPage, IsOrganisationResidentForTaxOtherCountriesPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage, PostcodePage, SelectAddressPage, WhatAreTheTaxNumbersForNonUKOrganisationPage, WhatAreTheTaxNumbersForUKOrganisationPage, WhichCountryTaxForOrganisationPage}
import pages.taxpayer.TaxpayerSelectTypePage

import java.time.{LocalDate, LocalDateTime}

class IntermediarySpec extends SpecBase {

  val country: Country = Country("valid", "GB", "United Kingdom")
  val taxRefNumbers: TaxReferenceNumbers = TaxReferenceNumbers("utr", None, None)
  val address: Address = Address(None, None, None, "", None, country)
  val loopDetails: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(None, Some(country), None,None, None, Some(taxRefNumbers)))

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

      val individual = Individual(
        individualName = Name("John", "Smith"),
        birthDate =  LocalDateTime.now().toLocalDate,
        None, None, None, Seq(TaxResidency(Some(country),Some(taxRefNumbers))).toIndexedSeq
      )

      val intermediary = Intermediary.buildIntermediaryDetails(userAnswers)

      intermediary.individual.get mustEqual individual
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

      val organisation = Organisation(
        organisationName = "Organisation name",Some(address),Some("email@email.com"), Seq(TaxResidency(Some(country),Some(taxRefNumbers))).toIndexedSeq)

      val intermediary = Intermediary.buildIntermediaryDetails(userAnswers)

      intermediary.organisation.get mustEqual organisation
    }
  }
}
