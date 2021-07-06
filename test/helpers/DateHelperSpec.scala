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

import base.SpecBase

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

class DateHelperSpec extends SpecBase {

  private def generateDate(hour: Int): ZonedDateTime =
    ZonedDateTime.of(2021, 1, 1, hour, 1, 0, 0, ZoneId.of("Europe/London"))

  "formatSummaryTimeStamp" - {

    "should display correct timestamp for morning(AM)" in {

      val result = DateHelper.getSummaryTimestamp(generateDate(4))
      result mustBe s"4:01AM on Friday 1 January 2021"
    }

    "should display correct timestamp for afternoon(PM)" in {

      val result = DateHelper.getSummaryTimestamp(generateDate(16))
      result mustBe s"4:01PM on Friday 1 January 2021"
    }
  }

  "formatXMLTimeStamp" - {

    "should display correct timestamp for XML" in {

      val result = DateHelper.formatXMLTimeStamp(LocalDateTime.of(2021, 1, 1, 1, 1, 1, 1))
      result mustBe s"2021-01-01T01:01:01"
    }
  }
}
