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

package pages.reporter

import java.time.LocalDate

import helpers.data.ValidUserAnswersForSubmission.{loopDetails, validCountry, validTaxReferenceNumber, validToday}
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{Address, AddressLookup, Country, CountryList, Name, ReporterOrganisationOrIndividual, UnsubmittedDisclosure, UserAnswers, YesNoDoNotKnowRadios}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation._
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.unsubmitted.UnsubmittedDisclosurePage

class ReporterOrganisationOrIndividualPageSpec extends PageBehaviours {

  val manualAddress = Address(
    Some("addressLine 1"),
    Some("addressLine 2"),
    Some("addressLine 3"),
    "city",
    Some("postcode"),
    Country("valid", "GB", "United Kingdom")
  )

  val addressLookup =
    AddressLookup(Some("addressLine 1"), Some("addressLine 2"), Some("addressLine 3"), Some("addressLine 4"), "town", Some("county"), "postcode")

  "ReporterOrganisationOrIndividualPage" - {

    beRetrievable[ReporterOrganisationOrIndividual](ReporterOrganisationOrIndividualPage)

    beSettable[ReporterOrganisationOrIndividual](ReporterOrganisationOrIndividualPage)

    beRemovable[ReporterOrganisationOrIndividual](ReporterOrganisationOrIndividualPage)

    "must remove organisation details if reporter selects individual" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(ReporterOrganisationNamePage, 0, "name")
            .success
            .value
            .set(ReporterOrganisationEmailAddressQuestionPage, 0, true)
            .success
            .value
            .set(ReporterOrganisationEmailAddressPage, 0, "email@email.com")
            .success
            .value
            .set(ReporterOrganisationAddressPage, 0, manualAddress)
            .success
            .value
            .set(ReporterOrganisationIsAddressUkPage, 0, true)
            .success
            .value
            .set(ReporterOrganisationPostcodePage, 0, "NE1")
            .success
            .value
            .set(ReporterOrganisationSelectAddressPage, 0, "selectAddress")
            .success
            .value
            .set(ReporterSelectedAddressLookupPage, 0, addressLookup)
            .success
            .value
            .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary)
            .success
            .value
            .set(IntermediaryDoYouKnowExemptionsPage, 0, true)
            .success
            .value
            .set(IntermediaryExemptionInEUPage, 0, YesNoDoNotKnowRadios.Yes)
            .success
            .value
            .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter)
            .success
            .value
            .set(IntermediaryWhichCountriesExemptPage, 0, CountryList.enumerable.withName("FR").toSet)
            .success
            .value
            .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.GovernedByLaw)
            .success
            .value
            .set(TaxpayerWhyReportArrangementPage, 0, TaxpayerWhyReportArrangement.OutsideUKOrEU)
            .success
            .value
            .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment)
            .success
            .value
            .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, validToday)
            .success
            .value
            .set(ReporterUKTaxNumbersPage, 0, validTaxReferenceNumber)
            .success
            .value
            .set(ReporterTaxResidencyLoopPage, 0, loopDetails)
            .success
            .value
            .set(ReporterTinNonUKQuestionPage, 0, true)
            .success
            .value
            .set(ReporterTaxResidentCountryPage, 0, validCountry)
            .success
            .value
            .set(ReporterOtherTaxResidentQuestionPage, 0, true)
            .success
            .value
            .set(ReporterNonUKTaxNumbersPage, 0, validTaxReferenceNumber)
            .success
            .value
            .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual)
            .success
            .value

          result.get(ReporterOrganisationNamePage, 0) must not be defined
          result.get(ReporterOrganisationEmailAddressQuestionPage, 0) must not be defined
          result.get(ReporterOrganisationEmailAddressPage, 0) must not be defined
          result.get(ReporterOrganisationAddressPage, 0) must not be defined
          result.get(ReporterOrganisationIsAddressUkPage, 0) must not be defined
          result.get(ReporterOrganisationPostcodePage, 0) must not be defined
          result.get(ReporterOrganisationSelectAddressPage, 0) must not be defined
          result.get(ReporterSelectedAddressLookupPage, 0) must not be defined
          result.get(RoleInArrangementPage, 0) must not be defined
          result.get(TaxpayerWhyReportArrangementPage, 0) must not be defined
          result.get(TaxpayerWhyReportInUKPage, 0) must not be defined
          result.get(ReporterTaxpayersStartDateForImplementingArrangementPage, 0) must not be defined
          result.get(IntermediaryDoYouKnowExemptionsPage, 0) must not be defined
          result.get(IntermediaryExemptionInEUPage, 0) must not be defined
          result.get(IntermediaryRolePage, 0) must not be defined
          result.get(IntermediaryWhichCountriesExemptPage, 0) must not be defined
          result.get(IntermediaryWhyReportInUKPage, 0) must not be defined
          result.get(ReporterUKTaxNumbersPage, 0) must not be defined
          result.get(ReporterTaxResidencyLoopPage, 0) must not be defined
          result.get(ReporterTinNonUKQuestionPage, 0) must not be defined
          result.get(ReporterTaxResidentCountryPage, 0) must not be defined
          result.get(ReporterOtherTaxResidentQuestionPage, 0) must not be defined
          result.get(ReporterNonUKTaxNumbersPage, 0) must not be defined
      }
    }

    "must remove Individual details if reporter selects organisation" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value
            .set(ReporterIndividualNamePage, 0, Name("firstName", "surname"))
            .success
            .value
            .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.now())
            .success
            .value
            .set(ReporterIndividualPlaceOfBirthPage, 0, "Place of Birth")
            .success
            .value
            .set(ReporterIndividualEmailAddressQuestionPage, 0, true)
            .success
            .value
            .set(ReporterIndividualEmailAddressPage, 0, "email@email.com")
            .success
            .value
            .set(ReporterIndividualAddressPage, 0, manualAddress)
            .success
            .value
            .set(ReporterIsIndividualAddressUKPage, 0, true)
            .success
            .value
            .set(ReporterIndividualPostcodePage, 0, "NE1")
            .success
            .value
            .set(ReporterIndividualSelectAddressPage, 0, "selectAddress")
            .success
            .value
            .set(ReporterSelectedAddressLookupPage, 0, addressLookup)
            .success
            .value
            .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary)
            .success
            .value
            .set(IntermediaryDoYouKnowExemptionsPage, 0, true)
            .success
            .value
            .set(IntermediaryExemptionInEUPage, 0, YesNoDoNotKnowRadios.Yes)
            .success
            .value
            .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter)
            .success
            .value
            .set(IntermediaryWhichCountriesExemptPage, 0, CountryList.enumerable.withName("FR").toSet)
            .success
            .value
            .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.GovernedByLaw)
            .success
            .value
            .set(TaxpayerWhyReportArrangementPage, 0, TaxpayerWhyReportArrangement.OutsideUKOrEU)
            .success
            .value
            .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment)
            .success
            .value
            .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, validToday)
            .success
            .value
            .set(ReporterUKTaxNumbersPage, 0, validTaxReferenceNumber)
            .success
            .value
            .set(ReporterTaxResidencyLoopPage, 0, loopDetails)
            .success
            .value
            .set(ReporterTinNonUKQuestionPage, 0, true)
            .success
            .value
            .set(ReporterTaxResidentCountryPage, 0, validCountry)
            .success
            .value
            .set(ReporterOtherTaxResidentQuestionPage, 0, true)
            .success
            .value
            .set(ReporterNonUKTaxNumbersPage, 0, validTaxReferenceNumber)
            .success
            .value
            .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation)
            .success
            .value

          result.get(ReporterIndividualNamePage, 0) must not be defined
          result.get(ReporterIndividualDateOfBirthPage, 0) must not be defined
          result.get(ReporterIndividualPlaceOfBirthPage, 0) must not be defined
          result.get(ReporterIndividualEmailAddressQuestionPage, 0) must not be defined
          result.get(ReporterIndividualEmailAddressPage, 0) must not be defined
          result.get(ReporterIndividualAddressPage, 0) must not be defined
          result.get(ReporterIsIndividualAddressUKPage, 0) must not be defined
          result.get(ReporterIndividualPostcodePage, 0) must not be defined
          result.get(ReporterIndividualSelectAddressPage, 0) must not be defined
          result.get(ReporterSelectedAddressLookupPage, 0) must not be defined
          result.get(RoleInArrangementPage, 0) must not be defined
          result.get(TaxpayerWhyReportArrangementPage, 0) must not be defined
          result.get(TaxpayerWhyReportInUKPage, 0) must not be defined
          result.get(ReporterTaxpayersStartDateForImplementingArrangementPage, 0) must not be defined
          result.get(IntermediaryDoYouKnowExemptionsPage, 0) must not be defined
          result.get(IntermediaryExemptionInEUPage, 0) must not be defined
          result.get(IntermediaryRolePage, 0) must not be defined
          result.get(IntermediaryWhichCountriesExemptPage, 0) must not be defined
          result.get(IntermediaryWhyReportInUKPage, 0) must not be defined
          result.get(ReporterUKTaxNumbersPage, 0) must not be defined
          result.get(ReporterTaxResidencyLoopPage, 0) must not be defined
          result.get(ReporterTinNonUKQuestionPage, 0) must not be defined
          result.get(ReporterTaxResidentCountryPage, 0) must not be defined
          result.get(ReporterOtherTaxResidentQuestionPage, 0) must not be defined
          result.get(ReporterNonUKTaxNumbersPage, 0) must not be defined
      }
    }
  }
}
