package utils.rows

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.{DoYouKnowAnyTINForUKIndividualPage, EmailAddressForIndividualPage, EmailAddressQuestionForIndividualPage, IndividualDateOfBirthPage, IndividualNamePage, IndividualPlaceOfBirthPage, IsIndividualAddressKnownPage, IsIndividualPlaceOfBirthKnownPage, IsIndividualResidentForTaxOtherCountriesPage, WhatAreTheTaxNumbersForUKIndividualPage, WhichCountryTaxForIndividualPage}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

trait IndividualRows extends RowBuilder {

  def individualName: Option[Row] = userAnswers.get(IndividualNamePage) map { answer =>
    toRow(
      msgKey  = "individualName",
      content = lit"$answer",
      href    = routes.IndividualNameController.onPageLoad(CheckMode).url
    )
  }

  def individualDateOfBirth: Option[Row] = userAnswers.get(IndividualDateOfBirthPage) map { answer =>
    toRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(answer.format(dateFormatter)),
      href    = routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url
    )
  }

  def isIndividualPlaceOfBirthKnown: Option[Row] = userAnswers.get(IsIndividualPlaceOfBirthKnownPage) map { answer =>
    toRow(
      msgKey  = "isIndividualPlaceOfBirthKnown",
      content = yesOrNo(answer),
      href    = routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url
    )
  }

  def individualPlaceOfBirth: Option[Row] = userAnswers.get(IndividualPlaceOfBirthPage) map { answer =>
      toRow(
        msgKey  = "individualPlaceOfBirth",
        content = lit"$answer",
        href    = routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url
      )
    }

  def isIndividualAddressKnown: Option[Row] = userAnswers.get(IsIndividualAddressKnownPage) map { answer =>
      toRow(
        msgKey  = "isIndividualAddressKnown",
        content = yesOrNo(answer),
        href    = routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url
      )
    }

  def emailAddressQuestionForIndividual: Option[Row] = userAnswers.get(EmailAddressQuestionForIndividualPage) map { answer =>
    toRow(
      msgKey  = "emailAddressQuestionForIndividual",
      content = yesOrNo(answer),
      href    = routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url
    )
  }

  def emailAddressForIndividual: Option[Row] = userAnswers.get(EmailAddressForIndividualPage) map { answer =>
    toRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$answer",
      href    = routes.EmailAddressForIndividualController.onPageLoad(CheckMode).url
    )
  }

}

trait CompareRows extends RowBuilder {

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


}