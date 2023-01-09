/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{Address, Country}

import scala.xml.PrettyPrinter

class AddressXMLSectionSpec extends SpecBase {

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

  "buildAddress" - {

    "must build the optional address section" in {
      val result = AddressXMLSection.buildAddress(Some(address))

      val expected =
        """<Address>
          |    <Street>value 1</Street>
          |    <BuildingIdentifier>value 2</BuildingIdentifier>
          |    <DistrictName>value 3</DistrictName>
          |    <PostCode>XX9 9XX</PostCode>
          |    <City>value 4</City>
          |    <Country>FR</Country>
          |</Address>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "must build the optional address section with only the mandatory fields" in {
      val result = AddressXMLSection.buildAddress(Some(Address(None, None, None, "City", None, Country("valid", "FR", "France"))))

      val expected =
        """<Address>
          |    <City>City</City>
          |    <Country>FR</Country>
          |</Address>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "must not build the optional address section if it's missing" in {
      val result = AddressXMLSection.buildAddress(None)

      prettyPrinter.formatNodes(result) mustBe ""
    }
  }
}
