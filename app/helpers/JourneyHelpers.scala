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

import models.{Country, UserAnswers}
import pages.{DisplayNamePage, OrganisationNamePage}
import play.api.libs.json.{JsObject, Json}

object JourneyHelpers {

  def getUsersName(userAnswers: UserAnswers)(implicit alternativeText: String): String = {
    userAnswers.get(DisplayNamePage) match {
      case Some(displayName) => displayName
      case _ => alternativeText
    }
  }

  def getOrganisationName(userAnswers: UserAnswers): String = {
    userAnswers.get(OrganisationNamePage) match {
      case Some(organisationName) => organisationName
      case None => "the organisation"
    }
  }

  def countryJsonList(value: Map[String, String], countries: Seq[Country]): Seq[JsObject] = {
    def containsCountry(country: Country): Boolean =
      value.get("country") match {
        case Some(countrycode) => countrycode == country.code
        case _ => false
      }

    val countryJsonList = countries.map {
      country =>
        Json.obj("text" -> country.description, "value" -> country.code, "selected" -> containsCountry(country))
    }

    Json.obj("value" -> "", "text" -> "") +: countryJsonList
  }

}
