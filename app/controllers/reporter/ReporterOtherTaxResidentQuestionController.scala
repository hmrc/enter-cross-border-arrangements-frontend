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
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.reporter.ReporterOtherTaxResidentQuestionFormProvider
import helpers.JourneyHelpers._
import javax.inject.Inject
import models.ReporterOrganisationOrIndividual.Individual
import models.{CheckMode, LoopDetails, Mode, NormalMode, UserAnswers}
import navigation.NavigatorForReporter
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterOtherTaxResidentQuestionPage, ReporterTaxResidencyLoopPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class ReporterOtherTaxResidentQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForReporter,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReporterOtherTaxResidentQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[Boolean], index: Int = 0): Call =
    navigator.routeMap(ReporterOtherTaxResidentQuestionPage)(checkRoute)(id)(value)(index)

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(ReporterTaxResidencyLoopPage, id) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val taxResidentOtherCountries = value.lift(index).get.taxResidentOtherCountries
          if (taxResidentOtherCountries.isDefined) {
            form.fill(taxResidentOtherCountries.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios" -> Radios.yesNo(preparedForm("value")),
        "index" -> index
      ) ++ contentProvider(request.userAnswers, id)

      renderer.render("reporter/reporterOtherTaxResidentQuestion.njk", json).map(Ok(_))
  }

  def onSubmit(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> Radios.yesNo(formWithErrors("value")),
            "index" -> index
          ) ++ contentProvider(request.userAnswers, id)

          renderer.render("reporter/reporterOtherTaxResidentQuestion.njk", json).map(BadRequest(_))
        },

        value => {

          val taxResidencyLoopDetails = getReporterTaxResidentLoopDetails(value, request.userAnswers, id, mode)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReporterOtherTaxResidentQuestionPage, id, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(ReporterTaxResidencyLoopPage, id, taxResidencyLoopDetails))
            _              <- sessionRepository.set(updatedAnswersWithLoopDetails)
            checkRoute                    =  toCheckRoute(mode, updatedAnswersWithLoopDetails, id)
          } yield Redirect(redirect(id, checkRoute, Some(value), index))
        }
      )
  }

  private def contentProvider(userAnswers: UserAnswers, id: Int) = userAnswers.get(ReporterOrganisationOrIndividualPage, id) match {
    case Some(Individual) => //Display Individual Content
      Json.obj("pageTitle" -> "reporterIndividualOtherTaxResidentQuestion.title",
        "pageHeading" -> "reporterIndividualOtherTaxResidentQuestion.heading")

    case _ => //Display Organisation Content
      Json.obj(
        "pageTitle" -> "reporterOrganisationOtherTaxResidentQuestion.title",
        "pageHeading" -> "reporterOrganisationOtherTaxResidentQuestion.heading",
        "name" -> getReporterDetailsOrganisationName(userAnswers, id)
      )
  }

  private def getReporterTaxResidentLoopDetails(
    value: Boolean, userAnswers: UserAnswers, id: Int, mode: Mode
     )(implicit request: Request[AnyContent]): IndexedSeq[LoopDetails] = {

    (userAnswers.get(ReporterTaxResidencyLoopPage, id), mode) match {
      case (Some(list), NormalMode) => // Add to Loop in NormalMode
        list :+ LoopDetails(taxResidentOtherCountries = Some(value), None, None, None, None, None)
      case (Some(list), CheckMode) =>
        if (value.equals(false)) {
          list.slice(0, currentIndexInsideLoop(request)) // Remove from loop in CheckMode
        } else {
          list :+ LoopDetails(taxResidentOtherCountries = Some(value), None, None, None, None, None) // Add to loop in CheckMode
        }
      case (None, _) => // Start new Loop in Normal Mode
        IndexedSeq[LoopDetails](LoopDetails(taxResidentOtherCountries = Some(value), None, None, None, None, None))
    }
  }
}
