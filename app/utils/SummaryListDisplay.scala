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

import play.api.i18n.Messages
import play.api.libs.functional.syntax._
import play.api.libs.json.{__, OWrites}
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Row, Value}

object SummaryListDisplay {

  final case class DisplayRow(key: Key, value: Value, classes: Seq[String] = Seq.empty)

  object DisplayRow {

    implicit def writes(implicit messages: Messages): OWrites[DisplayRow] = (
      (__ \ "classes").writeNullable[String] and
        (__ \ "key").write[Key] and
        (__ \ "value").write[Value]
    ) {
      row =>
        (classes(row.classes), row.key, row.value)
    }
  }

  def rowToDisplayRow(row: Row, columnWidth: String = "govuk-!-width-two-thirds"): DisplayRow = DisplayRow(
    Key(row.key.content, Seq(columnWidth)),
    row.value,
    classes = Seq.empty[String]
  )

  def removeClassFromDisplayRow(row: DisplayRow): DisplayRow = DisplayRow(row.key, row.value)

  def removeClassesFromLastElementInSeq(rows: Seq[DisplayRow]): Seq[DisplayRow] = if (rows.nonEmpty) {
    rows.updated(rows.length - 1, removeClassFromDisplayRow(rows.last))
  } else rows

  private def classes(classes: Seq[String]): Option[String] =
    if (classes.isEmpty) None else Some(classes.mkString(" "))
}
