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

package controllers.disclosure

import connectors.CrossBorderArrangementsConnector
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.disclosure.DisclosureIdentifyArrangementFormProvider
import models.{Country, Mode}
import navigation.NavigatorForDisclosure
import pages.disclosure.DisclosureIdentifyArrangementPage
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DisclosureIdentifyArrangementController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    countryListFactory: CountryListFactory,
    navigator: NavigatorForDisclosure,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    crossBorderArrangementsConnector: CrossBorderArrangementsConnector,
    formProvider: DisclosureIdentifyArrangementFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      val preparedForm = request.userAnswers.getBase(DisclosureIdentifyArrangementPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode
      )

      renderer.render("disclosure/disclosureIdentifyArrangement.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[String]): Call =
    navigator.routeMap(DisclosureIdentifyArrangementPage)(checkRoute)(None)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>

      val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)
      val formReturned = form.bindFromRequest()

      formReturned.fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode
          )

          renderer.render("disclosure/disclosureIdentifyArrangement.njk", json).map(BadRequest(_))
        },
        value => {
          crossBorderArrangementsConnector.verifyArrangementId(value.toUpperCase).flatMap {
            verificationStatus =>
              if (verificationStatus) {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.setBase(DisclosureIdentifyArrangementPage, value))
                  _ <- sessionRepository.set(updatedAnswers)
                  checkRoute = toCheckRoute(mode, updatedAnswers)
                } yield Redirect(redirect(checkRoute, Some(value)))
              } else {
                val formError = formReturned.withError(FormError("arrangementID", List("disclosureIdentifyArrangement.error.notFound")))

                val json = Json.obj(
                  "form" -> formError,
                  "mode" -> mode
                )

                renderer.render("disclosure/disclosureIdentifyArrangement.njk", json).map(BadRequest(_))
              }
          }
        }
      )
  }
}
