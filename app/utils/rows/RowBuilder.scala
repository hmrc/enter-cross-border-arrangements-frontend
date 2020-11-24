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

import models.{Address, AddressLookup, TaxReferenceNumbers, UserAnswers}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Content, Html, MessageInterpolators, Text}

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

  private[utils] def messageWithPluralFormatter(msgKey: String*)(isPlural: Boolean, argIfPlural: String = "s"
                                                , argIfSingular: String = ""): Text.Message =
    MessageInterpolators(StringContext.apply(msgKey.head)).msg()
      .withArgs(((if (isPlural) argIfPlural else argIfSingular) +: msgKey.tail):_*)

  private[utils] def toRow(msgKey: String, content: Content, href: String)(implicit messages: Messages): Row = {
    val message = MessageInterpolators(StringContext.apply(s"$msgKey.checkYourAnswersLabel")).msg()
    val camelCaseGroups = "(\\b[a-z]+|\\G(?!^))((?:[A-Z]|\\d+)[a-z]*)"
    Row(
      key     = Key(message, classes = Seq("govuk-!-width-one-half")),
      value   = Value(content),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = href,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(message)),
          attributes         = Map("id" -> msgKey.replaceAll(camelCaseGroups, "$1-$2").toLowerCase)
        )
      )
    )
  }

  private[utils] def formatAddress(address: Address): Html = {

    Html(s"""
        ${address.addressLine1.fold("")(address => s"$address<br>")}
        ${address.addressLine2.fold("")(address => s"$address<br>")}
        ${address.addressLine3.fold("")(address => s"$address<br>")}
        ${address.postCode.fold("")(postcode => s"$postcode<br>")}
        ${address.country.description}
     """)
  }

  private[utils] def formatAddress(addressLookup: AddressLookup): Html = {

    Html(s"""
        ${addressLookup.addressLine1.fold("")(address => s"$address<br>")}
        ${addressLookup.addressLine2.fold("")(address => s"$address<br>")}
        ${addressLookup.addressLine3.fold("")(address => s"$address<br>")}
        ${addressLookup.addressLine4.fold("")(address => s"$address<br>")}
        ${s"${addressLookup.town}<br>"}
        ${addressLookup.county.fold("")(county => s"$county<br>")}
        ${addressLookup.postcode}
     """)
  }

  private[utils] def formatReferenceNumbers(referenceNumber: TaxReferenceNumbers): String = {
    val first = referenceNumber.firstTaxNumber
    (referenceNumber.secondTaxNumber, referenceNumber.thirdTaxNumber) match {
      case (Some(second), Some(third)) => s"$first, $second, $third"
      case (Some(second), None) => s"$first, $second"
      case (None, Some(third)) => s"$first, $third"
      case _ => s"$first"
    }
  }

  private[utils] def formatMaxChars(text: String, maxVisibleChars: Int = 100, ellipsis: String = "...") = {
    val label = if (text.length > maxVisibleChars) text.take(maxVisibleChars) + ellipsis else text
    lit"${label}"
  }

}
