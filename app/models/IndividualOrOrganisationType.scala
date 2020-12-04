/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.{MessageInterpolators, Radios}

sealed trait IndividualOrOrganisationType

object IndividualOrOrganisationType extends Enumerable.Implicits {

  case object Individual extends WithName("individual") with IndividualOrOrganisationType
  case object Organisation extends WithName("organisation") with IndividualOrOrganisationType

  val values: Seq[IndividualOrOrganisationType] = Seq(
    Individual,
    Organisation
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("IDType")
    val items = Seq(
      Radios.Radio(msg"IDType.individual", Individual.toString),
      Radios.Radio(msg"IDType.organisation", Organisation.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[IndividualOrOrganisationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
