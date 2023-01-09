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

import models.Address

import scala.xml.NodeSeq

object AddressXMLSection {

  private[xml] def buildAddress(address: Option[Address]): NodeSeq =
    address match {
      case Some(address) =>
        val addressNode = Seq(
          address.addressLine1.map(
            addressLine1 => <Street>{addressLine1}</Street>
          ),
          address.addressLine2.map(
            addressLine2 => <BuildingIdentifier>{addressLine2}</BuildingIdentifier>
          ),
          address.addressLine3.map(
            addressLine3 => <DistrictName>{addressLine3}</DistrictName>
          ),
          address.postCode.map(
            postcode => <PostCode>{postcode}</PostCode>
          ),
          Some(<City>{address.city}</City>),
          Some(<Country>{address.country.code}</Country>)
        ).filter(_.isDefined).map(_.get)

        <Address>{addressNode}</Address>
      case None => NodeSeq.Empty
    }
}
