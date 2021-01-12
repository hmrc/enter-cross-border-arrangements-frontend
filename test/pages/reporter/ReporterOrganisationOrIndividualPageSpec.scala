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

import models.{Address, AddressLookup, Country, Name, ReporterOrganisationOrIndividual, UserAnswers}
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary
import pages.reporter.individual.{ReporterIndividualAddressPage, ReporterIndividualDateOfBirthPage, ReporterIndividualEmailAddressPage, ReporterIndividualEmailAddressQuestionPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage, ReporterIndividualPostcodePage, ReporterIndividualSelectAddressPage, ReporterIsIndividualAddressUKPage}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationEmailAddressQuestionPage, ReporterOrganisationIsAddressUkPage, ReporterOrganisationNamePage, ReporterOrganisationPostcodePage, ReporterOrganisationSelectAddressPage}

class ReporterOrganisationOrIndividualPageSpec extends PageBehaviours {

  val manualAddress = Address(
    Some("addressLine 1"),
    Some("addressLine 2"),
    Some("addressLine 3"),
    "city",
    Some("postcode"),
    Country("valid", "GB", "United Kingdom")
  )

  val addressLookup = AddressLookup(
    Some("addressLine 1"),
    Some("addressLine 2"),
    Some("addressLine 3"),
    Some("addressLine 4"),
    "town",
    Some("county"),
    "postcode")

  "ReporterOrganisationOrIndividualPage" - {

    beRetrievable[ReporterOrganisationOrIndividual](ReporterOrganisationOrIndividualPage)

    beSettable[ReporterOrganisationOrIndividual](ReporterOrganisationOrIndividualPage)

    beRemovable[ReporterOrganisationOrIndividual](ReporterOrganisationOrIndividualPage)

  "must remove organisation details if reporter selects individual" in {
    forAll(arbitrary[UserAnswers]) {
      answers =>
        val result = answers
          .set(ReporterOrganisationNamePage, "name")
          .success.value
          .set(ReporterOrganisationEmailAddressQuestionPage, true)
          .success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.com")
          .success.value
          .set(ReporterOrganisationAddressPage, manualAddress)
          .success.value
          .set(ReporterOrganisationIsAddressUkPage, true)
          .success.value
          .set(ReporterOrganisationPostcodePage, "NE1")
          .success.value
          .set(ReporterOrganisationSelectAddressPage, "selectAddress")
          .success.value
          .set(ReporterSelectedAddressLookupPage, addressLookup)
          .success.value
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual)
          .success.value

        result.get(ReporterOrganisationNamePage) must not be defined
        result.get(ReporterOrganisationEmailAddressQuestionPage) must not be defined
        result.get(ReporterOrganisationEmailAddressPage) must not be defined
        result.get(ReporterOrganisationAddressPage) must not be defined
        result.get(ReporterOrganisationIsAddressUkPage) must not be defined
        result.get(ReporterOrganisationPostcodePage) must not be defined
        result.get(ReporterOrganisationSelectAddressPage) must not be defined
        result.get(ReporterSelectedAddressLookupPage) must not be defined
    }
  }


    "must remove Individual details if reporter selects organisation" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val result = answers
            .set(ReporterIndividualNamePage, Name("firstName","surname"))
            .success.value
            .set(ReporterIndividualDateOfBirthPage, LocalDate.now())
            .success.value
            .set(ReporterIndividualPlaceOfBirthPage, "Place of Birth")
            .success.value
            .set(ReporterIndividualEmailAddressQuestionPage, true)
            .success.value
            .set(ReporterIndividualEmailAddressPage, "email@email.com")
            .success.value
            .set(ReporterIndividualAddressPage, manualAddress)
            .success.value
            .set(ReporterIsIndividualAddressUKPage, true)
            .success.value
            .set(ReporterIndividualPostcodePage, "NE1")
            .success.value
            .set(ReporterIndividualSelectAddressPage, "selectAddress")
            .success.value
            .set(ReporterSelectedAddressLookupPage, addressLookup)
            .success.value
            .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation)
            .success.value

          result.get(ReporterIndividualNamePage) must not be defined
          result.get(ReporterIndividualDateOfBirthPage) must not be defined
          result.get(ReporterIndividualPlaceOfBirthPage) must not be defined
          result.get(ReporterIndividualEmailAddressQuestionPage) must not be defined
          result.get(ReporterIndividualEmailAddressPage) must not be defined
          result.get(ReporterIndividualAddressPage) must not be defined
          result.get(ReporterIsIndividualAddressUKPage) must not be defined
          result.get(ReporterIndividualPostcodePage) must not be defined
          result.get(ReporterIndividualSelectAddressPage) must not be defined
          result.get(ReporterSelectedAddressLookupPage) must not be defined
      }
    }

  }
}
