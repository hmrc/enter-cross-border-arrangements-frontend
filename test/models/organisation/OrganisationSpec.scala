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

package models.organisation

import generators.ModelGenerators
import models.taxpayer.TaxResidency
import models.{Address, LoopDetails, UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import pages.unsubmitted.UnsubmittedDisclosurePage

class OrganisationSpec extends FreeSpec
  with MustMatchers
  with ScalaCheckPropertyChecks
  with ModelGenerators {

  "Organisation" - {

    "buildOrganisationDetails" - {

      "must create an Organisation if mandatory details are available" in {
        forAll(arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(OrganisationNamePage, 0, name)
                .success.value
                .set(OrganisationLoopPage, 0, loop)
                .success.value

            val expected = Organisation(
              organisationName = name,
              address = None,
              emailAddress = None,
              taxResidencies = TaxResidency.buildTaxResidency(loop)
            )

            val organisation = Organisation.buildOrganisationDetails(userAnswers, 0)

            organisation mustBe expected
        }
      }

      "must create an Organisation if all details are available" in {
        forAll(arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name,address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(OrganisationNamePage, 0, name)
                .success.value
                .set(OrganisationAddressPage, 0, address)
                .success.value
                .set(EmailAddressForOrganisationPage, 0, email)
                .success.value
                .set(OrganisationLoopPage, 0, loop)
                .success.value

            val expected = Organisation(
              organisationName = name,
              address = Some(address),
              emailAddress = Some(email),
              taxResidencies = TaxResidency.buildTaxResidency(loop)
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
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(OrganisationAddressPage, 0, address)
                .success.value
                .set(EmailAddressForOrganisationPage, 0, email)
                .success.value
                .set(OrganisationLoopPage, 0, loop)
                .success.value

            val ex = intercept[Exception] {
                Organisation.buildOrganisationDetails(userAnswers, 0)
              }

            ex.getMessage mustEqual "Organisation Taxpayer must contain a name and at minimum one tax residency"
        }
      }

    }
  }
}
