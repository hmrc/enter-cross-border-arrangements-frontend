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

import models.reporter.RoleInArrangement
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{Address, UserAnswers}
import org.joda.time.DateTime
import pages.GiveDetailsOfThisArrangementPage
import pages.arrangement.{DoYouKnowTheReasonToReportArrangementNowPage, WhatIsTheImplementationDatePage, WhatIsThisArrangementCalledPage, WhyAreYouReportingThisArrangementNowPage}
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{RoleInArrangementPage, WhatIsReporterTaxpayersStartDateForImplementingArrangementPage}
import pages.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementPage

import javax.inject.Inject
import scala.xml.{Elem, NodeSeq}

class XMLRenderer @Inject()() {

  private def buildHeader(userAnswers: UserAnswers): Elem = {
    val mandatoryMessageRefId = userAnswers.get(DisclosureNamePage) match {
      case Some(disclosureName) => "GB" + disclosureName
      case None => ""
    }

    //XML DateTime format e.g. 2021-01-06T12:25:14
    val mandatoryTimestamp = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")

    <Header>
      <MessageRefId>{mandatoryMessageRefId}</MessageRefId>
      <Timestamp>{mandatoryTimestamp}</Timestamp>
    </Header>
  }

  private def buildTINData(userAnswers: UserAnswers): IndexedSeq[Option[TIN]] = {
    //TODO Optional and repeatable
    userAnswers.get(OrganisationLoopPage) match {
      case Some(value) =>
        value.map {
          loop =>
            if (loop.doYouKnowTIN.isDefined && loop.doYouKnowTIN.get) {
              loop.whichCountry.get.code match {
                case "GB" =>
                  Some(TIN("GB", loop.taxNumbersUK.get.firstTaxNumber)) //TODO What about the other optional taxNumbers?
                case code: String =>
                  Some(TIN(code, loop.taxNumbersNonUK.get.firstTaxNumber))
              }
            } else {
              None
            }
        }
      case None => IndexedSeq[Option[TIN]]()
    }
  }

  private def buildResCountryCode(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(OrganisationLoopPage) match {
      case Some(value) =>
        value.map {
          loop =>
            <ResCountryCode>{loop.whichCountry.get.code}</ResCountryCode>
        }
      case None => NodeSeq.Empty //TODO Mandatory and Repeatable //IndexedSeq[Elem]()
    }
  }

  private def buildAddress(userAnswers: UserAnswers): NodeSeq = {
    userAnswers.get(OrganisationAddressPage) match {
      case Some(address) =>
        Seq(
          address.addressLine1.map(addressLine1 => <Street>{addressLine1}</Street>),
          address.addressLine2.map(addressLine2 => <BuildingIdentifier>{addressLine2}</BuildingIdentifier>),
          address.addressLine3.map(addressLine3 => <DistrictName>{addressLine3}</DistrictName>),
          address.postCode.map(postcode => <PostCode>{postcode}</PostCode>),
          Some(<City>{address.city}</City>),
          Some(<Country>{address.country.code}</Country>)
        ).filter(_.isDefined).map(_.get)
      case None => Seq()
    }
  }

  def buildIDForOrganisation(userAnswers: UserAnswers) = {
    //Mandatory
    val mandatoryOrganisationName = userAnswers.get(OrganisationNamePage) match {
      case Some(name) => name
      case None => ""
    }

    val tins: NodeSeq = buildTINData(userAnswers).filter(_.isDefined).map(_.get).map {
      tin =>
        <TIN issuedBy={tin.issuedBy}>{tin.tin}</TIN>
    }

    val address = Seq(<Address>{buildAddress(userAnswers)}</Address>)

    val email = userAnswers.get(EmailAddressForOrganisationPage)
      .fold(NodeSeq.Empty)(email => <EmailAddress>{email}</EmailAddress>)

    val mandatoryResCountryCode: NodeSeq = buildResCountryCode(userAnswers)

    val nodeBuffer = new xml.NodeBuffer
    val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

    val organisationNodes = {
      <Organisation>
        {nodeBuffer ++
        Seq(<OrganisationName>{mandatoryOrganisationName}</OrganisationName>) ++
        tins ++
        address ++
        email ++
        mandatoryResCountryCode}
      </Organisation>
    }

    prettyPrinter.format(<ID>{organisationNodes}</ID>)
    <ID>{organisationNodes}</ID>
  }

  def buildLiability(userAnswers: UserAnswers) = {
    val mandatoryRelevantTaxpayerNexus = userAnswers.get(RoleInArrangementPage) match {
      case Some(RoleInArrangement.Taxpayer) =>
        userAnswers.get(TaxpayerWhyReportInUKPage) match {
          case Some(value) =>
            value match {
              case TaxpayerWhyReportInUK.UkTaxResident => "RTNEXa"
              case TaxpayerWhyReportInUK.UkPermanentEstablishment => "RTNEXb"
              case TaxpayerWhyReportInUK.IncomeOrProfit => "RTNEXc"
              case TaxpayerWhyReportInUK.UkActivity => "RTNEXd"
              case TaxpayerWhyReportInUK.DoNotKnow => ""
            }
          case None => ""
        }
      case _ => ""
    }

    val capacity: NodeSeq = userAnswers.get(TaxpayerWhyReportArrangementPage)
      .fold(NodeSeq.Empty)({
        case TaxpayerWhyReportArrangement.NoIntermediaries => <Capacity>{"DAC61106"}</Capacity>
        case TaxpayerWhyReportArrangement.ProfessionalPrivilege => <Capacity>{"DAC61104"}</Capacity>
        case TaxpayerWhyReportArrangement.OutsideUKOrEU => <Capacity>{"DAC61105"}</Capacity>
        case capacity@TaxpayerWhyReportArrangement.DoNotKnow => <Capacity>{capacity.toString.toUpperCase}</Capacity>
      })

    val nodeBuffer = new xml.NodeBuffer
    val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)
    val relevantTaxPayersNode = {
        nodeBuffer ++
        Seq(<RelevantTaxpayerNexus>{mandatoryRelevantTaxpayerNexus}</RelevantTaxpayerNexus>) ++
        capacity
    }

    prettyPrinter.format(<Liability>{relevantTaxPayersNode}</Liability>)
    <Liability>{relevantTaxPayersNode}</Liability>
  }

  def buildRelevantTaxPayers(userAnswers: UserAnswers) = {

    val mandatoryImplementingDate: NodeSeq =
      (userAnswers.get(WhatIsReporterTaxpayersStartDateForImplementingArrangementPage),
        userAnswers.get(WhatIsTaxpayersStartDateForImplementingArrangementPage),
        userAnswers.get(DisclosureMarketablePage)) match {
        case (Some(implementingDate), _, _) =>
          Seq(<TaxpayerImplementingDate>{implementingDate}</TaxpayerImplementingDate>)
        case (_, Some(implementingDate), _) =>
          Seq(<TaxpayerImplementingDate>{implementingDate}</TaxpayerImplementingDate>)
        case _ => Seq() //TODO If Marketable then it's Mandatory.
      }

    val nodeBuffer = new xml.NodeBuffer
    val relevantTaxPayersNode = {
      <RelevantTaxpayer>
        {nodeBuffer ++
        buildIDForOrganisation(userAnswers) ++
        mandatoryImplementingDate}
      </RelevantTaxpayer>
    }

    val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

    prettyPrinter.format(<RelevantTaxPayers>{relevantTaxPayersNode}</RelevantTaxPayers>)
    <RelevantTaxPayers>{relevantTaxPayersNode}</RelevantTaxPayers>
  }

  private def buildDisclosureInformationSummary(userAnswers: UserAnswers) = {
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

  private def buildDisclosureInformation(userAnswers: UserAnswers) = {

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

    <DisclosureInformation>
      {mandatoryImplementingDate}
      {reason}
      {buildDisclosureInformationSummary(userAnswers)}
      <NationalProvision>I don't know what this is as I am just a cat</NationalProvision>
      <Amount currCode="GBP">4000</Amount>
      <ConcernedMSs>
        <ConcernedMS>CZ</ConcernedMS>
      </ConcernedMSs>
      <MainBenefitTest1>false</MainBenefitTest1>
      <Hallmarks>
        <ListHallmarks>
          <Hallmark>DAC6D1Other</Hallmark>
        </ListHallmarks>
        <DAC6D1OtherInfo>blah blah blah</DAC6D1OtherInfo>
      </Hallmarks>
    </DisclosureInformation>
  }

  def renderXML(userAnswers: UserAnswers) = {
    val mandatoryDisclosureImportInstruction = userAnswers.get(DisclosureTypePage) match {
      case Some(value) => value.toString.toUpperCase
      case None => ""
    }

    val mandatoryInitialDisclosureMA = userAnswers.get(DisclosureMarketablePage) match {//TODO Is this the right page?
      case Some(value) => value
      case None => false
    }

    val xml =
      <DAC6_Arrangement version="First">
        <DAC6Disclosures>
          {buildHeader(userAnswers)}
          <DisclosureImportInstruction>{mandatoryDisclosureImportInstruction}</DisclosureImportInstruction>
          <Disclosing>
            {buildIDForOrganisation(userAnswers)}
            {buildLiability(userAnswers)}
          </Disclosing>
          <InitialDisclosureMA>{mandatoryInitialDisclosureMA}</InitialDisclosureMA>
          {buildRelevantTaxPayers(userAnswers)}
        </DAC6Disclosures>
      </DAC6_Arrangement>

    val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

    prettyPrinter.format(xml)

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

