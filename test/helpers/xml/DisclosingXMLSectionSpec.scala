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

import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.taxpayer.TaxResidency
import models.{Country, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UserAnswers}
import pages.reporter.individual._
import pages.reporter.intermediary.{IntermediaryRolePage, IntermediaryWhyReportInUKPage}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.TaxpayerWhyReportInUKPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}

import java.time.LocalDate

class DisclosingXMLSectionSpec extends XmlBase {

  val loopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  "DisclosingXMLSection" - {

    "toXml" - {

      "must build the full disclosing section for an organisation" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

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

        DisclosingXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the disclosing section without the optional liability section for an organisation" +
        "when 'do not know' is selected on taxpayer/why-report-in-uk" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.DoNotKnow).success.value

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

        DisclosingXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the disclosing section without the optional liability section for an organisation" +
        "when 'do not know' is selected on intermediary/why-report-in-uk" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.DoNotKnow).success.value

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

        DisclosingXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the full disclosing section for an individual" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual).success.value
          .set(ReporterIndividualNamePage, Name("Reporter", "Name")).success.value
          .set(ReporterIndividualDateOfBirthPage, LocalDate.of(1990, 1, 1)).success.value
          .set(ReporterIndividualPlaceOfBirthPage, "SomePlace").success.value
          .set(ReporterIndividualAddressPage, address).success.value
          .set(ReporterIndividualEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value

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

        DisclosingXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build the full disclosing section for an organisation as an INTERMEDIARY" +
        "When a PROMOTER & RESIDENT IN UK" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryRolePage, IntermediaryRole.Promoter).success.value
          .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

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

        DisclosingXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }
    }
  }
}
