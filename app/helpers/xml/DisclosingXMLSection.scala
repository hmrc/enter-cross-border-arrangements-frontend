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

import helpers.xml.disclosing.{DiscloseDetailsForIndividualFragment, DiscloseDetailsForOrganisationFragment}
import models.{JourneyStatus, NotStarted, ReporterOrganisationOrIndividual, UserAnswers}
import pages.reporter.ReporterOrganisationOrIndividualPage

import scala.xml.{Elem, NodeSeq}

object DisclosingXMLSection extends XMLBuilder {

  override def toXml(userAnswers: UserAnswers): Either[JourneyStatus, Elem] = {

    val content: Either[JourneyStatus, NodeSeq] = userAnswers.get(ReporterOrganisationOrIndividualPage).toRight(NotStarted) flatMap {
      case ReporterOrganisationOrIndividual.Organisation => DiscloseDetailsForOrganisationFragment.build(userAnswers)
      case ReporterOrganisationOrIndividual.Individual   => DiscloseDetailsForIndividualFragment.build(userAnswers)
    }

    build(content) { nodes =>
      <Disclosing>{nodes}</Disclosing>
    }

  }

}
