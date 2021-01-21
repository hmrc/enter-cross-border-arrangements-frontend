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

import helpers.xml.{IndividualXMLSection, XMLFragmentBuilder}
import models.individual.Individual
import models.{JourneyStatus, NotStarted, UserAnswers}

import scala.util.Try
import scala.xml.NodeSeq

object DiscloseDetailsForIndividualFragment extends XMLFragmentBuilder {

  def build(userAnswers: UserAnswers): Either[JourneyStatus, NodeSeq] =

    for {
      individualDetailsForReporter <- Try { Individual.buildIndividualDetailsForReporter(userAnswers) }.toEither.left.map(_ => NotStarted)
      idForIndividual              =  IndividualXMLSection.buildIDForIndividual(individualDetailsForReporter)
      liability                    <- DiscloseDetailsLiability.build(userAnswers)
    } yield {
      new xml.NodeBuffer ++
        idForIndividual ++
        liability
    }

}
