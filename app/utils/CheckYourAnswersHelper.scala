/*
 * Copyright 2023 HM Revenue & Customs
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

import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import models.{CheckMode, UserAnswers}
import pages.hallmarks._
import pages.organisation.{PostcodePage, SelectAddressPage}
import pages.taxpayer.TaxpayerSelectTypePage
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels._
import utils.rows._

class CheckYourAnswersHelper(val userAnswers: UserAnswers, val maxVisibleChars: Int = 100)(implicit val messages: Messages)
    extends IndividualRows
    with OrganisationRows
    with ArrangementRows
    with EnterpriseRows
    with TaxpayerRows
    with IntermediariesRows
    with DisclosureRows
    with ReporterRows
    with AffectedRows {

  def selectType(id: Int): Option[Row] = userAnswers.get(TaxpayerSelectTypePage, id) map {
    answer =>
      Row(
        key = Key(msg"selectType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"selectType.$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def selectAddress(id: Int): Option[Row] = userAnswers.get(SelectAddressPage, id) map {
    answer =>
      Row(
        key = Key(msg"selectAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(msg"selectAddress.$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def postcode(id: Int): Option[Row] = userAnswers.get(PostcodePage, id) map {
    answer =>
      Row(
        key = Key(msg"postcode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"postcode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def hallmarkD1Other(id: Int): Option[Row] = userAnswers.get(HallmarkD1OtherPage, id) flatMap {
    answer =>
      userAnswers.get(HallmarkD1Page, id) match {
        case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
          Some(
            Row(
              key = Key(msg"hallmarkD1Other.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(formatMaxChars(answer, maxVisibleChars)),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(id, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"hallmarkD1Other.checkYourAnswersLabel"))
                )
              )
            )
          )
        case _ => None
      }
  }

  def buildHallmarksRow(id: Int): Row = {

    val hallmarkDPage = userAnswers.get(HallmarkDPage, id) match {
      case Some(set) if set.contains(D1) && set.size == 1 => None
      case Some(set) if set.contains(D1)                  => Some(set.filter(_ != D1))
      case hallmarkSet                                    => hallmarkSet
    }

    val hallmarkPages = Seq(
      userAnswers.get(HallmarkD1Page, id),
      hallmarkDPage
    )

    val selectedHallmarkParts = hallmarkPages.collect {
      case Some(value) => value
    }

    val hallmarksList: Seq[String] = for {
      selectedHallmark <- selectedHallmarkParts
    } yield selectedHallmark
      .map(_.toString.replace("DAC6", ""))
      .toList
      .sorted
      .map(
        hallmark => msg"$hallmark".resolve
      )
      .mkString(", ")

    Row(
      key = Key(msg"checkYourAnswers.selectedHallmarks.label"),
      value = Value(msg"${hallmarksList.mkString(", ")}"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = controllers.hallmarks.routes.HallmarkDController.onPageLoad(id, CheckMode).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"checkYourAnswers.selectedHallmarks.label"))
        )
      )
    )

  }
}
