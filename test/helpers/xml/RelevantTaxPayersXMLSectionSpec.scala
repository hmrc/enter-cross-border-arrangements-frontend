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
import models.{Address, AddressLookup, Country, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UserAnswers}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.ReporterTaxpayersStartDateForImplementingArrangementPage
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterSelectedAddressLookupPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage
import java.time.LocalDate

import models.individual.Individual
import pages.reporter.individual.{ReporterIndividualAddressPage, ReporterIndividualDateOfBirthPage, ReporterIndividualEmailAddressPage, ReporterIndividualNamePage, ReporterIndividualPlaceOfBirthPage}

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
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterSelectedAddressLookupPage, addressLookupAddress).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers)

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
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterSelectedAddressLookupPage, addressLookupAddress).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers)

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
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
          .set(ReporterIndividualNamePage, individualName).success.value
          .set(ReporterIndividualDateOfBirthPage, LocalDate.of(1990, 1, 1)).success.value
          .set(ReporterIndividualPlaceOfBirthPage, "SomePlace").success.value
          .set(ReporterSelectedAddressLookupPage, addressLookupAddress).success.value
          .set(ReporterIndividualEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers)

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
          .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value

        val result = RelevantTaxPayersXMLSection.buildReporterAsTaxpayer(userAnswers)

        prettyPrinter.formatNodes(result) mustBe ""
      }
    }

    "toXml" - {

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with additional taxpayers as organisations" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
          .set(ReporterIndividualNamePage, individualName).success.value
          .set(ReporterIndividualDateOfBirthPage, individualDOB).success.value
          .set(ReporterIndividualPlaceOfBirthPage, "SomePlace").success.value
          .set(ReporterIndividualAddressPage, address).success.value
          .set(ReporterIndividualEmailAddressPage, "email@email.com").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(TaxpayerLoopPage, taxpayersAsOrganisation).success.value

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

        RelevantTaxPayersXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected

        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation" +
        " with additional taxpayers as organisations" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(TaxpayerLoopPage, taxpayersAsOrganisation).success.value

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

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with additional taxpayers as Individuals" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
          .set(ReporterIndividualNamePage, Name("Reporter", "Name")).success.value
          .set(ReporterIndividualDateOfBirthPage, individualDOB).success.value
          .set(ReporterIndividualPlaceOfBirthPage, "SomePlace").success.value
          .set(ReporterIndividualAddressPage, address).success.value
          .set(ReporterIndividualEmailAddressPage, "email@email.com").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(TaxpayerLoopPage, taxpayersAsIndividuals).success.value

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


        RelevantTaxPayersXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation " +
        "with additional taxpayers as Individuals" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
          .set(ReporterOrganisationNamePage, "Reporter name").success.value
          .set(ReporterOrganisationAddressPage, address).success.value
          .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
          .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
          .set(TaxpayerLoopPage, taxpayersAsIndividuals).success.value

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

        RelevantTaxPayersXMLSection.toXml(userAnswers).map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }
    }
  }
}
