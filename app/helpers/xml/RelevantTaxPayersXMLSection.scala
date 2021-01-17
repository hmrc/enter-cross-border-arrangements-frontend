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

package helpers.xml

import models.individual.Individual
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.taxpayer.TaxResidency
import models.{Address, ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage

import scala.xml.{Elem, NodeSeq}

object RelevantTaxPayersXMLSection extends XMLBuilder {

  private[xml] def buildTINData(taxResidencies: IndexedSeq[TaxResidency]): NodeSeq = {
    taxResidencies.flatMap {
      loop =>
        if (loop.country.isDefined && loop.taxReferenceNumbers.isDefined) {
          val countryCode = loop.country.get.code

          Seq(
            Some(<TIN issuedBy={countryCode}>{loop.taxReferenceNumbers.get.firstTaxNumber}</TIN>),
            loop.taxReferenceNumbers.get.secondTaxNumber.map(taxNumber => <TIN issuedBy={countryCode}>{taxNumber}</TIN>),
            loop.taxReferenceNumbers.get.thirdTaxNumber.map(taxNumber => <TIN issuedBy={countryCode}>{taxNumber}</TIN>)
          ).filter(_.isDefined).map(_.get)
        } else {
          NodeSeq.Empty
        }
    }
  }

  private[xml] def buildResCountryCode(taxResidencies: IndexedSeq[TaxResidency]): NodeSeq = {
    taxResidencies.flatMap {
      taxResidency =>
        if (taxResidency.country.isDefined) {
          <ResCountryCode>{taxResidency.country.get.code}</ResCountryCode>
        } else {
          throw new Exception("Unable to build Relevant taxpayers section due to missing mandatory resident country/countries.")
        }
    }
  }

  private[xml] def buildAddress(address: Option[Address]): NodeSeq = {
    address match {
      case Some(address) =>
        val addressNode = Seq(
          address.addressLine1.map(addressLine1 => <Street>{addressLine1}</Street>),
          address.addressLine2.map(addressLine2 => <BuildingIdentifier>{addressLine2}</BuildingIdentifier>),
          address.addressLine3.map(addressLine3 => <DistrictName>{addressLine3}</DistrictName>),
          address.postCode.map(postcode => <PostCode>{postcode}</PostCode>),
          Some(<City>{address.city}</City>),
          Some(<Country>{address.country.code}</Country>)
        ).filter(_.isDefined).map(_.get)

        <Address>{addressNode}</Address>
      case None => NodeSeq.Empty
    }
  }

  private[xml] def buildIDForOrganisation(organisation: Organisation): Elem = {
    val mandatoryOrganisationName = <OrganisationName>{organisation.organisationName}</OrganisationName>

    val email = organisation.emailAddress.fold(NodeSeq.Empty)(email => <EmailAddress>{email}</EmailAddress>)

    val mandatoryResCountryCode: NodeSeq = buildResCountryCode(organisation.taxResidencies.filter(_.country.isDefined))

    val nodeBuffer = new xml.NodeBuffer
    val organisationNodes = {
      <Organisation>
        {nodeBuffer ++
        mandatoryOrganisationName ++
        buildTINData(organisation.taxResidencies) ++
        buildAddress(organisation.address) ++
        email ++
        mandatoryResCountryCode}
      </Organisation>
    }

    <ID>{organisationNodes}</ID>
  }

  private[xml] def buildIDForIndividual(individual: Individual): Elem = {
    val mandatoryIndividualName =
      <IndividualName><FirstName>{individual.individualName.firstName}</FirstName><LastName>{individual.individualName.secondName}</LastName></IndividualName>

    val mandatoryDOB = <BirthDate>{individual.birthDate}</BirthDate>
    val mandatoryPOB = <BirthPlace>{individual.birthPlace.fold("Unknown")(pob => pob)}</BirthPlace>
    val optionalEmail = individual.emailAddress.fold(NodeSeq.Empty)(email => <EmailAddress>{email}</EmailAddress>)

    val mandatoryResCountryCode: NodeSeq = buildResCountryCode(individual.taxResidencies.filter(_.country.isDefined))

    val nodeBuffer = new xml.NodeBuffer
    val individualNodes = {
      <Individual>
        {nodeBuffer ++
        mandatoryIndividualName ++
        mandatoryDOB ++
        mandatoryPOB ++
        buildTINData(individual.taxResidencies) ++
        buildAddress(individual.address) ++
        optionalEmail ++
        mandatoryResCountryCode}
      </Individual>
    }

    <ID>{individualNodes}</ID>
  }

  private[xml] def buildReporterAsTaxpayer(userAnswers: UserAnswers): NodeSeq = {
    (userAnswers.get(RoleInArrangementPage), userAnswers.get(ReporterTaxpayersStartDateForImplementingArrangementPage)) match {
      case (Some(RoleInArrangement.Taxpayer), Some(implementingDate)) =>

        userAnswers.get(ReporterOrganisationOrIndividualPage) match {

          case Some(ReporterOrganisationOrIndividual.Organisation) =>
            val organisationDetailsForReporter = Organisation.buildOrganisationDetailsForReporter(userAnswers)

            <RelevantTaxpayer>
              {buildIDForOrganisation(organisationDetailsForReporter)}
              <TaxpayerImplementingDate>{implementingDate}</TaxpayerImplementingDate>
            </RelevantTaxpayer>

          case _ =>
            val individualDetailsForReporter = Individual.buildIndividualDetailsForReporter(userAnswers)

            <RelevantTaxpayer>
              {buildIDForIndividual(individualDetailsForReporter)}
              <TaxpayerImplementingDate>{implementingDate}</TaxpayerImplementingDate>
            </RelevantTaxpayer>
        }
      case _ => NodeSeq.Empty
    }
  }

  private[xml] def getRelevantTaxpayers(userAnswers: UserAnswers) = {
    userAnswers.get(TaxpayerLoopPage) match {
      case Some(taxpayers) =>

        taxpayers.map {
          taxpayer =>
            (taxpayer.individual.isDefined, taxpayer.organisation.isDefined, taxpayer.implementingDate.isDefined) match {
              case (true, false, false) =>
                <RelevantTaxpayer>
                  {buildIDForIndividual(taxpayer.individual.get)}
                </RelevantTaxpayer>

              case (false, true, false) =>
                <RelevantTaxpayer>
                  {buildIDForOrganisation(taxpayer.organisation.get)}
                </RelevantTaxpayer>

              case (true, false, true) =>
                <RelevantTaxpayer>{buildIDForIndividual(taxpayer.individual.get)}
                  <TaxpayerImplementingDate>{taxpayer.implementingDate.get}</TaxpayerImplementingDate>
                </RelevantTaxpayer>

              case _ =>
                <RelevantTaxpayer>
                  {buildIDForOrganisation(taxpayer.organisation.get)}
                  <TaxpayerImplementingDate>{taxpayer.implementingDate.get}</TaxpayerImplementingDate>
                </RelevantTaxpayer>
            }
        }
      case _ => NodeSeq.Empty
    }
  }

  override def toXml(userAnswers: UserAnswers): Elem = {
    <RelevantTaxPayers>
      {buildReporterAsTaxpayer(userAnswers) ++ getRelevantTaxpayers(userAnswers)}
    </RelevantTaxPayers>
  }
}
