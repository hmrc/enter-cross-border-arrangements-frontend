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
import forms.PostcodeFormProvider
import helpers.JourneyHelpers.getIndividualName
import models.Mode
import navigation.NavigatorForIndividual
import pages.individual.IndividualUkPostcodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualPostcodeController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForIndividual,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: PostcodeFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  implicit val alternativeText: String = "the individual's"

  private def manualAddressURL(mode: Mode, id: Int): String = routes.IndividualAddressController.onPageLoad(id, mode).url //Todo update to correct value

  private def actionUrl(mode: Mode, id: Int) = routes.IndividualPostcodeController.onSubmit(id, mode).url

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>


      val preparedForm = request.userAnswers.get(IndividualUkPostcodePage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "displayName" -> getIndividualName(request.userAnswers, id),
        "manualAddressURL" -> manualAddressURL(mode, id),
        "actionUrl" -> actionUrl(mode, id),
        "individual" -> true,
        "mode" -> mode
      )

      renderer.render("postcode.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[String]): Call =
    navigator.routeMap(IndividualUkPostcodePage)(checkRoute)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {
          val json = Json.obj(
            "form" -> formWithErrors,
            "displayName" -> getIndividualName(request.userAnswers, id),
            "manualAddressURL" -> manualAddressURL(mode, id),
            "actionUrl" -> actionUrl(mode, id),
            "individual" -> true,
            "mode" -> mode
          )

          renderer.render("postcode.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualUkPostcodePage, id, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }
}
