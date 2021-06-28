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

package controllers.arrangement

import controllers.actions._
import forms.arrangement.WhatIsTheExpectedValueOfThisArrangementFormProvider
import helpers.JourneyHelpers.currencyJsonList
import javax.inject.Inject
import models.{Currency, Mode}
import navigation.Navigator
import pages.WhatIsTheExpectedValueOfThisArrangementPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CurrencyListFactory

import scala.concurrent.{ExecutionContext, Future}

class WhatIsTheExpectedValueOfThisArrangementController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    currencyListFactory: CurrencyListFactory,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhatIsTheExpectedValueOfThisArrangementFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  val currencies: Seq[Currency] = currencyListFactory.getCurrencyList.getOrElse(throw new Exception("Could not retrieve currency list"))

  private val form = formProvider(currencies)

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>


      val preparedForm = request.userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "id" -> id,
        "mode"   -> mode,
        "currencies" -> currencyJsonList(
          request.userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage, id).map(_.currency),currencies)
      )

      renderer.render("arrangement/whatIsTheExpectedValueOfThisArrangement.njk", json).map(Ok(_))
  }

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "id" -> id,
            "mode"   -> mode,
            "currencies" -> currencyJsonList(formWithErrors.data.get("currency"),currencies)
          )

          renderer.render("arrangement/whatIsTheExpectedValueOfThisArrangement.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsTheExpectedValueOfThisArrangementPage, id, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatIsTheExpectedValueOfThisArrangementPage, id, mode, updatedAnswers))
      )
  }

}
