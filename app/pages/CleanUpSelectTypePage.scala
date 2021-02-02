package pages

import models.{SelectType, UserAnswers}
import pages.individual.{DoYouKnowAnyTINForUKIndividualPage, DoYouKnowTINForNonUKIndividualPage, EmailAddressForIndividualPage, EmailAddressQuestionForIndividualPage, IndividualAddressPage, IndividualDateOfBirthPage, IndividualLoopPage, IndividualNamePage, IndividualPlaceOfBirthPage, IndividualSelectAddressPage, IndividualUkPostcodePage, IsIndividualAddressKnownPage, IsIndividualAddressUkPage, IsIndividualDateOfBirthKnownPage, IsIndividualPlaceOfBirthKnownPage, IsIndividualResidentForTaxOtherCountriesPage, WhatAreTheTaxNumbersForNonUKIndividualPage, WhatAreTheTaxNumbersForUKIndividualPage, WhichCountryTaxForIndividualPage}
import pages.organisation.{DoYouKnowAnyTINForUKOrganisationPage, DoYouKnowTINForNonUKOrganisationPage, EmailAddressForOrganisationPage, EmailAddressQuestionForOrganisationPage, IsOrganisationAddressKnownPage, IsOrganisationAddressUkPage, IsOrganisationResidentForTaxOtherCountriesPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage, PostcodePage, SelectAddressPage, WhatAreTheTaxNumbersForNonUKOrganisationPage, WhatAreTheTaxNumbersForUKOrganisationPage, WhichCountryTaxForOrganisationPage}
import queries.Settable

import scala.util.Try

trait CleanUpSelectTypePage {
  self: Settable[SelectType] =>

  override def cleanup(value: Option[SelectType], userAnswers: UserAnswers, id: Int): Try[UserAnswers] = {
    //Clear answers from unique pages in each journey
    value match {
      case Some(SelectType.Organisation) =>
        userAnswers.remove(IndividualNamePage, id)
          .flatMap(_.remove(IsIndividualDateOfBirthKnownPage, id))
          .flatMap(_.remove(IndividualDateOfBirthPage, id))
          .flatMap(_.remove(IsIndividualPlaceOfBirthKnownPage, id))
          .flatMap(_.remove(IndividualPlaceOfBirthPage, id))
          .flatMap(_.remove(IsIndividualAddressKnownPage, id))
          .flatMap(_.remove(IsIndividualAddressUkPage, id))
          .flatMap(_.remove(IndividualUkPostcodePage, id))
          .flatMap(_.remove(IndividualSelectAddressPage, id))
          .flatMap(_.remove(IndividualAddressPage, id))
          .flatMap(_.remove(EmailAddressQuestionForIndividualPage, id))
          .flatMap(_.remove(EmailAddressForIndividualPage, id))
          .flatMap(_.remove(WhichCountryTaxForIndividualPage, id))
          .flatMap(_.remove(DoYouKnowAnyTINForUKIndividualPage, id))
          .flatMap(_.remove(WhatAreTheTaxNumbersForUKIndividualPage, id))
          .flatMap(_.remove(IsIndividualResidentForTaxOtherCountriesPage, id))
          .flatMap(_.remove(DoYouKnowTINForNonUKIndividualPage, id))
          .flatMap(_.remove(WhatAreTheTaxNumbersForNonUKIndividualPage, id))
          .flatMap(_.remove(IndividualLoopPage, id))
      case Some(SelectType.Individual) =>
        userAnswers.remove(OrganisationNamePage, id)
          .flatMap(_.remove(IsOrganisationAddressKnownPage, id))
          .flatMap(_.remove(IsOrganisationAddressUkPage, id))
          .flatMap(_.remove(SelectAddressPage, id))
          .flatMap(_.remove(PostcodePage, id))
          .flatMap(_.remove(OrganisationAddressPage, id))
          .flatMap(_.remove(EmailAddressQuestionForOrganisationPage, id))
          .flatMap(_.remove(EmailAddressForOrganisationPage, id))
          .flatMap(_.remove(WhichCountryTaxForOrganisationPage, id))
          .flatMap(_.remove(DoYouKnowAnyTINForUKOrganisationPage, id))
          .flatMap(_.remove(WhatAreTheTaxNumbersForUKOrganisationPage, id))
          .flatMap(_.remove(IsOrganisationResidentForTaxOtherCountriesPage, id))
          .flatMap(_.remove(DoYouKnowTINForNonUKOrganisationPage, id))
          .flatMap(_.remove(WhatAreTheTaxNumbersForNonUKOrganisationPage, id))
          .flatMap(_.remove(OrganisationLoopPage, id))
      case _ => throw new IllegalStateException
    }
  }
}
