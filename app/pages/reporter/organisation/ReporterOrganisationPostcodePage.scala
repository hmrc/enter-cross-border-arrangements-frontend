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

package pages.reporter.organisation

import models.reporter.ReporterDetails
import pages.DetailsPage
import play.api.libs.json.JsPath

case object ReporterOrganisationPostcodePage extends DetailsPage[String, ReporterDetails] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reporterOrganisationPostcode"

  override def getFromModel(model: ReporterDetails): Option[String] = model.organisation.flatMap(_.address.flatMap(_.postCode))
}
