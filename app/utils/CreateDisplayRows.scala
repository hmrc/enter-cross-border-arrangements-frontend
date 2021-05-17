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
  def createDisplayRows(dac6Data: A)(implicit messages: Messages): Seq[DisplayRow]
}

object CreateDisplayRows extends DisclosureModelRows with ArrangementModelRows with IndividualModelRows with OrganisationModelRows
  with TaxpayerModelRows with EnterpriseModelRows with IntermediariesModelRows with AffectedModelRows {

  def apply[A](implicit instance: CreateDisplayRows[A]): CreateDisplayRows[A] = instance

  def createDisplayRows[A: CreateDisplayRows](dac6Data: A)(implicit messages: Messages): Seq[DisplayRow] =
    CreateDisplayRows[A].createDisplayRows(dac6Data: A)

  implicit class CreateDisplayRowOps[A: CreateDisplayRows](a: A) {

    def createDisplayRows()(implicit messages: Messages): Seq[DisplayRow] = CreateDisplayRows[A].createDisplayRows(a: A)

  }

  implicit val disclosureCreateDisplayRows: CreateDisplayRows[DisclosureDetails] = new CreateDisplayRows[DisclosureDetails] {
    override def createDisplayRows(disclosure: DisclosureDetails)(implicit messages: Messages): immutable.Seq[DisplayRow] =
      List(disclosureNamePage(disclosure),
      disclosureTypePage(disclosure)) ++
      buildDisclosureSummaryDetails(disclosure)
  }

  implicit val arrangementDetailsDisplayRows: CreateDisplayRows[ArrangementDetails] = new CreateDisplayRows[ArrangementDetails] {
    override def createDisplayRows(arrangement: ArrangementDetails)(implicit messages: Messages): Seq[DisplayRow] =
      Seq(whatIsThisArrangementCalledPage(arrangement)
        , whatIsTheImplementationDatePage(arrangement)
        , buildWhyAreYouReportingThisArrangementNow(arrangement)
        , whichExpectedInvolvedCountriesArrangement(arrangement)
        , whatIsTheExpectedValueOfThisArrangement(arrangement)
        , whichNationalProvisionsIsThisArrangementBasedOn(arrangement)
        , giveDetailsOfThisArrangement(arrangement)).flatten
  }

  implicit val taxpayerDisplayRows: CreateDisplayRows[Taxpayer] = new CreateDisplayRows[Taxpayer] {
    override def createDisplayRows(taxPayer: Taxpayer)(implicit messages: Messages): Seq[DisplayRow] =
      (taxPayer.individual, taxPayer.organisation) match {
        case (Some(individual), _) =>
          Seq(taxpayerSelectType(taxPayer)) ++
            individualRowsFromModel(individual) ++
            (whatIsTaxpayersStartDateForImplementingArrangement(taxPayer) match {
              case Some(row) => Seq(row)
              case _ => Seq()
            })
        case (_, Some(organisation)) =>
          Seq(taxpayerSelectType(taxPayer)) ++
            organisationRowsFromModel(organisation) ++
            (whatIsTaxpayersStartDateForImplementingArrangement(taxPayer) match {
              case Some(row) => Seq(row)
              case _ => Seq()
            })
      }
  }

  implicit val enterprisesDisplayRow: CreateDisplayRows[AssociatedEnterprise] = new CreateDisplayRows[AssociatedEnterprise] {
    override def createDisplayRows(enterprises: AssociatedEnterprise)(implicit messages: Messages): Seq[DisplayRow] = {
      (enterprises.individual, enterprises.organisation) match {
        case (Some(individual), _) =>
          selectAnyTaxpayersThisEnterpriseIsAssociatedWith(enterprises) ++
            Seq(associatedEnterpriseType(enterprises)) ++
            individualRowsFromModel(individual) ++
            Seq(isAssociatedEnterpriseAffected(enterprises))

        case (None, Some(organisation)) =>
          selectAnyTaxpayersThisEnterpriseIsAssociatedWith(enterprises) ++
            Seq(associatedEnterpriseType(enterprises)) ++
            organisationRowsFromModel(organisation) ++
            Seq(isAssociatedEnterpriseAffected(enterprises))
      }
    }
  }

  implicit val intermediariesDisplayRow: CreateDisplayRows[Intermediary] = new CreateDisplayRows[Intermediary] {
    override def createDisplayRows(inter: Intermediary)(implicit messages: Messages): Seq[DisplayRow] = {
      val header = (inter.individual, inter.organisation) match {
        case (Some(ind), None) =>
          Seq(intermediariesType(inter)) ++
            individualRowsFromModel(ind)
        case (None, Some(org)) =>
          Seq(intermediariesType(inter)) ++
            organisationRowsFromModel(org)
      }

      header ++
      Seq(whatTypeofIntermediary(inter),
      isExemptionKnown(inter)) ++
      isExemptionCountryKnown(inter) ++
      exemptCountries(inter).toSeq
    }
  }

  implicit val affectedDisplayRow: CreateDisplayRows[Affected] = new CreateDisplayRows[Affected] {
    override def createDisplayRows(affected: Affected)(implicit messages: Messages): Seq[DisplayRow] = {
     (affected.individual, affected.organisation) match {
        case (Some(ind), None) =>
          Seq(affectedType(affected)) ++
            individualRowsFromModel(ind)
        case (None, Some(org)) =>
          Seq(affectedType(affected)) ++
            organisationRowsFromModel(org)
      }
    }
  }

  def individualRowsFromModel(individual: Individual)(implicit messages: Messages): Seq[DisplayRow] =
    Seq(individualName(individual) ) ++
      buildIndividualDateOfBirthGroup(individual) ++
      buildIndividualPlaceOfBirthGroup(individual) ++
      buildIndividualAddressGroup(individual) ++
      buildIndividualEmailAddressGroup(individual) ++
      removeClassesFromLastElementInSeq(buildTaxResidencySummaryForIndividuals(individual))

  def organisationRowsFromModel(organisation: Organisation)(implicit messages: Messages): Seq[DisplayRow] =
    Seq(organisationName(organisation)) ++
      buildOrganisationAddressGroup(organisation) ++
      buildOrganisationEmailAddressGroup(organisation) ++
      removeClassesFromLastElementInSeq(buildTaxResidencySummaryForOrganisation(organisation))


}