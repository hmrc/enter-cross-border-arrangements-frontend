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
import helpers.JourneyHelpers.{countryJsonList, getOrganisationName, getUsersName}
import models.{Country, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{DisplayNamePage, OrganisationNamePage}
import play.api.libs.json.Json

class JourneyHelpersSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "JourneyHelpers" - {

    "getUsersName" - {
      "must return the user's name if it's available" in {
        forAll(validPersonalName) {
          userName =>
            implicit val alternativeText: String = "the individual's"
            val userAnswers = UserAnswers(userAnswersId)
              .set(DisplayNamePage, userName)
              .success
              .value

            getUsersName(userAnswers) mustBe userName
        }
      }

      "must return the alternative text if display name isn't available" in {
        implicit val alternativeText: String = "the individual's"

        getUsersName(emptyUserAnswers) mustBe alternativeText
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
  }

}
