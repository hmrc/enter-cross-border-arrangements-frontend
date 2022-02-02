/*
 * Copyright 2022 HM Revenue & Customs
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
import models.taxpayer.TaxResidency
import models.{Address, Country, TaxReferenceNumbers}

import scala.xml.PrettyPrinter

class OrganisationXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val address: Address =
    Address(
      Some("value 1"),
      Some("value 2"),
      Some("value 3"),
      "value 4",
      Some("XX9 9XX"),
      Country("valid", "FR", "France")
    )

  val email = "email@email.com"

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  "buildIDForOrganisation" - {

    "must build the ID section for Organisation" in {
      val result = OrganisationXMLSection.buildIDForOrganisation(organisation)

      val expected =
        """<ID>
          |    <Organisation>
          |        <OrganisationName>Taxpayers Ltd</OrganisationName>
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
          |    </Organisation>
          |</ID>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }
  }
}
