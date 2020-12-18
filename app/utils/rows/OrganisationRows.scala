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

import pages.organisation.{PostcodePage, SelectAddressPage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import models.CheckMode
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

trait OrganisationRows extends RowBuilder {

  def selectAddress: Option[Row] = userAnswers.get(SelectAddressPage) map {
    answer =>
      Row(
        key     = Key(msg"selectAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"selectAddress.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def postcode: Option[Row] = userAnswers.get(PostcodePage) map {
    answer =>
      Row(
        key     = Key(msg"postcode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"postcode.checkYourAnswersLabel"))
          )
        )
      )
  }

}
