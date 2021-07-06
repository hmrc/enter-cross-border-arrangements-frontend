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

package utils

import base.SpecBase
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import utils.SummaryListDisplay.DisplayRow

class SummaryListDisplaySpec extends SpecBase {

  "SummaryListDisplay" - {
    val displayRow  = DisplayRow(Key(Literal("row1"), List("govuk-!-width-two-thirds")), Value(Literal("Row1Content")), Seq("govuk-summary-list--no-border"))
    val displayRow2 = DisplayRow(Key(Literal("row2"), List("govuk-!-width-two-thirds")), Value(Literal("Row2Content")), Seq("govuk-summary-list--no-border"))

    "must convert a row into a display row with rowToDisplayRow" in {
      val row = Row(Key(Literal("row1")), Value(Literal("Row1Content")), Seq(Action(Literal("action"), "http://localhost")))
      SummaryListDisplay.rowToDisplayRow(row) mustBe DisplayRow(Key(Literal("row1"), List("govuk-!-width-two-thirds")), Value(Literal("Row1Content")))
    }
    "must remove formatting from a Displayrow with removeClassFromDisplayRow" in {

      SummaryListDisplay.removeClassFromDisplayRow(displayRow) mustBe DisplayRow(Key(Literal("row1"), List("govuk-!-width-two-thirds")),
                                                                                 Value(Literal("Row1Content")),
                                                                                 Seq.empty[String]
      )
    }
    "must remove the formatting for the last element in a seq of DisplayRow with removeClassesFromLastElementInSeq" in {
      val rows                         = Seq(displayRow, displayRow2)
      val displayRow2WithOutFormatting = DisplayRow(Key(Literal("row2"), List("govuk-!-width-two-thirds")), Value(Literal("Row2Content")), Seq.empty[String])
      SummaryListDisplay.removeClassesFromLastElementInSeq(rows) mustBe Seq(displayRow, displayRow2WithOutFormatting)
    }
  }
}
