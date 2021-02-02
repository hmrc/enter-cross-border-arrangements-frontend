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
import models.UserAnswers
import pages.enterprises.AssociatedEnterpriseLoopPage

import scala.util.Try
import scala.xml.{Elem, NodeSeq}

object AssociatedEnterprisesSection extends XMLBuilder {

  private[xml] def buildAssociatedEnterprises(userAnswers: UserAnswers, id: Int): NodeSeq = {
    userAnswers.get(AssociatedEnterpriseLoopPage, id) match {
      case Some(associatedEnterprises) =>
        associatedEnterprises.map {
          associatedEnterprise =>
            if(associatedEnterprise.individual.isDefined) {
              <AssociatedEnterprise>
                {IndividualXMLSection.buildIDForIndividual(associatedEnterprise.individual.get, isAssociatedEnterprise = true)}
              </AssociatedEnterprise>
            } else {
              <AssociatedEnterprise>
                {OrganisationXMLSection.buildIDForOrganisation(associatedEnterprise.organisation.get, isAssociatedEnterprise = true)}
              </AssociatedEnterprise>
            }
        }
      case None => NodeSeq.Empty
    }
  }

  override def toXml(userAnswers: UserAnswers, id: Int): Either[Throwable, Elem] = {
    Try {
      <AssociatedEnterprises>
        {buildAssociatedEnterprises(userAnswers, id)}
      </AssociatedEnterprises>
    }.toEither
  }
}
