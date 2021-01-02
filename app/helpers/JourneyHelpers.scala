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

import models.{CheckMode, Country, Currency, Mode, UserAnswers}
import pages._
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage}
import pages.QuestionPage
import pages.individual.{IndividualLoopPage, IndividualNamePage}
import play.api.libs.json.{JsObject, Json, Reads}
import play.api.mvc.{AnyContent, Request}

object JourneyHelpers {

  def getIndividualName(userAnswers: UserAnswers): String = {
    userAnswers.get(IndividualNamePage) match {
      case Some(indName) => indName.displayName
      case _ => "the individual"
    }
  }

  def getOrganisationName(userAnswers: UserAnswers): String = {
    userAnswers.get(OrganisationNamePage) match {
      case Some(organisationName) => organisationName
      case None => "the organisation"
    }
  }

  def currencyJsonList(value: Option[String], currencies: Seq[Currency]): Seq[JsObject] =
    Json.obj("value" -> "", "text" -> "") +: currencies.map {
      currency => Json.obj(
        "text" -> currency.description,
        "value" -> currency.code,
        "selected" -> value.contains(currency.code)
        )
    }

  @deprecated
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

  def incrementIndexIndividual(ua: UserAnswers, request: Request[AnyContent]): Int = {
    ua.get(IndividualLoopPage) match {
      case Some(_) =>
        try {
          val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
          val uriPattern(_, index) = request.uri

          index.toInt + 1
        } catch {
          case _: Exception => 1 //First index for Non-UK TIN pages after visiting UK TIN pages. UK tin pages will not match uri pattern
        }
      case _ => 0
    }
  }

  def incrementIndexOrganisation(ua: UserAnswers, request: Request[AnyContent]): Int = {
    ua.get(OrganisationLoopPage) match {
      case Some(_) =>
        try {
          val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
          val uriPattern(_, index) = request.uri

          index.toInt + 1
        } catch {
          case _: Exception => 1 //First index for Non-UK TIN pages after visiting UK TIN pages. UK tin pages will not match uri pattern
        }
      case _ => 0
    }
  }

  def currentIndexInsideLoop(request: Request[AnyContent]): Int = {
    val uriPattern = "([A-Za-z/-]+)([0-9]+)".r
    val uriPattern(_, index) = request.uri
    index.toInt
  }

  def hasValueChanged[T](value: T, page: QuestionPage[T], mode: Mode, ua: UserAnswers)
                        (implicit rds: Reads[T]): Boolean = {
    ua.get(page) match {
      case Some(ans) if (ans != value) && (mode == CheckMode) => true
      case _ => false
    }
  }

  @deprecated
  def getCountry[A](userAnswers: UserAnswers, index: Int): Option[Country] = for {
    loopPage <- userAnswers.get(IndividualLoopPage)
    loopDetails <- loopPage.lift(index)
    country <- loopDetails.whichCountry
  } yield country
}
