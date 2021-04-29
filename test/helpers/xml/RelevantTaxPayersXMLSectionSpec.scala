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
import helpers.data.ValidUserAnswersForSubmission._
import models.enterprises.AssociatedEnterprise
import models.individual.Individual
import models.reporter.{ReporterDetails, ReporterLiability, RoleInArrangement}
import models.taxpayer.Taxpayer
import models.{Name, Submission}

import scala.xml.PrettyPrinter

class RelevantTaxPayersXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val reporterSection: ReporterXMLSection = mock[ReporterXMLSection]
  val associatedEnterpriseSection: AssociatedEnterprisesXMLSection = mock[AssociatedEnterprisesXMLSection]

  val taxpayersAsOrganisation: IndexedSeq[Taxpayer] = IndexedSeq(
    Taxpayer("123", None, Some(validOrganisation), Some(todayMinusOneMonth)),
    Taxpayer("Another ID", None, Some(validOrganisation.copy(organisationName = "Other Taxpayers Ltd")), Some(todayMinusTwoMonths)))

  val taxpayersAsIndividuals: IndexedSeq[Taxpayer] = IndexedSeq(
    Taxpayer("TP-123", Some(validIndividual), None, Some(todayMinusOneMonth)),
    Taxpayer("TP-1230", Some(validIndividual.copy(individualName = Name("Another", "Individual"))), None, Some(todayMinusTwoMonths)))

  val taxpayerOrganisation: Taxpayer =
    Taxpayer("123", None, Some(validOrganisation), Some(todayMinusOneMonth))

  val taxpayerIndividual: Taxpayer = Taxpayer(
    "TP-123",
    Some(Individual(validIndividualName, Some(validIndividualDOB), Some("SomePlace"), Some(validAddress), Some(validEmail), validTaxResidencies)),
    None, Some(todayMinusOneMonth))

  private val submission: Submission = Submission("id", validDisclosureDetails)

  def toSubmission(reporterDetails: ReporterDetails, taxpayers: IndexedSeq[Taxpayer] = IndexedSeq.empty[Taxpayer]): Submission =
    submission.copy(reporterDetails = Option(reporterDetails), taxpayers = taxpayers)

  def toSubmission(taxpayers: IndexedSeq[Taxpayer]): Submission =
    submission.copy(taxpayers = taxpayers)

  "RelevantTaxPayersXMLSection" - {

    "toXml" - {

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with additional taxpayers as organisations" in {

        val reporterDetails = ReporterDetails(
          Some(validIndividual),
          None,
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(validToday))))

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
             |        <TaxpayerImplementingDate>${validToday}</TaxpayerImplementingDate>
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

        RelevantTaxPayersXMLSection(toSubmission(reporterDetails, taxpayersAsOrganisation)).buildRelevantTaxpayers.map { result =>

          prettyPrinter.format(result) mustBe expected

        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation" +
        " with additional taxpayers as organisations" in {

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(validToday))))

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
             |        <TaxpayerImplementingDate>${validToday}</TaxpayerImplementingDate>
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

        RelevantTaxPayersXMLSection(toSubmission(reporterDetails, taxpayersAsOrganisation)).buildRelevantTaxpayers.map { result =>

          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation" +
        " with associated enterprise section if reporter selected in associated enterprise journey" in {

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
             |        <TaxpayerImplementingDate>${validToday}</TaxpayerImplementingDate>
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
             |                <AffectedPerson>false</AffectedPerson>
             |            </AssociatedEnterprise>
             |        </AssociatedEnterprises>
             |    </RelevantTaxpayer>
             |</RelevantTaxPayers>""".stripMargin


        val enterpriseLoop = IndexedSeq(
          AssociatedEnterprise("id", Some(validIndividual), None, List(validOrganisation.organisationName), isAffectedBy = false))

        val reporterDetails = ReporterDetails(
          None,
          Some(validOrganisation),
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(validToday))))

        def toSubmission(reporterDetails: ReporterDetails, associatedEnterprise: IndexedSeq[AssociatedEnterprise]): Submission =
          submission.copy(reporterDetails = Option(reporterDetails), associatedEnterprises = associatedEnterprise)

          RelevantTaxPayersXMLSection(toSubmission(reporterDetails, enterpriseLoop)).buildRelevantTaxpayers.map { result =>
            prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with additional taxpayers as Individuals" in {

        val reporterDetails = ReporterDetails(
          Some(validIndividual),
          None,
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(validToday))))

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
             |        <TaxpayerImplementingDate>${validToday}</TaxpayerImplementingDate>
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

          RelevantTaxPayersXMLSection(toSubmission(reporterDetails, taxpayersAsIndividuals)).buildRelevantTaxpayers.map { result =>
          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Individual" +
        " with associated enterprise section if reporter selected in associated enterprise journey" +
        "and with additional taxpayers as individuals" in {

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
             |        <TaxpayerImplementingDate>${validToday}</TaxpayerImplementingDate>
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
             |                <AffectedPerson>false</AffectedPerson>
             |            </AssociatedEnterprise>
             |        </AssociatedEnterprises>
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
             |</RelevantTaxPayers>""".stripMargin

        val enterpriseLoop = IndexedSeq(
          AssociatedEnterprise("id", Some(validIndividual), None, List(taxpayerIndividual.nameAsString), isAffectedBy = false))

        val reporterDetails = ReporterDetails(
          Some(validIndividual),
          None,
          Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(validToday))))

        def toSubmission(reporterDetails: ReporterDetails,
                         taxpayer: IndexedSeq[Taxpayer],
                         associatedEnterprise: IndexedSeq[AssociatedEnterprise]): Submission =
          submission.copy(reporterDetails = Option(reporterDetails), taxpayers = taxpayer, associatedEnterprises = associatedEnterprise)

        RelevantTaxPayersXMLSection(toSubmission(reporterDetails, IndexedSeq(taxpayerIndividual), enterpriseLoop)).buildRelevantTaxpayers.map { result =>
          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML when Reporter is an Organisation " +
        "with additional taxpayers as Individuals" in {

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
             |        <TaxpayerImplementingDate>${validToday}</TaxpayerImplementingDate>
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

        val reporterDetails = ReporterDetails(None, Some(validOrganisation), Some(ReporterLiability(RoleInArrangement.Taxpayer.toString,
            None, None, None, None, Some(validToday))))

        RelevantTaxPayersXMLSection(toSubmission(reporterDetails, taxpayersAsIndividuals)).buildRelevantTaxpayers.map { result =>
          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML with additional taxpayers as organisations and " +
        "their associated enterprise - one organisation" in {

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

        val enterpriseLoop: IndexedSeq[AssociatedEnterprise] = IndexedSeq(
          AssociatedEnterprise("id", None, Some(validOrganisation), List(taxpayersAsOrganisation(1).taxpayerId), isAffectedBy = false))

        val updatedSubmission: Submission = submission.copy(taxpayers = taxpayersAsOrganisation, associatedEnterprises = enterpriseLoop)

        RelevantTaxPayersXMLSection(updatedSubmission).buildRelevantTaxpayers.map { result =>
          prettyPrinter.format(result) mustBe expected
        }
      }

      "must build a complete RelevantTaxPayers XML with additional taxpayers as Individuals and " +
        "their associated enterprises - one individual and one organisation" in {

        val enterpriseLoop: IndexedSeq[AssociatedEnterprise] = IndexedSeq(
          AssociatedEnterprise("id", Some(validIndividual), None, List(taxpayerIndividual.taxpayerId), isAffectedBy = true),
          AssociatedEnterprise("id2", None, Some(validOrganisation), List(taxpayerIndividual.taxpayerId), isAffectedBy = true))

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

        val updatedSubmission = submission.copy(taxpayers = taxpayersAsIndividuals, associatedEnterprises = enterpriseLoop)

        RelevantTaxPayersXMLSection(updatedSubmission).buildRelevantTaxpayers.map { result =>
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
            None, None, None, None, Some(validToday))))

        val submission = Submission("id", validDisclosureDetails, Some(reporterDetails))

        val result = RelevantTaxPayersXMLSection(submission).buildReporterAsTaxpayer

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
             |    <TaxpayerImplementingDate>$validToday</TaxpayerImplementingDate>
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

        val submission = Submission("id", validDisclosureDetails, Some(reporterDetails))

        val result = RelevantTaxPayersXMLSection(submission).buildReporterAsTaxpayer

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
            None, None, None, None, Some(validToday))))

        val submission = Submission("id", validDisclosureDetails, Some(reporterDetails))

        val result = RelevantTaxPayersXMLSection(submission).buildReporterAsTaxpayer

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
             |    <TaxpayerImplementingDate>$validToday</TaxpayerImplementingDate>
             |</RelevantTaxpayer>""".stripMargin

        prettyPrinter.formatNodes(result) mustBe expected
      }
    }
  }
}
