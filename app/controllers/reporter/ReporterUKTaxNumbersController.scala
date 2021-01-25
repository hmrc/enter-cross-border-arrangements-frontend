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

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.reporter.ReporterUKTaxNumbersFormProvider
import helpers.JourneyHelpers._
import javax.inject.Inject
import models.ReporterOrganisationOrIndividual.Individual
import models.{LoopDetails, Mode, TaxReferenceNumbers, UserAnswers}
import navigation.NavigatorForReporter
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, ReporterUKTaxNumbersPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class ReporterUKTaxNumbersController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForReporter,
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReporterUKTaxNumbersFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[TaxReferenceNumbers], index: Int = 0): Call =
    navigator.routeMap(ReporterUKTaxNumbersPage)(checkRoute)(id)(value)(index)

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(ReporterTaxResidencyLoopPage, id) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val taxNumbersUK = value.lift(index).get.taxNumbersUK
          if (taxNumbersUK.isDefined) {
            form.fill(taxNumbersUK.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "id" -> id,
        "mode" -> mode,
        "index" -> index,
        "lostUTRUrl" -> appConfig.lostUTRUrl,
      ) ++ contentProvider(request.userAnswers, id)

      renderer.render("reporter/reporterUKTaxNumbers.njk", json).map(Ok(_))
  }

  def onSubmit(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "id" -> id,
            "mode" -> mode,
            "index" -> index,
            "lostUTRUrl" -> appConfig.lostUTRUrl
          ) ++ contentProvider(request.userAnswers, id)

          renderer.render("reporter/reporterUKTaxNumbers.njk", json).map(BadRequest(_))
        },
        value => {
          val taxResidencyLoopDetails = getReporterTaxResidentLoopDetails(value, request.userAnswers, id, index)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterUKTaxNumbersPage, id, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(ReporterTaxResidencyLoopPage, id, taxResidencyLoopDetails))
            _              <- sessionRepository.set(updatedAnswersWithLoopDetails)
            checkRoute                    =  toCheckRoute(mode, updatedAnswersWithLoopDetails, id)
          } yield Redirect(redirect(id, checkRoute, Some(value), index))
        }
      )
  }

  private def contentProvider(userAnswers: UserAnswers, id: Int) =
    userAnswers.get(ReporterOrganisationOrIndividualPage, id) match {
      case Some(Individual) => //Display Individual Content
        Json.obj("pageTitle" -> "reporterIndividualUKTaxNumbers.title",
          "pageHeading" -> "reporterIndividualUKTaxNumbers.heading")

      case _ => //Display Organisation Content
        Json.obj(
          "pageTitle" -> "reporterOrganisationUKTaxNumbers.title",
          "pageHeading" -> "reporterOrganisationUKTaxNumbers.heading",
          "name" -> getReporterDetailsOrganisationName(userAnswers, id)
        )
    }

  private def getReporterTaxResidentLoopDetails(value: TaxReferenceNumbers, userAnswers: UserAnswers, id: Int, index: Int): IndexedSeq[LoopDetails] =
    userAnswers.get(ReporterTaxResidencyLoopPage, id) match {
      case None =>
        val newResidencyLoop = LoopDetails(None, None, None, None, None, taxNumbersUK = Some(value))
        IndexedSeq[LoopDetails](newResidencyLoop)
      case Some(list) =>
        if (list.lift(index).isDefined) {
          val updatedLoop = list.lift(index).get.copy(taxNumbersUK = Some(value))
          list.updated(index, updatedLoop)
        } else {
          list
        }
    }
}
