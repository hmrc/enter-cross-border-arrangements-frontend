/*
 * Copyright 2022 HM Revenue & Customs
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

package models.organisation

import generators.ModelGenerators
import helpers.data.ValidUserAnswersForSubmission.{validAddress, validOrganisation}
import models.taxpayer.TaxResidency
import models.{Address, Country, LoopDetails, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.organisation._
import pages.unsubmitted.UnsubmittedDisclosurePage

class OrganisationSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with ModelGenerators {

  "Organisation" - {

    "buildOrganisationDetails" - {

      "must create an Organisation if mandatory details are available" in {
        forAll(arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, loop) =>
            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success
                .value
                .set(OrganisationNamePage, 0, name)
                .success
                .value
                .set(OrganisationLoopPage, 0, loop)
                .success
                .value

            val expected = Organisation(
              organisationName = name,
              address = None,
              emailAddress = None,
              taxResidencies = TaxResidency.buildFromLoopDetails(loop)
            )

            val organisation = Organisation.buildOrganisationDetails(userAnswers, 0)

            organisation mustBe expected
        }
      }

      "must create an Organisation if all details are available" in {
        forAll(arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, address, email, loop) =>
            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success
                .value
                .set(OrganisationNamePage, 0, name)
                .success
                .value
                .set(OrganisationAddressPage, 0, address)
                .success
                .value
                .set(EmailAddressForOrganisationPage, 0, email)
                .success
                .value
                .set(OrganisationLoopPage, 0, loop)
                .success
                .value

            val expected = Organisation(
              organisationName = name,
              address = Some(address),
              emailAddress = Some(email),
              taxResidencies = TaxResidency.buildFromLoopDetails(loop)
            )

            val organisation = Organisation.buildOrganisationDetails(userAnswers, 0)

            organisation mustBe expected
        }
      }

      "must throw an Exception when mandatory data is not available" in {
        forAll(arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (address, email, loop) =>
            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
                .success
                .value
                .set(OrganisationAddressPage, 0, address)
                .success
                .value
                .set(EmailAddressForOrganisationPage, 0, email)
                .success
                .value
                .set(OrganisationLoopPage, 0, loop)
                .success
                .value

            val ex = intercept[Exception] {
              Organisation.buildOrganisationDetails(userAnswers, 0)
            }

            ex.getMessage mustEqual "Organisation Taxpayer must contain a name and at minimum one tax residency"
        }
      }

      "must be able to restore pages from organisation" in {
        val userAnswers =
          UserAnswers("id")
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
            .success
            .value

        val taxNumberUK    = Some(TaxReferenceNumbers("UTR1234", None, None))
        val taxNumberNonUK = Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None))
        val loopDetails = Some(
          Vector(
            LoopDetails(Some(false), Some(Country.UK), None, None, Some(true), taxNumberUK),
            LoopDetails(Some(false), Some(Country("", "FR", "France")), Some(true), taxNumberNonUK, None, None)
          )
        )

        validOrganisation.restore(userAnswers, 0).foreach {
          updatedAnswers =>
            updatedAnswers.get(OrganisationNamePage, 0) mustBe (Some("Taxpayers Ltd"))
            updatedAnswers.get(IsOrganisationAddressKnownPage, 0) mustBe (Some(true))
            updatedAnswers.get(IsOrganisationAddressUkPage, 0) mustBe (Some(false))
            updatedAnswers.get(OrganisationAddressPage, 0) mustBe (Some(validAddress))
            updatedAnswers.get(PostcodePage, 0) mustBe (validAddress.postCode)
            updatedAnswers.get(SelectAddressPage, 0) mustBe (Some("value 1, value 2, value 3, value 4XX9 9XX"))
            updatedAnswers.get(EmailAddressQuestionForOrganisationPage, 0) mustBe (Some(true))
            updatedAnswers.get(EmailAddressForOrganisationPage, 0) mustBe (Some("email@email.com"))
            updatedAnswers.get(OrganisationLoopPage, 0) mustBe loopDetails
            updatedAnswers.get(WhichCountryTaxForOrganisationPage, 0) mustBe (Some(Country.UK))
            updatedAnswers.get(DoYouKnowAnyTINForUKOrganisationPage, 0) mustBe (Some(true))
            updatedAnswers.get(WhatAreTheTaxNumbersForUKOrganisationPage, 0) mustBe taxNumberUK
            updatedAnswers.get(DoYouKnowTINForNonUKOrganisationPage, 0) mustBe (Some(false))
            updatedAnswers.get(WhatAreTheTaxNumbersForNonUKOrganisationPage, 0) mustBe None
            updatedAnswers.get(IsOrganisationResidentForTaxOtherCountriesPage, 0) mustBe (Some(false))
        }
      }

    }
  }
}
