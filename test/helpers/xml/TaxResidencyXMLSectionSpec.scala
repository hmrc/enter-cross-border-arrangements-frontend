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
import models.taxpayer.TaxResidency
import models.{Country, TaxReferenceNumbers}

import scala.xml.PrettyPrinter

class TaxResidencyXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  "buildTINData" - {

    "buildTINData must build a sequence of optional tax residencies" in {
      val result = TaxResidencyXMLSection.buildTINData(taxResidencies)

      val expected =
        """<TIN issuedBy="GB">UTR1234</TIN><TIN issuedBy="FR">CS700100A</TIN><TIN issuedBy="FR">UTR5678</TIN>"""

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildTINData must not build a sequence of optional tax residencies" in {
      val result = TaxResidencyXMLSection.buildTINData(IndexedSeq())

      prettyPrinter.formatNodes(result) mustBe ""
    }
  }

  "buildResCountryCode" - {

    "buildResCountryCode must build a sequence of resident countries" in {
      val result = TaxResidencyXMLSection.buildResCountryCode(taxResidencies)

      val expected = """<ResCountryCode>GB</ResCountryCode><ResCountryCode>FR</ResCountryCode>"""

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildResCountryCode must throw an exception if resident countries are missing" in {
      val taxResidencies = IndexedSeq(
        TaxResidency(None, Some(TaxReferenceNumbers("UTR1234", None, None)))
      )

      assertThrows[Exception] {
        TaxResidencyXMLSection.buildResCountryCode(taxResidencies)
      }
    }
  }
}
