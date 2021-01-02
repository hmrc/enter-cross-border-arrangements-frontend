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

package controllers.organisation

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.organisation.WhatAreTheTaxNumbersForNonUKOrganisationFormProvider
import helpers.JourneyHelpers.{currentIndexInsideLoop, getOrganisationName}
import models.{LoopDetails, Mode, TaxReferenceNumbers, UserAnswers}
import navigation.NavigatorForOrganisation
import pages.organisation.{OrganisationLoopPage, WhatAreTheTaxNumbersForNonUKOrganisationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatAreTheTaxNumbersForNonUKOrganisationController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForOrganisation,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhatAreTheTaxNumbersForNonUKOrganisationFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(OrganisationLoopPage) match {
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
        "organisationName" -> getOrganisationName(request.userAnswers),
        "country" -> getCountry(request.userAnswers),
        "index" -> index
      )

      renderer.render("organisation/whatAreTheTaxNumbersForNonUKOrganisation.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[TaxReferenceNumbers], index: Int = 0): Call =
    navigator.routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(checkRoute)(value)(index)

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "organisationName" -> getOrganisationName(request.userAnswers),
            "country" -> getCountry(request.userAnswers),
            "index" -> index
          )

          renderer.render("organisation/whatAreTheTaxNumbersForNonUKOrganisation.njk", json).map(BadRequest(_))
        },
        value => {
          val organisationLoopList = request.userAnswers.get(OrganisationLoopPage) match {
            case None =>
              val newOrganisationLoop = LoopDetails(None, None, None, taxNumbersNonUK = Some(value), None, None)
              IndexedSeq[LoopDetails](newOrganisationLoop)
            case Some(list) =>
              if (list.lift(index).isDefined) {
                val updatedLoop = list.lift(index).get.copy(taxNumbersNonUK = Some(value))
                list.updated(index, updatedLoop)
              } else {
                list
              }
          }

          for {
            updatedAnswers                <- Future.fromTry(request.userAnswers.set(WhatAreTheTaxNumbersForNonUKOrganisationPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(OrganisationLoopPage, organisationLoopList))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
            checkRoute                    =  toCheckRoute(mode, updatedAnswersWithLoopDetails)
          } yield Redirect(redirect(checkRoute, Some(value), index))
        }
      )
  }

  private def getCountry(userAnswers: UserAnswers)(implicit request: Request[AnyContent]): String = {
    userAnswers.get(OrganisationLoopPage) match {
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
