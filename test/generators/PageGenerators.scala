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

import org.scalacheck.Arbitrary
import pages._
import pages.arrangement._
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.organisation._
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryDoYouKnowExemptionsPage, IntermediaryExemptionInEUPage, IntermediaryRolePage, IntermediaryWhichCountriesExemptPage, IntermediaryWhyReportInUKPage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer._
import pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage
import pages.intermediaries.WhatTypeofIntermediaryPage
import pages.reporter.individual.{ReporterIndividualAddressPage, ReporterIndividualDateOfBirthPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage, ReporterIndividualPostcodePage, ReporterIsIndividualAddressUKPage}


trait PageGenerators {

  implicit lazy val arbitraryDisclosureIdentifyArrangementPage: Arbitrary[DisclosureIdentifyArrangementPage.type] =
    Arbitrary(DisclosureIdentifyArrangementPage)

  implicit lazy val arbitraryReporterIndividualAddressPage: Arbitrary[ReporterIndividualAddressPage.type] =
    Arbitrary(ReporterIndividualAddressPage)

  implicit lazy val arbitraryReporterIndividualPostcodePage: Arbitrary[ReporterIndividualPostcodePage.type] =
    Arbitrary(ReporterIndividualPostcodePage)

  implicit lazy val arbitraryReporterIsIndividualAddressUKPage: Arbitrary[ReporterIsIndividualAddressUKPage.type] =
    Arbitrary(ReporterIsIndividualAddressUKPage)

  implicit lazy val arbitraryReporterIndividualPlaceOfBirthPage: Arbitrary[ReporterIndividualPlaceOfBirthPage.type] =
    Arbitrary(ReporterIndividualPlaceOfBirthPage)

  implicit lazy val arbitraryReporterIndividualDateOfBirthPage: Arbitrary[ReporterIndividualDateOfBirthPage.type] =
    Arbitrary(ReporterIndividualDateOfBirthPage)

  implicit lazy val arbitraryReporterIndividualNamePage: Arbitrary[ReporterIndividualNamePage.type] =
    Arbitrary(ReporterIndividualNamePage)

  implicit lazy val arbitraryWhatTypeofIntermediaryPage: Arbitrary[WhatTypeofIntermediaryPage.type] =
    Arbitrary(WhatTypeofIntermediaryPage)

  implicit lazy val arbitraryYouHaveNotAddedAnyIntermediariesPage: Arbitrary[YouHaveNotAddedAnyIntermediariesPage.type] =
    Arbitrary(YouHaveNotAddedAnyIntermediariesPage)

  implicit lazy val arbitraryDisclosureMarketablePage: Arbitrary[DisclosureMarketablePage.type] =
    Arbitrary(DisclosureMarketablePage)

  implicit lazy val arbitraryDisclosureTypePage: Arbitrary[DisclosureTypePage.type] =
    Arbitrary(DisclosureTypePage)

  implicit lazy val arbitraryDisclosureNamePage: Arbitrary[DisclosureNamePage.type] =
    Arbitrary(DisclosureNamePage)

  implicit lazy val arbitraryTaxpayerWhyReportArrangementPage: Arbitrary[TaxpayerWhyReportArrangementPage.type] =
    Arbitrary(TaxpayerWhyReportArrangementPage)

  implicit lazy val arbitraryTaxpayerWhyReportInUKPage: Arbitrary[TaxpayerWhyReportInUKPage.type] =
    Arbitrary(TaxpayerWhyReportInUKPage)

  implicit lazy val arbitraryIntermediaryWhichCountriesExemptPage: Arbitrary[IntermediaryWhichCountriesExemptPage.type] =
    Arbitrary(IntermediaryWhichCountriesExemptPage)

  implicit lazy val arbitraryIntermediaryDoYouKnowExemptionsPage: Arbitrary[IntermediaryDoYouKnowExemptionsPage.type] =
    Arbitrary(IntermediaryDoYouKnowExemptionsPage)

  implicit lazy val arbitraryIntermediaryExemptionInEUPage: Arbitrary[IntermediaryExemptionInEUPage.type] =
    Arbitrary(IntermediaryExemptionInEUPage)

  implicit lazy val arbitraryIntermediaryRolePage: Arbitrary[IntermediaryRolePage.type] =
    Arbitrary(IntermediaryRolePage)

  implicit lazy val arbitraryWhyReportInUKPage: Arbitrary[IntermediaryWhyReportInUKPage.type] =
    Arbitrary(IntermediaryWhyReportInUKPage)

  implicit lazy val arbitraryRoleInArrangementPage: Arbitrary[RoleInArrangementPage.type] =
    Arbitrary(RoleInArrangementPage)

  implicit lazy val arbitraryIsIndividualDateOfBirthKnownPage: Arbitrary[IsIndividualDateOfBirthKnownPage.type] =
    Arbitrary(IsIndividualDateOfBirthKnownPage)

  implicit lazy val arbitraryWhatIsTaxpayersStartDateForImplementingArrangementPage: Arbitrary[WhatIsTaxpayersStartDateForImplementingArrangementPage.type] =
    Arbitrary(WhatIsTaxpayersStartDateForImplementingArrangementPage)

  implicit lazy val arbitraryAssociatedEnterpriseTypePage: Arbitrary[AssociatedEnterpriseTypePage.type] =
    Arbitrary(AssociatedEnterpriseTypePage)

  implicit lazy val arbitrarySelectTypePage: Arbitrary[TaxpayerSelectTypePage.type] =
    Arbitrary(TaxpayerSelectTypePage)

  implicit lazy val arbitrarySelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage: Arbitrary[SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage.type] =
    Arbitrary(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage)

  implicit lazy val arbitraryUpdateTaxpayerPage: Arbitrary[UpdateTaxpayerPage.type] =
    Arbitrary(UpdateTaxpayerPage)

  implicit lazy val arbitraryIsAssociatedEnterpriseAffectedPage: Arbitrary[IsAssociatedEnterpriseAffectedPage.type] =
    Arbitrary(IsAssociatedEnterpriseAffectedPage)

  implicit lazy val arbitraryYouHaveNotAddedAnyAssociatedEnterprisesPage: Arbitrary[YouHaveNotAddedAnyAssociatedEnterprisesPage.type] =
    Arbitrary(YouHaveNotAddedAnyAssociatedEnterprisesPage)

  implicit lazy val arbitraryGiveDetailsOfThisArrangementPage: Arbitrary[GiveDetailsOfThisArrangementPage.type] =
    Arbitrary(GiveDetailsOfThisArrangementPage)

  implicit lazy val arbitraryWhichNationalProvisionsIsThisArrangementBasedOnPage: Arbitrary[WhichNationalProvisionsIsThisArrangementBasedOnPage.type] =
    Arbitrary(WhichNationalProvisionsIsThisArrangementBasedOnPage)

  implicit lazy val arbitraryWhatIsTheExpectedValueOfThisArrangementPage: Arbitrary[WhatIsTheExpectedValueOfThisArrangementPage.type] =
    Arbitrary(WhatIsTheExpectedValueOfThisArrangementPage)

  implicit lazy val arbitraryWhichExpectedInvolvedCountriesArrangementPage: Arbitrary[WhichExpectedInvolvedCountriesArrangementPage.type] =
    Arbitrary(WhichExpectedInvolvedCountriesArrangementPage)

  implicit lazy val arbitraryWhyAreYouReportingThisArrangementNowPage: Arbitrary[WhyAreYouReportingThisArrangementNowPage.type] =
    Arbitrary(WhyAreYouReportingThisArrangementNowPage)

  implicit lazy val arbitraryDoYouKnowTheReasonToReportArrangementNowPage: Arbitrary[DoYouKnowTheReasonToReportArrangementNowPage.type] =
    Arbitrary(DoYouKnowTheReasonToReportArrangementNowPage)

  implicit lazy val arbitraryWhatIsTheImplementationDatePage: Arbitrary[WhatIsTheImplementationDatePage.type] =
    Arbitrary(WhatIsTheImplementationDatePage)

  implicit lazy val arbitraryWhatIsThisArrangementCalledPage: Arbitrary[WhatIsThisArrangementCalledPage.type] =
    Arbitrary(WhatIsThisArrangementCalledPage)

  implicit lazy val arbitraryDoYouKnowTINForNonUKIndividualPage: Arbitrary[DoYouKnowTINForNonUKIndividualPage.type] =
    Arbitrary(DoYouKnowTINForNonUKIndividualPage)

  implicit lazy val arbitraryWhichCountryTaxForIndividualPage: Arbitrary[WhichCountryTaxForIndividualPage.type] =
    Arbitrary(WhichCountryTaxForIndividualPage)

  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKIndividualPage: Arbitrary[WhatAreTheTaxNumbersForUKIndividualPage.type] =
    Arbitrary(WhatAreTheTaxNumbersForUKIndividualPage)

  implicit lazy val arbitraryIsIndividualResidentForTaxOtherCountriesPage: Arbitrary[IsIndividualResidentForTaxOtherCountriesPage.type] =
    Arbitrary(IsIndividualResidentForTaxOtherCountriesPage)

  implicit lazy val arbitraryDoYouKnowAnyTINForUKIndividualPage: Arbitrary[DoYouKnowAnyTINForUKIndividualPage.type] =
    Arbitrary(DoYouKnowAnyTINForUKIndividualPage)

  implicit lazy val arbitraryEmailAddressQuestionForIndividualPage: Arbitrary[EmailAddressQuestionForIndividualPage.type] =
    Arbitrary(EmailAddressQuestionForIndividualPage)

  implicit lazy val arbitraryEmailAddressForIndividualPage: Arbitrary[EmailAddressForIndividualPage.type] =
    Arbitrary(EmailAddressForIndividualPage)

  implicit lazy val arbitraryOrganisationLoopPage: Arbitrary[OrganisationLoopPage.type] =
    Arbitrary(OrganisationLoopPage)

  implicit lazy val arbitraryWhatAreTheTaxNumbersForNonUKOrganisationPage: Arbitrary[WhatAreTheTaxNumbersForNonUKOrganisationPage.type] =
    Arbitrary(WhatAreTheTaxNumbersForNonUKOrganisationPage)

  implicit lazy val arbitraryDoYouKnowTINForNonUKOrganisationPage: Arbitrary[DoYouKnowTINForNonUKOrganisationPage.type] =
    Arbitrary(DoYouKnowTINForNonUKOrganisationPage)

  implicit lazy val arbitraryIsIndividualPlaceOfBirthKnownPage: Arbitrary[IsIndividualPlaceOfBirthKnownPage.type] =
    Arbitrary(IsIndividualPlaceOfBirthKnownPage)

  implicit lazy val arbitraryIsIndividualAddressKnownPage: Arbitrary[IsIndividualAddressKnownPage.type] =
    Arbitrary(IsIndividualAddressKnownPage)

  implicit lazy val arbitraryIndividualPlaceOfBirthPage: Arbitrary[IndividualPlaceOfBirthPage.type] =
    Arbitrary(IndividualPlaceOfBirthPage)

  implicit lazy val arbitraryIndividualNamePage: Arbitrary[IndividualNamePage.type] =
    Arbitrary(IndividualNamePage)

  implicit lazy val arbitraryIndividualDateOfBirthPage: Arbitrary[IndividualDateOfBirthPage.type] =
    Arbitrary(IndividualDateOfBirthPage)

  implicit lazy val arbitraryIsOrganisationResidentForTaxOtherCountriesPage: Arbitrary[IsOrganisationResidentForTaxOtherCountriesPage.type] =
    Arbitrary(IsOrganisationResidentForTaxOtherCountriesPage)

  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKOrganisationPage: Arbitrary[WhatAreTheTaxNumbersForUKOrganisationPage.type] =
    Arbitrary(WhatAreTheTaxNumbersForUKOrganisationPage)

  implicit lazy val arbitraryDoYouKnowAnyTINForUKOrganisationPage: Arbitrary[DoYouKnowAnyTINForUKOrganisationPage.type] =
    Arbitrary(DoYouKnowAnyTINForUKOrganisationPage)

  implicit lazy val arbitraryWhichCountryTaxForOrganisationPage: Arbitrary[WhichCountryTaxForOrganisationPage.type] =
    Arbitrary(WhichCountryTaxForOrganisationPage)

  implicit lazy val arbitraryEmailAddressForOrganisationPage: Arbitrary[EmailAddressForOrganisationPage.type] =
    Arbitrary(EmailAddressForOrganisationPage)

  implicit lazy val arbitraryEmailAddressQuestionForOrganisationPage: Arbitrary[EmailAddressQuestionForOrganisationPage.type] =
    Arbitrary(EmailAddressQuestionForOrganisationPage)

  implicit lazy val arbitraryOrganisationAddressPage: Arbitrary[OrganisationAddressPage.type] =
    Arbitrary(OrganisationAddressPage)

  implicit lazy val arbitraryOrganisationNamePage: Arbitrary[OrganisationNamePage.type] =
    Arbitrary(OrganisationNamePage)

  implicit lazy val arbitraryIsOrganisationAddressKnownPage: Arbitrary[IsOrganisationAddressKnownPage.type] =
    Arbitrary(IsOrganisationAddressKnownPage)

  implicit lazy val arbitraryIsOrganisationAddressUkPage: Arbitrary[IsOrganisationAddressUkPage.type] =
    Arbitrary(IsOrganisationAddressUkPage)

  implicit lazy val arbitrarySelectAddressPage: Arbitrary[SelectAddressPage.type] =
    Arbitrary(SelectAddressPage)

  implicit lazy val arbitraryPostcodePage: Arbitrary[PostcodePage.type] =
    Arbitrary(PostcodePage)

  implicit lazy val arbitraryHallmarkEPage: Arbitrary[HallmarkEPage.type] =
    Arbitrary(HallmarkEPage)

  implicit lazy val arbitraryHallmarkC1Page: Arbitrary[HallmarkC1Page.type] =
    Arbitrary(HallmarkC1Page)

  implicit lazy val arbitraryHallmarkCPage: Arbitrary[HallmarkCPage.type] =
    Arbitrary(HallmarkCPage)

  implicit lazy val arbitraryHallmarkD1OtherPage: Arbitrary[HallmarkD1OtherPage.type] =
    Arbitrary(HallmarkD1OtherPage)

  implicit lazy val arbitraryHallmarkD1Page: Arbitrary[HallmarkD1Page.type] =
    Arbitrary(HallmarkD1Page)

  implicit lazy val arbitraryHallmarkDPage: Arbitrary[HallmarkDPage.type] =
    Arbitrary(HallmarkDPage)

  implicit lazy val arbitraryHallmarkBPage: Arbitrary[HallmarkBPage.type] =
    Arbitrary(HallmarkBPage)

  implicit lazy val arbitraryMeetMainBenefitTestPage: Arbitrary[MainBenefitTestPage.type] =
    Arbitrary(MainBenefitTestPage)

  implicit lazy val arbitraryHallmarkAPage: Arbitrary[HallmarkAPage.type] =
    Arbitrary(HallmarkAPage)

  implicit lazy val arbitraryHallmarkCategoriesPage: Arbitrary[HallmarkCategoriesPage.type] =
    Arbitrary(HallmarkCategoriesPage)
}
