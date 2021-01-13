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

package pages.taxpayer

import models.{Address, Country, LoopDetails, Name, SelectType, TaxReferenceNumbers, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.individual._
import pages.organisation._

import java.time.LocalDate

class TaxpayerSelectTypePageSpec extends PageBehaviours {

  val country: Country = Country("valid", "GB", "United Kingdom")
  val taxRefNumbers: TaxReferenceNumbers = TaxReferenceNumbers("utr", None, None)
  val address: Address = Address(None, None, None, "", None, country)
  val loopDetails: IndexedSeq[LoopDetails] = IndexedSeq(LoopDetails(None, Some(country), None,None, None, None))

  "SelectTypePage" - {

    beRetrievable[SelectType](TaxpayerSelectTypePage)

    beSettable[SelectType](TaxpayerSelectTypePage)

    beRemovable[SelectType](TaxpayerSelectTypePage)

    "must remove any unique individual journey pages if Organisation is selected" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .set(IndividualNamePage, Name("First", "Last"))
            .success.value
            .set(IndividualDateOfBirthPage, LocalDate.now())
            .success.value
            .set(IsIndividualPlaceOfBirthKnownPage, true)
            .success.value
            .set(IsIndividualDateOfBirthKnownPage, true)
            .success.value
            .set(IndividualPlaceOfBirthPage, "Place of birth")
            .success.value
            .set(IsIndividualAddressKnownPage, true)
            .success.value
            .set(IsIndividualAddressUkPage, true)
            .success.value
            .set(IndividualUkPostcodePage, "ZZ1 1ZZ")
            .success.value
            .set(IndividualSelectAddressPage, "Some address")
            .success.value
            .set(IndividualAddressPage, address)
            .success.value
            .set(EmailAddressQuestionForIndividualPage, true)
            .success.value
            .set(EmailAddressForIndividualPage, "email@email.com")
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
            .set(WhatAreTheTaxNumbersForNonUKIndividualPage, taxRefNumbers)
            .success.value
            .set(IndividualLoopPage, loopDetails)
            .success.value
            .set(TaxpayerSelectTypePage, SelectType.Organisation)
            .success.value

          result.get(IndividualNamePage) mustBe None
          result.get(IndividualDateOfBirthPage) mustBe None
          result.get(IsIndividualPlaceOfBirthKnownPage) mustBe None
          result.get(IsIndividualDateOfBirthKnownPage) mustBe None
          result.get(IndividualPlaceOfBirthPage) mustBe None
          result.get(IsIndividualAddressKnownPage) mustBe None
          result.get(IsIndividualAddressUkPage) mustBe None
          result.get(IndividualUkPostcodePage) mustBe None
          result.get(IndividualSelectAddressPage) mustBe None
          result.get(IndividualAddressPage) mustBe None
          result.get(EmailAddressQuestionForIndividualPage) mustBe None
          result.get(EmailAddressForIndividualPage) mustBe None
          result.get(WhichCountryTaxForIndividualPage) mustBe None
          result.get(DoYouKnowAnyTINForUKIndividualPage) mustBe None
          result.get(WhatAreTheTaxNumbersForUKIndividualPage) mustBe None
          result.get(IsIndividualResidentForTaxOtherCountriesPage) mustBe None
          result.get(DoYouKnowTINForNonUKIndividualPage) mustBe None
          result.get(WhatAreTheTaxNumbersForNonUKIndividualPage) mustBe None
          result.get(IndividualLoopPage) mustBe None
      }
    }

    "must remove any unique individual journey pages if Individual is selected" in {

      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
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
            .set(TaxpayerSelectTypePage, SelectType.Individual)
            .success.value

          result.get(OrganisationNamePage) mustBe None
          result.get(IsOrganisationAddressKnownPage) mustBe None
          result.get(IsOrganisationAddressUkPage) mustBe None
          result.get(SelectAddressPage) mustBe None
          result.get(PostcodePage) mustBe None
          result.get(OrganisationAddressPage) mustBe None
          result.get(EmailAddressQuestionForOrganisationPage) mustBe None
          result.get(EmailAddressForOrganisationPage) mustBe None
          result.get(WhichCountryTaxForOrganisationPage) mustBe None
          result.get(DoYouKnowAnyTINForUKOrganisationPage) mustBe None
          result.get(WhatAreTheTaxNumbersForUKOrganisationPage) mustBe None
          result.get(IsOrganisationResidentForTaxOtherCountriesPage) mustBe None
          result.get(DoYouKnowTINForNonUKOrganisationPage) mustBe None
          result.get(WhatAreTheTaxNumbersForNonUKOrganisationPage) mustBe None
          result.get(OrganisationLoopPage) mustBe None
      }
    }
  }
}
