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
import models.enterprises.AssociatedEnterprise
import models.individual.Individual
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, AddressLookup, Country, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.reporter.individual._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate
import scala.xml.PrettyPrinter

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

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val email = "email@email.com"
  val individualName: Name = Name("FirstName", "Surname")
  val individualDOB: LocalDate = LocalDate.of(1990, 1,1)
  val individual: Individual = Individual(individualName, individualDOB, Some("SomePlace"), Some(address), Some(email), taxResidencies)


  val loopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  val today: LocalDate = LocalDate.now
  val todayMinusOneMonth: LocalDate = LocalDate.now.minusMonths(1)
  val todayMinusTwoMonths: LocalDate = LocalDate.now.minusMonths(2)
  val taxpayersAsOrganisation = IndexedSeq(
    Taxpayer("123", None, Some(organisation), Some(todayMinusOneMonth)),
    Taxpayer("Another ID", None, Some(organisation.copy(organisationName = "Other Taxpayers Ltd")), Some(todayMinusTwoMonths)))

  val taxpayersAsIndividuals = IndexedSeq(
    Taxpayer("123", Some(individual), None, Some(todayMinusOneMonth)),
    Taxpayer("Another ID", Some(individual.copy(individualName = Name("Another", "Individual"))), None, Some(todayMinusTwoMonths)))


  "RelevantTaxPayersXMLSection" - {

    "buildReporterAsTaxpayer" - {

      "must build a TAXPAYER section from REPORTER DETAILS JOURNEY when user selects ORGANISATION option " +
        "on 'reporter/organisation-or-individual page" in {
        val addressLookupAddress = AddressLookup(Some("value 1"), Some("value 2"), Some("value 3"), None, "value 5", None, "XX9 9XX")

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, today).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterSelectedAddressLookupPage, 0, addressLookupAddress).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers, 0)

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

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build a TAXPAYER section from REPORTER DETAILS JOURNEY without TaxpayerImplementingDate" +
        "when arrangement is NOT MARKETABLE" in {
        val addressLookupAddress = AddressLookup(Some("value 1"), Some("value 2"), Some("value 3"), None, "value 5", None, "XX9 9XX")

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterSelectedAddressLookupPage, 0, addressLookupAddress).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers, 0)

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
             |</RelevantTaxpayer>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build a TAXPAYER section from REPORTER DETAILS JOURNEY when user selects INDIVIDUAL option " +
        "on 'reporter/organisation-or-individual page" in {
        val addressLookupAddress = AddressLookup(Some("value 1"), Some("value 2"), Some("value 3"), None, "value 5", None, "XX9 9XX")

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, today).success.value
          .set(ReporterIndividualNamePage, 0, individualName).success.value
          .set(ReporterIndividualDateOfBirthPage, 0, LocalDate.of(1990, 1, 1)).success.value
          .set(ReporterIndividualPlaceOfBirthPage, 0, "SomePlace").success.value
          .set(ReporterSelectedAddressLookupPage, 0, addressLookupAddress).success.value
          .set(ReporterIndividualEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers, 0)

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
             |    <TaxpayerImplementingDate>${today}</TaxpayerImplementingDate>
             |</RelevantTaxpayer>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "buildTaxPayerIsAReporter must not build a taxpayer section if they're not a reporter" in {
        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers, 0)

        prettyPrinter.formatNodes(result) mustBe ""
      }
    }

    "toXml" - {

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with additional taxpayers as organisations" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, today).success.value
          .set(ReporterIndividualNamePage, 0, individualName).success.value
          .set(ReporterIndividualDateOfBirthPage, 0, individualDOB).success.value
          .set(ReporterIndividualPlaceOfBirthPage, 0, "SomePlace").success.value
          .set(ReporterIndividualAddressPage, 0, address).success.value
          .set(ReporterIndividualEmailAddressPage, 0, "email@email.com").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(TaxpayerLoopPage, 0, taxpayersAsOrganisation).success.value

        val expected =
          s"""<RelevantTaxPayers>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>FirstName</FirstName>
             |                    <LastName>Surname</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |                <EmailAddress>email@email.com</EmailAddress>
             |                <ResCountryCode>GB</ResCountryCode>
             |                <ResCountryCode>FR</ResCountryCode>
             |            </Individual>
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

        RelevantTaxPayersXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected

        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation" +
        " with additional taxpayers as organisations" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, today).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, 0, address).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(TaxpayerLoopPage, 0, taxpayersAsOrganisation).success.value

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

        RelevantTaxPayersXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with additional taxpayers as Individuals" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Individual).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, today).success.value
          .set(ReporterIndividualNamePage, 0, Name("Reporter", "Name")).success.value
          .set(ReporterIndividualDateOfBirthPage, 0, individualDOB).success.value
          .set(ReporterIndividualPlaceOfBirthPage, 0, "SomePlace").success.value
          .set(ReporterIndividualAddressPage, 0, address).success.value
          .set(ReporterIndividualEmailAddressPage, 0, "email@email.com").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(TaxpayerLoopPage, 0, taxpayersAsIndividuals).success.value

        val expected =
          s"""<RelevantTaxPayers>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>Reporter</FirstName>
             |                    <LastName>Name</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |                <EmailAddress>email@email.com</EmailAddress>
             |                <ResCountryCode>GB</ResCountryCode>
             |                <ResCountryCode>FR</ResCountryCode>
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>${today}</TaxpayerImplementingDate>
             |    </RelevantTaxpayer>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>FirstName</FirstName>
             |                    <LastName>Surname</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>${todayMinusOneMonth}</TaxpayerImplementingDate>
             |    </RelevantTaxpayer>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>Another</FirstName>
             |                    <LastName>Individual</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>${todayMinusTwoMonths}</TaxpayerImplementingDate>
             |    </RelevantTaxpayer>
             |</RelevantTaxPayers>""".stripMargin


        RelevantTaxPayersXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation " +
        "with additional taxpayers as Individuals" in {

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterOrganisationOrIndividualPage, 0, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, 0, today).success.value
          .set(ReporterOrganisationNamePage, 0, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, 0, address).success.value
          .set(ReporterOrganisationEmailAddressPage, 0, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, 0, loopDetails).success.value
          .set(TaxpayerLoopPage, 0, taxpayersAsIndividuals).success.value

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
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>FirstName</FirstName>
             |                    <LastName>Surname</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>${todayMinusOneMonth}</TaxpayerImplementingDate>
             |    </RelevantTaxpayer>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>Another</FirstName>
             |                    <LastName>Individual</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>${todayMinusTwoMonths}</TaxpayerImplementingDate>
             |    </RelevantTaxpayer>
             |</RelevantTaxPayers>""".stripMargin

        RelevantTaxPayersXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML with additional taxpayers as organisations and " +
        "their associated enterprise - one organisation" in {

        val enterpriseLoop = IndexedSeq(
          AssociatedEnterprise("id", None, Some(organisation), List("Other Taxpayers Ltd"), isAffectedBy = false))

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value
          .set(TaxpayerLoopPage, 0, taxpayersAsOrganisation).success.value
          .set(AssociatedEnterpriseLoopPage, 0, enterpriseLoop).success.value

        val expected =
          s"""<RelevantTaxPayers>
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
             |        <TaxpayerImplementingDate>$todayMinusOneMonth</TaxpayerImplementingDate>
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
             |        <TaxpayerImplementingDate>$todayMinusTwoMonths</TaxpayerImplementingDate>
             |        <AssociatedEnterprises>
             |            <AssociatedEnterprise>
             |                <AssociatedEnterpriseID>
             |                    <Organisation>
             |                        <OrganisationName>Taxpayers Ltd</OrganisationName>
             |                        <TIN issuedBy="GB">UTR1234</TIN>
             |                        <TIN issuedBy="FR">CS700100A</TIN>
             |                        <TIN issuedBy="FR">UTR5678</TIN>
             |                        <Address>
             |                            <Street>value 1</Street>
             |                            <BuildingIdentifier>value 2</BuildingIdentifier>
             |                            <DistrictName>value 3</DistrictName>
             |                            <PostCode>XX9 9XX</PostCode>
             |                            <City>value 4</City>
             |                            <Country>FR</Country>
             |                        </Address>
             |                        <EmailAddress>email@email.com</EmailAddress>
             |                        <ResCountryCode>GB</ResCountryCode>
             |                        <ResCountryCode>FR</ResCountryCode>
             |                    </Organisation>
             |                </AssociatedEnterpriseID>
             |                <AffectedPerson>false</AffectedPerson>
             |            </AssociatedEnterprise>
             |        </AssociatedEnterprises>
             |    </RelevantTaxpayer>
             |</RelevantTaxPayers>""".stripMargin

        RelevantTaxPayersXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML with additional taxpayers as Individuals and " +
        "their associated enterprises - one individual and one organisation" in {

        val enterpriseLoop = IndexedSeq(
          AssociatedEnterprise("id", Some(individual), None, List(individual.nameAsString), isAffectedBy = true),
          AssociatedEnterprise("id2", None, Some(organisation), List(individual.nameAsString), isAffectedBy = true))

        val userAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary).success.value
          .set(TaxpayerLoopPage, 0, taxpayersAsIndividuals).success.value
          .set(AssociatedEnterpriseLoopPage, 0, enterpriseLoop).success.value

        val expected =
          s"""<RelevantTaxPayers>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>FirstName</FirstName>
             |                    <LastName>Surname</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>$todayMinusOneMonth</TaxpayerImplementingDate>
             |        <AssociatedEnterprises>
             |            <AssociatedEnterprise>
             |                <AssociatedEnterpriseID>
             |                    <Individual>
             |                        <IndividualName>
             |                            <FirstName>FirstName</FirstName>
             |                            <LastName>Surname</LastName>
             |                        </IndividualName>
             |                        <BirthDate>1990-01-01</BirthDate>
             |                        <BirthPlace>SomePlace</BirthPlace>
             |                        <TIN issuedBy="GB">UTR1234</TIN>
             |                        <TIN issuedBy="FR">CS700100A</TIN>
             |                        <TIN issuedBy="FR">UTR5678</TIN>
             |                        <Address>
             |                            <Street>value 1</Street>
             |                            <BuildingIdentifier>value 2</BuildingIdentifier>
             |                            <DistrictName>value 3</DistrictName>
             |                            <PostCode>XX9 9XX</PostCode>
             |                            <City>value 4</City>
             |                            <Country>FR</Country>
             |                        </Address>
             |                        <EmailAddress>email@email.com</EmailAddress>
             |                        <ResCountryCode>GB</ResCountryCode>
             |                        <ResCountryCode>FR</ResCountryCode>
             |                    </Individual>
             |                </AssociatedEnterpriseID>
             |                <AffectedPerson>true</AffectedPerson>
             |            </AssociatedEnterprise>
             |            <AssociatedEnterprise>
             |                <AssociatedEnterpriseID>
             |                    <Organisation>
             |                        <OrganisationName>Taxpayers Ltd</OrganisationName>
             |                        <TIN issuedBy="GB">UTR1234</TIN>
             |                        <TIN issuedBy="FR">CS700100A</TIN>
             |                        <TIN issuedBy="FR">UTR5678</TIN>
             |                        <Address>
             |                            <Street>value 1</Street>
             |                            <BuildingIdentifier>value 2</BuildingIdentifier>
             |                            <DistrictName>value 3</DistrictName>
             |                            <PostCode>XX9 9XX</PostCode>
             |                            <City>value 4</City>
             |                            <Country>FR</Country>
             |                        </Address>
             |                        <EmailAddress>email@email.com</EmailAddress>
             |                        <ResCountryCode>GB</ResCountryCode>
             |                        <ResCountryCode>FR</ResCountryCode>
             |                    </Organisation>
             |                </AssociatedEnterpriseID>
             |                <AffectedPerson>true</AffectedPerson>
             |            </AssociatedEnterprise>
             |        </AssociatedEnterprises>
             |    </RelevantTaxpayer>
             |    <RelevantTaxpayer>
             |        <ID>
             |            <Individual>
             |                <IndividualName>
             |                    <FirstName>Another</FirstName>
             |                    <LastName>Individual</LastName>
             |                </IndividualName>
             |                <BirthDate>1990-01-01</BirthDate>
             |                <BirthPlace>SomePlace</BirthPlace>
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
             |            </Individual>
             |        </ID>
             |        <TaxpayerImplementingDate>$todayMinusTwoMonths</TaxpayerImplementingDate>
             |    </RelevantTaxpayer>
             |</RelevantTaxPayers>""".stripMargin

        RelevantTaxPayersXMLSection.toXml(userAnswers, 0).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }
    }
  }
}
