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

package controllers.disclosure

import connectors.{CrossBorderArrangementsConnector, HistoryConnector}
import controllers.actions._
import controllers.exceptions.DiscloseDetailsAlreadyDeletedException
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.disclosure.ReplaceOrDeleteADisclosureFormProvider
import handlers.ErrorHandler
import models.disclosure.DisclosureType.{Dac6del, Dac6rep}
import models.disclosure.{DisclosureType, IDVerificationStatus, ReplaceOrDeleteADisclosure}
import models.{Country, Mode, UserAnswers}
import navigation.NavigatorForDisclosure
import pages.disclosure.{DisclosureTypePage, InitialDisclosureMAPage, ReplaceOrDeleteADisclosurePage}
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{Html, NunjucksSupport}
import utils.CountryListFactory

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReplaceOrDeleteADisclosureController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  countryListFactory: CountryListFactory,
  crossBorderArrangementsConnector: CrossBorderArrangementsConnector,
  navigator: NavigatorForDisclosure,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  errorHandler: ErrorHandler,
  requireData: DataRequiredAction,
  formProvider: ReplaceOrDeleteADisclosureFormProvider,
  historyConnector: HistoryConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form                    = formProvider(countries)

      try {
        val replaceorDelete = replaceOrDelete(request.userAnswers)

        val preparedForm = request.userAnswers.getBase(ReplaceOrDeleteADisclosurePage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        val json = Json.obj(
          "form"               -> preparedForm,
          "mode"               -> mode,
          "arrangementIDLabel" -> arrangementIDLabel,
          "replaceOrDelete"    -> replaceorDelete
        )

        renderer.render("disclosure/replaceOrDeleteADisclosure.njk", json).map(Ok(_))
      } catch {
        case ex: Exception => errorHandler.onServerError(request, ex)
      }
  }

  def redirect(checkRoute: CheckRoute, disclosureType: Option[DisclosureType]): Call =
    navigator.routeMap(ReplaceOrDeleteADisclosurePage)(checkRoute)(None)(disclosureType)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val countries: Seq[Country]                        = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
      val form                                           = formProvider(countries)
      val formReturned: Form[ReplaceOrDeleteADisclosure] = form.bindFromRequest()

      formReturned.fold(
        formWithErrors => {
          val json = Json.obj(
            "form"               -> formWithErrors,
            "mode"               -> mode,
            "arrangementIDLabel" -> arrangementIDLabel,
            "replaceOrDelete"    -> replaceOrDelete(request.userAnswers)
          )
          renderer.render("disclosure/replaceOrDeleteADisclosure.njk", json).map(BadRequest(_))
        },
        (value: ReplaceOrDeleteADisclosure) =>
          crossBorderArrangementsConnector
            .verifyDisclosureIDs(value.arrangementID.toUpperCase, value.disclosureID.toUpperCase, request.enrolmentID)
            .flatMap {
              verificationStatus =>
                if (!verificationStatus.isValid) {
                  val json = Json.obj(
                    "form"               -> buildFormError(verificationStatus.message, formReturned),
                    "mode"               -> mode,
                    "arrangementIDLabel" -> arrangementIDLabel,
                    "replaceOrDelete"    -> replaceOrDelete(request.userAnswers)
                  )
                  renderer.render("disclosure/replaceOrDeleteADisclosure.njk", json).map(BadRequest(_))
                } else
                  for {
                    disclosureDetail <- historyConnector.getSubmissionDetailForDisclosure(value.disclosureID)
                    updatedAnswers   <- Future.fromTry(request.userAnswers.setBase(ReplaceOrDeleteADisclosurePage, value))
                    updatedAnswers1  <- Future.fromTry(updatedAnswers.setBase(InitialDisclosureMAPage, disclosureDetail.initialDisclosureMA))
                    _                <- sessionRepository.set(updatedAnswers1)
                    checkRoute     = toCheckRoute(mode, updatedAnswers1)
                    disclosureType = request.userAnswers.getBase(DisclosureTypePage)
                  } yield Redirect(redirect(checkRoute, disclosureType))
            }
      ) recoverWith {
        case ex: Exception => errorHandler.onServerError(request, ex)
      }
  }

  private def arrangementIDLabel()(implicit messages: Messages): Html =
    Html(
      s"${messages("replaceOrDeleteADisclosure.arrangementID")}" +
        s"<br><p class='govuk-body'>${messages("replaceOrDeleteADisclosure.arrangementID.p")}</p>"
    )

  private def buildFormError(message: String, formReturned: Form[ReplaceOrDeleteADisclosure]): Form[ReplaceOrDeleteADisclosure] =
    message match {
      case IDVerificationStatus.ArrangementIDNotFound =>
        formReturned
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.arrangementID.notFound")))
      case IDVerificationStatus.DisclosureIDNotFound =>
        formReturned
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
      case IDVerificationStatus.IDsDoNotMatch =>
        formReturned
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.mismatch")))
      case _ =>
        formReturned
          .withError(FormError("arrangementID", List("replaceOrDeleteADisclosure.error.arrangementID.notFound")))
          .withError(FormError("disclosureID", List("replaceOrDeleteADisclosure.error.disclosureID.notFound")))
    }

  private def replaceOrDelete(userAnswers: UserAnswers)(implicit request: Request[AnyContent]): Boolean = userAnswers.getBase(DisclosureTypePage) match {
    case Some(Dac6rep) => true
    case Some(Dac6del) => false
    case _             => throw new DiscloseDetailsAlreadyDeletedException("Disclosure type should only be replace or delete")
  }
}
