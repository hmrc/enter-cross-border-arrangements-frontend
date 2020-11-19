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

package utils

import java.time.format.DateTimeFormatter

import controllers.routes
import models.HallmarkA._
import models.HallmarkC.C1
import models.HallmarkC1._
import models.HallmarkCategories.{CategoryA, CategoryB}
import models.HallmarkD.D1
import models.HallmarkD1.D1other
import models.{CheckMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.CheckYourAnswersHelper.dateFormatter

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def whichCountryTaxForIndividual: Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage) map {
    answer =>
      Row(
        key     = Key(msg"whichCountryTaxForIndividual.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whichCountryTaxForIndividual.checkYourAnswersLabel"))
          )
        )
      )
  }

  def whatAreTheTaxNumbersForUKIndividual: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage) map {
    answer =>
      Row(
        key     = Key(msg"whatAreTheTaxNumbersForUKIndividual.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatAreTheTaxNumbersForUKIndividual.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isIndividualResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsIndividualResidentForTaxOtherCountriesPage) map {
    answer =>
      Row(
        key     = Key(msg"isIndividualResidentForTaxOtherCountries.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isIndividualResidentForTaxOtherCountries.checkYourAnswersLabel"))
          )
        )
      )
  }

  def doYouKnowAnyTINForUKIndividual: Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage) map {
    answer =>
      Row(
        key     = Key(msg"doYouKnowAnyTINForUKIndividual.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"doYouKnowAnyTINForUKIndividual.checkYourAnswersLabel"))
          )
        )
      )
  }

  def emailAddressQuestionForIndividual: Option[Row] = userAnswers.get(EmailAddressQuestionForIndividualPage) map {
    answer =>
      Row(
        key     = Key(msg"emailAddressQuestionForIndividual.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"emailAddressQuestionForIndividual.checkYourAnswersLabel"))
          )
        )
      )
  }

  def emailAddressForIndividual: Option[Row] = userAnswers.get(EmailAddressForIndividualPage) map {
    answer =>
      Row(
        key     = Key(msg"emailAddressForIndividual.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.EmailAddressForIndividualController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"emailAddressForIndividual.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isIndividualPlaceOfBirthKnown: Option[Row] = userAnswers.get(IsIndividualPlaceOfBirthKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isIndividualPlaceOfBirthKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isIndividualPlaceOfBirthKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isIndividualAddressKnown: Option[Row] = userAnswers.get(IsIndividualAddressKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isIndividualAddressKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isIndividualAddressKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def individualPlaceOfBirth: Option[Row] = userAnswers.get(IndividualPlaceOfBirthPage) map {
    answer =>
      Row(
        key     = Key(msg"individualPlaceOfBirth.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"individualPlaceOfBirth.checkYourAnswersLabel"))
          )
        )
      )
  }

  def individualName: Option[Row] = userAnswers.get(IndividualNamePage) map {
    answer =>
      Row(
        key     = Key(msg"individualName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IndividualNameController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"individualName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def individualDateOfBirth: Option[Row] = userAnswers.get(IndividualDateOfBirthPage) map {
    answer =>
      Row(
        key     = Key(msg"individualDateOfBirth.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(Literal(answer.format(dateFormatter))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"individualDateOfBirth.checkYourAnswersLabel"))
          )
        )
      )
  }

  def selectAddress: Option[Row] = userAnswers.get(SelectAddressPage) map {
    answer =>
      Row(
        key     = Key(msg"selectAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(msg"selectAddress.$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.OrganisationSelectAddressController.onPageLoad(CheckMode).url,
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
            href               = routes.OrganisationPostcodeController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"postcode.checkYourAnswersLabel"))
          )
        )
      )
  }

  val d1OtherVisibleCharacters = 100
  val ellipsis = " ..."

  def hallmarkD1Other: Option[Row] = userAnswers.get(HallmarkD1OtherPage) flatMap {
    answer => userAnswers.get(HallmarkD1Page) match {
      case Some(hallmarkSet) if hallmarkSet.contains(D1other) =>
        Some(Row(
          key = Key(msg"hallmarkD1Other.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value = Value(lit"${
            if (answer.length > 100) answer.take(d1OtherVisibleCharacters) + ellipsis else answer
          }"),
          actions = List(
            Action(
              content = msg"site.edit",
              href = routes.HallmarkD1OtherController.onPageLoad(CheckMode).url,
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

  def mainBenefitTest: Option[Row] = if(
    mainBenefitPredicate(userAnswers.get(HallmarkC1Page), C1bi) ||
    mainBenefitPredicate(userAnswers.get(HallmarkC1Page), C1c) ||
    mainBenefitPredicate(userAnswers.get(HallmarkC1Page), C1d) ||
    mainBenefitPredicate(userAnswers.get(HallmarkCategoriesPage), CategoryA) ||
    mainBenefitPredicate(userAnswers.get(HallmarkCategoriesPage), CategoryB)) {

    userAnswers.get(MainBenefitTestPage) map {
      answer =>
        Row(
          key     = Key(msg"mainBenefitTest.checkYourAnswersLabel"),
          value   = Value(yesOrNo(answer)),
          actions = List(
            Action(
              content            = msg"site.edit",
              href               = routes.MainBenefitTestController.onPageLoad(CheckMode).url,
              visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"mainBenefitTest.checkYourAnswersLabel"))
            )
          )
        )
    }
  } else{
   None
}

  def hallmarkCategories: Option[Row] = userAnswers.get(HallmarkCategoriesPage) map {
    answer =>
      Row(
        key     = Key(msg"hallmarkCategories.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-third")),
        value   = Value(Html(answer.map(a => msg"hallmarkCategories.$a".resolve).mkString(",<br>"))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.HallmarkCategoriesController.onPageLoad(CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"hallmarkCategories.checkYourAnswersLabel"))
          )
        )
      )
  }

  def buildHallmarksRow(ua: UserAnswers): Row = {

    val hallmarkCPage = ua.get(HallmarkCPage)  match {
      case Some(set) if set.contains(C1) && set.size == 1 => None
      case Some(set) if set.contains(C1) => Some(set.filter(_ != C1))
      case hallmarkSet => hallmarkSet
    }

    val hallmarkDPage = ua.get(HallmarkDPage)  match {
      case Some(set) if set.contains(D1) && set.size == 1 => None
      case Some(set) if set.contains(D1) => Some(set.filter(_ != D1))
      case hallmarkSet => hallmarkSet
    }

    val hallmarkPages = Seq(
      ua.get(HallmarkAPage),
      ua.get(HallmarkBPage),
      ua.get(HallmarkC1Page),
      ua.get(HallmarkD1Page),
      hallmarkCPage,
      hallmarkDPage,
      ua.get(HallmarkEPage)
    )

    val selectedHallmarkParts = hallmarkPages.collect{ case Some(value) => value }

    val hallmarksList = for {
      selectedHallmark <- selectedHallmarkParts
    } yield {
      selectedHallmark.map(_.toString).toList.sorted.map(hallmark => msg"$hallmark".resolve).mkString(", ")
    }

    Row(
      key     = Key(msg"checkYourAnswers.selectedHallmarks.label"),
      value   = Value(msg"${hallmarksList.mkString(", ")}"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.HallmarkCategoriesController.onPageLoad(CheckMode).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"checkYourAnswers.selectedHallmarks.label"))
        )
      )
    )

  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }
}

object CheckYourAnswersHelper {
  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
