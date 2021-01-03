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

package controllers.reporter

import controllers.actions._
import controllers.mixins.{CheckRoute, CountrySupport, RoutingSupport}
import forms.reporter.ReporterTaxResidentCountryFormProvider
import helpers.JourneyHelpers._
import javax.inject.Inject
import models.ReporterOrganisationOrIndividual.Individual
import models.{Country, LoopDetails, Mode, UserAnswers}
import navigation.NavigatorForReporter
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, ReporterTaxResidentCountryPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import scala.concurrent.{ExecutionContext, Future}

class ReporterTaxResidentCountryController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForReporter,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: ReporterTaxResidentCountryFormProvider,
    countryListFactory: CountryListFactory,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController
  with I18nSupport
  with NunjucksSupport
  with RoutingSupport
  with CountrySupport {

  private def redirect(checkRoute: CheckRoute, value: Option[Country], index: Int = 0): Call =
    navigator.routeMap(ReporterTaxResidentCountryPage)(checkRoute)(value)(index)

  private def actionUrl(mode: Mode, index: Int) = routes.ReporterTaxResidentCountryController.onSubmit(mode, index).url

  val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
  private val form = formProvider(countries)

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

    val preparedForm: Form[Country] = request.userAnswers.get(ReporterTaxResidentCountryPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

        val json = Json.obj(
          "form" -> preparedForm,
            "mode" -> mode,
            "actionUrl" -> actionUrl(mode, index),
            "countries" -> countryJsonList(preparedForm.data, countries),
            "index" -> index
          ) ++ contentProvider(request.userAnswers)


      renderer.render("reporter/reporterTaxResidentCountry.njk", json).map(Ok(_))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "actionUrl" -> actionUrl(mode, index),
            "countries" -> countryJsonList(formWithErrors.data, countries),
            "index" -> index
          ) ++ contentProvider(request.userAnswers)

          renderer.render("reporter/reporterTaxResidentCountry.njk", json).map(BadRequest(_))
        },
        value => {
          val taxResidencyLoopDetails = getReporterTaxResidentLoopDetails(value, request.userAnswers, index)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterTaxResidentCountryPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(ReporterTaxResidencyLoopPage, taxResidencyLoopDetails))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute                    =  toCheckRoute(mode, updatedAnswersWithLoopDetails)
          } yield Redirect(redirect(checkRoute, Some(value), index))
        }
      )
  }

  private def contentProvider(userAnswers: UserAnswers) = userAnswers.get(ReporterOrganisationOrIndividualPage) match {

    case Some(Individual) => //Display Individual Content
      Json.obj("pageTitle" -> "reporterIndividualTaxResidentCountry.title",
        "pageHeading" -> "reporterIndividualTaxResidentCountry.heading")

    case _ => //Display Organisation Content
      Json.obj(
        "pageTitle" -> "reporterOrganisationTaxResidentCountry.title",
        "pageHeading" -> "reporterOrganisationTaxResidentCountry.heading",
        "name" -> getReporterDetailsOrganisationName(userAnswers))
  }

  private def getReporterTaxResidentLoopDetails(value: Country, userAnswers: UserAnswers, index: Int): IndexedSeq[LoopDetails] =
    userAnswers.get(ReporterTaxResidencyLoopPage) match {
      case None =>
        val newResidencyLoop = LoopDetails(None, whichCountry = Some(value), None, None, None, None)
        IndexedSeq(newResidencyLoop)
      case Some(list) =>
        if (list.lift(index).isDefined) {
          //Update value
          val updateResidencyLoop = list.lift(index).get.copy(whichCountry = Some(value))
          list.updated(index, updateResidencyLoop)
        } else {
          //Add to loop
          val addResidencyLoop = LoopDetails(None, whichCountry = Some(value), None, None, None, None)
          list :+ addResidencyLoop
        }
    }
}
