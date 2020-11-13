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

package utils.rows

import java.time.format.DateTimeFormatter

import models.{Address, UserAnswers}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.{Content, Html, MessageInterpolators}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

trait RowBuilder {

  implicit val messages: Messages
  val userAnswers: UserAnswers
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private[utils] def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  private[utils] def toRow(msgKey: String, content: Content, href: String)(implicit messages: Messages): Row = {
    val message = MessageInterpolators(StringContext.apply(s"$msgKey.checkYourAnswersLabel")).msg()
    Row(
      key     = Key(message, classes = Seq("govuk-!-width-one-half")),
      value   = Value(content),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = href,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(message))
        )
      )
    )
  }

  private[utils] def formatAddress(answer: Address): Html = {
    Html(s"""
        ${answer.addressLine1.fold("")(address => s"$address<br>")}
        ${answer.addressLine2.fold("")(address => s"$address<br>")}
        ${answer.addressLine3.fold("")(address => s"$address<br>")}
        ${answer.postCode.fold("")(postcode => s"$postcode<br>")}
        ${answer.country.description}
     """)
  }
}
