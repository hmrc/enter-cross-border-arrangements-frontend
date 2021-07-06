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

import models.organisation.Organisation

import scala.xml.{Elem, NodeSeq}

object OrganisationXMLSection {

  private[xml] def buildIDForOrganisation(organisation: Organisation, isAssociatedEnterprise: Boolean = false): Elem = {
    val mandatoryOrganisationName = <OrganisationName>{organisation.organisationName}</OrganisationName>

    val email = organisation.emailAddress.fold(NodeSeq.Empty)(
      email => <EmailAddress>{email}</EmailAddress>
    )

    val mandatoryResCountryCode: NodeSeq = TaxResidencyXMLSection.buildResCountryCode(organisation.taxResidencies.filter(_.country.isDefined))

    val nodeBuffer = new xml.NodeBuffer
    val organisationNodes =
      <Organisation>
        {
        nodeBuffer ++
          mandatoryOrganisationName ++
          TaxResidencyXMLSection.buildTINData(organisation.taxResidencies) ++
          AddressXMLSection.buildAddress(organisation.address) ++
          email ++
          mandatoryResCountryCode
      }
      </Organisation>

    if (isAssociatedEnterprise) {
      <AssociatedEnterpriseID>{organisationNodes}</AssociatedEnterpriseID>
    } else {
      <ID>{organisationNodes}</ID>
    }
  }
}
