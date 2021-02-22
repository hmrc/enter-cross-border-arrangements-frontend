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
import helpers.data.ValidUserAnswersForSubmission.validDisclosureDetails
import models.affected.Affected
import models.individual.Individual
import models.{Address, Country, Name, Submission, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import pages.affected.AffectedLoopPage
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage

import java.time.LocalDate
import scala.xml.{Elem, PrettyPrinter}

class AffectedXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val address: Address =
    Address(
      Some("value 1"),
      Some("value 2"),
      Some("value 3"),
      "value 4",
      Some("XX9 9XX"),
      Country("", "GB", "United Kingdom")
    )

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None)))
  )

  val email = "email@email.com"
  val individualName: Name = Name("FirstName", "Surname")
  val individualDOB: LocalDate = LocalDate.of(1990, 1,1)
  val individual: Individual = Individual(individualName, individualDOB, Some("SomePlace"), Some(address), Some(email), taxResidencies)

  val organisation: Organisation = Organisation(
    organisationName = "Organisation Ltd.",
    address = Some(address),
    emailAddress = Some(email),
    taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), None))
  )

  private def toSubmission(affectedPersons: IndexedSeq[Affected]) = Submission("id", validDisclosureDetails).copy(affectedPersons = affectedPersons)

  "affectedPersons" - {

    "must build AffectedPerson section for individual" in {
      val affectedLoop = IndexedSeq(Affected("id", Some(individual), None))
      val updatedSubmission = toSubmission(affectedPersons = affectedLoop)

      val expected: Elem =
        <AffectedPersons>
           	<AffectedPerson>
           		<AffectedPersonID>
                 <Individual>
                           <IndividualName>
                               <FirstName>FirstName</FirstName>
                               <LastName>Surname</LastName>
                           </IndividualName>
                           <BirthDate>1990-01-01</BirthDate>
                           <BirthPlace>SomePlace</BirthPlace>
                           <TIN issuedBy="GB">UTR1234</TIN>
                           <Address>
                               <Street>value 1</Street>
                               <BuildingIdentifier>value 2</BuildingIdentifier>
                               <DistrictName>value 3</DistrictName>
                               <PostCode>XX9 9XX</PostCode>
                               <City>value 4</City>
                               <Country>GB</Country>
                           </Address>
                           <EmailAddress>email@email.com</EmailAddress>
                           <ResCountryCode>GB</ResCountryCode>
                       </Individual>
           		</AffectedPersonID>
           	</AffectedPerson>
           </AffectedPersons>

      AffectedXMLSection(updatedSubmission).buildAffectedPersons.map { result =>
           prettyPrinter.formatNodes(result) mustBe prettyPrinter.formatNodes(expected)
      }

    }

    "must build AffectedPerson section for organisation" in {

      val affectedLoop = IndexedSeq(Affected("id", None, Some(organisation)))
      val updatedSubmission = toSubmission(affectedPersons = affectedLoop)

      val expected =
        <AffectedPersons>
           	<AffectedPerson>
           		<AffectedPersonID>
           			<Organisation>
                           <OrganisationName>Organisation Ltd.</OrganisationName>
                           <Address>
                               <Street>value 1</Street>
                               <BuildingIdentifier>value 2</BuildingIdentifier>
                               <DistrictName>value 3</DistrictName>
                               <PostCode>XX9 9XX</PostCode>
                               <City>value 4</City>
                               <Country>GB</Country>
                           </Address>
                           <EmailAddress>email@email.com</EmailAddress>
                           <ResCountryCode>GB</ResCountryCode>
                       </Organisation>
           		</AffectedPersonID>
           	</AffectedPerson>
           </AffectedPersons>

      AffectedXMLSection(updatedSubmission).buildAffectedPersons.map { result =>
        prettyPrinter.formatNodes(result) mustBe prettyPrinter.formatNodes(expected)
      }
    }

    "must build AffectedPerson section for organisation and individual" in {

      val affectedLoop = IndexedSeq(Affected("id", None, Some(organisation)), Affected("id",Some(individual),None))
      val updatedSubmission = toSubmission(affectedPersons = affectedLoop)

      val expected =
        <AffectedPersons>
           	<AffectedPerson>
           		<AffectedPersonID>
           			<Organisation>
                           <OrganisationName>Organisation Ltd.</OrganisationName>
                           <Address>
                               <Street>value 1</Street>
                               <BuildingIdentifier>value 2</BuildingIdentifier>
                               <DistrictName>value 3</DistrictName>
                               <PostCode>XX9 9XX</PostCode>
                               <City>value 4</City>
                               <Country>GB</Country>
                           </Address>
                           <EmailAddress>email@email.com</EmailAddress>
                           <ResCountryCode>GB</ResCountryCode>
                       </Organisation>
           		</AffectedPersonID>
           	</AffectedPerson>
          <AffectedPerson>
            <AffectedPersonID>
          <Individual>
            <IndividualName>
              <FirstName>FirstName</FirstName>
              <LastName>Surname</LastName>
            </IndividualName>
            <BirthDate>1990-01-01</BirthDate>
            <BirthPlace>SomePlace</BirthPlace>
            <TIN issuedBy="GB">UTR1234</TIN>
            <Address>
              <Street>value 1</Street>
              <BuildingIdentifier>value 2</BuildingIdentifier>
              <DistrictName>value 3</DistrictName>
              <PostCode>XX9 9XX</PostCode>
              <City>value 4</City>
              <Country>GB</Country>
            </Address>
            <EmailAddress>email@email.com</EmailAddress>
            <ResCountryCode>GB</ResCountryCode>
          </Individual>
            </AffectedPersonID>
          </AffectedPerson>
           </AffectedPersons>

      AffectedXMLSection(updatedSubmission).buildAffectedPersons.map { result =>
        prettyPrinter.formatNodes(result) mustBe prettyPrinter.formatNodes(expected)
      }
    }
  }
}
