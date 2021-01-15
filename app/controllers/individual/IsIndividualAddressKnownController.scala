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

package controllers.individual

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.individual.IsIndividualAddressKnownFormProvider
import models.{Mode, UserAnswers}
import navigation.NavigatorForIndividual
import pages.individual.{IndividualNamePage, IsIndividualAddressKnownPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsIndividualAddressKnownController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForIndividual,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: IsIndividualAddressKnownFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IsIndividualAddressKnownPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "radios" -> Radios.yesNo(preparedForm("confirm")),
        "name" -> getIndividualName(request.userAnswers, id)
      )

      renderer.render("individual/isIndividualAddressKnown.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[Boolean]): Call =
    navigator.routeMap(IsIndividualAddressKnownPage)(checkRoute)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "radios" -> Radios.yesNo(formWithErrors("confirm")),
            "name" -> getIndividualName(request.userAnswers, id)
          )

          renderer.render("individual/isIndividualAddressKnown.njk", json).map(BadRequest(_))
        },
        value =>

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsIndividualAddressKnownPage, id, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }

  private def getIndividualName(userAnswers: UserAnswers, id: Int): String = {
    userAnswers.get(IndividualNamePage, id) match {
      case Some(name) => s"${name.firstName + " " + name.secondName + "’s"}"
      case None => "their"
    }
  }
}
