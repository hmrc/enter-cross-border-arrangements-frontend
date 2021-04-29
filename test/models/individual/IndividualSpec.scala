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

package models.individual

import generators.ModelGenerators
import models.taxpayer.TaxResidency
import models.{Address, LoopDetails, Name, UnsubmittedDisclosure, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individual._
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate

class IndividualSpec extends FreeSpec
  with MustMatchers
  with ScalaCheckPropertyChecks
  with ModelGenerators {

  "Individual" - {

    "buildIndividualDetails" - {

      "must create an Individual if mandatory details are available" in {
        forAll(arbitrary[Name], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IndividualNamePage, 0, name)
                .success.value
                .set(IndividualDateOfBirthPage, 0, LocalDate.now())
                .success.value
                .set(IndividualLoopPage, 0, loop)
                .success.value

            val expected = Individual(
              individualName = name,
              birthDate = Some(LocalDate.now()),
              birthPlace = None,
              address = None,
              emailAddress = None,
              taxResidencies = TaxResidency.buildFromLoopDetails(loop)
            )

            val individual = Individual.buildIndividualDetails(userAnswers, 0)

            individual mustBe expected
        }
      }

      "must create an Individual if all details are available" in {
        forAll(arbitrary[Name], arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, birthPlace, address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IndividualNamePage, 0, name)
                .success.value
                .set(IndividualDateOfBirthPage, 0, LocalDate.now())
                .success.value
                .set(IndividualPlaceOfBirthPage, 0, birthPlace)
                .success.value
                .set(IndividualAddressPage, 0, address)
                .success.value
                .set(EmailAddressForIndividualPage, 0, email)
                .success.value
                .set(IndividualLoopPage, 0, loop)
                .success.value

            val expected = Individual(
              individualName = name,
              birthDate = Some(LocalDate.now()),
              birthPlace = Some(birthPlace),
              address = Some(address),
              emailAddress = Some(email),
              taxResidencies = TaxResidency.buildFromLoopDetails(loop)
            )

            val individual = Individual.buildIndividualDetails(userAnswers, 0)

            individual mustBe expected
        }
      }

      "must be None if date of birth data is missing" in {
        forAll(arbitrary[Name], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IndividualNamePage, 0, name)
                .success.value
                .set(IndividualAddressPage, 0, address)
                .success.value
                .set(EmailAddressForIndividualPage, 0, email)
                .success.value
                .set(IndividualLoopPage, 0, loop)
                .success.value

            val expected = Individual.buildIndividualDetails(userAnswers, 0)

            expected.birthDate mustEqual None
        }
      }

      "must throw an Exception if name is missing" in {
        forAll(arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IndividualDateOfBirthPage, 0, LocalDate.now())
                .success.value
                .set(IndividualAddressPage, 0, address)
                .success.value
                .set(EmailAddressForIndividualPage, 0, email)
                .success.value
                .set(IndividualLoopPage, 0, loop)
                .success.value

            val ex = intercept[Exception] {
              Individual.buildIndividualDetails(userAnswers, 0)
            }

            ex.getMessage mustEqual "Individual Taxpayer must contain a name"
        }
      }

      "must throw an Exception if tax residency is missing" in {
        forAll(arbitrary[Name], arbitrary[Address], arbitrary[String]) {
          (name, address, email) =>

            val userAnswers =
              UserAnswers("id")
                .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
                .set(IndividualNamePage, 0, name)
                .success.value
                .set(IndividualDateOfBirthPage, 0, LocalDate.now())
                .success.value
                .set(IndividualAddressPage, 0, address)
                .success.value
                .set(EmailAddressForIndividualPage, 0, email)
                .success.value

            val ex = intercept[Exception] {
              Individual.buildIndividualDetails(userAnswers, 0)
            }

            ex.getMessage mustEqual "Individual Taxpayer must contain at minimum one tax residency"
        }
      }
    }
  }
}
