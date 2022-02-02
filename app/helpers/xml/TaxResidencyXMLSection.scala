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

import models.taxpayer.TaxResidency

import scala.xml.NodeSeq

object TaxResidencyXMLSection {

  private[xml] def buildTINData(taxResidencies: IndexedSeq[TaxResidency]): NodeSeq =
    taxResidencies.flatMap {
      loop =>
        if (loop.country.isDefined && loop.taxReferenceNumbers.isDefined) {
          val countryCode = loop.country.get.code

          Seq(
            Some(<TIN issuedBy={countryCode}>{loop.taxReferenceNumbers.get.firstTaxNumber}</TIN>),
            loop.taxReferenceNumbers.get.secondTaxNumber.map(
              taxNumber => <TIN issuedBy={countryCode}>{taxNumber}</TIN>
            ),
            loop.taxReferenceNumbers.get.thirdTaxNumber.map(
              taxNumber => <TIN issuedBy={countryCode}>{taxNumber}</TIN>
            )
          ).filter(_.isDefined).map(_.get)
        } else {
          NodeSeq.Empty
        }
    }

  private[xml] def buildResCountryCode(taxResidencies: IndexedSeq[TaxResidency]): NodeSeq =
    taxResidencies.flatMap {
      taxResidency =>
        if (taxResidency.country.isDefined) {
          <ResCountryCode>{taxResidency.country.get.code}</ResCountryCode>
        } else {
          throw new Exception("Unable to build Relevant taxpayers section due to missing mandatory resident country/countries.")
        }
    }
}
