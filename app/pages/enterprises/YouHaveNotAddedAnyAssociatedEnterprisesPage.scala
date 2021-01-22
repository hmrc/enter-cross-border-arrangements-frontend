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

import models.UserAnswers
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import pages.QuestionPage
import pages.individual._
import pages.organisation._
import play.api.libs.json.JsPath

import scala.util.Try

case object YouHaveNotAddedAnyAssociatedEnterprisesPage extends QuestionPage[YouHaveNotAddedAnyAssociatedEnterprises] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "youHaveNotAddedAnyAssociatedEnterprises"

  override def cleanup(value: Option[YouHaveNotAddedAnyAssociatedEnterprises], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(_) =>
        userAnswers.remove(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage)
          .flatMap(_.remove(AssociatedEnterpriseTypePage))
          .flatMap(_.remove(IndividualNamePage))
          .flatMap(_.remove(IsIndividualDateOfBirthKnownPage))
          .flatMap(_.remove(IndividualDateOfBirthPage))
          .flatMap(_.remove(IsIndividualPlaceOfBirthKnownPage))
          .flatMap(_.remove(IndividualPlaceOfBirthPage))
          .flatMap(_.remove(IsIndividualAddressKnownPage))
          .flatMap(_.remove(IsIndividualAddressUkPage))
          .flatMap(_.remove(IndividualUkPostcodePage))
          .flatMap(_.remove(IndividualSelectAddressPage))
          .flatMap(_.remove(IndividualAddressPage))
          .flatMap(_.remove(EmailAddressQuestionForIndividualPage))
          .flatMap(_.remove(EmailAddressForIndividualPage))
          .flatMap(_.remove(WhichCountryTaxForIndividualPage))
          .flatMap(_.remove(DoYouKnowAnyTINForUKIndividualPage))
          .flatMap(_.remove(WhatAreTheTaxNumbersForUKIndividualPage))
          .flatMap(_.remove(IsIndividualResidentForTaxOtherCountriesPage))
          .flatMap(_.remove(DoYouKnowTINForNonUKIndividualPage))
          .flatMap(_.remove(WhatAreTheTaxNumbersForNonUKIndividualPage))
          .flatMap(_.remove(IndividualLoopPage))
          .flatMap(_.remove(OrganisationNamePage))
          .flatMap(_.remove(IsOrganisationAddressKnownPage))
          .flatMap(_.remove(IsOrganisationAddressUkPage))
          .flatMap(_.remove(SelectAddressPage))
          .flatMap(_.remove(PostcodePage))
          .flatMap(_.remove(OrganisationAddressPage))
          .flatMap(_.remove(EmailAddressQuestionForOrganisationPage))
          .flatMap(_.remove(EmailAddressForOrganisationPage))
          .flatMap(_.remove(WhichCountryTaxForOrganisationPage))
          .flatMap(_.remove(DoYouKnowAnyTINForUKOrganisationPage))
          .flatMap(_.remove(WhatAreTheTaxNumbersForUKOrganisationPage))
          .flatMap(_.remove(IsOrganisationResidentForTaxOtherCountriesPage))
          .flatMap(_.remove(DoYouKnowTINForNonUKOrganisationPage))
          .flatMap(_.remove(WhatAreTheTaxNumbersForNonUKOrganisationPage))
          .flatMap(_.remove(OrganisationLoopPage))
          .flatMap(_.remove(IsAssociatedEnterpriseAffectedPage))
      case None => super.cleanup(value, userAnswers)
    }
  }
}
