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
import uk.gov.hmrc.viewmodels._

sealed trait AssociatedEnterpriseType

object AssociatedEnterpriseType extends Enumerable.Implicits {
  case object Organisation extends WithName("organisation") with AssociatedEnterpriseType
  case object Individual extends WithName("individual") with AssociatedEnterpriseType

  val values: Seq[AssociatedEnterpriseType] = Seq(
    Organisation,
    Individual
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {
    val field = form("associatedEnterpriseType")
    Seq(
      Radios.Item("business",msg"associatedEnterpriseType.organisation", Organisation.toString, field.values.contains(Organisation.toString)),
      Radios.Item("individual",msg"associatedEnterpriseType.individual", Individual.toString, field.values.contains(Individual.toString))
    )
  }

  implicit val enumerable: Enumerable[AssociatedEnterpriseType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
