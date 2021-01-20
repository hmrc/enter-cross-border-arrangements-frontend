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
import models.IsExemptionKnown.Yes
import models.intermediaries.WhatTypeofIntermediary.Promoter
import models.intermediaries.{ExemptCountries, Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.taxpayer.TaxResidency
import models.{Address, AddressLookup, CountriesListEUCheckboxes, Country, IsExemptionKnown, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UserAnswers, YesNoDoNotKnowRadios}
import pages.intermediaries.{IntermediaryLoopPage, WhatTypeofIntermediaryPage}
import pages.reporter.individual.{ReporterIndividualDateOfBirthPage, ReporterIndividualEmailAddressPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage}
import pages.reporter.intermediary._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}

import scala.xml.PrettyPrinter

class IntermediariesXMLSectionSpec extends SpecBase {

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

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val loopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val organisation: Organisation = Organisation("Intermediaries Ltd", Some(address), Some("email@email.com"), taxResidencies)

  val exemptCountry: Set[ExemptCountries] = ExemptCountries.enumerable.withName("FR").toSet

  val intermediary: Intermediary = Intermediary("123", None, Some(organisation),
    WhatTypeofIntermediary.IDoNotKnow, IsExemptionKnown.Unknown, None, None)

  val intermediaryCountriesUnknown: Intermediary = intermediary.copy(
    whatTypeofIntermediary = Promoter,
    isExemptionKnown = Yes,
    isExemptionCountryKnown = Some(false),
    exemptCountries = None)

  val intermediaryCountriesKnown: Intermediary = intermediary.copy(
    whatTypeofIntermediary = Promoter,
    isExemptionKnown = Yes,
    isExemptionCountryKnown = Some(true),
    exemptCountries = Some(exemptCountry))

  val intermediaryLoop = IndexedSeq(intermediary)

  "IntermediariesXMLSection" - {

    "getIntermediaryCapacity" - {

      "must build optional intermediary capacity for PROMOTER" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(WhatTypeofIntermediaryPage, WhatTypeofIntermediary.Promoter)
          .success
          .value

        val result = IntermediariesXMLSection.getIntermediaryCapacity(userAnswers)
        val expected = "<Capacity>DAC61101</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build optional intermediary capacity for SERVICE PROVIDER" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(WhatTypeofIntermediaryPage, WhatTypeofIntermediary.Serviceprovider)
          .success
          .value

        val result = IntermediariesXMLSection.getIntermediaryCapacity(userAnswers)
        val expected = "<Capacity>DAC61102</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must NOT build optional intermediary capacity for 'I DO NOT KNOW'" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(WhatTypeofIntermediaryPage, WhatTypeofIntermediary.IDoNotKnow)
          .success
          .value

        val result = IntermediariesXMLSection.getIntermediaryCapacity(userAnswers)
        val expected = ""
        prettyPrinter.formatNodes(result) mustBe expected
      }
    }

    "buildNationalExemption" - {

      "must build optional NATIONAL EXEMPTION when KNOWN EXEMPTION and countries are KNOWN" in {

        val result = IntermediariesXMLSection.buildNationalExemption(intermediaryCountriesKnown)

        val expected =
          """<NationalExemption>
            |    <Exemption>true</Exemption>
            |    <CountryExemptions>
            |        <CountryExemption>FR</CountryExemption>
            |    </CountryExemptions>
            |</NationalExemption>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build optional NATIONAL EXEMPTION when KNOWN EXEMPTION but countries NOT KNOWN" in {

        val result = IntermediariesXMLSection.buildNationalExemption(intermediaryCountriesUnknown)

        val expected =
          """<NationalExemption>
            |    <Exemption>true</Exemption>
            |</NationalExemption>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }


    "must NOT build optional NATIONAL EXEMPTION when EXEMPTION are NOT KNOWN" in {

      val result = IntermediariesXMLSection.buildNationalExemption(intermediary)

      val expected = ""

      prettyPrinter.formatNodes(result) mustBe expected
    }
  }

    "buildReporterAsIntermediary" - {

    "must build intermediary section from REPORTER DETAILS for an ORGANISATION as an INTERMEDIARY " +
      "who is a PROMOTER with KNOWN country EXEMPTION in FRANCE" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
        .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
        .set(IntermediaryRolePage, IntermediaryRole.Promoter).success.value
        .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.TaxResidentUK).success.value
        .set(IntermediaryDoYouKnowExemptionsPage, true).success.value
        .set(IntermediaryExemptionInEUPage, YesNoDoNotKnowRadios.Yes).success.value
        .set(IntermediaryWhichCountriesExemptPage, CountriesListEUCheckboxes.enumerable.withName("FR").toSet).success.value
        .set(ReporterOrganisationNamePage, "Reporter name").success.value
        .set(ReporterOrganisationAddressPage, address).success.value
        .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

      val result = IntermediariesXMLSection.buildReporterAsIntermediary(userAnswers)

      val expected =
        """<Intermediary>
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
      val addressLookupAddress = AddressLookup(Some("value 1"), Some("value 2"), Some("value 3"), None, "value 5", None, "XX9 9XX")

      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual).success.value
        .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
        .set(IntermediaryRolePage, IntermediaryRole.ServiceProvider).success.value
        .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.TaxResidentUK).success.value
        .set(IntermediaryDoYouKnowExemptionsPage, true).success.value
        .set(IntermediaryExemptionInEUPage, YesNoDoNotKnowRadios.Yes).success.value
        .set(IntermediaryWhichCountriesExemptPage, CountriesListEUCheckboxes.enumerable.withName("FR").toSet).success.value
        .set(ReporterIndividualNamePage,  Name("FirstName", "Surname")).success.value
        .set(ReporterIndividualDateOfBirthPage, LocalDate.of(1990, 1, 1)).success.value
        .set(ReporterIndividualPlaceOfBirthPage, "SomePlace").success.value
        .set(ReporterSelectedAddressLookupPage, addressLookupAddress).success.value
        .set(ReporterIndividualEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

      val result = IntermediariesXMLSection.buildReporterAsIntermediary(userAnswers)

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

    "toXML" - {

      "must build intermediary section from REPORTER DETAILS JOURNEY & INTERMEDIARIES JOURNEY" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryRolePage, IntermediaryRole.Promoter).success.value
          .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(IntermediaryDoYouKnowExemptionsPage, true).success.value
          .set(IntermediaryExemptionInEUPage, YesNoDoNotKnowRadios.Yes).success.value
          .set(IntermediaryWhichCountriesExemptPage, CountriesListEUCheckboxes.enumerable.withName("FR").toSet).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(IntermediaryLoopPage, intermediaryLoop).success.value

        val expected =
          """<Intermediaries>
            |    <Intermediary>
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
            |        <Capacity>DAC61101</Capacity>
            |        <NationalExemption>
            |            <Exemption>true</Exemption>
            |            <CountryExemptions>
            |                <CountryExemption>FR</CountryExemption>
            |            </CountryExemptions>
            |        </NationalExemption>
            |    </Intermediary>
            |    <Intermediary>
            |        <ID>
            |            <Organisation>
            |                <OrganisationName>Intermediaries Ltd</OrganisationName>
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
            |    </Intermediary>
            |</Intermediaries>""".stripMargin

        IntermediariesXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.formatNodes(result) mustBe expected
        }
      }
    }
  }
}
