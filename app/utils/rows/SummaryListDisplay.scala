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

package utils.rows

import play.api.i18n.Messages
import play.api.libs.json.{OWrites, __}
import uk.gov.hmrc.viewmodels.SummaryList.{Key, Value}
import play.api.libs.functional.syntax._

object SummaryListDisplay {

  final case class DisplayRow(key: Key, value: Value)

  object DisplayRow {

    implicit def writes(implicit messages: Messages): OWrites[DisplayRow] = (
      (__ \ "key").write[Key] and
        (__ \ "value").write[Value]
      ){ row =>
         (row.key, row.value)
    }
  }
}
