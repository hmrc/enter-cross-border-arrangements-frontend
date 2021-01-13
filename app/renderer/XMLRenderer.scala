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

package renderer

import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.requests.DataRequest
import models.taxpayer.TaxResidency
import models.{Address, UserAnswers}
import org.joda.time.DateTime
import pages.arrangement._
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.reporter.RoleInArrangementPage
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer.TaxpayerLoopPage
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}
import play.api.mvc.AnyContent

import javax.inject.Inject
import scala.collection.mutable.ArrayBuffer
import scala.xml.{Elem, Node, NodeSeq}

class XMLRenderer @Inject()() {

  private[renderer] def buildHeader(userAnswers: UserAnswers)
                                   (implicit request: DataRequest[AnyContent]): Elem = {
    val mandatoryMessageRefId = userAnswers.get(DisclosureNamePage) match {
      case Some(disclosureName) => "GB" + request.internalId +disclosureName
      case None => ""
    }

    //XML DateTime format e.g. 2021-01-06T12:25:14
    val mandatoryTimestamp = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")

    <Header>
      <MessageRefId>{mandatoryMessageRefId}</MessageRefId>
      <Timestamp>{mandatoryTimestamp}</Timestamp>
    </Header>
  }

  private[renderer] def buildTINData(taxResidencies: IndexedSeq[TaxResidency]): IndexedSeq[Option[TIN]] = {
    taxResidencies.map {
      loop =>
        if (loop.country.isDefined && loop.taxReferenceNumbers.isDefined) {
          loop.country.get.code match {
            case "GB" =>
              Some(TIN("GB", loop.taxReferenceNumbers.get.firstTaxNumber)) //TODO What about the other optional taxNumbers?
            case code: String =>
              Some(TIN(code, loop.taxReferenceNumbers.get.firstTaxNumber))
          }
        } else {
          None
        }
    }
  }

  private[renderer] def buildResCountryCode(taxResidencies: IndexedSeq[TaxResidency]): NodeSeq = {
    taxResidencies.flatMap {
      taxResidency =>
        if (taxResidency.country.isDefined) {
          <ResCountryCode>{taxResidency.country.get.code}</ResCountryCode>
        } else {
          NodeSeq.Empty
        }
    }
  }

  private[renderer] def buildAddress(address: Option[Address]): NodeSeq = {
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

  private[renderer] def buildIDForOrganisation(organisation: Organisation): Elem = {
    val mandatoryOrganisationName = <OrganisationName>{organisation.organisationName}</OrganisationName>

    val tins: NodeSeq = buildTINData(organisation.taxResidencies).filter(_.isDefined).map(_.get).map {
      tin =>
        <TIN issuedBy={tin.issuedBy}>{tin.tin}</TIN>
    }

    val email = organisation.emailAddress.fold(NodeSeq.Empty)(email => <EmailAddress>{email}</EmailAddress>)

    val mandatoryResCountryCode: NodeSeq = buildResCountryCode(organisation.taxResidencies)

    val nodeBuffer = new xml.NodeBuffer
    val organisationNodes = {
      <Organisation>
        {nodeBuffer ++
        mandatoryOrganisationName ++
        tins ++
        buildAddress(organisation.address) ++
        email ++
        mandatoryResCountryCode}
      </Organisation>
    }

    <ID>{organisationNodes}</ID>
  }

  private[renderer] def buildLiability(userAnswers: UserAnswers): Elem = {
    //TODO This is optional. If value is don't know, don't include this section

    //Note: If Taxpayer is selected, it's mandatory
    val mandatoryRelevantTaxpayerNexus: NodeSeq =
      (userAnswers.get(RoleInArrangementPage), userAnswers.get(TaxpayerWhyReportInUKPage)) match {
        case (Some(RoleInArrangement.Taxpayer), Some(taxpayerWhyReportInUK)) if taxpayerWhyReportInUK != TaxpayerWhyReportInUK.DoNotKnow =>
          <RelevantTaxpayerNexus>{taxpayerWhyReportInUK.toString}</RelevantTaxpayerNexus>
        case (Some(RoleInArrangement.Taxpayer), None) =>
          throw new Exception("Missing report details when building RelevantTaxpayerNexus")
        case _ => NodeSeq.Empty
    }

    val capacity: NodeSeq = userAnswers.get(TaxpayerWhyReportArrangementPage)
      .fold(NodeSeq.Empty)(capacity => <Capacity>{capacity.toString}</Capacity>)

    val nodeBuffer = new xml.NodeBuffer
    val relevantTaxPayersNode = {
      nodeBuffer ++
        mandatoryRelevantTaxpayerNexus ++
        capacity
    }

    <Liability>
      <RelevantTaxpayerDiscloser>{relevantTaxPayersNode}</RelevantTaxpayerDiscloser>
    </Liability>
  }

  private[renderer] def buildDisclosingSection(userAnswers: UserAnswers): Elem =  {

    //TODO Need to use ReporterCheckYourAnswersController - ReporterTaxResidencyLoopPage?
    val discloseDetails = userAnswers.get(TaxpayerLoopPage) match {
      case Some(taxpayers) =>
        val nodeBuffer = new xml.NodeBuffer

        taxpayers.map {
          taxpayer =>
            //TODO Add a check for individual
            // if (taxpayer.organisation.isDefined)...
            val organisationDetails = taxpayer.organisation.get

            nodeBuffer ++
              buildIDForOrganisation(organisationDetails) ++
              buildLiability(userAnswers)
        }
      case None => throw new Exception("Unable to build Disclosing section due to missing data.")
    }

    <Disclosing>{discloseDetails}</Disclosing>
  }

  private[renderer] def buildRelevantTaxPayers(userAnswers: UserAnswers): Elem = {
    val relevantTaxPayersNode: IndexedSeq[ArrayBuffer[Node]] = userAnswers.get(TaxpayerLoopPage) match {
      case Some(taxpayers) =>
        val nodeBuffer = new xml.NodeBuffer

        taxpayers.map {
          taxpayer =>
            //TODO Add a check for individual
            // if (taxpayer.organisation.isDefined)...
            val organisationDetails = taxpayer.organisation.get

            //TODO Does this need to be a Seq[Elem]?
            val mandatoryImplementingDate = {
              Seq(
                taxpayer.implementingDate.map(implementingDate =>
                  <TaxpayerImplementingDate>{implementingDate}</TaxpayerImplementingDate>)
              ).filter(_.isDefined).map(_.get)
            }

            nodeBuffer ++
            <RelevantTaxpayer>
              {buildIDForOrganisation(organisationDetails) ++
              mandatoryImplementingDate}
            </RelevantTaxpayer>
        }
      case None => throw new Exception("Unable to build Relevant taxpayers section due to missing data.")
    }

    <RelevantTaxPayers>{relevantTaxPayersNode}</RelevantTaxPayers>
  }

  private[renderer] def buildDisclosureInformationSummary(userAnswers: UserAnswers): Elem = {
    val mandatoryDisclosureName = userAnswers.get(WhatIsThisArrangementCalledPage) match {
      case Some(name) => Seq(<Disclosure_Name>{name}</Disclosure_Name>)
      case None => NodeSeq.Empty
    }

    val mandatoryDisclosureDescription: NodeSeq = userAnswers.get(GiveDetailsOfThisArrangementPage) match {
      case Some(description) =>
        val splitString = description.grouped(10).toList

        splitString.map(string =>
          <Disclosure_Description>{string}</Disclosure_Description>
        )
      case None => NodeSeq.Empty
    }

    val nodeBuffer = new xml.NodeBuffer

    <Summary>
      {nodeBuffer ++
      mandatoryDisclosureName ++
      mandatoryDisclosureDescription
      }
    </Summary>
  }

  private[renderer] def buildConcernedMS(userAnswers: UserAnswers): Elem = {
    val mandatoryConcernedMS: Set[Elem] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage) match {
      case Some(countries) =>
        countries.map {
          country =>
            <ConcernedMS>{country.toString}</ConcernedMS>
        }
      case None => Set.empty[Elem]
    }

    <ConcernedMSs>{mandatoryConcernedMS}</ConcernedMSs>
  }

  private[renderer] def buildHallmarks(userAnswers: UserAnswers): Elem = {

    val mandatoryHallmarks: Set[Elem] = {
      userAnswers.get(HallmarkDPage) match {
        case Some(hallmarkDSet) =>
          hallmarkDSet.flatMap {
            hallmark =>
              if (hallmark == D1) {
                userAnswers.get(HallmarkD1Page) match {
                  case Some(hallmarkSet) =>
                    hallmarkSet.map(hallmark =>
                      <Hallmark>{hallmark.toString}</Hallmark>
                    )
                  case None => Set.empty[Elem]
                }
              } else {
                Set(<Hallmark>{"DAC6D2"}</Hallmark>)
              }
          }
        case _ => Set.empty[Elem]
      }
    }

    val dac6D1OtherInfo: NodeSeq = userAnswers.get(HallmarkD1Page) match {
      case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
        userAnswers.get(HallmarkD1OtherPage) match {
          case Some(description) =>
            val splitString = description.grouped(10).toList

            splitString.map(string =>
              <DAC6D1OtherInfo>{string}</DAC6D1OtherInfo>
            )
          case None => NodeSeq.Empty
        }
      case _ => NodeSeq.Empty
    }

    <Hallmarks>
      <ListHallmarks>
        {mandatoryHallmarks}
      </ListHallmarks>
      {dac6D1OtherInfo}
    </Hallmarks>
  }

  private[renderer] def buildDisclosureInformation(userAnswers: UserAnswers): Elem = {

    val mandatoryImplementingDate = userAnswers.get(WhatIsTheImplementationDatePage) match {
      case Some(date) => Seq(<ImplementingDate>{date}</ImplementingDate>)
      case None => NodeSeq.Empty
    }

    val reason: NodeSeq = userAnswers.get(DoYouKnowTheReasonToReportArrangementNowPage) match {
      case Some(reasonKnown) if reasonKnown =>
        userAnswers.get(WhyAreYouReportingThisArrangementNowPage)
          .fold(NodeSeq.Empty)(reason => <Reason>{reason.toString.toUpperCase}</Reason>)
      case _ => NodeSeq.Empty
    }

    val mandatoryNationalProvision: NodeSeq = userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage) match {
      case Some(nationalProvisions) =>
        val splitString = nationalProvisions.grouped(10).toList

        splitString.map(string =>
          <NationalProvision>{string}</NationalProvision>
        )
      case None => NodeSeq.Empty
    }

    val mandatoryAmountType: NodeSeq = userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage) match {
      case Some(value) => <Amount currCode={value.currency}>{value.amount}</Amount>
      case None => NodeSeq.Empty
    }

    //Note: MainBenefitTest1 is now always false as it doesn't apply to Hallmark D
    <DisclosureInformation>
      {mandatoryImplementingDate}
      {reason}
      {buildDisclosureInformationSummary(userAnswers)}
      {mandatoryNationalProvision}
      {mandatoryAmountType}
      {buildConcernedMS(userAnswers)}
      <MainBenefitTest1>false</MainBenefitTest1>
      {buildHallmarks(userAnswers)}
    </DisclosureInformation>
  }

  def renderXML(userAnswers: UserAnswers)
               (implicit request: DataRequest[AnyContent]): Elem = {
    val mandatoryDisclosureImportInstruction = userAnswers.get(DisclosureTypePage) match {
      case Some(value) => value.toString.toUpperCase
      case None => ""
    }

    val mandatoryInitialDisclosureMA = userAnswers.get(DisclosureMarketablePage) match {//TODO Is this the right page?
      case Some(value) => value
      case None => false
    }

    val xml =
      <DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
        {buildHeader(userAnswers)}
        <DAC6Disclosures>
          <DisclosureImportInstruction>{mandatoryDisclosureImportInstruction}</DisclosureImportInstruction>
          {buildDisclosingSection(userAnswers)}
          <InitialDisclosureMA>{mandatoryInitialDisclosureMA}</InitialDisclosureMA>
          {buildRelevantTaxPayers(userAnswers)}
          {buildDisclosureInformation(userAnswers)}
        </DAC6Disclosures>
      </DAC6_Arrangement>

    val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

    prettyPrinter.format(xml)
    xml

  }
}


/*===================================Models below=================================*/

//case class XMLHeader(messageRefId: String,
//                     timestamp: String)

case class RelevantTaxpayerDiscloser(relevantTaxpayerNexus: String,
                                     capacity: Option[String])

case class Liability(relevantTaxpayerDiscloser: RelevantTaxpayerDiscloser)

case class TIN(issuedBy: String, tin: String)

case class OrganisationXML(organisationName: String,
                           TIN: Seq[String],
                           address: Address,
                           emailAddress: String,
                           resCountryCode: Seq[String])

case class ID(organisation: OrganisationXML)

case class Disclosing(id: ID,
                      liability: Option[Liability])

case class RelevantTaxpayer(id: ID,
                            taxpayerImplementingDate: String)

case class RelevantTaxPayers(relevantTaxpayer: Seq[RelevantTaxpayer])

case class Summary(disclosureName: String,
                   disclosureDescription: String)

case class ConcernedMSs(concernedMS: String)

case class Hallmarks(listHallmarks: Seq[String],
                     dac6D1OtherInfo: String)

case class DisclosureInformation(implementingDate: String,
                                 reason: String,
                                 summary: Summary,
                                 nationalProvision: String,
                                 amount: Int,
                                 concernedMSs: ConcernedMSs,
                                 mainBenefitTest1: Boolean,
                                 Hallmarks: Hallmarks)

case class DAC6Disclosures(disclosureImportInstruction: String,
                           disclosing: Disclosing,
                           initialDisclosureMA: Boolean,
                           relevantTaxPayers: RelevantTaxPayers,
                           disclosureInformation: DisclosureInformation)

