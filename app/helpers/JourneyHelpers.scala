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

import models.{CheckMode, Country, Currency, LoopDetails, Mode, UserAnswers}
import pages.QuestionPage
import pages.individual.{IndividualLoopPage, IndividualNamePage}
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage}
import pages.reporter.organisation.ReporterOrganisationNamePage
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, Json, Reads}
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.viewmodels.Html

object JourneyHelpers {

  def getIndividualName(userAnswers: UserAnswers, id: Int): String = {
    userAnswers.get(IndividualNamePage, id) match {
      case Some(indName) => indName.displayName
      case _ => "the individual"
    }
  }

  def getOrganisationName(userAnswers: UserAnswers, id: Int): String = {
    userAnswers.get(OrganisationNamePage, id) match {
      case Some(organisationName) => organisationName
      case None => "the organisation"
    }
  }

  def getReporterDetailsOrganisationName(userAnswers: UserAnswers, id: Int): String = {
    userAnswers.get(ReporterOrganisationNamePage, id) match {
      case Some(organisationName) => organisationName
      case None => "the organisation"
    }
  }

  def pageHeadingLegendProvider(messageKey: String, name: String)(implicit messages: Messages): Html = {
    Html(s"<legend><h1 class='govuk-heading-xl name-overflow'>${{ messages(messageKey, name) }}</h1></legend>")
  }

  def pageHeadingProvider(messageKey: String, name: String)(implicit messages: Messages): Html = {
    Html(s"<h1 class='govuk-heading-xl name-overflow'>${{ messages(messageKey, name) }}</h1>")
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

  def incrementIndexIndividual(ua: UserAnswers, id:Int, request: Request[AnyContent]): Int = {
    ua.get(IndividualLoopPage, id) match {
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

  def incrementIndexOrganisation(ua: UserAnswers, id:Int, request: Request[AnyContent]): Int = {
    ua.get(OrganisationLoopPage, id) match {
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
    val uriPattern = "([A-Za-z/-]+)([0-9]+)/([0-9]+)".r
    val uriPattern(_, index, _) = request.uri
    index.toInt
  }

  def hasValueChanged[T](value: T, id: Int, page: QuestionPage[T], mode: Mode, ua: UserAnswers)
                        (implicit rds: Reads[T]): Boolean = {
    ua.get(page, id) match {
      case Some(ans) if (ans != value) && (mode == CheckMode) => true
      case _ => false
    }
  }

  def checkLoopDetailsContainsCountry(ua: UserAnswers, id: Int, questionPage: QuestionPage[IndexedSeq[LoopDetails]]): Boolean = {
    ua.get(questionPage, id).fold(throw new Exception("Mandatory userAnswer missing - LoopDetails must contain at least one country"))(
      loopDetails => loopDetails.map(_.whichCountry.isDefined).head
    )
  }

  @deprecated
  def getCountry[A](userAnswers: UserAnswers, id: Int, index: Int): Option[Country] = for {
    loopPage <- userAnswers.get(IndividualLoopPage, id)
    loopDetails <- loopPage.lift(index)
    country <- loopDetails.whichCountry
  } yield country

  def linkToHomePageText(href: String, linkText: String = "confirmation.link.text")(implicit messages: Messages): Html = {
    Html(s"<a class='govuk-link' id='homepage-link' href='$href'>${{ messages(linkText) }}</a>")
  }

  def surveyLinkText(href: String)(implicit messages: Messages): Html = {
    Html(s"<a class='govuk-link' id='feedback-link' href='$href' rel='noreferrer noopener' target='_blank'>" +
      s"${{ messages("confirmation.survey.link")}}</a> ${{ messages("confirmation.survey.text")}}")
  }
}
