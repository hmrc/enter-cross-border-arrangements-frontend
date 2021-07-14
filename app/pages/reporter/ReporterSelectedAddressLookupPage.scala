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

package pages.reporter

import models.reporter.ReporterDetails
import models.{AddressLookup, UserAnswers}
import pages.DetailsPage
import pages.reporter.individual.ReporterIndividualAddressPage
import pages.reporter.organisation.ReporterOrganisationAddressPage
import play.api.libs.json.JsPath

import scala.util.Try

object ReporterSelectedAddressLookupPage extends DetailsPage[AddressLookup, ReporterDetails] {
  override def path: JsPath = JsPath \ toString

  override def toString: String = "reporterSelectedAddressLookup"

  override def cleanup(value: Option[AddressLookup], userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    value match {
      case Some(_) =>
        userAnswers
          .remove(ReporterOrganisationAddressPage, id)
          .flatMap(_.remove(ReporterIndividualAddressPage, id))
      case None => super.cleanup(value, userAnswers, id)
    }

  override def getFromModel(model: ReporterDetails): Option[AddressLookup] =
    (model.organisation, model.individual) match {
      case (Some(organisation), None) => organisation.address.map(_.toAddressLookup)
      case (None, Some(individual))   => individual.address.map(_.toAddressLookup)
      case _                          => None
    }
}
