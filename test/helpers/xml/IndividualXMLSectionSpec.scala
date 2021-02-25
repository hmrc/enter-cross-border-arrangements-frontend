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
import models.individual.Individual
import models.taxpayer.TaxResidency
import models.{Address, Country, Name, TaxReferenceNumbers}

import java.time.LocalDate
import scala.xml.PrettyPrinter

class IndividualXMLSectionSpec extends SpecBase {

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

  "buildIDForIndividual" - {

    "must build the ID section for Individuals" in {
      val result = IndividualXMLSection.buildIDForIndividual(individual)

      val expected =
        """<ID>
          |    <Individual>
          |        <IndividualName>
          |            <FirstName>FirstName</FirstName>
          |            <LastName>Surname</LastName>
          |        </IndividualName>
          |        <BirthDate>1990-01-01</BirthDate>
          |        <BirthPlace>SomePlace</BirthPlace>
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
          |    </Individual>
          |</ID>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }
  }
}
