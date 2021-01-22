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

package pages.enterprises

import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.{Address, Country, LoopDetails, Name, SelectType, TaxReferenceNumbers, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.individual._
import pages.organisation._

import java.time.LocalDate

class YouHaveNotAddedAnyAssociatedEnterprisesPageSpec extends PageBehaviours {

  "YouHaveNotAddedAnyAssociatedEnterprisesPage" - {

    beRetrievable[YouHaveNotAddedAnyAssociatedEnterprises](YouHaveNotAddedAnyAssociatedEnterprisesPage)

    beSettable[YouHaveNotAddedAnyAssociatedEnterprises](YouHaveNotAddedAnyAssociatedEnterprisesPage)

    beRemovable[YouHaveNotAddedAnyAssociatedEnterprises](YouHaveNotAddedAnyAssociatedEnterprisesPage)

    "must remove previous answers and individual journey pages if Individual was selected" in {

      forAll(arbitrary[Country], arbitrary[Address], arbitrary[IndexedSeq[LoopDetails]], arbitrary[TaxReferenceNumbers]) {
        (country, address, loopDetails, taxRefNumbers) =>
          val result = UserAnswers("id")
            .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, List("Taxpayer"))
            .success.value
            .set(AssociatedEnterpriseTypePage, SelectType.Individual)
            .success.value
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
            .set(IsAssociatedEnterpriseAffectedPage, true)
            .success.value
            .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)
            .success.value

          result.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage) mustBe None
          result.get(AssociatedEnterpriseTypePage) mustBe None
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
          result.get(IsAssociatedEnterpriseAffectedPage) mustBe None
      }
    }

    "must remove previous answers and organisation journey pages if Organisation was selected" in {

      forAll(arbitrary[Country], arbitrary[Address], arbitrary[IndexedSeq[LoopDetails]], arbitrary[TaxReferenceNumbers]) {
        (country, address, loopDetails, taxRefNumbers) =>
          val result = UserAnswers("id")
            .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, List("Taxpayer"))
            .success.value
            .set(AssociatedEnterpriseTypePage, SelectType.Organisation)
            .success.value
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
            .set(IsAssociatedEnterpriseAffectedPage, false)
            .success.value
            .set(YouHaveNotAddedAnyAssociatedEnterprisesPage, YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)
            .success.value

          result.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage) mustBe None
          result.get(AssociatedEnterpriseTypePage) mustBe None
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
          result.get(IsAssociatedEnterpriseAffectedPage) mustBe None
      }
    }
  }
}
