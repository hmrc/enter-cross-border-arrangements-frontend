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

import models.individual.Individual

import scala.xml.{Elem, NodeSeq}

object IndividualXMLSection {

  private[xml] def buildIDForIndividual(individual: Individual): Elem = {
    val mandatoryIndividualName =
      <IndividualName><FirstName>{individual.individualName.firstName}</FirstName><LastName>{individual.individualName.secondName}</LastName></IndividualName>

    val mandatoryDOB = <BirthDate>{individual.birthDate}</BirthDate>
    val mandatoryPOB = <BirthPlace>{individual.birthPlace.fold("Unknown")(pob => pob)}</BirthPlace>
    val optionalEmail = individual.emailAddress.fold(NodeSeq.Empty)(email => <EmailAddress>{email}</EmailAddress>)

    val mandatoryResCountryCode: NodeSeq = TaxResidencyXMLSection.buildResCountryCode(individual.taxResidencies.filter(_.country.isDefined))

    val nodeBuffer = new xml.NodeBuffer
    val individualNodes = {
      <Individual>
        {nodeBuffer ++
        mandatoryIndividualName ++
        mandatoryDOB ++
        mandatoryPOB ++
        TaxResidencyXMLSection.buildTINData(individual.taxResidencies) ++
        AddressXMLSection.buildAddress(individual.address) ++
        optionalEmail ++
        mandatoryResCountryCode}
      </Individual>
    }

    <ID>{individualNodes}</ID>
  }
}
