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

import models.arrangement.ArrangementDetails
import models.disclosure.DisclosureDetails
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import utils.model.rows.{ArrangementModelRows, DisclosureModelRows}

import scala.language.implicitConversions

trait SummaryImplicits  extends DisclosureModelRows with ArrangementModelRows {

  implicit def convertDisclosureDetails(id: Int, dis: DisclosureDetails)(implicit messages: Messages): Seq[Row] =
    List(disclosureNamePage(dis),
      disclosureTypePage(dis)) ++
      buildDisclosureSummaryDetails(dis)

  implicit def convertArrangementDetails(id: Int, arrangementDetails: ArrangementDetails)(implicit messages: Messages): Seq[Row] =
    Seq(whatIsThisArrangementCalledPage(id, arrangementDetails)
      , whatIsTheImplementationDatePage(id, arrangementDetails)
      , buildWhyAreYouReportingThisArrangementNow(id, arrangementDetails)
      , whichExpectedInvolvedCountriesArrangement(id, arrangementDetails)
      , whatIsTheExpectedValueOfThisArrangement(id, arrangementDetails)
      , whichNationalProvisionsIsThisArrangementBasedOn(id, arrangementDetails)
      , giveDetailsOfThisArrangement(id, arrangementDetails)).flatten
}
