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

package controllers.intermediaries

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.intermediaries.WhatTypeofIntermediaryFormProvider
import javax.inject.Inject
import models.intermediaries.WhatTypeofIntermediary
import models.{Mode, UserAnswers}
import navigation.NavigatorForIntermediaries
import pages.individual.IndividualNamePage
import pages.intermediaries.WhatTypeofIntermediaryPage
import pages.organisation.OrganisationNamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class WhatTypeofIntermediaryController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForIntermediaries,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhatTypeofIntermediaryFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatTypeofIntermediaryPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "intermediary" -> getName(request.userAnswers),
        "radios"  -> WhatTypeofIntermediary.radios(preparedForm)
      )

      renderer.render("intermediaries/whatTypeofIntermediary.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[WhatTypeofIntermediary]): Call =
    navigator.routeMap(WhatTypeofIntermediaryPage)(checkRoute)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "intermediary" -> getName(request.userAnswers),
            "radios" -> WhatTypeofIntermediary.radios(formWithErrors)
          )

          renderer.render("intermediaries/whatTypeofIntermediary.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatTypeofIntermediaryPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }

  private def getName(userAnswers: UserAnswers) = {
    (userAnswers.get(IndividualNamePage), userAnswers.get(OrganisationNamePage)) match {
      case (Some(name), _) => name.displayName
      case (_, Some(name)) => name
      case _ => "this intermediary"
    }
  }
}
