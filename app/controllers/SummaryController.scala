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

import controllers.actions._
import models.ReporterOrganisationOrIndividual.Organisation
import models.reporter.RoleInArrangement.Intermediary
import models.{Submission, UserAnswers}
import models.taxpayer.Taxpayer
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.SummaryList
import utils.{CheckYourAnswersHelper, SummaryListGenerator}
import utils.CreateDisplayRows._
import utils.rows.SummaryListDisplay
import utils.rows.SummaryListDisplay.DisplayRow

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SummaryController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    summaryListGenerator: SummaryListGenerator,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport  {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
    val helper = new CheckYourAnswersHelper(request.userAnswers, 0)

      val submission = Submission(request.userAnswers, id, request.enrolmentID)

      val arrangementList: Seq[DisplayRow] = submission.arrangementDetails.fold[Seq[DisplayRow]](Seq.empty)(a => a.rowToDisplayRow(id))

      val hallmarksList = getHallmarkSummaryList(id, helper).map(summaryListGenerator.rowToDisplayRow)

      val reporterDetails = getOrganisationOrIndividualSummary(request.userAnswers, id, helper).map(summaryListGenerator.rowToDisplayRow)
      val residentCountryDetails = helper.buildTaxResidencySummaryForReporter(id).map(summaryListGenerator.rowToDisplayRow)
      val roleDetails = getIntermediaryOrTaxpayerSummary(request.userAnswers, id, helper).map(summaryListGenerator.rowToDisplayRow)

      val taxpayersList = submission.taxpayers.map(_.createDisplayRows(id).map(summaryListGenerator.rowToDisplayRow))

      val taxpayerUpdateRow = Seq(helper.updateTaxpayers(id)).flatten.map(summaryListGenerator.rowToDisplayRow)

      val enterprisesWithDisplayTaxnames = submission.associatedEnterprises map { ent =>
        ent.copy(associatedTaxpayers =
          ent.associatedTaxpayers.map(
            txname => getTaxpayerNameFromID(txname, submission.taxpayers).getOrElse(txname)
          ))
      }

      val enterprisesList =
        enterprisesWithDisplayTaxnames.map(entp =>
          summaryListGenerator.generateSummaryList(id, entp))

      val enterprisesUpdateRow = Seq(helper.youHaveNotAddedAnyAssociatedEnterprises(id)).flatten.map(summaryListGenerator.rowToDisplayRow)

      val intermediaryList = submission.intermediaries map (_.createDisplayRows(id).map(summaryListGenerator.rowToDisplayRow))

      val intermediaryUpdateRow = Seq(helper.youHaveNotAddedAnyIntermediaries(id)).flatten.map(summaryListGenerator.rowToDisplayRow)

      val affectedList =  submission.affectedPersons map (_.createDisplayRows(id).map(summaryListGenerator.rowToDisplayRow))

      val affectedUpdateRow = Seq(helper.youHaveNotAddedAnyAffected(id)).flatten.map(summaryListGenerator.rowToDisplayRow)

      renderer.render("summary.njk",
        Json.obj(
          "disclosureList" -> submission.disclosureDetails.createDisplayRows(id),
                 "arrangementList" -> arrangementList,
                 "reporterDetails" -> reporterDetails,
                 "residentCountryDetails" -> residentCountryDetails,
                 "roleDetails" -> roleDetails,
                 "hallmarksList" -> hallmarksList,
                 "taxpayersList" -> taxpayersList,
                 "taxpayerUpdateRow" -> taxpayerUpdateRow,
                 "enterprisesList"-> enterprisesList,
                 "enterprisesUpdateRow" -> enterprisesUpdateRow,
                 "intermediaryList"-> intermediaryList,
                 "intermediaryUpdateRow" -> intermediaryUpdateRow,
                 "affectedList" -> affectedList,
                 "affectedUpdateRow" -> affectedUpdateRow
          )
      ).map(Ok(_))
  }

  def getHallmarkSummaryList(id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] =
    Seq(Some(helper.buildHallmarksRow(id)), helper.mainBenefitTest(id), helper.hallmarkD1Other(id))
      .flatten

  private def getTaxpayerNameFromID(search: String, taxpayers: Seq[Taxpayer]): Option[String] =
    taxpayers.find(_.taxpayerId == search).map(_.nameAsString)

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

}
