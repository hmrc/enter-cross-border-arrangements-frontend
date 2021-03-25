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

package pages

import models.{SelectType, UserAnswers}
import pages.individual._
import pages.organisation._


import scala.util.{Success, Try}

trait CleanUpSelectTypePage extends QuestionPage[SelectType] {

  override def cleanup(value: Option[SelectType], userAnswers: UserAnswers, id: Int): Try[UserAnswers] = {
    //Clear answers from unique pages in each journey
    (value match {
      case Some(_) =>
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
          .flatMap(_.remove(OrganisationNamePage, id)
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
          )
      case _ => super.cleanup(value, userAnswers, id)
    }).flatMap{ cleanup(_, id) }
  }

  def cleanup(userAnswers: UserAnswers, id: Int): Try[UserAnswers] = Success(userAnswers)
}
