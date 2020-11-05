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

package controllers

import controllers.actions._
import forms.WhatAreTheTaxNumbersForNonUKIndividualFormProvider
import helpers.JourneyHelpers.{currentIndexInsideLoop, getIndividualName}
import javax.inject.Inject
import models.{IndividualLoopDetails, Mode, UserAnswers}
import navigation.Navigator
import pages.{IndividualLoopPage, WhatAreTheTaxNumbersForNonUKIndividualPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class WhatAreTheTaxNumbersForNonUKIndividualController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhatAreTheTaxNumbersForNonUKIndividualFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IndividualLoopPage) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val taxNumbers = value.lift(index).get.taxNumbersNonUK
          if (taxNumbers.isDefined) {
            form.fill(taxNumbers.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "individualName" -> getIndividualName(request.userAnswers),
        "country" -> getCountry(request.userAnswers),
        "index" -> index
      )

      renderer.render("whatAreTheTaxNumbersForNonUKIndividual.njk", json).map(Ok(_))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "individualName" -> getIndividualName(request.userAnswers),
            "country" -> getCountry(request.userAnswers),
            "index" -> index
          )

          renderer.render("whatAreTheTaxNumbersForNonUKIndividual.njk", json).map(BadRequest(_))
        },
        value => {
          val individualLoopList = request.userAnswers.get(IndividualLoopPage) match {
            case None =>
              val newIndividualLoop = IndividualLoopDetails(None, None, None, taxNumbersNonUK = Some(value))
              IndexedSeq[IndividualLoopDetails](newIndividualLoop)
            case Some(list) =>
              if (list.lift(index).isDefined) {
                val updatedLoop = list.lift(index).get.copy(taxNumbersNonUK = Some(value))
                list.updated(index, updatedLoop)
              } else {
                list
              }
          }

          for {
            updatedAnswers                <- Future.fromTry(request.userAnswers.set(WhatAreTheTaxNumbersForNonUKIndividualPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(IndividualLoopPage, individualLoopList))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
          } yield Redirect(navigator.nextPage(WhatAreTheTaxNumbersForNonUKIndividualPage, mode, updatedAnswersWithLoopDetails))
        }
      )
  }

  private def getCountry(userAnswers: UserAnswers)(implicit request: Request[AnyContent]): String = {
    userAnswers.get(IndividualLoopPage) match {
      case Some(loopDetailsSeq) =>
        val whichCountry = loopDetailsSeq(currentIndexInsideLoop(request)).whichCountry
        if (whichCountry.isDefined) {
          whichCountry.get.description
        } else {
          "the country"
        }
      case None => "the country"
    }
  }
}
