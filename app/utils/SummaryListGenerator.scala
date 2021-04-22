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

import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.viewmodels.SummaryList.Row
import utils.rows.SummaryListDisplay
import utils.rows.SummaryListDisplay.DisplayRow


import javax.inject.Inject

class SummaryListGenerator @Inject()(val controllerComponents: MessagesControllerComponents) {
  implicit def messagesApi: MessagesApi = controllerComponents.messagesApi

  def generateSummaryList[A](id: Int, dac6Data: A)(implicit converter: (Int, A) => Seq[Row]):
    Seq[SummaryListDisplay.DisplayRow] = converter(id, dac6Data).map(rowToDisplayRow)

  def generateCYAList[A](id: Int, dac6Data: A)(implicit converter: (Int, A) => Seq[Row]): Seq[Row] = converter(id, dac6Data)

  def generateSummaryListByImplicitParameter[A](id: Int, dac6Data: A)(implicit converter: CreateDisplayRows[A], messages: Messages): Seq[Row]
      = converter.createDisplayRows(id, dac6Data)

  def rowToDisplayRow(row: Row): DisplayRow = DisplayRow(row.key, row.value)
}
