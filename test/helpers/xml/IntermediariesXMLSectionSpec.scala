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
import models.individual.Individual
import models.intermediaries.WhatTypeofIntermediary.{Promoter, Serviceprovider}
import models.intermediaries.{ExemptCountries, Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.{ReporterDetails, ReporterLiability, RoleInArrangement}
import models.taxpayer.TaxResidency
import models.{Address, Country, IsExemptionKnown, LoopDetails, Name, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.ReporterDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage

import scala.xml.PrettyPrinter

class IntermediariesXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val reporterSection: ReporterXMLSection = mock[ReporterXMLSection]

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
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR000", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("FR000", None, None)))
  )

  val loopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val organisation: Organisation = Organisation("Reporter Name", Some(address), Some("email@email.com"), taxResidencies)

  val exemptCountry: Set[ExemptCountries] = ExemptCountries.enumerable.withName("FR").toSet

  val individualName: Name = Name("Reporter", "Name")
  val individualDOB: LocalDate = LocalDate.of(1990, 1,1)
  val individual: Individual = Individual(individualName, individualDOB, Some("SomePlace"), Some(address), Some("email@email.com"), taxResidencies)

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

  val intermediaryServiceProvider: Intermediary = intermediary.copy(
    whatTypeofIntermediary = Serviceprovider,
    isExemptionKnown = Yes,
    isExemptionCountryKnown = Some(false),
    exemptCountries = None)

  val intermediaryLoop = IndexedSeq(intermediary)

  "IntermediariesXMLSection" - {

    "getIntermediaryCapacity" - {

      "must build optional intermediary capacity for PROMOTER" in {

        val result = IntermediariesXMLSection(IndexedSeq.empty[Intermediary], Option(reporterSection)).getIntermediaryCapacity(intermediaryCountriesKnown)
        val expected = "<Capacity>DAC61101</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build optional intermediary capacity for SERVICE PROVIDER" in {

        val result = IntermediariesXMLSection(IndexedSeq.empty[Intermediary], Option(reporterSection)).getIntermediaryCapacity(intermediaryServiceProvider)
        val expected = "<Capacity>DAC61102</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must NOT build optional intermediary capacity for 'I DO NOT KNOW'" in {

        val result = IntermediariesXMLSection(IndexedSeq.empty[Intermediary], Option(reporterSection)).getIntermediaryCapacity(intermediary)
        val expected = ""
        prettyPrinter.formatNodes(result) mustBe expected
      }
    }

    "buildNationalExemption" - {

      "must build optional NATIONAL EXEMPTION when KNOWN EXEMPTION and countries are KNOWN" in {

        val result = IntermediariesXMLSection(IndexedSeq.empty[Intermediary], Option(reporterSection)).buildNationalExemption(intermediaryCountriesKnown)

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

        val result = IntermediariesXMLSection(IndexedSeq.empty[Intermediary], Option(reporterSection)).buildNationalExemption(intermediaryCountriesUnknown)

        val expected =
          """<NationalExemption>
            |    <Exemption>true</Exemption>
            |</NationalExemption>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }


    "must NOT build optional NATIONAL EXEMPTION when EXEMPTION are NOT KNOWN" in {

      val result = IntermediariesXMLSection(IndexedSeq.empty[Intermediary], Option(reporterSection)).buildNationalExemption(intermediary)
      val expected = ""

      prettyPrinter.formatNodes(result) mustBe expected
    }
  }

    "buildIntermediaries" - {

      "must build intermediary section from REPORTER DETAILS JOURNEY & INTERMEDIARIES JOURNEY" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(organisation),
          Some(ReporterLiability(RoleInArrangement.Intermediary.toString,
            Some(IntermediaryWhyReportInUK.TaxResidentUK.toString),
            Some(IntermediaryRole.Promoter.toString), Some(true), Some(List("FR")), None)))

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(IntermediaryLoopPage, 0, intermediaryLoop).success.value
          .set(ReporterDetailsPage, 0, reporterDetails).success.value

        val expected =
          """<Intermediaries>
            |    <Intermediary>
            |        <ID>
            |            <Organisation>
            |                <OrganisationName>Reporter Name</OrganisationName>
            |                <TIN issuedBy="GB">UTR000</TIN>
            |                <TIN issuedBy="FR">FR000</TIN>
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
            |                <OrganisationName>Reporter Name</OrganisationName>
            |                <TIN issuedBy="GB">UTR000</TIN>
            |                <TIN issuedBy="FR">FR000</TIN>
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

        IntermediariesXMLSection(intermediaryLoop, Option(reporterSection)).buildIntermediaries.map { result =>

          prettyPrinter.formatNodes(result) mustBe expected
        }
      }
    }
  }
}
