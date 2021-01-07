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

package controllers.reporter.organisation

import controllers.actions._
import controllers.mixins.{CheckRoute, CountrySupport, RoutingSupport}
import forms.AddressFormProvider
import helpers.JourneyHelpers.{getReporterDetailsOrganisationName, hasValueChanged, pageHeadingProvider}
import javax.inject.Inject
import models.{Address, Mode, UserAnswers}
import navigation.NavigatorForReporter
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationIsAddressUkPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.{ExecutionContext, Future}

class ReporterOrganisationAddressController @Inject()(override val messagesApi: MessagesApi,
  countryListFactory: CountryListFactory,
  sessionRepository: SessionRepository,
  navigator: NavigatorForReporter,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController
  with I18nSupport
  with NunjucksSupport
  with RoutingSupport
  with CountrySupport {

  private def actionUrl(mode: Mode) = routes.ReporterOrganisationAddressController.onSubmit(mode).url

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      val preparedForm = request.userAnswers.get(ReporterOrganisationAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "countries" -> countryJsonList(preparedForm.data, countries.filter(_ != countryListFactory.uk)),
        "isUkAddress" -> isUkAddress(request.userAnswers),
        "actionUrl" -> actionUrl(mode),
        "pageTitle" -> "reporterOrganisationAddress.title",
        "pageHeading" -> pageHeadingProvider("reporterOrganisationAddress.heading",
          getReporterDetailsOrganisationName(request.userAnswers))
      )

      renderer.render("address.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[Address], isAlt: Boolean): Call =
    if (isAlt) {
      navigator.routeAltMap(ReporterOrganisationAddressPage)(checkRoute)(value)(0)
    }
    else {
      navigator.routeMap(ReporterOrganisationAddressPage)(checkRoute)(value)(0)
    }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "countries" -> countryJsonList(formWithErrors.data, countries.filter(_ != countryListFactory.uk)),
            "isUkAddress" -> isUkAddress(request.userAnswers),
            "actionUrl" -> actionUrl(mode),
            "pageTitle" -> "reporterOrganisationAddress.title",
            "pageHeading" -> pageHeadingProvider("reporterOrganisationAddress.heading", getReporterDetailsOrganisationName(request.userAnswers))
          )

          renderer.render("address.njk", json).map(BadRequest(_))
        },
        value => {

          val redirectUsers = hasValueChanged(value, ReporterOrganisationAddressPage, mode, request.userAnswers)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterOrganisationAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value), redirectUsers))
        }
      )
  }

  private def isUkAddress(userAnswers: UserAnswers): Boolean = userAnswers.get(ReporterOrganisationIsAddressUkPage) match {
    case Some(true) => true
    case _ => false
  }
}
