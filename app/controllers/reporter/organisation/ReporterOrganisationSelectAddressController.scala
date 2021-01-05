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

import connectors.AddressLookupConnector
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.SelectAddressFormProvider
import helpers.JourneyHelpers.{getReporterDetailsOrganisationName, hasValueChanged}
import javax.inject.Inject
import models.requests.DataRequest
import models.{AddressLookup, Mode}
import navigation.NavigatorForReporter
import pages.reporter.ReporterSelectedAddressLookupPage
import pages.reporter.organisation.{ReporterOrganisationPostcodePage, ReporterOrganisationSelectAddressPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class ReporterOrganisationSelectAddressController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForReporter,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: SelectAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  addressLookupConnector: AddressLookupConnector,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private def manualAddressURL(mode: Mode): String = routes.ReporterOrganisationAddressController.onPageLoad(mode).url

  def actionUrl(mode: Mode) = routes.ReporterOrganisationSelectAddressController.onPageLoad(mode).url

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val postCode = getPostCodeFromRequest(request)

      addressLookupConnector.addressLookupByPostcode(postCode) flatMap {
        case Nil => Future.successful(Redirect(manualAddressURL(mode)))
        case addresses =>

          val preparedForm = request.userAnswers.get(ReporterOrganisationSelectAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          val addressItems: Seq[Radios.Radio] = getAddressItemsFromAddressLookup(addresses)
          val radios = Radios(field = preparedForm("value"), items = addressItems)

            val json = Json.obj(
              "form" -> preparedForm,
              "mode" -> mode,
              "radios" -> radios,
              "pageTitle" -> "reporterOrganisationAddress.title",
              "pageHeading" -> "reporterOrganisationAddress.heading",
              "name" -> getReporterDetailsOrganisationName(request.userAnswers),
              "manualAddressURL" -> manualAddressURL(mode),
              "actionUrl" -> actionUrl(mode)

        )

            renderer.render("reporter/reporterSelectAddress.njk", json).map(Ok(_))
      } recover {
        case _: Exception => Redirect(manualAddressURL(mode))
      }
  }

  def redirect(checkRoute: CheckRoute, value: Option[String], isAlt: Boolean): Call =
    if (isAlt) {
      navigator.routeAltMap(ReporterOrganisationSelectAddressPage)(checkRoute)(value)(0)
    }
    else {
      navigator.routeMap(ReporterOrganisationSelectAddressPage)(checkRoute)(value)(0)
    }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val postCode = getPostCodeFromRequest(request)

      addressLookupConnector.addressLookupByPostcode(postCode) flatMap {
        addresses =>
          form.bindFromRequest().fold(
            formWithErrors => {
              val addressItems: Seq[Radios.Radio] = getAddressItemsFromAddressLookup(addresses)
              val radios = Radios(field = formWithErrors("value"), items = addressItems)

              val json = Json.obj(
                "form" -> formWithErrors,
                "mode" -> mode,
                "radios" -> radios,
                "pageTitle" -> "reporterOrganisationAddress.title",
                "pageHeading" -> "reporterOrganisationAddress.heading",
                "name" -> getReporterDetailsOrganisationName(request.userAnswers),
                "manualAddressURL" -> manualAddressURL(mode),
                "actionUrl" -> actionUrl(mode)
              )

              renderer.render("reporter/reporterSelectAddress.njk", json).map(BadRequest(_))
            },
            value => {
              val addressToStore: AddressLookup = addresses.find(formatAddress(_) == value).getOrElse(throw new Exception("Cannot get address"))

              val redirectUsers = hasValueChanged(value, ReporterOrganisationSelectAddressPage, mode, request.userAnswers)

              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterOrganisationSelectAddressPage, value))
                updatedAnswersWithAddress <- Future.fromTry(updatedAnswers.set(ReporterSelectedAddressLookupPage, addressToStore))
                _ <- sessionRepository.set(updatedAnswersWithAddress)
                checkRoute = toCheckRoute(mode, updatedAnswersWithAddress)
              } yield Redirect(redirect(checkRoute, Some(value), redirectUsers))
            }
          )
      }
  }

  private def getPostCodeFromRequest[A](request: DataRequest[A]): String =
    request.userAnswers.get(ReporterOrganisationPostcodePage) match {
      case Some(postCode) => postCode.replaceAll(" ", "").toUpperCase
      case None => ""
    }

  private def getAddressItemsFromAddressLookup(addresses: Seq[AddressLookup]): Seq[Radios.Radio] = addresses.map(address =>
    Radios.Radio(label = msg"${formatAddress(address)}", value = s"${formatAddress(address)}")
  )

  private def formatAddress(address: AddressLookup): String = {
    val lines = Seq(address.addressLine1, address.addressLine2, address.addressLine3, address.addressLine4).flatten.mkString(", ")
    val county = address.county.fold("")(county => s"$county, ")

    s"$lines, ${address.town}, $county${address.postcode}"
  }
}
