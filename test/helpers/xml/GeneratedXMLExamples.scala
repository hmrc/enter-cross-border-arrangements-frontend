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

import org.joda.time.DateTime

import java.time.LocalDate

object GeneratedXMLExamples {

  private val mandatoryTimestamp: String = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")
  private val today: LocalDate = LocalDate.now
  private val todayMinusOneMonth: LocalDate = LocalDate.now.minusMonths(1)
  private val todayMinusTwoMonths: LocalDate = LocalDate.now.minusMonths(2)

  val xmlForOrganisation: String =
    s"""<DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
       |    <Header>
       |        <MessageRefId>GBXADAC0001122345DisclosureName</MessageRefId>
       |        <Timestamp>$mandatoryTimestamp</Timestamp>
       |    </Header>
       |    <DAC6Disclosures>
       |        <DisclosureImportInstruction>DAC6NEW</DisclosureImportInstruction>
       |        <Disclosing>
       |            <ID>
       |                <Organisation>
       |                    <OrganisationName>Reporter name</OrganisationName>
       |                    <TIN issuedBy="GB">1234567890</TIN>
       |                    <TIN issuedBy="GB">0987654321</TIN>
       |                    <Address>
       |                        <Street>value 1</Street>
       |                        <BuildingIdentifier>value 2</BuildingIdentifier>
       |                        <DistrictName>value 3</DistrictName>
       |                        <PostCode>XX9 9XX</PostCode>
       |                        <City>value 4</City>
       |                        <Country>FR</Country>
       |                    </Address>
       |                    <EmailAddress>email@email.co.uk</EmailAddress>
       |                    <ResCountryCode>GB</ResCountryCode>
       |                    <ResCountryCode>FR</ResCountryCode>
       |                </Organisation>
       |            </ID>
       |            <Liability>
       |                <RelevantTaxpayerDiscloser>
       |                    <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
       |                </RelevantTaxpayerDiscloser>
       |            </Liability>
       |        </Disclosing>
       |        <InitialDisclosureMA>true</InitialDisclosureMA>
       |        <RelevantTaxPayers>
       |            <RelevantTaxpayer>
       |                <ID>
       |                    <Organisation>
       |                        <OrganisationName>Reporter name</OrganisationName>
       |                        <TIN issuedBy="GB">1234567890</TIN>
       |                        <TIN issuedBy="GB">0987654321</TIN>
       |                        <Address>
       |                            <Street>value 1</Street>
       |                            <BuildingIdentifier>value 2</BuildingIdentifier>
       |                            <DistrictName>value 3</DistrictName>
       |                            <PostCode>XX9 9XX</PostCode>
       |                            <City>value 4</City>
       |                            <Country>FR</Country>
       |                        </Address>
       |                        <EmailAddress>email@email.co.uk</EmailAddress>
       |                        <ResCountryCode>GB</ResCountryCode>
       |                        <ResCountryCode>FR</ResCountryCode>
       |                    </Organisation>
       |                </ID>
       |                <TaxpayerImplementingDate>$today</TaxpayerImplementingDate>
       |            </RelevantTaxpayer>
       |            <RelevantTaxpayer>
       |                <ID>
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
       |                </ID>
       |                <TaxpayerImplementingDate>$todayMinusOneMonth</TaxpayerImplementingDate>
       |            </RelevantTaxpayer>
       |            <RelevantTaxpayer>
       |                <ID>
       |                    <Organisation>
       |                        <OrganisationName>Other Taxpayers Ltd</OrganisationName>
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
       |                </ID>
       |                <TaxpayerImplementingDate>$todayMinusTwoMonths</TaxpayerImplementingDate>
       |            </RelevantTaxpayer>
       |        </RelevantTaxPayers>
       |        <Intermediaries> </Intermediaries>
       |        <DisclosureInformation>
       |            <ImplementingDate>$today</ImplementingDate>
       |            <Reason>DAC6703</Reason>
       |            <Summary>
       |                <Disclosure_Name>Arrangement name</Disclosure_Name>
       |                <Disclosure_Description>Some description</Disclosure_Description>
       |            </Summary>
       |            <NationalProvision>National provisions description</NationalProvision>
       |            <Amount currCode="GBP">1000</Amount>
       |            <ConcernedMSs>
       |                <ConcernedMS>GB</ConcernedMS>
       |                <ConcernedMS>FR</ConcernedMS>
       |            </ConcernedMSs>
       |            <MainBenefitTest1>false</MainBenefitTest1>
       |            <Hallmarks>
       |                <ListHallmarks>
       |                    <Hallmark>DAC6D1a</Hallmark>
       |                    <Hallmark>DAC6D1Other</Hallmark>
       |                    <Hallmark>DAC6D2</Hallmark>
       |                </ListHallmarks>
       |                <DAC6D1OtherInfo>Hallmark D1 other description</DAC6D1OtherInfo>
       |            </Hallmarks>
       |        </DisclosureInformation>
       |    </DAC6Disclosures>
       |</DAC6_Arrangement>""".stripMargin

  val xmlForIndividual: String =
    s"""<DAC6_Arrangement version="First" xmlns="urn:ukdac6:v0.1">
       |    <Header>
       |        <MessageRefId>GBXADAC0001122345DisclosureName</MessageRefId>
       |        <Timestamp>$mandatoryTimestamp</Timestamp>
       |    </Header>
       |    <DAC6Disclosures>
       |        <DisclosureImportInstruction>DAC6NEW</DisclosureImportInstruction>
       |        <Disclosing>
       |            <ID>
       |                <Individual>
       |                    <IndividualName>
       |                        <FirstName>Reporter</FirstName>
       |                        <LastName>Name</LastName>
       |                    </IndividualName>
       |                    <BirthDate>1990-01-01</BirthDate>
       |                    <BirthPlace>SomePlace</BirthPlace>
       |                    <TIN issuedBy="GB">1234567890</TIN>
       |                    <TIN issuedBy="GB">0987654321</TIN>
       |                    <Address>
       |                        <Street>value 1</Street>
       |                        <BuildingIdentifier>value 2</BuildingIdentifier>
       |                        <DistrictName>value 3</DistrictName>
       |                        <PostCode>XX9 9XX</PostCode>
       |                        <City>value 4</City>
       |                        <Country>FR</Country>
       |                    </Address>
       |                    <EmailAddress>email@email.com</EmailAddress>
       |                    <ResCountryCode>GB</ResCountryCode>
       |                    <ResCountryCode>FR</ResCountryCode>
       |                </Individual>
       |            </ID>
       |            <Liability>
       |                <RelevantTaxpayerDiscloser>
       |                    <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
       |                </RelevantTaxpayerDiscloser>
       |            </Liability>
       |        </Disclosing>
       |        <InitialDisclosureMA>true</InitialDisclosureMA>
       |        <RelevantTaxPayers>
       |            <RelevantTaxpayer>
       |                <ID>
       |                    <Individual>
       |                        <IndividualName>
       |                            <FirstName>Reporter</FirstName>
       |                            <LastName>Name</LastName>
       |                        </IndividualName>
       |                        <BirthDate>1990-01-01</BirthDate>
       |                        <BirthPlace>SomePlace</BirthPlace>
       |                        <TIN issuedBy="GB">1234567890</TIN>
       |                        <TIN issuedBy="GB">0987654321</TIN>
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
       |                </ID>
       |                <TaxpayerImplementingDate>$today</TaxpayerImplementingDate>
       |            </RelevantTaxpayer>
       |            <RelevantTaxpayer>
       |                <ID>
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
       |                </ID>
       |                <TaxpayerImplementingDate>$todayMinusOneMonth</TaxpayerImplementingDate>
       |            </RelevantTaxpayer>
       |            <RelevantTaxpayer>
       |                <ID>
       |                    <Organisation>
       |                        <OrganisationName>Other Taxpayers Ltd</OrganisationName>
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
       |                </ID>
       |                <TaxpayerImplementingDate>$todayMinusTwoMonths</TaxpayerImplementingDate>
       |            </RelevantTaxpayer>
       |        </RelevantTaxPayers>
       |        <Intermediaries> </Intermediaries>
       |        <DisclosureInformation>
       |            <ImplementingDate>$today</ImplementingDate>
       |            <Reason>DAC6703</Reason>
       |            <Summary>
       |                <Disclosure_Name>Arrangement name</Disclosure_Name>
       |                <Disclosure_Description>Some description</Disclosure_Description>
       |            </Summary>
       |            <NationalProvision>National provisions description</NationalProvision>
       |            <Amount currCode="GBP">1000</Amount>
       |            <ConcernedMSs>
       |                <ConcernedMS>GB</ConcernedMS>
       |                <ConcernedMS>FR</ConcernedMS>
       |            </ConcernedMSs>
       |            <MainBenefitTest1>false</MainBenefitTest1>
       |            <Hallmarks>
       |                <ListHallmarks>
       |                    <Hallmark>DAC6D1a</Hallmark>
       |                    <Hallmark>DAC6D1Other</Hallmark>
       |                    <Hallmark>DAC6D2</Hallmark>
       |                </ListHallmarks>
       |                <DAC6D1OtherInfo>Hallmark D1 other description</DAC6D1OtherInfo>
       |            </Hallmarks>
       |        </DisclosureInformation>
       |    </DAC6Disclosures>
       |</DAC6_Arrangement>""".stripMargin

}
