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
import pages.{DoYouKnowAnyTINForUKOrganisationPage, OrganisationLoopPage, OrganisationNamePage}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContent, Request}

object JourneyHelpers {

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

  def nextPageIndexOrganisation(ua: UserAnswers, request: Request[AnyContent]): Int = {
    (ua.get(OrganisationLoopPage), ua.get(DoYouKnowAnyTINForUKOrganisationPage)) match {
      case (Some(countryList), Some(_)) if countryList.size == 1 => 1 //Setup first index for Non-UK TIN pages after UK TIN pages
      case (Some(_), _) =>
        val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
        val uriPattern(_, index) = request.uri

        index.toInt + 1
      case _ => 0
    }
  }

  def currentIndexInsideLoop(request: Request[AnyContent]): Int = {
    val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
    val uriPattern(_, index) = request.uri

    index.toInt
  }

}
