/*
 * Copyright 2022 HM Revenue & Customs
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

package helpers

import java.time.format.{DateTimeFormatter, TextStyle}
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import java.util.Locale;

object DateHelper {

  def today: LocalDate             = LocalDate.now()
  def yesterday: LocalDate         = LocalDate.now().minusDays(1)
  def todayDateTime: LocalDateTime = LocalDateTime.now()

  val dateFormatterNumericDMY: DateTimeFormatter = DateTimeFormatter.ofPattern("d M yyyy")

  val dateFormatterDMY: DateTimeFormatter         = DateTimeFormatter.ofPattern("d MMMM yyyy")
  def formatDateToString(date: LocalDate): String = date.format(dateFormatterDMY)

  val dateFormatterForXML: DateTimeFormatter          = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")
  def formatXMLTimeStamp(date: LocalDateTime): String = date.format(dateFormatterForXML)

  //Locale US so that AM/PM appear in capitals after JDK 11 upgrade

  private def summaryTimestampFormatter(dayOfWeek: String): DateTimeFormatter =
    DateTimeFormatter.ofPattern(s"h:mma 'on' '$dayOfWeek' d MMMM yyyy", Locale.US)

  def getSummaryTimestamp(dateTime: ZonedDateTime): String = {
    val str    = summaryTimestampFormatter(dateTime.getDayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)).format(dateTime)
    val suffix = str.takeRight(2)
    val prefix = str.take(str.length - 2)
    prefix + suffix
  }
}
