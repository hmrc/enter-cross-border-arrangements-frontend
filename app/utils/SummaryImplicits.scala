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

package utils

import models.ReporterOrganisationOrIndividual.Organisation
import models.UserAnswers
import models.arrangement.ArrangementDetails
import models.disclosure.DisclosureDetails
import models.enterprises.AssociatedEnterprise
import models.individual.Individual
import models.organisation.Organisation
import models.reporter.RoleInArrangement.Intermediary
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import utils.model.rows.{ArrangementModelRows, DisclosureModelRows, IndividualModelRows, OrganisationModelRows, TaxpayerModelRows}
import models.taxpayer.Taxpayer
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import uk.gov.hmrc.viewmodels.SummaryList

import scala.language.implicitConversions

trait SummaryImplicits  extends DisclosureModelRows with ArrangementModelRows with IndividualModelRows with OrganisationModelRows
with TaxpayerModelRows {

  implicit def convertDisclosureDetails(id: Int, dis: DisclosureDetails)(implicit messages: Messages): Seq[Row] =
    List(disclosureNamePage(dis),
      disclosureTypePage(dis)) ++
      buildDisclosureSummaryDetails(dis)

  implicit def convertArrangementDetails(id: Int, arrangementDetails: ArrangementDetails)(implicit messages: Messages): Seq[Row] =
    Seq(whatIsThisArrangementCalledPage(id, arrangementDetails)
      , whatIsTheImplementationDatePage(id, arrangementDetails)
      , buildWhyAreYouReportingThisArrangementNow(id, arrangementDetails)
      , whichExpectedInvolvedCountriesArrangement(id, arrangementDetails)
      , whatIsTheExpectedValueOfThisArrangement(id, arrangementDetails)
      , whichNationalProvisionsIsThisArrangementBasedOn(id, arrangementDetails)
      , giveDetailsOfThisArrangement(id, arrangementDetails)).flatten

  implicit def convertTaxPayer(id: Int, taxPayer: Taxpayer)(implicit messages: Messages): Seq[Row] =
    (taxPayer.individual, taxPayer.organisation) match {
      case (Some(individual), _) =>
        Seq(taxpayerSelectType(id, taxPayer)) ++
        individualRowsFromModel(id, individual) ++
        (whatIsTaxpayersStartDateForImplementingArrangement(id, taxPayer) match {
            case Some(row) => Seq(row)
            case _ => Seq()
          })
      case (_, Some(organisation)) =>
            Seq(taxpayerSelectType(id, taxPayer)) ++
            organisationRowsFromModel(id, organisation) ++
            (whatIsTaxpayersStartDateForImplementingArrangement(id, taxPayer) match {
              case Some(row) => Seq(row)
              case _ => Seq()
            })
    }

  def individualRowsFromModel(id: Int, individual: Individual)(implicit messages: Messages): Seq[Row] =
      Seq(individualName(id, individual) ) ++
      buildIndividualDateOfBirthGroup(id, individual) ++
      buildIndividualPlaceOfBirthGroup(id, individual) ++
      buildIndividualAddressGroup(id, individual) ++
      buildIndividualEmailAddressGroup(id, individual) ++
      buildTaxResidencySummaryForIndividuals(id, individual)

  def organisationRowsFromModel(id: Int, organisation: Organisation)(implicit messages: Messages): Seq[Row] =
      Seq(organisationName(id, organisation)) ++
      buildOrganisationAddressGroup(id, organisation) ++
      buildOrganisationEmailAddressGroup(id, organisation) ++
      buildTaxResidencySummaryForOrganisation(id, organisation)

  def convertEnterprises(id: Int, enterprises: AssociatedEnterprise)(implicit messages: Messages): Seq[Row] = {
    (enterprises.individual, enterprises.organisation) match {
      case (Some(individual), _) => ???
      case (None, Some(organisation)) => ???
    }
//    val (summaryRows, countrySummary) = restoredUserAnswers.get(AssociatedEnterpriseTypePage, id) match {
//
//      case Some(SelectType.Organisation) =>
//        (
//          helper.selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id) ++
//            Seq(helper.associatedEnterpriseType(id),
//              helper.organisationName(id)
//            ).flatten ++
//            helper.buildOrganisationAddressGroup(id) ++
//            helper.buildOrganisationEmailAddressGroup(id),
//          helper.buildTaxResidencySummaryForOrganisation(id)
//        )
//
//      case Some(SelectType.Individual) =>
//        (
//          helper.selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id) ++
//            Seq(
//              helper.associatedEnterpriseType(id),
//              helper.individualName(id)
//            ).flatten ++
//            helper.buildIndividualDateOfBirthGroup(id) ++
//            helper.buildIndividualPlaceOfBirthGroup(id) ++
//            helper.buildIndividualAddressGroup(id) ++
//            helper.buildIndividualEmailAddressGroup(id),
//          helper.buildTaxResidencySummaryForIndividuals(id)
//        )
//
//      case _ => throw new UnsupportedRouteException(id)
//    }
//
//    val isEnterpriseAffected = Seq(helper.isAssociatedEnterpriseAffected(id)).flatten
  }
  def getHallmarkSummaryList(id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] =
    Seq(Some(helper.buildHallmarksRow(id)), helper.mainBenefitTest(id), helper.hallmarkD1Other(id))
      .flatten

  def getArrangementSummaryList(id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] =
    Seq(helper.whatIsThisArrangementCalledPage(id) //ToDo make unlimited string
      , helper.whatIsTheImplementationDatePage(id)
      , helper.buildWhyAreYouReportingThisArrangementNow(id)
      , helper.whichExpectedInvolvedCountriesArrangement(id)
      , helper.whatIsTheExpectedValueOfThisArrangement(id)
      , helper.whichNationalProvisionsIsThisArrangementBasedOn(id) //ToDo make unlimited string
      , helper.giveDetailsOfThisArrangement(id) //ToDo make unlimited string
    ).flatten

  def getOrganisationOrIndividualSummary(ua: UserAnswers, id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] = {
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

  def getIntermediaryOrTaxpayerSummary(ua: UserAnswers, id: Int, helper: CheckYourAnswersHelper): Seq[SummaryList.Row] = {
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
