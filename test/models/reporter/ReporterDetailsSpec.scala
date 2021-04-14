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

package models.reporter

import generators.ModelGenerators
import models.individual.Individual
import models.organisation.Organisation
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.taxpayer.TaxResidency
import models.{Address, CountriesListEUCheckboxes, LoopDetails, Name, ReporterOrganisationOrIndividual, UnsubmittedDisclosure, UserAnswers, YesNoDoNotKnowRadios}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.reporter.individual._
import pages.reporter.intermediary._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate

class ReporterDetailsSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with ModelGenerators {

  "ReporterDetails" - {

    "buildReporterDetails" - {

      "must create an reporterDetails as Organisation with role as Taxpayer if all details are available" in {
        forAll(arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name,address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success.value
                .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation)
                .success.value
                .set(ReporterOrganisationNamePage, 0, name)
                .success.value
                .set(ReporterOrganisationAddressPage, 0, address)
                .success.value
                .set(ReporterOrganisationEmailAddressPage, 0, email)
                .success.value
                .set(ReporterTaxResidencyLoopPage, 0, loop)
                .success.value
                .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer)
                .success.value
                .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkTaxResident)
                .success.value
                .set(TaxpayerWhyReportArrangementPage, 0, TaxpayerWhyReportArrangement.NoIntermediaries)
                .success.value
                .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now())
                .success.value


            val expected = ReporterDetails(
              organisation = Some(Organisation(
                organisationName = name,
                address = Some(address),
                emailAddress = Some(email),
                taxResidencies = TaxResidency.buildFromLoopDetails(loop)
              )),
              liability = Some(ReporterLiability(
                role = RoleInArrangement.Taxpayer.toString,
                nexus = Some("RTNEXa"),
                capacity = Some("DAC61106"),
                implementingDate = Some(LocalDate.now())
              ))
            )

            val reporterOrganisation = ReporterDetails.buildReporterDetails(userAnswers, 0)

            reporterOrganisation mustBe expected
        }
      }

      "must create an reporterDetails as Organisation with role as Intermediary if all details are available" in {
        forAll(arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name,address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success.value
                .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation)
                .success.value
                .set(ReporterOrganisationNamePage, 0, name)
                .success.value
                .set(ReporterOrganisationAddressPage, 0, address)
                .success.value
                .set(ReporterOrganisationEmailAddressPage, 0, email)
                .success.value
                .set(ReporterTaxResidencyLoopPage, 0, loop)
                .success.value
                .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary)
                .success.value
                .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.TaxResidentUK)
                .success.value
                .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter)
                .success.value
                .set(IntermediaryExemptionInEUPage, 0, YesNoDoNotKnowRadios.Yes)
                .success.value
                .set(IntermediaryDoYouKnowExemptionsPage, 0, true)
                .success.value
                .set(IntermediaryWhichCountriesExemptPage, 0, CountriesListEUCheckboxes.enumerable.withName("FR").toSet)
                .success.value


            val expected = ReporterDetails(
              organisation = Some(Organisation(
                organisationName = name,
                address = Some(address),
                emailAddress = Some(email),
                taxResidencies = TaxResidency.buildFromLoopDetails(loop)
              )),
              liability = Some(ReporterLiability(
                role = RoleInArrangement.Intermediary.toString,
                nexus = Some("INEXa"),
                capacity = Some("DAC61101"),
                nationalExemption = Some(true),
                exemptCountries = Some(List("FR")),
                implementingDate = None
              ))
            )

            val reporterOrganisation = ReporterDetails.buildReporterDetails(userAnswers, 0)

            reporterOrganisation mustBe expected
        }
      }

      "must create an reporterDetails as Individual with role as Taxpayer if all details are available" in {
        forAll(arbitrary[Name], arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, birthPlace, address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success.value
                .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual)
                .success.value
                .set(ReporterIndividualNamePage, 0, name)
                .success.value
                .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.now())
                .success.value
                .set(ReporterIndividualPlaceOfBirthPage, 0, birthPlace)
                .success.value
                .set(ReporterIndividualAddressPage, 0, address)
                .success.value
                .set(ReporterIndividualEmailAddressPage, 0, email)
                .success.value
                .set(ReporterTaxResidencyLoopPage, 0, loop)
                .success.value
                .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer)
                .success.value
                .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkTaxResident)
                .success.value
                .set(TaxpayerWhyReportArrangementPage, 0, TaxpayerWhyReportArrangement.NoIntermediaries)
                .success.value
                .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, LocalDate.now())
                .success.value


            val expected = ReporterDetails(
              individual = Some(Individual(
                individualName = name,
                birthDate = LocalDate.now(),
                birthPlace = Some(birthPlace),
                address = Some(address),
                emailAddress = Some(email),
                taxResidencies = TaxResidency.buildFromLoopDetails(loop)
              )),
              liability = Some(ReporterLiability(
                role = RoleInArrangement.Taxpayer.toString,
                nexus = Some("RTNEXa"),
                capacity = Some("DAC61106"),
                implementingDate = Some(LocalDate.now())
              ))
            )

            val reporterIndividual = ReporterDetails.buildReporterDetails(userAnswers, 0)

            reporterIndividual mustBe expected
        }
      }

      "must create an reporterDetails as Individual with role as Intermediary if all details are available" in {
        forAll(arbitrary[Name], arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, birthPlace, address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success.value
                .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual)
                .success.value
                .set(ReporterIndividualNamePage, 0, name)
                .success.value
                .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.now())
                .success.value
                .set(ReporterIndividualPlaceOfBirthPage, 0, birthPlace)
                .success.value
                .set(ReporterIndividualAddressPage, 0, address)
                .success.value
                .set(ReporterIndividualEmailAddressPage, 0, email)
                .success.value
                .set(ReporterTaxResidencyLoopPage, 0, loop)
                .success.value
                .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary)
                .success.value
                .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.TaxResidentUK)
                .success.value
                .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter)
                .success.value
                .set(IntermediaryExemptionInEUPage, 0, YesNoDoNotKnowRadios.Yes)
                .success.value
                .set(IntermediaryDoYouKnowExemptionsPage, 0, true)
                .success.value
                .set(IntermediaryWhichCountriesExemptPage, 0, CountriesListEUCheckboxes.enumerable.withName("FR").toSet)
                .success.value


            val expected = ReporterDetails(
              individual = Some(Individual(
                individualName = name,
                birthDate = LocalDate.now(),
                birthPlace = Some(birthPlace),
                address = Some(address),
                emailAddress = Some(email),
                taxResidencies = TaxResidency.buildFromLoopDetails(loop)
              )),
              liability = Some(ReporterLiability(
                role = RoleInArrangement.Intermediary.toString,
                nexus = Some("INEXa"),
                capacity = Some("DAC61101"),
                nationalExemption = Some(true),
                exemptCountries = Some(List("FR")),
                implementingDate = None
              ))
            )

            val reporterIndividual = ReporterDetails.buildReporterDetails(userAnswers, 0)

            reporterIndividual mustBe expected
        }
      }
    }
  }
}
