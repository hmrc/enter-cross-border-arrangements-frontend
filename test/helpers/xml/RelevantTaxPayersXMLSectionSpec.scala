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

import base.SpecBase
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, AddressLookup, Country, LoopDetails, TaxReferenceNumbers, UserAnswers}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage

import java.time.LocalDate
import scala.xml.{NodeSeq, PrettyPrinter}

class RelevantTaxPayersXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val address: Address =
    Address(
      Some("value 1"),
      Some("value 2"),
      Some("value 3"),
      "value 4",
      Some("XX9 9XX"),
      Country("valid","FR","France")
    )

  val organisationLoopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val email = "email@email.com"

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  val today: LocalDate = LocalDate.now
  val todayMinusOneMonth: LocalDate = LocalDate.now.minusMonths(1)
  val todayMinusTwoMonths: LocalDate = LocalDate.now.minusMonths(2)
  val taxpayers = IndexedSeq(
    Taxpayer("123", None, Some(organisation), Some(todayMinusOneMonth)),
    Taxpayer("Another ID", None, Some(organisation.copy(organisationName = "Other Taxpayers Ltd")), Some(todayMinusTwoMonths)))


  "RelevantTaxPayersXMLSection" - {

    "buildTaxPayerIsAReporter must build a taxpayer section for reporter details journey" in {
      val addressLookupAddress = AddressLookup(Some("value 1"), Some("value 2"), Some("value 3"), None, "value 5", None, "XX9 9XX")

      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
        .set(ReporterOrganisationNamePage, "Reporter name").success.value
        .set(ReporterSelectedAddressLookupPage, addressLookupAddress).success.value
        .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, organisationLoopDetails).success.value

      val expected =
        s"""<RelevantTaxpayer>
          |    <ID>
          |        <Organisation>
          |            <OrganisationName>Reporter name</OrganisationName>
          |            <TIN issuedBy="GB">1234567890</TIN>
          |            <TIN issuedBy="GB">0987654321</TIN>
          |            <Address>
          |                <Street>value 1</Street>
          |                <BuildingIdentifier>value 2</BuildingIdentifier>
          |                <DistrictName>value 3</DistrictName>
          |                <PostCode>XX9 9XX</PostCode>
          |                <City>value 5</City>
          |                <Country>GB</Country>
          |            </Address>
          |            <EmailAddress>email@email.co.uk</EmailAddress>
          |            <ResCountryCode>GB</ResCountryCode>
          |            <ResCountryCode>FR</ResCountryCode>
          |        </Organisation>
          |    </ID>
          |    <TaxpayerImplementingDate>${today}</TaxpayerImplementingDate>
          |</RelevantTaxpayer>""".stripMargin

      RelevantTaxPayersXMLSection.buildTaxPayerIsAReporter(userAnswers).map { result =>

        prettyPrinter.formatNodes(result) mustBe expected
      }


    }

    "buildTaxPayerIsAReporter must not build a taxpayer section if they're not a reporter" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value

      RelevantTaxPayersXMLSection.buildTaxPayerIsAReporter(userAnswers).map { result =>

        prettyPrinter.formatNodes(result) mustBe ""
      }
    }

    "buildTINData must build a sequence of optional tax residencies" in {
      val result = RelevantTaxPayersXMLSection.buildTINData(taxResidencies)

      val expected =
      """<TIN issuedBy="GB">UTR1234</TIN><TIN issuedBy="FR">CS700100A</TIN><TIN issuedBy="FR">UTR5678</TIN>"""

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildTINData must not build a sequence of optional tax residencies" in {
      val result = RelevantTaxPayersXMLSection.buildTINData(IndexedSeq())

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildResCountryCode must build a sequence of resident countries" in {
      val result = RelevantTaxPayersXMLSection.buildResCountryCode(taxResidencies)

      val expected = """<ResCountryCode>GB</ResCountryCode><ResCountryCode>FR</ResCountryCode>"""

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildResCountryCode must throw an exception if resident countries are missing" in {
      val taxResidencies = IndexedSeq(
        TaxResidency(None, Some(TaxReferenceNumbers("UTR1234", None, None)))
      )

      assertThrows[Exception] {
        RelevantTaxPayersXMLSection.buildResCountryCode(taxResidencies)
      }
    }

    "buildAddress must build the optional address section" in {
      val result = RelevantTaxPayersXMLSection.buildAddress(Some(address))

      val expected =
        """<Address>
          |    <Street>value 1</Street>
          |    <BuildingIdentifier>value 2</BuildingIdentifier>
          |    <DistrictName>value 3</DistrictName>
          |    <PostCode>XX9 9XX</PostCode>
          |    <City>value 4</City>
          |    <Country>FR</Country>
          |</Address>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildAddress must build the optional address section with only the mandatory fields" in {
      val result = RelevantTaxPayersXMLSection.buildAddress(
        Some(Address(None, None, None, "City", None, Country("valid","FR","France"))))

      val expected =
        """<Address>
          |    <City>City</City>
          |    <Country>FR</Country>
          |</Address>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildAddress must not build the optional address section if it's missing" in {
      val result = RelevantTaxPayersXMLSection.buildAddress(None)

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildIDForOrganisation must build the ID section in taxpayers" in {
      val result = RelevantTaxPayersXMLSection.buildIDForOrganisation(organisation)

      val expected =
        """<ID>
          |    <Organisation>
          |        <OrganisationName>Taxpayers Ltd</OrganisationName>
          |        <TIN issuedBy="GB">UTR1234</TIN>
          |        <TIN issuedBy="FR">CS700100A</TIN>
          |        <TIN issuedBy="FR">UTR5678</TIN>
          |        <Address>
          |            <Street>value 1</Street>
          |            <BuildingIdentifier>value 2</BuildingIdentifier>
          |            <DistrictName>value 3</DistrictName>
          |            <PostCode>XX9 9XX</PostCode>
          |            <City>value 4</City>
          |            <Country>FR</Country>
          |        </Address>
          |        <EmailAddress>email@email.com</EmailAddress>
          |        <ResCountryCode>GB</ResCountryCode>
          |        <ResCountryCode>FR</ResCountryCode>
          |    </Organisation>
          |</ID>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "toXml must build a complete RelevantTaxPayers section Elem" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
        .set(ReporterOrganisationNamePage, "Reporter name").success.value
        .set(ReporterOrganisationAddressPage, address).success.value
        .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, organisationLoopDetails).success.value
        .set(TaxpayerLoopPage, taxpayers).success.value

      val expected =
        s"""<RelevantTaxPayers>
           |    <RelevantTaxpayer>
           |        <ID>
           |            <Organisation>
           |                <OrganisationName>Reporter name</OrganisationName>
           |                <TIN issuedBy="GB">1234567890</TIN>
           |                <TIN issuedBy="GB">0987654321</TIN>
           |                <Address>
           |                    <Street>value 1</Street>
           |                    <BuildingIdentifier>value 2</BuildingIdentifier>
           |                    <DistrictName>value 3</DistrictName>
           |                    <PostCode>XX9 9XX</PostCode>
           |                    <City>value 4</City>
           |                    <Country>FR</Country>
           |                </Address>
           |                <EmailAddress>email@email.co.uk</EmailAddress>
           |                <ResCountryCode>GB</ResCountryCode>
           |                <ResCountryCode>FR</ResCountryCode>
           |            </Organisation>
           |        </ID>
           |        <TaxpayerImplementingDate>${today}</TaxpayerImplementingDate>
           |    </RelevantTaxpayer>
           |    <RelevantTaxpayer>
           |        <ID>
           |            <Organisation>
           |                <OrganisationName>Taxpayers Ltd</OrganisationName>
           |                <TIN issuedBy="GB">UTR1234</TIN>
           |                <TIN issuedBy="FR">CS700100A</TIN>
           |                <TIN issuedBy="FR">UTR5678</TIN>
           |                <Address>
           |                    <Street>value 1</Street>
           |                    <BuildingIdentifier>value 2</BuildingIdentifier>
           |                    <DistrictName>value 3</DistrictName>
           |                    <PostCode>XX9 9XX</PostCode>
           |                    <City>value 4</City>
           |                    <Country>FR</Country>
           |                </Address>
           |                <EmailAddress>email@email.com</EmailAddress>
           |                <ResCountryCode>GB</ResCountryCode>
           |                <ResCountryCode>FR</ResCountryCode>
           |            </Organisation>
           |        </ID>
           |        <TaxpayerImplementingDate>${todayMinusOneMonth}</TaxpayerImplementingDate>
           |    </RelevantTaxpayer>
           |    <RelevantTaxpayer>
           |        <ID>
           |            <Organisation>
           |                <OrganisationName>Other Taxpayers Ltd</OrganisationName>
           |                <TIN issuedBy="GB">UTR1234</TIN>
           |                <TIN issuedBy="FR">CS700100A</TIN>
           |                <TIN issuedBy="FR">UTR5678</TIN>
           |                <Address>
           |                    <Street>value 1</Street>
           |                    <BuildingIdentifier>value 2</BuildingIdentifier>
           |                    <DistrictName>value 3</DistrictName>
           |                    <PostCode>XX9 9XX</PostCode>
           |                    <City>value 4</City>
           |                    <Country>FR</Country>
           |                </Address>
           |                <EmailAddress>email@email.com</EmailAddress>
           |                <ResCountryCode>GB</ResCountryCode>
           |                <ResCountryCode>FR</ResCountryCode>
           |            </Organisation>
           |        </ID>
           |        <TaxpayerImplementingDate>${todayMinusTwoMonths}</TaxpayerImplementingDate>
           |    </RelevantTaxpayer>
           |</RelevantTaxPayers>""".stripMargin

      RelevantTaxPayersXMLSection.toXml(userAnswers).map { result =>

        prettyPrinter.format(result) mustBe expected
      }

    }

    "toXml must throw an exception if taxpayer loop details are missing" in {
      assertThrows[Exception] {
        RelevantTaxPayersXMLSection.toXml(UserAnswers(userAnswersId))
      }
    }
  }

}
