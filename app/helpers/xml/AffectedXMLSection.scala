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

import models.Submission

import scala.util.Try
import scala.xml.{Elem, Node, NodeSeq}

case class AffectedXMLSection(submission: Submission) {

  private[xml] def getAffectedPersons: Seq[Node] =
    Option(submission.affectedPersons) match {
      case Some(affectedList) => affectedList.map {
        affectedPerson =>  if (affectedPerson.individual.isDefined) {
          <AffectedPerson>
            <AffectedPersonID>
              {IndividualXMLSection.buildIDForIndividual(affectedPerson.individual.get) \\ "Individual"}
            </AffectedPersonID>
          </AffectedPerson>
        } else {
          <AffectedPerson>
            <AffectedPersonID>
              {OrganisationXMLSection.buildIDForOrganisation(affectedPerson.organisation.get) \\ "Organisation"}
            </AffectedPersonID>
          </AffectedPerson>
        }
      }
      case _ => NodeSeq.Empty
    }

  def buildAffectedPersons: Either[Throwable, Elem] =
    Try {
      <AffectedPersons>
        {getAffectedPersons}
      </AffectedPersons>
    }.toEither
}

