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

import models.hallmarks.HallmarkC.C1
import models.hallmarks.HallmarkC1._
import models.hallmarks.HallmarkCategories.{CategoryA, CategoryB}
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
  extends IndividualRows with OrganisationRows with ArrangementRows with EnterpriseRows with TaxpayerRows
    with IntermediariesRows with DisclosureRows with ReporterRows with AffectedRows {

  def selectType(id: Int): Option[Row] = userAnswers.get(TaxpayerSelectTypePage, id) map {
    answer =>
      Row(
        key     = Key(msg"selectType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"selectType.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.taxpayer.routes.TaxpayerSelectTypeController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectType.checkYourAnswersLabel"))
          )
        )
      )
  }

  def selectAddress(id: Int): Option[Row] = userAnswers.get(SelectAddressPage, id) map {
    answer =>
      Row(
        key     = Key(msg"selectAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"selectAddress.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"selectAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def postcode(id: Int): Option[Row] = userAnswers.get(PostcodePage, id) map {
    answer =>
      Row(
        key     = Key(msg"postcode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"postcode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def hallmarkD1Other(id: Int): Option[Row] = userAnswers.get(HallmarkD1OtherPage, id) flatMap {
    answer => userAnswers.get(HallmarkD1Page, id) match {
      case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
        Some(Row(
          key = Key(msg"hallmarkD1Other.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(formatMaxChars(answer, maxVisibleChars)),
          actions = List(
            Action(
              content = msg"site.edit",
              href = controllers.hallmarks.routes.HallmarkD1OtherController.onPageLoad(id, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"hallmarkD1Other.checkYourAnswersLabel"))
            )
          )
        ))
      case _ => None
    }
  }

  def mainBenefitPredicate[A](set: Option[Set[A]], elem: A): Boolean = {
    set match {
      case Some(hm) => hm.contains(elem)
      case None => false
    }
  }

  def mainBenefitTest(id: Int): Option[Row] = if(
    mainBenefitPredicate(userAnswers.get(HallmarkC1Page, id), C1bi) ||
      mainBenefitPredicate(userAnswers.get(HallmarkC1Page, id), C1c) ||
      mainBenefitPredicate(userAnswers.get(HallmarkC1Page, id), C1d) ||
      mainBenefitPredicate(userAnswers.get(HallmarkCategoriesPage, id), CategoryA) ||
      mainBenefitPredicate(userAnswers.get(HallmarkCategoriesPage, id), CategoryB)) {

    userAnswers.get(MainBenefitTestPage, id) map {
      answer =>
        Row(
          key     = Key(msg"mainBenefitTest.checkYourAnswersLabel"),
          value   = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = controllers.hallmarks.routes.MainBenefitTestController.onPageLoad(id, CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"mainBenefitTest.checkYourAnswersLabel"))
            )
          )
        )
    }
  } else{
    None
  }

  def hallmarkCategories(id: Int): Option[Row] = userAnswers.get(HallmarkCategoriesPage, id) map {
    answer =>
      Row(
        key     = Key(msg"hallmarkCategories.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-third")),
        value   = Value(Html(answer.map(a => msg"hallmarkCategories.$a".resolve).mkString(",<br>"))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.hallmarks.routes.HallmarkCategoriesController.onPageLoad(id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"hallmarkCategories.checkYourAnswersLabel"))
          )
        )
      )
  }

  def buildHallmarksRow(id: Int): Row = {

    val hallmarkCPage = userAnswers.get(HallmarkCPage, id)  match {
      case Some(set) if set.contains(C1) && set.size == 1 => None
      case Some(set) if set.contains(C1) => Some(set.filter(_ != C1))
      case hallmarkSet => hallmarkSet
    }

    val hallmarkDPage = userAnswers.get(HallmarkDPage, id)  match {
      case Some(set) if set.contains(D1) && set.size == 1 => None
      case Some(set) if set.contains(D1) => Some(set.filter(_ != D1))
      case hallmarkSet => hallmarkSet
    }

    val hallmarkPages = Seq(
      userAnswers.get(HallmarkD1Page, id),
      hallmarkDPage
    )

    val selectedHallmarkParts = hallmarkPages.collect{ case Some(value) => value }

    val hallmarksList: Seq[String] = for {
      selectedHallmark <- selectedHallmarkParts
    } yield {
      selectedHallmark.map(_.toString.replace("DAC6", "")).toList.sorted.map(hallmark => msg"$hallmark".resolve).mkString(", ")
    }

    Row(
      key     = Key(msg"checkYourAnswers.selectedHallmarks.label"),
      value   = Value(msg"${hallmarksList.mkString(", ")}"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = controllers.hallmarks.routes.HallmarkDController.onPageLoad(id, CheckMode).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"checkYourAnswers.selectedHallmarks.label"))
        )
      )
    )

  }
}
