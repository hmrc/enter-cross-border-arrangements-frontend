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

package helpers.xml.disclosing

import helpers.xml.{OrganisationXMLSection, RelevantTaxPayersXMLSection, XMLFragmentBuilder}
import models.organisation.Organisation
import models.{CompletionState, NotStarted, UserAnswers}

import scala.util.Try
import scala.xml.NodeSeq

object DiscloseDetailsForOrganisationFragment extends XMLFragmentBuilder {

  def build(userAnswers: UserAnswers): Either[CompletionState, NodeSeq] =

    for {
      // TODO refactor organisation builder to have an Option
      organisationDetailsForReporter <- Try { Organisation.buildOrganisationDetailsForReporter(userAnswers) }.toEither.left.map(_ => NotStarted)
      idForOrganisation              =  OrganisationXMLSection.buildIDForOrganisation(organisationDetailsForReporter)
      liability                      <- DiscloseDetailsLiability.build(userAnswers)
    } yield {
      new xml.NodeBuffer ++
        idForOrganisation ++
        liability
    }

}
