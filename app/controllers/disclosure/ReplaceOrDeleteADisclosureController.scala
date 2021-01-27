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
import forms.disclosure.ReplaceOrDeleteADisclosureFormProvider
import models.disclosure.{IDVerificationStatus, ReplaceOrDeleteADisclosure}
import models.{Country, Mode}
import navigation.NavigatorForDisclosure
import pages.disclosure.ReplaceOrDeleteADisclosurePage
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{Html, NunjucksSupport}
import utils.CountryListFactory

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReplaceOrDeleteADisclosureController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    countryListFactory: CountryListFactory,
    crossBorderArrangementsConnector: CrossBorderArrangementsConnector,
    navigator: NavigatorForDisclosure,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: ReplaceOrDeleteADisclosureFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)

      val preparedForm = request.userAnswers.getBase(ReplaceOrDeleteADisclosurePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "arrangementIDLabel" -> arrangementIDLabel
      )

      renderer.render("disclosure/replaceOrDeleteADisclosure.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[ReplaceOrDeleteADisclosure]): Call =
    navigator.routeMap(ReplaceOrDeleteADisclosurePage)(checkRoute)(None)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form = formProvider(countries)
      val formReturned: Form[ReplaceOrDeleteADisclosure] =  form.bindFromRequest()

      formReturned.fold(
        formWithErrors => {
          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "arrangementIDLabel" -> arrangementIDLabel
          )
          renderer.render("disclosure/replaceOrDeleteADisclosure.njk", json).map(BadRequest(_))
        },
        value => {
          crossBorderArrangementsConnector.verifyDisclosureId(value.arrangementID, value.disclosureID, request.enrolmentID) flatMap {
            verificationStatus =>
              if (!verificationStatus.isValid) {
                val json = Json.obj(
                  "form" -> buildFormError(verificationStatus.message, formReturned),
                  "mode" -> mode,
                  "arrangementIDLabel" -> arrangementIDLabel
                )
                renderer.render("disclosure/replaceOrDeleteADisclosure.njk", json).map(BadRequest(_))
              } else {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.setBase(ReplaceOrDeleteADisclosurePage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                  checkRoute = toCheckRoute(mode, updatedAnswers)
                } yield Redirect(redirect(checkRoute, Some(value)))
              }
          }
        }
      )
  }

  private def arrangementIDLabel()(implicit messages: Messages): Html = {
    Html(s"${{ messages("replaceOrDeleteADisclosure.arrangementID") }}" +
      s"<br><p class='govuk-body'>${{ messages("replaceOrDeleteADisclosure.arrangementID.p") }}</p>")
  }

  private def buildFormError(message: String, formReturned: Form[ReplaceOrDeleteADisclosure]): Form[ReplaceOrDeleteADisclosure] = {
    message match {
      case IDVerificationStatus.ArrangementIDNotFound =>
        formReturned
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.arrangementID.notFound")))
      case IDVerificationStatus.DisclosureIDNotFound =>
        formReturned
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
      case IDVerificationStatus.IDsDoNotMatch =>
        formReturned
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.invalid")))
      case IDVerificationStatus.IDsNotFound =>
        formReturned
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.arrangementID.notFound")))
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
    }
  }
}