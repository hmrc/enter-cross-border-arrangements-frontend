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

import models.affected.Affected
import models.arrangement.ArrangementDetails
import models.disclosure.DisclosureDetails
import models.enterprises.AssociatedEnterprise
import models.individual.Individual
import models.intermediaries.Intermediary
import models.organisation.Organisation
import models.taxpayer.Taxpayer
import play.api.i18n.Messages
import utils.SummaryListDisplay.{DisplayRow, removeClassesFromLastElementInSeq}
import utils.model.rows._

import scala.collection.immutable

trait CreateDisplayRows[A] {
  def createDisplayRows(id: Int, dac6Data: A)(implicit messages: Messages): Seq[DisplayRow]
}

object CreateDisplayRows extends DisclosureModelRows with ArrangementModelRows with IndividualModelRows with OrganisationModelRows
  with TaxpayerModelRows with EnterpriseModelRows with IntermediariesModelRows with AffectedModelRows {

  def apply[A](implicit instance: CreateDisplayRows[A]): CreateDisplayRows[A] = instance

  def createDisplayRows[A: CreateDisplayRows](id: Int, dac6Data: A)(implicit messages: Messages): Seq[DisplayRow] =
    CreateDisplayRows[A].createDisplayRows(id: Int, dac6Data: A)

  implicit class CreateDisplayRowOps[A: CreateDisplayRows](a: A) {

    def createDisplayRows(id: Int)(implicit messages: Messages): Seq[DisplayRow] = CreateDisplayRows[A].createDisplayRows(id: Int, a: A)

  }

  implicit val disclosureCreateDisplayRows: CreateDisplayRows[DisclosureDetails] = new CreateDisplayRows[DisclosureDetails] {
    override def createDisplayRows(id: Int, disclosure: DisclosureDetails)(implicit messages: Messages): immutable.Seq[DisplayRow] =
      List(disclosureNamePage(disclosure),
      disclosureTypePage(disclosure)) ++
      buildDisclosureSummaryDetails(disclosure)
  }

  implicit val arrangementDetailsDisplayRows: CreateDisplayRows[ArrangementDetails] = new CreateDisplayRows[ArrangementDetails] {
    override def createDisplayRows(id: Int, arrangement: ArrangementDetails)(implicit messages: Messages): Seq[DisplayRow] =
      Seq(whatIsThisArrangementCalledPage(id, arrangement)
        , whatIsTheImplementationDatePage(id, arrangement)
        , buildWhyAreYouReportingThisArrangementNow(id, arrangement)
        , whichExpectedInvolvedCountriesArrangement(id, arrangement)
        , whatIsTheExpectedValueOfThisArrangement(id, arrangement)
        , whichNationalProvisionsIsThisArrangementBasedOn(id, arrangement)
        , giveDetailsOfThisArrangement(id, arrangement)).flatten
  }

  implicit val taxpayerDisplayRows: CreateDisplayRows[Taxpayer] = new CreateDisplayRows[Taxpayer] {
    override def createDisplayRows(id: Int, taxPayer: Taxpayer)(implicit messages: Messages): Seq[DisplayRow] =
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
    override def createDisplayRows(id: Int, enterprises: AssociatedEnterprise)(implicit messages: Messages): Seq[DisplayRow] = {
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
    override def createDisplayRows(id: Int, inter: Intermediary)(implicit messages: Messages): Seq[DisplayRow] = {
      val header = (inter.individual, inter.organisation) match {
        case (Some(ind), None) =>
          Seq(intermediariesType(id, inter)) ++
            individualRowsFromModel(id, ind)
        case (None, Some(org)) =>
          Seq(intermediariesType(id, inter)) ++
            organisationRowsFromModel(id, org)
      }

      header ++
      Seq(whatTypeofIntermediary(id, inter),
      isExemptionKnown(id, inter)) ++
      isExemptionCountryKnown(id, inter) ++
      exemptCountries(id, inter).toSeq
    }
  }

  implicit val affectedDisplayRow: CreateDisplayRows[Affected] = new CreateDisplayRows[Affected] {
    override def createDisplayRows(id: Int, affected: Affected)(implicit messages: Messages): Seq[DisplayRow] = {
     (affected.individual, affected.organisation) match {
        case (Some(ind), None) =>
          Seq(affectedType(id, affected)) ++
            individualRowsFromModel(id, ind)
        case (None, Some(org)) =>
          Seq(affectedType(id, affected)) ++
            organisationRowsFromModel(id, org)
      }
    }
  }

  def individualRowsFromModel(id: Int, individual: Individual)(implicit messages: Messages): Seq[DisplayRow] =
    Seq(individualName(id, individual) ) ++
      buildIndividualDateOfBirthGroup(id, individual) ++
      buildIndividualPlaceOfBirthGroup(id, individual) ++
      buildIndividualAddressGroup(id, individual) ++
      buildIndividualEmailAddressGroup(id, individual) ++
      removeClassesFromLastElementInSeq(buildTaxResidencySummaryForIndividuals(id, individual))

  def organisationRowsFromModel(id: Int, organisation: Organisation)(implicit messages: Messages): Seq[DisplayRow] =
    Seq(organisationName(id, organisation)) ++
      buildOrganisationAddressGroup(id, organisation) ++
      buildOrganisationEmailAddressGroup(id, organisation) ++
      removeClassesFromLastElementInSeq(buildTaxResidencySummaryForOrganisation(id, organisation))


}