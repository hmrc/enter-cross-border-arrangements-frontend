/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.AddressFormProvider
import helpers.JourneyHelpers.{countryJsonList, getIndividualName}
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.individual.{IndividualAddressPage, IsIndividualAddressUkPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.{ExecutionContext, Future}

class IndividualAddressController @Inject()(override val messagesApi: MessagesApi,
                                            countryListFactory: CountryListFactory,
                                            sessionRepository: SessionRepository,
                                            navigator: Navigator,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            formProvider: AddressFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            renderer: Renderer
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private def actionUrl(mode: Mode) = routes.IndividualAddressController.onSubmit(mode).url

  implicit val alternativeText: String = "the individual's"

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      val preparedForm = request.userAnswers.get(IndividualAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "countries" -> countryJsonList(preparedForm.data, countries.filter(_ != countryListFactory.uk)),
        "isUkAddress" -> isUkAddress(request.userAnswers),
        "actionUrl" -> actionUrl(mode),
        "individual" -> true,
        "displayName" -> getIndividualName(request.userAnswers)
      )

      renderer.render("address.njk", json).map(Ok(_))
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
            "individual" -> true,
            "displayName" -> getIndividualName(request.userAnswers)
          )

          renderer.render("address.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualAddressPage, mode, updatedAnswers))
      )
  }

  private def isUkAddress(userAnswers: UserAnswers): Boolean = userAnswers.get(IsIndividualAddressUkPage) match {
    case Some(true) => true
    case _ => false
  }
}
