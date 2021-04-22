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

import models.disclosure.DisclosureDetails
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import model.rows.{AffectedModelRows, ArrangementModelRows, DisclosureModelRows, EnterpriseModelRows, IndividualModelRows, IntermediariesModelRows, OrganisationModelRows, TaxpayerModelRows}
import models.affected.Affected
import models.arrangement.ArrangementDetails
import models.enterprises.AssociatedEnterprise
import models.individual.Individual
import models.intermediaries.Intermediary
import models.organisation.Organisation
import models.taxpayer.Taxpayer

import scala.collection.immutable

abstract class CreateDisplayRows[A] {
  def createDisplayRows(id: Int, dac6Data: A)(implicit messages: Messages): Seq[Row]
}

object CreateDisplayRows extends DisclosureModelRows with ArrangementModelRows with IndividualModelRows with OrganisationModelRows
  with TaxpayerModelRows with EnterpriseModelRows with IntermediariesModelRows with AffectedModelRows {

  implicit val disclosureCreateDisplayRows: CreateDisplayRows[DisclosureDetails] = new CreateDisplayRows[DisclosureDetails] {
    override def createDisplayRows(id: Int, dac6Data: DisclosureDetails)(implicit messages: Messages): immutable.Seq[Row] = List(disclosureNamePage(dac6Data),
      disclosureTypePage(dac6Data)) ++
      buildDisclosureSummaryDetails(dac6Data)
  }

  implicit val arrangementDetailsDisplayRows: CreateDisplayRows[ArrangementDetails] = new CreateDisplayRows[ArrangementDetails] {
    override def createDisplayRows(id: Int, dac6Data: ArrangementDetails)(implicit messages: Messages): Seq[Row] =
      Seq(whatIsThisArrangementCalledPage(id, dac6Data)
        , whatIsTheImplementationDatePage(id, dac6Data)
        , buildWhyAreYouReportingThisArrangementNow(id, dac6Data)
        , whichExpectedInvolvedCountriesArrangement(id, dac6Data)
        , whatIsTheExpectedValueOfThisArrangement(id, dac6Data)
        , whichNationalProvisionsIsThisArrangementBasedOn(id, dac6Data)
        , giveDetailsOfThisArrangement(id, dac6Data)).flatten
  }

  implicit val taxpayerDisplayRows: CreateDisplayRows[Taxpayer] = new CreateDisplayRows[Taxpayer] {
    override def createDisplayRows(id: Int, taxPayer: Taxpayer)(implicit messages: Messages): Seq[Row] =
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
  }

  implicit val enterprisesDisplayRow: CreateDisplayRows[AssociatedEnterprise] = new CreateDisplayRows[AssociatedEnterprise] {
    override def createDisplayRows(id: Int, enterprises: AssociatedEnterprise)(implicit messages: Messages): Seq[Row] = {
      (enterprises.individual, enterprises.organisation) match {
        case (Some(individual), _) =>
          selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id, enterprises) ++
            Seq(associatedEnterpriseType(id, enterprises)) ++
            individualRowsFromModel(id, individual) ++
            Seq(isAssociatedEnterpriseAffected(id, enterprises))

        case (None, Some(organisation)) =>
          selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id, enterprises) ++
            Seq(associatedEnterpriseType(id, enterprises)) ++
            organisationRowsFromModel(id, organisation) ++
            Seq(isAssociatedEnterpriseAffected(id, enterprises))
      }
    }
  }

  implicit val intermediariesDisplayRow: CreateDisplayRows[Intermediary] = new CreateDisplayRows[Intermediary] {
    override def createDisplayRows(id: Int, dac6Data: Intermediary)(implicit messages: Messages): Seq[Row] = {
      val header = (dac6Data.individual, dac6Data.organisation) match {
        case (Some(ind), None) =>
          Seq(intermediariesType(id, dac6Data)) ++
            individualRowsFromModel(id, ind)
        case (None, Some(org)) =>
          Seq(intermediariesType(id, dac6Data)) ++
            organisationRowsFromModel(id, org)
      }

      header ++
      Seq(whatTypeofIntermediary(id, dac6Data),
      isExemptionKnown(id, dac6Data)) ++
      isExemptionCountryKnown(id, dac6Data) ++
      exemptCountries(id, dac6Data).toSeq
    }
  }

  implicit val affectedDisplayRow: CreateDisplayRows[Affected] = new CreateDisplayRows[Affected] {
    override def createDisplayRows(id: Int, dac6Data: Affected)(implicit messages: Messages): Seq[Row] = {
     (dac6Data.individual, dac6Data.organisation) match {
        case (Some(ind), None) =>
          Seq(affectedType(id, dac6Data)) ++
            individualRowsFromModel(id, ind)
        case (None, Some(org)) =>
          Seq(affectedType(id, dac6Data)) ++
            organisationRowsFromModel(id, org)
      }
    }
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

}