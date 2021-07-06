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

package helpers

import base.{ControllerMockFixtures, SpecBase}
import generators.Generators
import helpers.JourneyHelpers._
import models.{CheckMode, Country, Currency, LoopDetails, Name, UnsubmittedDisclosure, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individual.IndividualNamePage
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.viewmodels.Html

class JourneyHelpersSpec extends ControllerMockFixtures with SpecBase with ScalaCheckPropertyChecks with Generators {

  "JourneyHelpers" - {

    "getIndividualName" - {
      "must return the individuals name if it's available" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(IndividualNamePage, 0, Name("firstName", "lastName"))
          .success
          .value

        getIndividualName(userAnswers, 0) mustBe "firstName lastName"
      }
    }

    "getOrganisationName" - {
      "must return the organisation name if it's available" in {
        forAll(validOrganisationName) {
          orgName =>
            val userAnswers = UserAnswers(userAnswersId)
              .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
              .success
              .value
              .set(OrganisationNamePage, 0, orgName)
              .success
              .value

            getOrganisationName(userAnswers, 0) mustBe orgName
        }
      }

      "must return 'the organisation' if organisation name isn't available" in {
        getOrganisationName(emptyUserAnswers, 0) mustBe "the organisation"
      }
    }

    "currencyJsonList" - {
      "must return currency list with selected currency" in {
        val value = Some("ALL")
        val currenciesSeq = Seq(
          Currency("AFN", "AFGHANI", "AFGHANISTAN", "Afghanistan Afghani (AFN)"),
          Currency("ALL", "LEK", "ALBANIA", "Albanian Lek (ALL)"),
          Currency("AMD", "DRAM", "ARMENIA", "Armenian Dram (AMD)")
        )
        val expectedJsonList = Seq(
          Json.obj("text" -> "", "value"                          -> ""),
          Json.obj("text" -> "Afghanistan Afghani (AFN)", "value" -> "AFN", "selected" -> false),
          Json.obj("text" -> "Albanian Lek (ALL)", "value"        -> "ALL", "selected" -> true),
          Json.obj("text" -> "Armenian Dram (AMD)", "value"       -> "AMD", "selected" -> false)
        )
        currencyJsonList(value, currenciesSeq) mustBe expectedJsonList
      }
    }

    "countryJsonList" - {
      "must return the country list with the selected country" in {
        val value                      = Map("country" -> "GB")
        val countriesSeq: Seq[Country] = Seq(Country("valid", "GB", "United Kingdom"), Country("valid", "FR", "France"))
        val expectedJsonList = Seq(
          Json.obj("text" -> "", "value"               -> ""),
          Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> true),
          Json.obj("text" -> "France", "value"         -> "FR", "selected" -> false)
        )

        countryJsonList(value, countriesSeq) mustBe expectedJsonList
      }
    }

    "incrementIndexOrganisation" - {
      val selectedCountry: Country = Country("valid", "GB", "United Kingdom")

      "must return index as 1 if user previously visited UK tin pages and they know TIN for another country (matching URI pattern failed)" in {
        val organisationLoopDetails                      = IndexedSeq(LoopDetails(Some(true), Some(selectedCountry), Some(false), None, None, None))
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/")

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(OrganisationLoopPage, 0, organisationLoopDetails)
          .success
          .value

        incrementIndexOrganisation(userAnswers, 0, request) mustBe 1
      }

      "must add 1 to index from uri if users go through the loop more than once" in {
        val organisationLoopDetails = IndexedSeq(LoopDetails(Some(true), Some(selectedCountry), Some(false), None, None, None),
                                                 LoopDetails(None, Some(selectedCountry), None, None, None, None)
        )
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/1")

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(OrganisationLoopPage, 0, organisationLoopDetails)
          .success
          .value

        incrementIndexOrganisation(userAnswers, 0, request) mustBe 2
      }

      "must return 0 if it's the first iteration in the loop" in {
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/")

        incrementIndexOrganisation(emptyUserAnswers, 0, request) mustBe 0
      }
    }

    "currentIndexInsideLoop" - {
      "must return the current index in the URI" in {
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/3/0")

        currentIndexInsideLoop(request) mustBe 3
      }
    }

    "calling hasValueChanged" - {
      "must return true if mode is CheckMode and user answer has changed" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(OrganisationNamePage, 0, "Organisation")
          .success
          .value

        val result = hasValueChanged("new Organisation", 0, OrganisationNamePage, CheckMode, userAnswers)

        result mustBe true
      }

      "must return false if user answer has not changed (NormalMode or CheckMode)" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(OrganisationNamePage, 0, "Organisation")
          .success
          .value

        val result = hasValueChanged("Organisation", 0, OrganisationNamePage, CheckMode, userAnswers)

        result mustBe false
      }
    }

    "calling checkLoopDetailsForInitialCountry" - {
      "must return true if loopDetails contains an initial country" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("valid", "GB", "United Kingdom")), None, None, None, None)))
          .success
          .value

        val result = checkLoopDetailsContainsCountry(userAnswers, 0, OrganisationLoopPage)

        result mustBe true
      }

      "must return false if loopDetails does notcontains an initial country" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, None, None, None, None, None)))
          .success
          .value

        val result = checkLoopDetailsContainsCountry(userAnswers, 0, OrganisationLoopPage)

        result mustBe false
      }

      "must throw an exception if loopDetails contains None" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value

        val ex = intercept[Exception] {
          checkLoopDetailsContainsCountry(userAnswers, 0, OrganisationLoopPage)
        }

        ex.getMessage mustBe "Mandatory userAnswer missing - LoopDetails must contain at least one country"
      }
    }

    "linkToHomePageText" - {

      "must return the correct go to home page content" in {
        val mockURL = "home.gov.uk"

        linkToHomePageText(mockURL) mustBe Html(
          s"<a class='govuk-link' id='homepage-link' href='$mockURL'>" +
            s"Disclose a cross-border arrangement</a>"
        )

      }
    }

    "surveyLinkText" - {

      "must return the correct beta feedback content" in {
        val mockURL = "home.gov.uk"

        surveyLinkText(mockURL) mustBe Html(
          s"<a class='govuk-link' id='feedback-link' href='$mockURL' rel='noreferrer noopener' target='_blank'>" +
            s"What did you think of this service?</a> (opens in a new tab)"
        )

      }
    }
  }
}
