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

package helpers

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateHelper {

  val dateFormatterDMY: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  val dateFormatterNumericDMY: DateTimeFormatter = DateTimeFormatter.ofPattern("d M yyyy")

  def today: LocalDate = LocalDate.now()
  def yesterday: LocalDate = LocalDate.now().minusDays(1)
  def formatDateToString(date: LocalDate): String = date.format(dateFormatterDMY)
  def dateOfWeek = today.getDayOfWeek

  private val dateFormatterSummaryDate: DateTimeFormatter = DateTimeFormatter.ofPattern(s"h:mma 'on' d MMMM yyyy", Locale.UK)

  def formatSummaryTimeStamp(dateTime: ZonedDateTime): String = {
    val str = dateFormatterSummaryDate.format(dateTime)
    val suffix = str.takeRight(2).toLowerCase
    val prefix = str.take(str.length -2)
    prefix + suffix
  }


}
