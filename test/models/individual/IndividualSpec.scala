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

package models.individual

import java.time.LocalDate

import generators.ModelGenerators
import models.taxpayer.TaxResidency
import models.{Address, LoopDetails, Name, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individual._

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
              UserAnswers("id").set(IndividualNamePage, name)
                .success.value
                .set(IndividualDateOfBirthPage, LocalDate.now())
                .success.value
                .set(IndividualLoopPage, loop)
                .success.value

            val expected = Individual(
              individualName = name,
              birthDate = LocalDate.now(),
              birthPlace = None,
              address = None,
              emailAddress = None,
              taxResidencies = TaxResidency.buildTaxResidency(loop)
            )

            val individual = Individual.buildIndividualDetails(userAnswers)

            individual mustBe expected
        }
      }

      "must create an Individual if all details are available" in {
        forAll(arbitrary[Name], arbitrary[String], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, birthPlace, address, email, loop) =>

            val userAnswers =
              UserAnswers("id").set(IndividualNamePage, name)
                .success.value
                .set(IndividualDateOfBirthPage, LocalDate.now())
                .success.value
                .set(IndividualPlaceOfBirthPage, birthPlace)
                .success.value
                .set(IndividualAddressPage, address)
                .success.value
                .set(EmailAddressForIndividualPage, email)
                .success.value
                .set(IndividualLoopPage, loop)
                .success.value

            val expected = Individual(
              individualName = name,
              birthDate = LocalDate.now(),
              birthPlace = Some(birthPlace),
              address = Some(address),
              emailAddress = Some(email),
              taxResidencies = TaxResidency.buildTaxResidency(loop)
            )

            val individual = Individual.buildIndividualDetails(userAnswers)

            individual mustBe expected
        }
      }

      "must throw an Exception if date of birth data is missing" in {
        forAll(arbitrary[Name], arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (name, address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .set(IndividualNamePage, name)
                .success.value
                .set(IndividualAddressPage, address)
                .success.value
                .set(EmailAddressForIndividualPage, email)
                .success.value
                .set(IndividualLoopPage, loop)
                .success.value

            val ex = intercept[Exception] {
              Individual.buildIndividualDetails(userAnswers)
            }

            ex.getMessage mustEqual "Individual Taxpayer must contain a date of birth"
        }
      }

      "must throw an Exception if name is missing" in {
        forAll(arbitrary[Address], arbitrary[String], arbitrary[IndexedSeq[LoopDetails]]) {
          (address, email, loop) =>

            val userAnswers =
              UserAnswers("id")
                .set(IndividualDateOfBirthPage, LocalDate.now())
                .success.value
                .set(IndividualAddressPage, address)
                .success.value
                .set(EmailAddressForIndividualPage, email)
                .success.value
                .set(IndividualLoopPage, loop)
                .success.value

            val ex = intercept[Exception] {
              Individual.buildIndividualDetails(userAnswers)
            }

            ex.getMessage mustEqual "Individual Taxpayer must contain a name"
        }
      }

      "must throw an Exception if tax residency is missing" in {
        forAll(arbitrary[Name], arbitrary[Address], arbitrary[String]) {
          (name, address, email) =>

            val userAnswers =
              UserAnswers("id")
                .set(IndividualNamePage, name)
                .success.value
                .set(IndividualDateOfBirthPage, LocalDate.now())
                .success.value
                .set(IndividualAddressPage, address)
                .success.value
                .set(EmailAddressForIndividualPage, email)
                .success.value

            val ex = intercept[Exception] {
              Individual.buildIndividualDetails(userAnswers)
            }

            ex.getMessage mustEqual "Individual Taxpayer must contain at minimum one tax residency"
        }
      }
    }
  }
}
