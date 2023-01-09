/*
 * Copyright 2023 HM Revenue & Customs
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

sealed trait YouHaveNotAddedAnyAssociatedEnterprises

object YouHaveNotAddedAnyAssociatedEnterprises extends Enumerable.Implicits {

  case object YesAddNow extends WithName("yesAddNow") with YouHaveNotAddedAnyAssociatedEnterprises
  case object YesAddLater extends WithName("yesAddLater") with YouHaveNotAddedAnyAssociatedEnterprises
  case object No extends WithName("no") with YouHaveNotAddedAnyAssociatedEnterprises

  val values: Seq[YouHaveNotAddedAnyAssociatedEnterprises] = Seq(
    YesAddNow,
    YesAddLater,
    No
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"youHaveNotAddedAnyAssociatedEnterprises.yesAddNow", YesAddNow.toString),
      Radios.Radio(msg"youHaveNotAddedAnyAssociatedEnterprises.yesAddLater", YesAddLater.toString),
      Radios.Radio(msg"youHaveNotAddedAnyAssociatedEnterprises.no", No.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[YouHaveNotAddedAnyAssociatedEnterprises] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
