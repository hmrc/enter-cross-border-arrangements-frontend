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
import helpers.data.ValidUserAnswersForSubmission.{validIndividual, validOrganisation}
import models.intermediaries.{Intermediary, WhatTypeofIntermediary}
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.reporter.{ReporterDetails, ReporterLiability, RoleInArrangement}
import models.{IsExemptionKnown, UnsubmittedDisclosure, UserAnswers}
import pages.reporter.ReporterDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate
import scala.xml.PrettyPrinter

class ReporterXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val today: LocalDate = LocalDate.now

  "DisclosingXMLSection" - {

    "buildReporterCapacity" - {

      "must build optional reporter capacity for intermediary promoter" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability("Intermediary", None, Some("DAC61101"), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildReporterCapacity
        val expected = "<Capacity>DAC61101</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build optional reporter capacity for intermediary service provider" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability("Intermediary", None, Some("DAC61102"), None, None, None)))


        val result = ReporterXMLSection(reporterDetails).buildReporterCapacity
        val expected = "<Capacity>DAC61102</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not build the optional reporter capacity if answer is 'doNotKnow' in intermediary/why-report-in-uk" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability("Intermediary", None, Some(IntermediaryRole.Unknown.toString), None, None, None)))


        val result = ReporterXMLSection(reporterDetails).buildReporterCapacity
        val expected = ""
        prettyPrinter.formatNodes(result) mustBe expected
      }
    }

    "buildLiability" - {

      "must build the optional liability section for TAXPAYER" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString, Some("RTNEXb"), Some("DAC61104"), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |        <Capacity>DAC61104</Capacity>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build the optional liability section for INTERMEDIARY" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString, Some("INEXa"), Some("DAC61101"), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        val expected =
          """<Liability>
            |    <IntermediaryDiscloser>
            |        <IntermediaryNexus>INEXa</IntermediaryNexus>
            |        <Capacity>DAC61101</Capacity>
            |    </IntermediaryDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not build the optional liability section if data is missing" in {

        val reporterDetails = ReporterDetails(None, Some(validOrganisation))
        val result = ReporterXMLSection(reporterDetails).buildLiability

        prettyPrinter.formatNodes(result) mustBe ""
      }

      "must not build the optional liability section if answer is 'doNotKnow' in intermediary/why-report-in-uk" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString, Some(IntermediaryWhyReportInUK.DoNotKnow.toString), Some("DAC61101"), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        prettyPrinter.formatNodes(result) mustBe ""
      }

      "must not build the optional liability section if answer is 'doNotKnow' in taxpayer/why-report-in-uk" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString, Some(TaxpayerWhyReportInUK.DoNotKnow.toString), Some("DAC61101"), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        prettyPrinter.formatNodes(result) mustBe ""
      }


      "must not include the optional capacity section if answer is missing in taxpayer/why-reporting" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            Some(TaxpayerWhyReportInUK.UkPermanentEstablishment.toString), None, None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not include the optional capacity section if answer is 'Unknown' in intermediary/role" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString,
            Some(IntermediaryWhyReportInUK.TaxResidentUK.toString),
            Some(IntermediaryRole.Unknown.toString), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        val expected =
          """<Liability>
            |    <IntermediaryDiscloser>
            |        <IntermediaryNexus>INEXa</IntermediaryNexus>
            |    </IntermediaryDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not include the optional capacity section if answer is 'doNotKnow' in taxpayer/why-reporting" in {


        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            Some(TaxpayerWhyReportInUK.UkPermanentEstablishment.toString),
            Some(TaxpayerWhyReportArrangement.DoNotKnow.toString), None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildLiability

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }
    }

    "buildDisclosureDetails" - {

      "must build the full disclosing section for an organisation" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            Some("RTNEXb"),
            None, None, None, None)))

        val expected =
          """<Disclosing>
            |    <ID>
            |        <Organisation>
            |            <OrganisationName>Taxpayers Ltd</OrganisationName>
            |            <TIN issuedBy="GB">UTR1234</TIN>
            |            <TIN issuedBy="FR">CS700100A</TIN>
            |            <TIN issuedBy="FR">UTR5678</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.com</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |    <Liability>
            |        <RelevantTaxpayerDiscloser>
            |            <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |        </RelevantTaxpayerDiscloser>
            |    </Liability>
            |</Disclosing>""".stripMargin

        ReporterXMLSection(reporterDetails).buildDisclosureDetails.map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the disclosing section without the optional liability section for an organisation" +
        "when 'do not know' is selected on taxpayer/why-report-in-uk" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            Some(TaxpayerWhyReportInUK.DoNotKnow.toString),
            None, None, None, None)))

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterDetailsPage, 0, reporterDetails).success.value

        val expected =
          """<Disclosing>
            |    <ID>
            |        <Organisation>
            |            <OrganisationName>Taxpayers Ltd</OrganisationName>
            |            <TIN issuedBy="GB">UTR1234</TIN>
            |            <TIN issuedBy="FR">CS700100A</TIN>
            |            <TIN issuedBy="FR">UTR5678</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.com</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |</Disclosing>""".stripMargin

        ReporterXMLSection(reporterDetails).buildDisclosureDetails.map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the disclosing section without the optional liability section for an organisation" +
        "when 'do not know' is selected on intermediary/why-report-in-uk" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString,
            Some(IntermediaryWhyReportInUK.DoNotKnow.toString),
            None, None, None, None)))

        val expected =
          """<Disclosing>
            |    <ID>
            |        <Organisation>
            |            <OrganisationName>Taxpayers Ltd</OrganisationName>
            |            <TIN issuedBy="GB">UTR1234</TIN>
            |            <TIN issuedBy="FR">CS700100A</TIN>
            |            <TIN issuedBy="FR">UTR5678</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.com</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |</Disclosing>""".stripMargin

        ReporterXMLSection(reporterDetails).buildDisclosureDetails.map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the full disclosing section for an individual" in {

        val reporterDetails = ReporterDetails(
          Some(validIndividual),
          None,
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            Some(TaxpayerWhyReportInUK.UkPermanentEstablishment.toString),
            None, None, None, None)))

        val expected =
          """<Disclosing>
            |    <ID>
            |        <Individual>
            |            <IndividualName>
            |                <FirstName>FirstName</FirstName>
            |                <LastName>Surname</LastName>
            |            </IndividualName>
            |            <BirthDate>1990-01-01</BirthDate>
            |            <BirthPlace>SomePlace</BirthPlace>
            |            <TIN issuedBy="GB">UTR1234</TIN>
            |            <TIN issuedBy="FR">CS700100A</TIN>
            |            <TIN issuedBy="FR">UTR5678</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.com</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Individual>
            |    </ID>
            |    <Liability>
            |        <RelevantTaxpayerDiscloser>
            |            <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |        </RelevantTaxpayerDiscloser>
            |    </Liability>
            |</Disclosing>""".stripMargin

        ReporterXMLSection(reporterDetails).buildDisclosureDetails.map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the full disclosing section for an organisation as an INTERMEDIARY" +
        "When a PROMOTER & RESIDENT IN UK" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString,
            Some(IntermediaryWhyReportInUK.TaxResidentUK.toString),
            Some(IntermediaryRole.Promoter.toString), None, None, None)))

        val expected =
          """<Disclosing>
            |    <ID>
            |        <Organisation>
            |            <OrganisationName>Taxpayers Ltd</OrganisationName>
            |            <TIN issuedBy="GB">UTR1234</TIN>
            |            <TIN issuedBy="FR">CS700100A</TIN>
            |            <TIN issuedBy="FR">UTR5678</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.com</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |    <Liability>
            |        <IntermediaryDiscloser>
            |            <IntermediaryNexus>INEXa</IntermediaryNexus>
            |            <Capacity>DAC61101</Capacity>
            |        </IntermediaryDiscloser>
            |    </Liability>
            |</Disclosing>""".stripMargin

        ReporterXMLSection(reporterDetails).buildDisclosureDetails.map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }
    }

    "buildReporterAsTaxpayer" - {

      "must build a TAXPAYER section from REPORTER DETAILS JOURNEY when user selects ORGANISATION option " +
        "on 'reporter/organisation-or-individual page" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(today))))

        val result = ReporterXMLSection(reporterDetails).buildReporterAsTaxpayer

        val expected =
          s"""<RelevantTaxpayer>
             |    <ID>
             |        <Organisation>
             |            <OrganisationName>Taxpayers Ltd</OrganisationName>
             |            <TIN issuedBy="GB">UTR1234</TIN>
             |            <TIN issuedBy="FR">CS700100A</TIN>
             |            <TIN issuedBy="FR">UTR5678</TIN>
             |            <Address>
             |                <Street>value 1</Street>
             |                <BuildingIdentifier>value 2</BuildingIdentifier>
             |                <DistrictName>value 3</DistrictName>
             |                <PostCode>XX9 9XX</PostCode>
             |                <City>value 4</City>
             |                <Country>FR</Country>
             |            </Address>
             |            <EmailAddress>email@email.com</EmailAddress>
             |            <ResCountryCode>GB</ResCountryCode>
             |            <ResCountryCode>FR</ResCountryCode>
             |        </Organisation>
             |    </ID>
             |    <TaxpayerImplementingDate>$today</TaxpayerImplementingDate>
             |</RelevantTaxpayer>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build a TAXPAYER section from REPORTER DETAILS JOURNEY without TaxpayerImplementingDate" +
        "when arrangement is NOT MARKETABLE" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, None)))

        val result = ReporterXMLSection(reporterDetails).buildReporterAsTaxpayer

        val expected =
          s"""<RelevantTaxpayer>
             |    <ID>
             |        <Organisation>
             |            <OrganisationName>Taxpayers Ltd</OrganisationName>
             |            <TIN issuedBy="GB">UTR1234</TIN>
             |            <TIN issuedBy="FR">CS700100A</TIN>
             |            <TIN issuedBy="FR">UTR5678</TIN>
             |            <Address>
             |                <Street>value 1</Street>
             |                <BuildingIdentifier>value 2</BuildingIdentifier>
             |                <DistrictName>value 3</DistrictName>
             |                <PostCode>XX9 9XX</PostCode>
             |                <City>value 4</City>
             |                <Country>FR</Country>
             |            </Address>
             |            <EmailAddress>email@email.com</EmailAddress>
             |            <ResCountryCode>GB</ResCountryCode>
             |            <ResCountryCode>FR</ResCountryCode>
             |        </Organisation>
             |    </ID>
             |</RelevantTaxpayer>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build a TAXPAYER section from REPORTER DETAILS JOURNEY when user selects INDIVIDUAL option " +
        "on 'reporter/organisation-or-individual page" in {

        val reporterDetails = ReporterDetails(
          Some(validIndividual),
          None,
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(today))))

        val result = ReporterXMLSection(reporterDetails).buildReporterAsTaxpayer

        val expected =
          s"""<RelevantTaxpayer>
             |    <ID>
             |        <Individual>
             |            <IndividualName>
             |                <FirstName>FirstName</FirstName>
             |                <LastName>Surname</LastName>
             |            </IndividualName>
             |            <BirthDate>1990-01-01</BirthDate>
             |            <BirthPlace>SomePlace</BirthPlace>
             |            <TIN issuedBy="GB">UTR1234</TIN>
             |            <TIN issuedBy="FR">CS700100A</TIN>
             |            <TIN issuedBy="FR">UTR5678</TIN>
             |            <Address>
             |                <Street>value 1</Street>
             |                <BuildingIdentifier>value 2</BuildingIdentifier>
             |                <DistrictName>value 3</DistrictName>
             |                <PostCode>XX9 9XX</PostCode>
             |                <City>value 4</City>
             |                <Country>FR</Country>
             |            </Address>
             |            <EmailAddress>email@email.com</EmailAddress>
             |            <ResCountryCode>GB</ResCountryCode>
             |            <ResCountryCode>FR</ResCountryCode>
             |        </Individual>
             |    </ID>
             |    <TaxpayerImplementingDate>$today</TaxpayerImplementingDate>
             |</RelevantTaxpayer>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

    }

    "buildReporterAsIntermediary" - {

      val intermediary: Intermediary = Intermediary("123", None, Some(validOrganisation),
        WhatTypeofIntermediary.IDoNotKnow, IsExemptionKnown.Unknown, None, None)

      "must build intermediary section from REPORTER DETAILS for an ORGANISATION as an INTERMEDIARY " +
        "who is a PROMOTER with KNOWN country EXEMPTION in FRANCE" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString,
            Some(IntermediaryWhyReportInUK.TaxResidentUK.toString),
            Some(IntermediaryRole.Promoter.toString), Some(true), Some(List("FR")), None)))

        val result = ReporterXMLSection(reporterDetails).buildReporterAsIntermediary//(intermediary)

        val expected =
          """<Intermediary>
            |    <ID>
            |        <Organisation>
            |            <OrganisationName>Taxpayers Ltd</OrganisationName>
            |            <TIN issuedBy="GB">UTR1234</TIN>
            |            <TIN issuedBy="FR">CS700100A</TIN>
            |            <TIN issuedBy="FR">UTR5678</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.com</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |    <Capacity>DAC61101</Capacity>
            |    <NationalExemption>
            |        <Exemption>true</Exemption>
            |        <CountryExemptions>
            |            <CountryExemption>FR</CountryExemption>
            |        </CountryExemptions>
            |    </NationalExemption>
            |</Intermediary>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build intermediary section from REPORTER DETAILS for an INDIVIDUAL as an INTERMEDIARY " +
        "who is a SERVICE PROVIDER with KNOWN country EXEMPTION in FRANCE" in {

        val reporterDetails = ReporterDetails(
          Some(validIndividual),
          None,
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString,
            Some(IntermediaryWhyReportInUK.TaxResidentUK.toString),
            Some(IntermediaryRole.ServiceProvider.toString), Some(true), Some(List("FR")), None)))

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterDetailsPage, 0, reporterDetails).success.value

        val result = ReporterXMLSection(reporterDetails).buildReporterAsIntermediary//(intermediary)

        val expected =
          s"""<Intermediary>
             |    <ID>
             |        <Individual>
             |            <IndividualName>
             |                <FirstName>FirstName</FirstName>
             |                <LastName>Surname</LastName>
             |            </IndividualName>
             |            <BirthDate>1990-01-01</BirthDate>
             |            <BirthPlace>SomePlace</BirthPlace>
             |            <TIN issuedBy="GB">UTR1234</TIN>
             |            <TIN issuedBy="FR">CS700100A</TIN>
             |            <TIN issuedBy="FR">UTR5678</TIN>
             |            <Address>
             |                <Street>value 1</Street>
             |                <BuildingIdentifier>value 2</BuildingIdentifier>
             |                <DistrictName>value 3</DistrictName>
             |                <PostCode>XX9 9XX</PostCode>
             |                <City>value 4</City>
             |                <Country>FR</Country>
             |            </Address>
             |            <EmailAddress>email@email.com</EmailAddress>
             |            <ResCountryCode>GB</ResCountryCode>
             |            <ResCountryCode>FR</ResCountryCode>
             |        </Individual>
             |    </ID>
             |    <Capacity>DAC61102</Capacity>
             |    <NationalExemption>
             |        <Exemption>true</Exemption>
             |        <CountryExemptions>
             |            <CountryExemption>FR</CountryExemption>
             |        </CountryExemptions>
             |    </NationalExemption>
             |</Intermediary>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }
    }




  }
}
