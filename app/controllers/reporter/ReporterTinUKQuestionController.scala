/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.reporter.ReporterTinUKQuestionFormProvider
import helpers.JourneyHelpers._
import models.ReporterOrganisationOrIndividual.Individual
import models.{LoopDetails, Mode, UserAnswers}
import navigation.NavigatorForReporter
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, ReporterTinUKQuestionPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{Html, NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReporterTinUKQuestionController @Inject() (
  override val messagesApi: MessagesApi,
  appConfig: FrontendAppConfig,
  sessionRepository: SessionRepository,
  navigator: NavigatorForReporter,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReporterTinUKQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private def redirect(id: Int, checkRoute: CheckRoute, value: Option[Boolean], index: Int = 0): Call =
    navigator.routeMap(ReporterTinUKQuestionPage)(checkRoute)(id)(value)(index)

  def onPageLoad(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val form = formProvider(getReporterTypeKey(request.userAnswers, id))

      val preparedForm = request.userAnswers.get(ReporterTaxResidencyLoopPage, id) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val pageValue = value.lift(index).get.doYouKnowUTR
          if (pageValue.isDefined) {
            form.fill(pageValue.get)
          } else {
            form
          }
        case _ => form
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "id"     -> id,
        "mode"   -> mode,
        "radios" -> Radios.yesNo(preparedForm("value")),
        "index"  -> index
      ) ++ contentProvider(request.userAnswers, id)

      renderer.render("reporter/reporterTinUKQuestion.njk", json).map(Ok(_))
  }

  def onSubmit(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val form = formProvider(getReporterTypeKey(request.userAnswers, id))

      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"   -> formWithErrors,
              "id"     -> id,
              "mode"   -> mode,
              "radios" -> Radios.yesNo(formWithErrors("value")),
              "index"  -> index
            ) ++ contentProvider(request.userAnswers, id)

            renderer.render("reporter/reporterTinUKQuestion.njk", json).map(BadRequest(_))
          },
          value => {
            val taxResidencyLoopDetails = getReporterTaxResidentLoopDetails(value, request.userAnswers, id, index)

            for {
              updatedAnswers                <- Future.fromTry(request.userAnswers.set(ReporterTinUKQuestionPage, id, value))
              updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(ReporterTaxResidencyLoopPage, id, taxResidencyLoopDetails))
              _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
              checkRoute = toCheckRoute(mode, updatedAnswersWithLoopDetails, id)
            } yield Redirect(redirect(id, checkRoute, Some(value), index))
          }
        )
  }

  private def contentProvider(userAnswers: UserAnswers, id: Int)(implicit messages: Messages) =
    userAnswers.get(ReporterOrganisationOrIndividualPage, id) match {
      case Some(Individual) => //Display Individual Content
        Json.obj(
          "pageTitle"   -> "reporterIndividualTinUKQuestion.title",
          "pageHeading" -> "reporterIndividualTinUKQuestion.heading",
          "hintText"    -> hintWithLostUtrLink("reporterIndividualTinUKQuestion.hint")
        )

      case _ => //Display Organisation Content
        Json.obj(
          "pageTitle"   -> "reporterOrganisationTinUKQuestion.title",
          "pageHeading" -> "reporterOrganisationTinUKQuestion.heading",
          "name"        -> getReporterDetailsOrganisationName(userAnswers, id),
          "hintText"    -> hintWithLostUtrLink("reporterOrganisationTinUKQuestion.hint")
        )
    }

  private def hintWithLostUtrLink(hintText: String)(implicit messages: Messages): Html =
    Html(
      s"${messages(hintText)}<span> You can <a class='govuk-link' id='feedback-link' href='${appConfig.lostUTRUrl}' rel='noreferrer noopener' target='_blank'>" +
        s"${messages("findLostUTR.link")}</a>.</span>"
    )

  private def getReporterTaxResidentLoopDetails(value: Boolean, userAnswers: UserAnswers, id: Int, index: Int): IndexedSeq[LoopDetails] =
    userAnswers.get(ReporterTaxResidencyLoopPage, id) match {
      case None =>
        val newResidencyLoop = LoopDetails(None, None, None, None, doYouKnowUTR = Some(value), None)
        IndexedSeq[LoopDetails](newResidencyLoop)
      case Some(list) =>
        if (list.lift(index).isDefined) {
          val updatedLoop = list.lift(index).get.copy(doYouKnowUTR = Some(value))
          list.updated(index, updatedLoop)
        } else {
          list
        }
    }
}
