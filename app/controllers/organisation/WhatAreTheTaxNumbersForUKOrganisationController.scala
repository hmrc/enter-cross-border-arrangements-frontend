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

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.organisation.WhatAreTheTaxNumbersForUKOrganisationFormProvider
import helpers.JourneyHelpers.{currentIndexInsideLoop, getOrganisationName}
import models.{LoopDetails, Mode, TaxReferenceNumbers}
import navigation.NavigatorForOrganisation
import pages.organisation.{OrganisationLoopPage, WhatAreTheTaxNumbersForUKOrganisationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatAreTheTaxNumbersForUKOrganisationController @Inject()(
                                                                 override val messagesApi: MessagesApi,
                                                                 sessionRepository: SessionRepository,
                                                                 navigator: NavigatorForOrganisation,
                                                                 appConfig: FrontendAppConfig,
                                                                 identify: IdentifierAction,
                                                                 getData: DataRetrievalAction,
                                                                 requireData: DataRequiredAction,
                                                                 formProvider: WhatAreTheTaxNumbersForUKOrganisationFormProvider,
                                                                 val controllerComponents: MessagesControllerComponents,
                                                                 renderer: Renderer
                                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(OrganisationLoopPage) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val taxNumbersUK = value.lift(index).get.taxNumbersUK
          if (taxNumbersUK.isDefined) {
            form.fill(taxNumbersUK.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "organisationName" -> getOrganisationName(request.userAnswers),
        "lostUTRUrl" -> appConfig.lostUTRUrl,
        "index" -> index
      )

      renderer.render("organisation/whatAreTheTaxNumbersForUKOrganisation.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[TaxReferenceNumbers], index: Int = 0): Call =
    navigator.routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(checkRoute)(value)(index)

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "organisationName" -> getOrganisationName(request.userAnswers),
            "lostUTRUrl" -> appConfig.lostUTRUrl,
            "index" -> index
          )

          renderer.render("organisation/whatAreTheTaxNumbersForUKOrganisation.njk", json).map(BadRequest(_))
        },
        value => {
          val organisationLoopList = request.userAnswers.get(OrganisationLoopPage) match {
            case None =>
              val newOrganisationLoop = LoopDetails(None, None, None, None, None, taxNumbersUK = Some(value))
              IndexedSeq[LoopDetails](newOrganisationLoop)
            case Some(list) =>
              if (list.lift(index).isDefined) {
                val updatedLoop = list.lift(index).get.copy(taxNumbersUK = Some(value))
                list.updated(index, updatedLoop)
              } else {
                list
              }
          }

          for {
            updatedAnswers                <- Future.fromTry(request.userAnswers.set(WhatAreTheTaxNumbersForUKOrganisationPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(OrganisationLoopPage, organisationLoopList))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
            checkRoute                    =  toCheckRoute(mode, updatedAnswersWithLoopDetails)
          } yield Redirect(redirect(checkRoute, Some(value), index))
        }
      )
  }
}