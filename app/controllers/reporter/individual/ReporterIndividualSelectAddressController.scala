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

package controllers.reporter.individual

import connectors.AddressLookupConnector
import controllers.actions._
import forms.SelectAddressFormProvider
import helpers.JourneyHelpers.hasValueChanged
import models.requests.DataRequest
import models.{AddressLookup, Mode}
import navigation.NavigatorForReporter
import pages.SelectedAddressLookupPage
import pages.reporter.individual.{ReporterIndividualPostcodePage, ReporterIndividualSelectAddressPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReporterIndividualSelectAddressController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: SelectAddressFormProvider,
    val controllerComponents: MessagesControllerComponents,
    addressLookupConnector: AddressLookupConnector,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val postCode = getPostCodeFromRequest(request)

      addressLookupConnector.addressLookupByPostcode(postCode) flatMap {
        case Nil => Future.successful(Redirect(manualAddressURL(mode)))
        case addresses =>

          val preparedForm = request.userAnswers.get(ReporterIndividualSelectAddressPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          val addressItems: Seq[Radios.Radio] = getAddressItemsFromAddressLookup(addresses)
          val radios = Radios(field = preparedForm("value"), items = addressItems)

          val json = Json.obj(
            "form" -> preparedForm,
            "mode" -> mode,
            "manualAddressURL" -> manualAddressURL(mode),
            "radios" -> radios
          )

          renderer.render("reporter/individual/reporterIndividualSelectAddress.njk", json).map(Ok(_))
      } recover {
        case _: Exception => Redirect(manualAddressURL(mode))
      }
  }

  def redirect(mode: Mode, value: Option[String], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForReporter.nextPage(ReporterIndividualSelectAddressPage, mode, value, index, alternative)

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
                "manualAddressURL" -> manualAddressURL(mode),
                "radios" -> radios
              )

              renderer.render("reporter/individual/reporterIndividualSelectAddress.njk", json).map(BadRequest(_))
            },
            value => {
              val addressToStore: AddressLookup = addresses.find(formatAddress(_) == value).getOrElse(throw new Exception("Cannot get address"))

              val redirectUsers = hasValueChanged(value, ReporterIndividualSelectAddressPage, mode, request.userAnswers)

              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterIndividualSelectAddressPage, value))
                updatedAnswersWithAddress <- Future.fromTry(updatedAnswers.set(SelectedAddressLookupPage, addressToStore))
                _ <- sessionRepository.set(updatedAnswersWithAddress)
              } yield {
                if (redirectUsers) {
                  Redirect(controllers.routes.IndexController.onPageLoad()) //TODO Redirect to correct CYA page when ready
                } else {
                  Redirect(redirect(mode, Some(value)))
                }
              }
            }
          )
      }
  }

  private def manualAddressURL(mode: Mode): String = routes.ReporterIndividualAddressController.onPageLoad(mode).url //TODO Change to UK page when ready

  private def getPostCodeFromRequest[A](request: DataRequest[A]): String =
    request.userAnswers.get(ReporterIndividualPostcodePage) match {
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
