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

package controllers.taxpayer

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.taxpayer.UpdateTaxpayerFormProvider
import models.taxpayer.UpdateTaxpayer
import models.{Mode, UserAnswers}
import navigation.NavigatorForTaxpayer
import pages.taxpayer.{TaxpayerLoopPage, UpdateTaxpayerPage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdateTaxpayerController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          sessionRepository: SessionRepository,
                                          navigator: NavigatorForTaxpayer,
                                          identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          formProvider: UpdateTaxpayerFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport  with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val namesOfTaxpayers: IndexedSeq[String] = request.userAnswers.flatMap(_.get(TaxpayerLoopPage)) match {
        case Some(list) =>
          for {
            taxpayer <- list
          } yield {
            taxpayer.nameAsString
          }
        case None => IndexedSeq.empty
      }

      val preparedForm =  request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "taxpayerList" -> namesOfTaxpayers,
        "mode"   -> mode,
        "radios"  -> UpdateTaxpayer.radios(preparedForm)
      )

      renderer.render("taxpayer/updateTaxpayer.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[UpdateTaxpayer]): Call =
    navigator.routeMap(UpdateTaxpayerPage)(checkRoute)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> UpdateTaxpayer.radios(formWithErrors)
          )

          renderer.render("taxpayer/updateTaxpayer.njk", json).map(BadRequest(_))
        },
        value => {
          val initialUserAnswers = UserAnswers(request.internalId)
          val userAnswers = request.userAnswers.fold(initialUserAnswers)(ua => ua)

          for {
                updatedAnswers <- Future.fromTry(userAnswers.set(UpdateTaxpayerPage, value))
                cleanAnswers   <- Future.fromTry(updatedAnswers.remove(WhatIsTaxpayersStartDateForImplementingArrangementPage)) // TODO test when userAnswers are properly supplied
                _              <- sessionRepository.set(cleanAnswers)
                checkRoute     =  toCheckRoute(mode, cleanAnswers)
           } yield Redirect(redirect(checkRoute, Some(value)))

        }
      )
  }
}
