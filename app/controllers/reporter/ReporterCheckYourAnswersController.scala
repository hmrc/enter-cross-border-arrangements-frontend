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
import handlers.ErrorHandler
import models.ReporterOrganisationOrIndividual.Organisation
import models.UserAnswers
import models.reporter.RoleInArrangement.Intermediary
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.ExecutionContext

class ReporterCheckYourAnswersController  @Inject()(
   override val messagesApi: MessagesApi,
   identify: IdentifierAction,
   getData: DataRetrievalAction,
   requireData: DataRequiredAction,
   errorHandler: ErrorHandler,
   val controllerComponents: MessagesControllerComponents,
   renderer: Renderer
 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val reporterDetails = getOrganisationOrIndividualSummary(request.userAnswers, helper)
      val residentCountryDetails = helper.buildTaxResidencySummaryForReporter
      val roleDetails = getIntermediaryOrTaxpayerSummary(request.userAnswers, helper)

      renderer.render(
        "reporter/reporterCheckYourAnswers.njk",
        Json.obj("reporterDetails" -> reporterDetails,
          "residentCountryDetails" -> residentCountryDetails,
          "roleDetails" -> roleDetails

        )
      ).map(Ok(_))
  }

  private def getOrganisationOrIndividualSummary(ua: UserAnswers, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] = {
    ua.get(ReporterOrganisationOrIndividualPage) match {
      case Some(Organisation) =>
        Seq(helper.reporterOrganisationOrIndividual ++
        helper.reporterOrganisationName ++
        helper.buildOrganisationReporterAddressGroup ++
        helper.buildReporterOrganisationEmailGroup).flatten

      case _ =>
        Seq(helper.reporterOrganisationOrIndividual ++
        helper.reporterIndividualName ++
        helper.reporterIndividualDateOfBirth ++
        helper.reporterIndividualPlaceOfBirth ++
        helper.buildIndividualReporterAddressGroup ++
        helper.buildReporterIndividualEmailGroup).flatten
    }
  }

  private def getIntermediaryOrTaxpayerSummary(ua: UserAnswers, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] = {
    ua.get(RoleInArrangementPage) match {
      case Some(Intermediary) =>
        Seq(helper.roleInArrangementPage ++
          helper.intermediaryWhyReportInUKPage ++
          helper.intermediaryRolePage ++
          helper.buildExemptCountriesSummary).flatten

      case _ =>
        Seq(helper.roleInArrangementPage ++
          helper.buildTaxpayerReporterReasonGroup ++
          helper.taxpayerImplementationDate).flatten

    }
  }
}
