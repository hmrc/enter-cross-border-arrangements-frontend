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

package helpers.data

import models.CountryList.{France, UnitedKingdom}
import models.affected.Affected
import models.arrangement.{ArrangementDetails, ExpectedArrangementValue, WhyAreYouReportingThisArrangementNow}
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.enterprises.AssociatedEnterprise
import models.hallmarks.{HallmarkD, HallmarkD1, HallmarkDetails}
import models.individual.Individual
import models.intermediaries.{Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.reporter.{ReporterDetails, ReporterLiability, RoleInArrangement}
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{
  Address,
  Country,
  CountryList,
  IsExemptionKnown,
  LoopDetails,
  Name,
  ReporterOrganisationOrIndividual,
  TaxReferenceNumbers,
  UnsubmittedDisclosure,
  UserAnswers
}
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import pages.GiveDetailsOfThisArrangementPage
import pages.affected.AffectedLoopPage
import pages.arrangement._
import pages.disclosure.{DisclosureDetailsPage, DisclosureMarketablePage}
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage, HallmarkDetailsPage}
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.individual._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterDetailsPage, ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate

object ValidUserAnswersForSubmission {

  val validAddress: Address =
    Address(
      Some("value 1"),
      Some("value 2"),
      Some("value 3"),
      "value 4",
      Some("XX9 9XX"),
      Country("valid", "FR", "France")
    )

  val validCountry: Country                        = Country("valid", "GB", "United Kingdom")
  val validTaxReferenceNumber: TaxReferenceNumbers = TaxReferenceNumbers("UTR1234", None, None)

  val loopDetails = IndexedSeq(
    LoopDetails(Some(true),
                Some(Country("valid", "GB", "United Kingdom")),
                Some(true),
                None,
                None,
                Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))
    ),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None)
  )

  val validEmail = "email@email.com"

  val validTaxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val validIndividualName: Name     = Name("FirstName", "Surname")
  val validIndividualDOB: LocalDate = LocalDate.of(1990, 1, 1)

  val validIndividual: Individual =
    Individual(validIndividualName, Some(validIndividualDOB), Some("SomePlace"), Some(validAddress), Some(validEmail), validTaxResidencies)

  val validOrganisation: Organisation = Organisation("Taxpayers Ltd", Some(validAddress), Some(validEmail), validTaxResidencies)

  val reporterDetailsAsIndividual = ReporterDetails(Some(validIndividual))

  val reporterDetailsAsOrganisation = ReporterDetails(None, Some(validOrganisation))

  val validLiability = ReporterLiability(role = "ROLE",
                                         nexus = Some("RTNEXa"),
                                         capacity = Some("DAC61105"),
                                         nationalExemption = Some(true),
                                         exemptCountries = None,
                                         implementingDate = None
  )

  def validToday: LocalDate = LocalDate.now

  val todayMinusOneMonth: LocalDate  = LocalDate.now.minusMonths(1)
  val todayMinusTwoMonths: LocalDate = LocalDate.now.minusMonths(2)

  val validTaxpayers = IndexedSeq(
    Taxpayer("123", None, Some(validOrganisation), Some(todayMinusOneMonth)),
    Taxpayer("Another ID", None, Some(validOrganisation.copy(organisationName = "Other Taxpayers Ltd")), Some(todayMinusTwoMonths))
  )

  val validExemptCountries: Set[CountryList] = Seq(CountryList.UnitedKingdom, CountryList.France).toSet

  val validIntermediaries = IndexedSeq(
    Intermediary("123", None, Some(validOrganisation), WhatTypeofIntermediary.Promoter, IsExemptionKnown.Yes, Some(true), Some(validExemptCountries)),
    Intermediary("Another ID",
                 None,
                 Some(validOrganisation.copy(organisationName = "Other Taxpayers Ltd")),
                 WhatTypeofIntermediary.Promoter,
                 IsExemptionKnown.No,
                 None,
                 None
    )
  )

  private val countries: Set[CountryList] =
    Seq(CountryList.UnitedKingdom, CountryList.France).toSet

  val validEnterprises = IndexedSeq(AssociatedEnterprise("id", Some(validIndividual), None, List(validOrganisation.organisationName), isAffectedBy = false))

  val validAffectedPersons = IndexedSeq(
    Affected("id1", Some(validIndividual), None),
    Affected("id2", None, Some(validOrganisation))
  )

  val validDisclosureDetails = DisclosureDetails(
    disclosureName = "DisclosureName",
    disclosureType = DisclosureType.Dac6new,
    initialDisclosureMA = true
  )

  val validHallmarkDetails = HallmarkDetails(
    hallmarkType = List("DAC6D1a", "DAC6D1Other", "DAC6D2"),
    hallmarkContent = Some("Hallmark D1 other description")
  )

  val validArrangementDetails: ArrangementDetails =
    ArrangementDetails(
      "name",
      validToday,
      Some("DAC6703"),
      List(UnitedKingdom, France),
      ExpectedArrangementValue("GBP", 1000),
      "nationalProvisions",
      "arrangementDetails"
    )

  val userAnswersForOrganisation = UserAnswers("id")
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
    .success
    .value
    .set(DisclosureDetailsPage, 0, validDisclosureDetails)
    .success
    .value
    .set(ArrangementDetailsPage, 0, validArrangementDetails)
    .success
    .value
    .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation)
    .success
    .value
    .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer)
    .success
    .value
    .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment)
    .success
    .value
    .set(ReporterOrganisationNamePage, 0, "Reporter name")
    .success
    .value
    .set(ReporterOrganisationAddressPage, 0, validAddress)
    .success
    .value
    .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk")
    .success
    .value
    .set(ReporterTaxResidencyLoopPage, 0, loopDetails)
    .success
    .value
    .set(DisclosureMarketablePage, 0, true)
    .success
    .value
    .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, validToday)
    .success
    .value
    .set(TaxpayerLoopPage, 0, validTaxpayers)
    .success
    .value
    .set(WhatIsTheImplementationDatePage, 0, validToday)
    .success
    .value
    .set(WhyAreYouReportingThisArrangementNowPage, 0, WhyAreYouReportingThisArrangementNow.Dac6703)
    .success
    .value
    .set(WhatIsThisArrangementCalledPage, 0, "Arrangement name")
    .success
    .value
    .set(GiveDetailsOfThisArrangementPage, 0, "Some description")
    .success
    .value
    .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, "National provisions description")
    .success
    .value
    .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 1000))
    .success
    .value
    .set(WhichExpectedInvolvedCountriesArrangementPage, 0, countries)
    .success
    .value
    .set(HallmarkDPage, 0, HallmarkD.values.toSet)
    .success
    .value
    .set(HallmarkD1Page,
         0,
         (HallmarkD1.enumerable.withName("DAC6D1a") ++
           HallmarkD1.enumerable.withName("DAC6D1Other")).toSet
    )
    .success
    .value
    .set(HallmarkD1OtherPage, 0, "Hallmark D1 other description")
    .success
    .value
    .set(AssociatedEnterpriseLoopPage, 0, validEnterprises)
    .success
    .value

  val userAnswersForIndividual = UserAnswers("id")
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
    .success
    .value
    .set(DisclosureDetailsPage, 0, validDisclosureDetails)
    .success
    .value
    .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual)
    .success
    .value
    .set(ReporterIndividualNamePage, 0, Name("Reporter", "Name"))
    .success
    .value
    .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.of(1990, 1, 1))
    .success
    .value
    .set(ReporterIndividualPlaceOfBirthPage, 0, "SomePlace")
    .success
    .value
    .set(ReporterIndividualAddressPage, 0, validAddress)
    .success
    .value
    .set(ReporterIndividualEmailAddressPage, 0, "email@email.com")
    .success
    .value
    .set(ReporterTaxResidencyLoopPage, 0, loopDetails)
    .success
    .value
    .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer)
    .success
    .value
    .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment)
    .success
    .value
    .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, validToday)
    .success
    .value
    .set(TaxpayerLoopPage, 0, validTaxpayers)
    .success
    .value
    .set(WhatIsTheImplementationDatePage, 0, validToday)
    .success
    .value
    .set(WhyAreYouReportingThisArrangementNowPage, 0, WhyAreYouReportingThisArrangementNow.Dac6703)
    .success
    .value
    .set(WhatIsThisArrangementCalledPage, 0, "Arrangement name")
    .success
    .value
    .set(GiveDetailsOfThisArrangementPage, 0, "Some description")
    .success
    .value
    .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, 0, "National provisions description")
    .success
    .value
    .set(WhatIsTheExpectedValueOfThisArrangementPage, 0, ExpectedArrangementValue("GBP", 1000))
    .success
    .value
    .set(WhichExpectedInvolvedCountriesArrangementPage, 0, countries)
    .success
    .value
    .set(HallmarkDPage, 0, HallmarkD.values.toSet)
    .success
    .value
    .set(HallmarkD1Page,
         0,
         (HallmarkD1.enumerable.withName("DAC6D1a") ++
           HallmarkD1.enumerable.withName("DAC6D1Other")).toSet
    )
    .success
    .value
    .set(HallmarkD1OtherPage, 0, "Hallmark D1 other description")
    .success
    .value
    .set(AssociatedEnterpriseLoopPage, 0, validEnterprises)
    .success
    .value

  val userAnswersModelsForOrganisation = UserAnswers("id")
    .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
    .success
    .value
    .set(DisclosureDetailsPage, 0, validDisclosureDetails)
    .success
    .value
    .set(ReporterDetailsPage, 0, reporterDetailsAsOrganisation)
    .success
    .value
    .set(AssociatedEnterpriseLoopPage, 0, validEnterprises)
    .success
    .value
    .set(TaxpayerLoopPage, 0, validTaxpayers)
    .success
    .value
    .set(IntermediaryLoopPage, 0, validIntermediaries)
    .success
    .value
    .set(AffectedLoopPage, 0, validAffectedPersons)
    .success
    .value
    .set(HallmarkDetailsPage, 0, validHallmarkDetails)
    .success
    .value
    .set(ArrangementDetailsPage, 0, validArrangementDetails)
    .success
    .value
}
