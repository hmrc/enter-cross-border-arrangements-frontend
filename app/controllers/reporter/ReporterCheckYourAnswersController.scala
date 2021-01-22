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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.DefaultRouting
import handlers.ErrorHandler
import models.ReporterOrganisationOrIndividual.Organisation
import models.hallmarks.JourneyStatus
import models.reporter.RoleInArrangement.Intermediary
import models.{NormalMode, UserAnswers}
import navigation.NavigatorForReporter
import pages.reporter.{ReporterCheckYourAnswersPage, ReporterOrganisationOrIndividualPage, ReporterStatusPage, RoleInArrangementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class ReporterCheckYourAnswersController  @Inject()(
   override val messagesApi: MessagesApi,
   identify: IdentifierAction,
   getData: DataRetrievalAction,
   requireData: DataRequiredAction,
   errorHandler: ErrorHandler,
   navigator: NavigatorForReporter,
   sessionRepository: SessionRepository,
   val controllerComponents: MessagesControllerComponents,
   renderer: Renderer
 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val reporterDetails = getOrganisationOrIndividualSummary(request.userAnswers, id, helper)
      val residentCountryDetails = helper.buildTaxResidencySummaryForReporter(id)
      val roleDetails = getIntermediaryOrTaxpayerSummary(request.userAnswers, id, helper)

      renderer.render(
        "reporter/reporterCheckYourAnswers.njk",
        Json.obj("reporterDetails" -> reporterDetails,
          "residentCountryDetails" -> residentCountryDetails,
          "roleDetails" -> roleDetails,
          "id" -> id
        )
      ).map(Ok(_))
  }

  private def getOrganisationOrIndividualSummary(ua: UserAnswers, id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] = {
    ua.get(ReporterOrganisationOrIndividualPage, id) match {
      case Some(Organisation) =>
        Seq(helper.reporterOrganisationOrIndividual(id) ++
        helper.reporterOrganisationName(id) ++
        helper.buildOrganisationReporterAddressGroup(id) ++
        helper.buildReporterOrganisationEmailGroup(id)).flatten

      case _ =>
        Seq(helper.reporterOrganisationOrIndividual(id) ++
        helper.reporterIndividualName(id) ++
        helper.reporterIndividualDateOfBirth(id) ++
        helper.reporterIndividualPlaceOfBirth(id) ++
        helper.buildIndividualReporterAddressGroup(id) ++
        helper.buildReporterIndividualEmailGroup(id)).flatten
    }
  }

  private def getIntermediaryOrTaxpayerSummary(ua: UserAnswers, id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] = {
    ua.get(RoleInArrangementPage, id) match {
      case Some(Intermediary) =>
        Seq(helper.roleInArrangementPage(id) ++
          helper.intermediaryWhyReportInUKPage(id) ++
          helper.intermediaryRolePage(id) ++
          helper.buildExemptCountriesSummary(id)).flatten

      case _ =>
        Seq(helper.roleInArrangementPage(id) ++
          helper.buildTaxpayerReporterReasonGroup(id) ++
          helper.taxpayerImplementationDate(id)).flatten

    }
  }

    def onContinue(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
      implicit request =>

        for {
          userAnswers: UserAnswers <- Future.fromTry(request.userAnswers.set(ReporterStatusPage, JourneyStatus.Completed))
          _ <- sessionRepository.set(userAnswers)
        } yield Redirect(navigator.routeMap(ReporterCheckYourAnswersPage)(DefaultRouting(NormalMode))(id)(None)(0))
    }
}
