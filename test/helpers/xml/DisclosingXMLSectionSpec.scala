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

import java.time.LocalDate

import base.SpecBase
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.taxpayer.TaxResidency
import models.{Address, Country, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UserAnswers}
import pages.reporter.individual.{ReporterIndividualAddressPage, ReporterIndividualDateOfBirthPage, ReporterIndividualEmailAddressPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage}
import pages.reporter.intermediary.{IntermediaryRolePage, IntermediaryWhyReportInUKPage}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import models.{Address, Country, LoopDetails, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.unsubmitted.UnsubmittedDisclosurePage

import scala.xml.PrettyPrinter

class DisclosingXMLSectionSpec extends SpecBase {

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

  val loopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val email = "email@email.com"

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  "DisclosingXMLSection" - {

    "buildReporterCapacity" - {

      "must build optional reporter capacity for intermediary promoter" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter)
          .success
          .value

        val result = DisclosingXMLSection.buildReporterCapacity(userAnswers, 0)
        val expected = "<Capacity>DAC61101</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build optional reporter capacity for intermediary service provider" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(IntermediaryRolePage, 0, IntermediaryRole.ServiceProvider)
          .success
          .value

        val result = DisclosingXMLSection.buildReporterCapacity(userAnswers, 0)
        val expected = "<Capacity>DAC61102</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not build the optional reporter capacity if answer is 'doNotKnow' in intermediary/why-report-in-uk" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(IntermediaryRolePage, 0, IntermediaryRole.Unknown)
          .success
          .value

        val result = DisclosingXMLSection.buildReporterCapacity(userAnswers, 0)
        val expected = ""
        prettyPrinter.formatNodes(result) mustBe expected
      }
    }

    "buildLiability" - {

      "must build the optional liability section for TAXPAYER" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
          .set(TaxpayerWhyReportArrangementPage, 0, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

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
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

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
        val result = DisclosingXMLSection.buildLiability(
          UserAnswers(userAnswersId)
            .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value,
          0
        )

        prettyPrinter.formatNodes(result) mustBe ""
      }

      "must not build the optional liability section if answer is 'doNotKnow' in intermediary/why-report-in-uk" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.DoNotKnow).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

        prettyPrinter.formatNodes(result) mustBe ""
      }

      "must not build the optional liability section if answer is 'doNotKnow' in taxpayer/why-report-in-uk" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.DoNotKnow).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

        prettyPrinter.formatNodes(result) mustBe ""
      }

      "must not include the optional capacity section if answer is missing in taxpayer/why-reporting" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not include the optional capacity section if answer is 'Unknown' in intermediary/role" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(IntermediaryRolePage, 0, IntermediaryRole.Unknown).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

        val expected =
          """<Liability>
            |    <IntermediaryDiscloser>
            |        <IntermediaryNexus>INEXa</IntermediaryNexus>
            |    </IntermediaryDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not include the optional capacity section if answer is 'doNotKnow' in taxpayer/why-reporting" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
          .set(TaxpayerWhyReportArrangementPage, 0, TaxpayerWhyReportArrangement.DoNotKnow).success.value

        val result = DisclosingXMLSection.buildLiability(userAnswers, 0)

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }
    }


    "toXml" - {

      "must build the full disclosing section for an organisation" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, 0, address).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value

        val expected =
          """<Disclosing>
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
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.co.uk</EmailAddress>
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

        DisclosingXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the disclosing section without the optional liability section for an organisation" +
        "when 'do not know' is selected on taxpayer/why-report-in-uk" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, 0, address).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.DoNotKnow).success.value

        val expected =
          """<Disclosing>
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
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.co.uk</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |</Disclosing>""".stripMargin

        DisclosingXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the disclosing section without the optional liability section for an organisation" +
        "when 'do not know' is selected on intermediary/why-report-in-uk" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, 0, address).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.DoNotKnow).success.value

        val expected =
          """<Disclosing>
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
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.co.uk</EmailAddress>
            |            <ResCountryCode>GB</ResCountryCode>
            |            <ResCountryCode>FR</ResCountryCode>
            |        </Organisation>
            |    </ID>
            |</Disclosing>""".stripMargin

        DisclosingXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the full disclosing section for an individual" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual).success.value
          .set(ReporterIndividualNamePage, 0, Name("Reporter", "Name")).success.value
          .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.of(1990, 1, 1)).success.value
          .set(ReporterIndividualPlaceOfBirthPage, 0, "SomePlace").success.value
          .set(ReporterIndividualAddressPage, 0, address).success.value
          .set(ReporterIndividualEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, 0, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value

        val expected =
          """<Disclosing>
            |    <ID>
            |        <Individual>
            |            <IndividualName>
            |                <FirstName>Reporter</FirstName>
            |                <LastName>Name</LastName>
            |            </IndividualName>
            |            <BirthDate>1990-01-01</BirthDate>
            |            <BirthPlace>SomePlace</BirthPlace>
            |            <TIN issuedBy="GB">1234567890</TIN>
            |            <TIN issuedBy="GB">0987654321</TIN>
            |            <Address>
            |                <Street>value 1</Street>
            |                <BuildingIdentifier>value 2</BuildingIdentifier>
            |                <DistrictName>value 3</DistrictName>
            |                <PostCode>XX9 9XX</PostCode>
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.co.uk</EmailAddress>
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

        DisclosingXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the full disclosing section for an organisation as an INTERMEDIARY" +
        "When a PROMOTER & RESIDENT IN UK" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryRolePage, 0, IntermediaryRole.Promoter).success.value
          .set(IntermediaryWhyReportInUKPage, 0, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, 0, address).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value

        val expected =
          """<Disclosing>
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
            |                <City>value 4</City>
            |                <Country>FR</Country>
            |            </Address>
            |            <EmailAddress>email@email.co.uk</EmailAddress>
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

        DisclosingXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }
    }
  }
}
