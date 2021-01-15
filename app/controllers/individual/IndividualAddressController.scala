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
import forms.AddressFormProvider
import helpers.JourneyHelpers.{countryJsonList, getIndividualName, pageHeadingProvider}
import javax.inject.Inject
import models.{Address, Mode, UserAnswers}
import navigation.NavigatorForIndividual
import pages.individual.{IndividualAddressPage, IsIndividualAddressUkPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.{ExecutionContext, Future}

class IndividualAddressController @Inject()(override val messagesApi: MessagesApi,
                                            countryListFactory: CountryListFactory,
                                            sessionRepository: SessionRepository,
                                            navigator: NavigatorForIndividual,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            formProvider: AddressFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            renderer: Renderer
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private def actionUrl(id: Int, mode: Mode) = routes.IndividualAddressController.onSubmit(id, mode).url

  implicit val alternativeText: String = "the individual's"

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      val preparedForm = request.userAnswers.get(IndividualAddressPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "countries" -> countryJsonList(preparedForm.data, countries.filter(_ != countryListFactory.uk)),
        "isUkAddress" -> isUkAddress(request.userAnswers, id),
        "actionUrl" -> actionUrl(id, mode),
        "pageTitle" -> "individualAddress.title",
        "pageHeading" -> pageHeadingProvider("individualAddress.heading", getIndividualName(request.userAnswers, id))
      )

      renderer.render("address.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[Address]): Call =
    navigator.routeMap(IndividualAddressPage)(checkRoute)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "countries" -> countryJsonList(formWithErrors.data, countries.filter(_ != countryListFactory.uk)),
            "isUkAddress" -> isUkAddress(request.userAnswers, id),
            "actionUrl" -> actionUrl(id, mode),
            "pageTitle" -> "individualAddress.title",
            "pageHeading" -> pageHeadingProvider("individualAddress.heading", getIndividualName(request.userAnswers, id))
          )

          renderer.render("address.njk", json).map(BadRequest(_))
        },
        value =>

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualAddressPage, id, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }

  private def isUkAddress(userAnswers: UserAnswers, id: Int): Boolean =
    userAnswers.get(IsIndividualAddressUkPage, id) match {
      case Some(true) => true
      case _ => false
    }
}
