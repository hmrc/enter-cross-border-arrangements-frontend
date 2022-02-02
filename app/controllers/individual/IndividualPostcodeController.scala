/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.AddressLookupConnector
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.PostcodeFormProvider
import helpers.JourneyHelpers.getIndividualName
import javax.inject.Inject
import models.Mode
import navigation.NavigatorForIndividual
import pages.AddressLookupPage
import pages.individual.IndividualUkPostcodePage
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class IndividualPostcodeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForIndividual,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PostcodeFormProvider,
  addressLookupConnector: AddressLookupConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private val form = formProvider()

  implicit val alternativeText: String = "the individual’s"

  private def manualAddressURL(mode: Mode, id: Int): String = routes.IndividualAddressController.onPageLoad(id, mode).url

  private def actionUrl(mode: Mode, id: Int) = routes.IndividualPostcodeController.onSubmit(id, mode).url

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(IndividualUkPostcodePage, id) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"             -> preparedForm,
        "displayName"      -> getIndividualName(request.userAnswers, id),
        "manualAddressURL" -> manualAddressURL(mode, id),
        "actionUrl"        -> actionUrl(mode, id),
        "individual"       -> true,
        "mode"             -> mode
      )

      renderer.render("postcode.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[String]): Call =
    navigator.routeMap(IndividualUkPostcodePage)(checkRoute)(id)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val formReturned = form.bindFromRequest()

      formReturned.fold(
        formWithErrors => {
          val json = Json.obj(
            "form"             -> formWithErrors,
            "displayName"      -> getIndividualName(request.userAnswers, id),
            "manualAddressURL" -> manualAddressURL(mode, id),
            "actionUrl"        -> actionUrl(mode, id),
            "individual"       -> true,
            "mode"             -> mode
          )

          {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(IndividualUkPostcodePage, id))
              _              <- sessionRepository.set(updatedAnswers)
            } yield renderer.render("postcode.njk", json).map(BadRequest(_))
          }.flatten
        },
        postCode =>
          addressLookupConnector.addressLookupByPostcode(postCode).flatMap {
            case Nil =>
              val formError = formReturned.withError(FormError("postcode", List("postcode.error.notFound")))

              val json = Json.obj(
                "form"             -> formError,
                "displayName"      -> getIndividualName(request.userAnswers, id),
                "manualAddressURL" -> manualAddressURL(mode, id),
                "actionUrl"        -> actionUrl(mode, id),
                "individual"       -> true,
                "mode"             -> mode
              )

              {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualUkPostcodePage, id, postCode))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield renderer.render("postcode.njk", json).map(BadRequest(_))
              }.flatten
            case addresses =>
              for {
                updatedAnswers              <- Future.fromTry(request.userAnswers.set(IndividualUkPostcodePage, id, postCode))
                updatedAnswersWithAddresses <- Future.fromTry(updatedAnswers.set(AddressLookupPage, id, addresses))
                _                           <- sessionRepository.set(updatedAnswersWithAddresses)
                checkRoute = toCheckRoute(mode, updatedAnswers, id)
              } yield Redirect(redirect(id, checkRoute, Some(postCode)))
          }
      )
  }
}
