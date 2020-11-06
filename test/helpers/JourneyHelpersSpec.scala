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

package helpers

import base.SpecBase
import generators.Generators
import helpers.JourneyHelpers.{countryJsonList, currentIndexInsideLoop, getIndividualName, getOrganisationName, incrementIndexOrganisation}
import models.{Country, Name, OrganisationLoopDetails, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{IndividualNamePage, OrganisationLoopPage, OrganisationNamePage}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class JourneyHelpersSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "JourneyHelpers" - {

    "getIndividualName" - {
      "must return the individuals name if it's available" in {
            val userAnswers = UserAnswers(userAnswersId)
              .set(IndividualNamePage, Name("firstName", "lastName"))
                .success
                .value

            getIndividualName(userAnswers) mustBe ("firstName lastName")
        }
    }

    "getOrganisationName" - {
      "must return the organisation name if it's available" in {
        forAll(validOrganisationName) {
          orgName =>
            val userAnswers = UserAnswers(userAnswersId)
              .set(OrganisationNamePage, orgName)
              .success
              .value

            getOrganisationName(userAnswers) mustBe orgName
        }
      }

      "must return 'the organisation' if organisation name isn't available" in {
        getOrganisationName(emptyUserAnswers) mustBe "the organisation"
      }
    }

    "countryJsonList" - {
      "must return the country list with the selected country" in {
        val value = Map("country" -> "GB")
        val countriesSeq: Seq[Country] = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))
        val expectedJsonList = Seq(
          Json.obj("text" -> "", "value" -> ""),
          Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> true),
          Json.obj("text" -> "France", "value" -> "FR", "selected" -> false)
        )

        countryJsonList(value, countriesSeq) mustBe expectedJsonList
      }
    }

  "incrementIndexOrganisation" - {
      val selectedCountry: Country = Country("valid", "GB", "United Kingdom")

      "must return index as 1 if user previously visited UK tin pages and they know TIN for another country (matching URI pattern failed)" in {
        val organisationLoopDetails = IndexedSeq(OrganisationLoopDetails(Some(true), Some(selectedCountry), Some(false), None))
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/")

        val userAnswers = UserAnswers(userAnswersId)
          .set(OrganisationLoopPage, organisationLoopDetails)
          .success
          .value

        incrementIndexOrganisation(userAnswers, request) mustBe 1
      }

      "must add 1 to index from uri if users go through the loop more than once" in {
        val organisationLoopDetails = IndexedSeq(OrganisationLoopDetails(Some(true), Some(selectedCountry), Some(false), None),
          OrganisationLoopDetails(None, Some(selectedCountry), None, None))
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/1")

        val userAnswers = UserAnswers(userAnswersId)
          .set(OrganisationLoopPage, organisationLoopDetails)
          .success
          .value

        incrementIndexOrganisation(userAnswers, request) mustBe 2
      }

      "must return 0 if it's the first iteration in the loop" in {
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/")

        incrementIndexOrganisation(emptyUserAnswers, request) mustBe 0
      }
    }

    "currentIndexInsideLoop" - {
      "must return the current index in the URI" in {
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/3")

        currentIndexInsideLoop(request) mustBe 3
      }
    }
  }



}
