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
import models.organisation.Organisation
import models.{Address, Country, UserAnswers}
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import pages.reporter.{ReporterSelectedAddressLookupPage, RoleInArrangementPage}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer.TaxpayerLoopPage
import renderer.RelevantTaxPayersXMLSection.buildIDForOrganisation

import scala.xml.{Elem, NodeSeq}

object DisclosingXMLSection extends XMLBuilder {

//  private[renderer] def buildAddressForReporter(userAnswers: UserAnswers): NodeSeq = {
//    (userAnswers.get(ReporterOrganisationAddressPage), userAnswers.get(ReporterSelectedAddressLookupPage)) match {
//      case (Some(address), _) => RelevantTaxPayersXMLSection.buildAddress(Some(address))
//      case (_, Some(address)) =>
//        //Note: /select-address page address is UK-based
//        val country = Country(state = "valid", code = "GB", description = "United Kingdom")
//        val convertToAddress = Address(
//          addressLine1 = address.addressLine1,
//          addressLine2 = address.addressLine2,
//          addressLine3 = address.addressLine3,
//          city = address.town,
//          postCode = Some(address.postcode),
//          country = country)
//
//        RelevantTaxPayersXMLSection.buildAddress(Some(convertToAddress))
//      case _ => NodeSeq.Empty
//    }
//  }

//  private[renderer] def buildIDForOrganisation(userAnswers: UserAnswers): Elem = {
//    val mandatoryOrganisationName = userAnswers.get(ReporterOrganisationNamePage) match {
//      case Some(name) => <OrganisationName>{name}</OrganisationName>
//      case None => throw new Exception("Missing reporter organisation name when building Disclosing XML section")
//    }
//
//    val email = userAnswers.get(ReporterOrganisationEmailAddressPage).fold(NodeSeq.Empty)(email => <EmailAddress>{email}</EmailAddress>)
//
//    val nodeBuffer = new xml.NodeBuffer
//    val organisationNodes = {
//      <Organisation>
//        {nodeBuffer ++
//        mandatoryOrganisationName ++
//        buildAddressForReporter(userAnswers) ++
//        email}
//      </Organisation>
//    }
//
//    <ID>{organisationNodes}</ID>
//  }

  private[renderer] def buildLiability(userAnswers: UserAnswers): NodeSeq = {
    //TODO This is optional. If value is don't know, don't include this section
    userAnswers.get(TaxpayerWhyReportInUKPage).fold(NodeSeq.Empty) {
      case TaxpayerWhyReportInUK.DoNotKnow =>
        NodeSeq.Empty
      case taxpayerWhyReportInUK: TaxpayerWhyReportInUK =>
        val mandatoryRelevantTaxpayerNexus: NodeSeq =
          userAnswers.get(RoleInArrangementPage) match {
            case Some(RoleInArrangement.Taxpayer) =>
              <RelevantTaxpayerNexus>{taxpayerWhyReportInUK.toString}</RelevantTaxpayerNexus>
            case _ =>
              throw new Exception("Missing report details when building Disclosing XML section")
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
  }

  override def toXml(userAnswers: UserAnswers): Elem = {
    val nodeBuffer = new xml.NodeBuffer

    //TODO Need to check here if reporter is an individual or organisation. Then add to nodeBuffer
    val organisationDetailsForReporter = Organisation.buildOrganisationDetailsForReporter(userAnswers)

    val discloseDetails = {
      nodeBuffer ++
        RelevantTaxPayersXMLSection.buildIDForOrganisation(organisationDetailsForReporter) ++
        buildLiability(userAnswers)
    }

    <Disclosing>{discloseDetails}</Disclosing>
  }
}
