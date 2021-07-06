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

package models.intermediaries

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._
import models._

sealed trait YouHaveNotAddedAnyIntermediaries

object YouHaveNotAddedAnyIntermediaries extends Enumerable.Implicits {

  case object YesAddNow extends WithName("yesAddNow") with YouHaveNotAddedAnyIntermediaries
  case object YesAddLater extends WithName("yesAddLater") with YouHaveNotAddedAnyIntermediaries
  case object No extends WithName("no") with YouHaveNotAddedAnyIntermediaries

  val values: Seq[YouHaveNotAddedAnyIntermediaries] = Seq(
    YesAddNow,
    YesAddLater,
    No
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"youHaveNotAddedAnyIntermediaries.yesAddNow", YesAddNow.toString),
      Radios.Radio(msg"youHaveNotAddedAnyIntermediaries.yesAddLater", YesAddLater.toString),
      Radios.Radio(msg"youHaveNotAddedAnyIntermediaries.no", No.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[YouHaveNotAddedAnyIntermediaries] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
