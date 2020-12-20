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

package generators

import models.SelectType
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.hallmarks._
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryExemptionInEU, IntermediaryRole, IntermediaryWhyReportInUK}
import models.taxpayer.UpdateTaxpayer
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.arrangement._
import pages.enterprises.{IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.organisation._
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhyReportInUKPage}
import pages.taxpayer._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryIntermediaryDoYouKnowExemptionsUserAnswersEntry: Arbitrary[(IntermediaryDoYouKnowExemptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IntermediaryDoYouKnowExemptionsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIntermediaryExemptionInEUUserAnswersEntry: Arbitrary[(IntermediaryExemptionInEUPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IntermediaryExemptionInEUPage.type]
        value <- arbitrary[IntermediaryExemptionInEU].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIntermediaryRoleUserAnswersEntry: Arbitrary[(IntermediaryRolePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IntermediaryRolePage.type]
        value <- arbitrary[IntermediaryRole].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhyReportInUKUserAnswersEntry: Arbitrary[(IntermediaryWhyReportInUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IntermediaryWhyReportInUKPage.type]
        value <- arbitrary[IntermediaryWhyReportInUK].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRoleInArrangementUserAnswersEntry: Arbitrary[(RoleInArrangementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RoleInArrangementPage.type]
        value <- arbitrary[RoleInArrangement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTaxpayersStartDateForImplementingArrangementUserAnswersEntry: Arbitrary[(WhatIsTaxpayersStartDateForImplementingArrangementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTaxpayersStartDateForImplementingArrangementPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySelectTypeUserAnswersEntry: Arbitrary[(TaxpayerSelectTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxpayerSelectTypePage.type]
        value <- arbitrary[SelectType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySelectAnyTaxpayersThisEnterpriseIsAssociatedWithUserAnswersEntry: Arbitrary[(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage.type]
        value <- arbitrary[List[String]].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUpdateTaxpayerUserAnswersEntry: Arbitrary[(UpdateTaxpayerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UpdateTaxpayerPage.type]
        value <- arbitrary[UpdateTaxpayer].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYouHaveNotAddedAnyAssociatedEnterprisesUserAnswersEntry: Arbitrary[(YouHaveNotAddedAnyAssociatedEnterprisesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YouHaveNotAddedAnyAssociatedEnterprisesPage.type]
        value <- arbitrary[YouHaveNotAddedAnyAssociatedEnterprises].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsAssociatedEnterpriseAffectedUserAnswersEntry: Arbitrary[(IsAssociatedEnterpriseAffectedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsAssociatedEnterpriseAffectedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGiveDetailsOfThisArrangementUserAnswersEntry: Arbitrary[(GiveDetailsOfThisArrangementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GiveDetailsOfThisArrangementPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichNationalProvisionsIsThisArrangementBasedOnUserAnswersEntry: Arbitrary[(WhichNationalProvisionsIsThisArrangementBasedOnPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichNationalProvisionsIsThisArrangementBasedOnPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheExpectedValueOfThisArrangementUserAnswersEntry: Arbitrary[(WhatIsTheExpectedValueOfThisArrangementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheExpectedValueOfThisArrangementPage.type]
        value <- arbitrary[WhatIsTheExpectedValueOfThisArrangement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichExpectedInvolvedCountriesArrangementUserAnswersEntry: Arbitrary[(WhichExpectedInvolvedCountriesArrangementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichExpectedInvolvedCountriesArrangementPage.type]
        value <- arbitrary[WhichExpectedInvolvedCountriesArrangement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhyAreYouReportingThisArrangementNowUserAnswersEntry: Arbitrary[(WhyAreYouReportingThisArrangementNowPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhyAreYouReportingThisArrangementNowPage.type]
        value <- arbitrary[WhyAreYouReportingThisArrangementNow].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouKnowTheReasonToReportArrangementNowUserAnswersEntry: Arbitrary[(DoYouKnowTheReasonToReportArrangementNowPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouKnowTheReasonToReportArrangementNowPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheImplementationDateUserAnswersEntry: Arbitrary[(WhatIsTheImplementationDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheImplementationDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsThisArrangementCalledUserAnswersEntry: Arbitrary[(WhatIsThisArrangementCalledPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsThisArrangementCalledPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouKnowTINForNonUKIndividualUserAnswersEntry: Arbitrary[(DoYouKnowTINForNonUKIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouKnowTINForNonUKIndividualPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryEmailAddressQuestionForIndividualUserAnswersEntry: Arbitrary[(EmailAddressQuestionForIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmailAddressQuestionForIndividualPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmailAddressForIndividualUserAnswersEntry: Arbitrary[(EmailAddressForIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmailAddressForIndividualPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatAreTheTaxNumbersForNonUKOrganisationUserAnswersEntry: Arbitrary[(WhatAreTheTaxNumbersForNonUKOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatAreTheTaxNumbersForNonUKOrganisationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouKnowTINForNonUKOrganisationUserAnswersEntry: Arbitrary[(DoYouKnowTINForNonUKOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouKnowTINForNonUKOrganisationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichCountryTaxForIndividualUserAnswersEntry: Arbitrary[(WhichCountryTaxForIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichCountryTaxForIndividualPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKIndividualUserAnswersEntry: Arbitrary[(WhatAreTheTaxNumbersForUKIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatAreTheTaxNumbersForUKIndividualPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsIndividualResidentForTaxOtherCountriesUserAnswersEntry: Arbitrary[(IsIndividualResidentForTaxOtherCountriesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsIndividualResidentForTaxOtherCountriesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouKnowAnyTINForUKIndividualUserAnswersEntry: Arbitrary[(DoYouKnowAnyTINForUKIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouKnowAnyTINForUKIndividualPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsOrganisationResidentForTaxOtherCountriesUserAnswersEntry: Arbitrary[(IsOrganisationResidentForTaxOtherCountriesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsOrganisationResidentForTaxOtherCountriesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKOrganisationUserAnswersEntry: Arbitrary[(WhatAreTheTaxNumbersForUKOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatAreTheTaxNumbersForUKOrganisationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouKnowAnyTINForUKOrganisationUserAnswersEntry: Arbitrary[(DoYouKnowAnyTINForUKOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouKnowAnyTINForUKOrganisationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichCountryTaxForOrganisationUserAnswersEntry: Arbitrary[(WhichCountryTaxForOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichCountryTaxForOrganisationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmailAddressForOrganisationUserAnswersEntry: Arbitrary[(EmailAddressForOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmailAddressForOrganisationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmailAddressQuestionForOrganisationUserAnswersEntry: Arbitrary[(EmailAddressQuestionForOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmailAddressQuestionForOrganisationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsIndividualPlaceOfBirthKnownUserAnswersEntry: Arbitrary[(IsIndividualPlaceOfBirthKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsIndividualPlaceOfBirthKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsIndividualAddressKnownUserAnswersEntry: Arbitrary[(IsIndividualAddressKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsIndividualAddressKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualPlaceOfBirthUserAnswersEntry: Arbitrary[(IndividualPlaceOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualPlaceOfBirthPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualNameUserAnswersEntry: Arbitrary[(IndividualNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualDateOfBirthUserAnswersEntry: Arbitrary[(IndividualDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOrganisationAddressUserAnswersEntry: Arbitrary[(OrganisationAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OrganisationAddressPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOrganisationNameUserAnswersEntry: Arbitrary[(OrganisationNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OrganisationNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsOrganisationAddressKnownUserAnswersEntry: Arbitrary[(IsOrganisationAddressKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsOrganisationAddressKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsOrganisationAddressUkUserAnswersEntry: Arbitrary[(IsOrganisationAddressUkPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsOrganisationAddressUkPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryPostcodeUserAnswersEntry: Arbitrary[(PostcodePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PostcodePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkEUserAnswersEntry: Arbitrary[(HallmarkEPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkEPage.type]
        value <- arbitrary[HallmarkE].map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryHallmarkC1UserAnswersEntry: Arbitrary[(HallmarkC1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkC1Page.type]
        value <- arbitrary[HallmarkC1].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkCUserAnswersEntry: Arbitrary[(HallmarkCPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkCPage.type]
        value <- arbitrary[HallmarkC].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkD1OtherUserAnswersEntry: Arbitrary[(HallmarkD1OtherPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkD1OtherPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkD1UserAnswersEntry: Arbitrary[(HallmarkD1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkD1Page.type]
        value <- arbitrary[HallmarkD1].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkDUserAnswersEntry: Arbitrary[(HallmarkDPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkDPage.type]
        value <- arbitrary[HallmarkD].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkBUserAnswersEntry: Arbitrary[(HallmarkBPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkBPage.type]
        value <- arbitrary[HallmarkB].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMeetMainBenefitTestUserAnswersEntry: Arbitrary[(MainBenefitTestPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MainBenefitTestPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkAUserAnswersEntry: Arbitrary[(HallmarkAPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkAPage.type]
        value <- arbitrary[HallmarkA].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkCategoriesUserAnswersEntry: Arbitrary[(HallmarkCategoriesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkCategoriesPage.type]
        value <- arbitrary[HallmarkCategories].map(Json.toJson(_))
      } yield (page, value)
    }
}
