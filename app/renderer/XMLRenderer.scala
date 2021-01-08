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
import models.{Address, UserAnswers}
import org.joda.time.DateTime
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import pages.reporter.RoleInArrangementPage
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.twirl.api.XmlFormat

import javax.inject.Inject
import scala.xml.{Elem, NodeSeq}

class XMLRenderer @Inject()() {

  private def createHeader(userAnswers: UserAnswers): Elem = {
    val messageRefId = userAnswers.get(DisclosureNamePage) match {
      case Some(disclosureName) => "GB" + disclosureName
      case None => ""
    }

    //XML DateTime format e.g. 2021-01-06T12:25:14
    val timestamp = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")

    <Header>
      <MessageRefId>{messageRefId}</MessageRefId>
      <Timestamp>{timestamp}</Timestamp>
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
      case None => IndexedSeq[Elem]() //TODO Mandatory and Repeatable
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

  private def createIDForOrganisation(userAnswers: UserAnswers) = {
    //Mandatory
    val organisationName = userAnswers.get(OrganisationNamePage) match {
      case Some(name) => name
      case None => ""
    }

    val tins: NodeSeq = buildTINData(userAnswers).filter(_.isDefined).map(_.get).map {
      tin =>
        <TIN issuedBy={tin.issuedBy}>{tin.tin}</TIN>
    }

    val resCountryCode: NodeSeq = buildResCountryCode(userAnswers)

    <ID>
      <Organisation>
        <OrganisationName>{organisationName}</OrganisationName>
        {tins}
        <Address>{buildAddress(userAnswers)}</Address>
        {userAnswers.get(EmailAddressForOrganisationPage).map(email => <EmailAddress>{email}</EmailAddress>)}
        {resCountryCode}
      </Organisation>
    </ID>

    ID(???)

    ???
  }

  private def buildLiability(userAnswers: UserAnswers): Option[Elem] = {
    val liabilityData = userAnswers.get(RoleInArrangementPage) match {
      case Some(RoleInArrangement.Taxpayer) =>
        val relevantTaxpayerNexus = userAnswers.get(TaxpayerWhyReportInUKPage) match {
          case Some(value) => value.toString.toUpperCase
          case None => ""
        }

        val capacity = userAnswers.get(TaxpayerWhyReportArrangementPage) match {
          case Some(value) => Some(value.toString.toUpperCase)
          case None => None
        }

        Some(Liability(RelevantTaxpayerDiscloser(relevantTaxpayerNexus, capacity)))
      case _ => None
    }

    liabilityData.map { liability =>
      <Liability>
        {liability.relevantTaxpayerDiscloser.relevantTaxpayerNexus}
        {liability.relevantTaxpayerDiscloser.capacity.map(capacity => <Capacity>{capacity.toUpperCase}</Capacity>)}
      </Liability>
    }
  }

  private def createRelevantTaxPayers(userAnswers: UserAnswers): RelevantTaxPayers = {
    ???
  }

  private def createDisclosureInformation(userAnswers: UserAnswers): DisclosureInformation = {
    ???
  }

  def createDac6Disclosures(userAnswers: UserAnswers): DAC6Disclosures = {
    val disclosureImportInstructionData = userAnswers.get(DisclosureTypePage) match {
      case Some(value) => value.toString.toUpperCase
      case None => ""
    }

    val disclosureImportInstruction = <DisclosureImportInstruction>{disclosureImportInstructionData}</DisclosureImportInstruction>

    val isMarketableData = userAnswers.get(DisclosureMarketablePage) match {//TODO Is this the right page?
      case Some(value) => value
      case None => false
    }

    val isMarketable = <InitialDisclosureMA>{isMarketableData}</InitialDisclosureMA>

//    DAC6Disclosures(
//      disclosureImportInstruction = disclosureImportInstruction,
//      disclosing = Disclosing(???, createLiability(userAnswers)),
//      initialDisclosureMA = isMarketable,
//      relevantTaxPayers = ???,
//      disclosureInformation = ???)
    ???
  }

  def renderXML(userAnswers: UserAnswers): XmlFormat.Appendable = {

    val header = createHeader(userAnswers)

    val dac6Disclosures = createDac6Disclosures(userAnswers)

//    views.xml.generateXMLForDAC(header, dac6Disclosures) //TODO Remove. Have method return a NodeSeq of everything
    ???
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
