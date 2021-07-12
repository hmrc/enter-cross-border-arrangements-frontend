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

package controllers

import connectors.HistoryConnector
import controllers.actions._
import helpers.DateHelper.getSummaryTimestamp
import helpers.TaskListHelper.{isInitialDisclosureMarketable, userCanSubmit}
import models.ReporterOrganisationOrIndividual.Organisation
import models.reporter.RoleInArrangement.Intermediary
import models.taxpayer.Taxpayer
import models.{Submission, UserAnswers}
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{Html, SummaryList}
import utils.CreateDisplayRows._
import utils.SummaryListDisplay.DisplayRow
import utils.{CheckYourAnswersHelper, SummaryListDisplay}

import java.time.{ZoneId, ZonedDateTime}
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SummaryController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  historyConnector: HistoryConnector,
  sessionRepository: SessionRepository,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val backtoDisclosuresLink = controllers.routes.DisclosureDetailsController.onPageLoad(id).url

      isInitialDisclosureMarketable(request.userAnswers, id, historyConnector, sessionRepository).flatMap {
        isInitialDisclosureMarketable =>
          if (userCanSubmit(request.userAnswers, id, isInitialDisclosureMarketable)) {
            val helper = new CheckYourAnswersHelper(request.userAnswers, 0)

            val submission = Submission(request.userAnswers, id, request.enrolmentID)

            val enterprisesWithDisplayTaxnames = submission.associatedEnterprises map {
              ent =>
                ent.copy(associatedTaxpayers =
                  ent.associatedTaxpayers.map(
                    txname => getTaxpayerNameFromID(txname, submission.taxpayers).getOrElse(txname)
                  )
                )
            }

            renderer
              .render(
                "summary.njk",
                Json.obj(
                  "disclosureList" -> submission.disclosureDetails.createDisplayRows,
                  "arrangementList" -> submission.arrangementDetails.fold[Seq[DisplayRow]](Seq.empty)(
                    a => a.createDisplayRows
                  ),
                  "reporterDetails"              -> getOrganisationOrIndividualSummary(request.userAnswers, id, helper).map(SummaryListDisplay.rowToDisplayRow(_)),
                  "residentCountryDetails"       -> helper.buildTaxResidencySummaryForReporter(id).map(SummaryListDisplay.rowToDisplayRow(_)),
                  "roleDetails"                  -> getIntermediaryOrTaxpayerSummary(request.userAnswers, id, helper).map(SummaryListDisplay.rowToDisplayRow(_)),
                  "hallmarksList"                -> getHallmarkSummaryList(id, helper).map(SummaryListDisplay.rowToDisplayRow(_)),
                  "taxpayersList"                -> submission.taxpayers.map(_.createDisplayRows),
                  "taxpayerUpdateRow"            -> Seq(helper.updateTaxpayers(id)).flatten.map(SummaryListDisplay.rowToDisplayRow(_)),
                  "enterprisesList"              -> enterprisesWithDisplayTaxnames.map(_.createDisplayRows),
                  "enterprisesUpdateRow"         -> Seq(helper.youHaveNotAddedAnyAssociatedEnterprises(id)).flatten.map(SummaryListDisplay.rowToDisplayRow(_)),
                  "intermediaryList"             -> submission.intermediaries.map(_.createDisplayRows),
                  "intermediaryUpdateRow"        -> Seq(helper.youHaveNotAddedAnyIntermediaries(id)).flatten.map(SummaryListDisplay.rowToDisplayRow(_)),
                  "affectedList"                 -> submission.affectedPersons.map(_.createDisplayRows),
                  "affectedUpdateRow"            -> Seq(helper.youHaveNotAddedAnyAffected(id)).flatten.map(SummaryListDisplay.rowToDisplayRow(_)),
                  "backtoDisclosuresLink"        -> backtoDisclosuresLink,
                  "displayAssociatedEnterprises" -> submission.displayAssociatedEnterprises,
                  "timeStamp"                    -> getTimeStamp
                )
              )
              .map(Ok(_))
          } else {
            throw new RuntimeException("Submission not ready")
          }
      }
  }

  private def getTimeStamp()(implicit messages: Messages) = {
    val today = ZonedDateTime.now(ZoneId.of("Europe/London"))
    Html(s"<p class='govuk-body'>${messages("summary.timestamp", getSummaryTimestamp(today))}</p>")
  }

  def getHallmarkSummaryList(id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] =
    Seq(Some(helper.buildHallmarksRow(id)), helper.hallmarkD1Other(id)).flatten

  private def getTaxpayerNameFromID(search: String, taxpayers: Seq[Taxpayer]): Option[String] =
    taxpayers.find(_.taxpayerId == search).map(_.nameAsString)

  private def getOrganisationOrIndividualSummary(ua: UserAnswers, id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] =
    ua.get(ReporterOrganisationOrIndividualPage, id) match {
      case Some(Organisation) =>
        Seq(
          helper.reporterOrganisationOrIndividual(id) ++
            helper.reporterOrganisationName(id) ++
            helper.buildOrganisationReporterAddressGroup(id) ++
            helper.buildReporterOrganisationEmailGroup(id)
        ).flatten

      case _ =>
        Seq(
          helper.reporterOrganisationOrIndividual(id) ++
            helper.reporterIndividualName(id) ++
            helper.reporterIndividualDateOfBirth(id) ++
            helper.reporterIndividualPlaceOfBirth(id) ++
            helper.buildIndividualReporterAddressGroup(id) ++
            helper.buildReporterIndividualEmailGroup(id)
        ).flatten
    }

  private def getIntermediaryOrTaxpayerSummary(ua: UserAnswers, id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] =
    ua.get(RoleInArrangementPage, id) match {
      case Some(Intermediary) =>
        Seq(
          helper.roleInArrangementPage(id) ++
            helper.intermediaryWhyReportInUKPage(id) ++
            helper.intermediaryRolePage(id) ++
            helper.buildExemptCountriesSummary(id)
        ).flatten

      case _ =>
        Seq(
          helper.roleInArrangementPage(id) ++
            helper.buildTaxpayerReporterReasonGroup(id) ++
            helper.taxpayerImplementationDate(id)
        ).flatten

    }

}
