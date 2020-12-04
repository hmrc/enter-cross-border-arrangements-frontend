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

package models.enterprises

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait SelectAnyTaxpayersThisEnterpriseIsAssociatedWith

object SelectAnyTaxpayersThisEnterpriseIsAssociatedWith extends Enumerable.Implicits {

  case object One extends WithName("one") with SelectAnyTaxpayersThisEnterpriseIsAssociatedWith
  case object Option2 extends WithName("option2") with SelectAnyTaxpayersThisEnterpriseIsAssociatedWith

  val values: Seq[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith] = Seq(
    One,
    Option2
  )

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"selectAnyTaxpayersThisEnterpriseIsAssociatedWith.one", One.toString),
      Checkboxes.Checkbox(msg"selectAnyTaxpayersThisEnterpriseIsAssociatedWith.option2", Option2.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
