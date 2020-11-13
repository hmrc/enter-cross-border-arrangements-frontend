package utils.rows

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.{EmailAddressForIndividualPage, EmailAddressQuestionForIndividualPage, IndividualAddressPage, IndividualDateOfBirthPage, IndividualNamePage, IndividualNonUkTaxNumbersPage, IndividualPlaceOfBirthPage, IndividualSelectAddressPage, IndividualTaxOtherCountriesQuestionPage, IndividualUkPostcodePage, IndividualUkTaxNumbersPage, IndividualUkTinQuestionPage, IndividualWhichCountryTaxPage, IndividulaResidentCountryTinQuestionPage, IsIndividualAddressKnownPage, IsIndividualAddressUkPage, IsIndividualPlaceOfBirthKnownPage, SelectAddressPage}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._

trait IndividualRows extends RowBuilder {

  // 1
  def individualName: Option[Row] = userAnswers.get(IndividualNamePage) map { answer =>
    toRow(
      msgKey  = "individualName",
      content = lit"$answer",
      href    = routes.IndividualNameController.onPageLoad(CheckMode).url
    )
  }


  // 2
  def individualDateOfBirth: Option[Row] = userAnswers.get(IndividualDateOfBirthPage) map { answer =>
    toRow(
      msgKey  = "individualDateOfBirth",
      content = Literal(answer.format(dateFormatter)),
      href    = routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url
    )
  }

  // 3
  def isIndividualPlaceOfBirthKnown: Option[Row] = userAnswers.get(IsIndividualPlaceOfBirthKnownPage) map { answer =>
    toRow(
      msgKey  = "isIndividualPlaceOfBirthKnown",
      content = yesOrNo(answer),
      href    = routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url
    )
  }

  // 4
  def individualPlaceOfBirth: Option[Row] = userAnswers.get(IndividualPlaceOfBirthPage) map { answer =>
      toRow(
        msgKey  = "individualPlaceOfBirth",
        content = lit"$answer",
        href    = routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url
      )
    }

  // 5
  def isIndividualAddressKnown: Option[Row] = userAnswers.get(IsIndividualAddressKnownPage) map { answer =>
      toRow(
        msgKey  = "isIndividualAddressKnown",
        content = yesOrNo(answer),
        href    = routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url
      )
    }

  // 6
  def isIndividualAddressUk: Option[Row] = userAnswers.get(IsIndividualAddressUkPage) map { answer =>
      toRow(
        msgKey  = "isIndividualAddressUk",
        content = yesOrNo(answer),
        href    = routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
      )
    }

  // 7
  def individualUkPostcode: Option[Row] = userAnswers.get(IndividualUkPostcodePage) map { answer =>
      toRow(
        msgKey  = "individualUkPostcode",
        content = lit"$answer",
        href    = routes.IndividualPostcodeController.onPageLoad(CheckMode).url
      )
    }

  // 8
  def individualSelectAddress: Option[Row] = userAnswers.get(IndividualSelectAddressPage) map { answer =>
      toRow(
        msgKey  = "selectAddress",
        content = lit"$answer",
        href    = routes.IndividualSelectAddressController.onPageLoad(CheckMode).url
      )
    }

  // 9
  def individualAddress: Option[Row] = userAnswers.get(IndividualAddressPage) map { answer =>
      toRow(
        msgKey  = "individualAddress",
        content = lit"$answer",
        href    = routes.IndividualAddressController.onPageLoad(CheckMode).url
      )
    }

  // 10
  def emailAddressQuestionForIndividual: Option[Row] = userAnswers.get(EmailAddressQuestionForIndividualPage) map { answer =>
    toRow(
      msgKey  = "emailAddressQuestionForIndividual",
      content = yesOrNo(answer),
      href    = routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url
    )
  }

  // 11
  def emailAddressForIndividual: Option[Row] = userAnswers.get(EmailAddressForIndividualPage) map { answer =>
    toRow(
      msgKey  = "emailAddressForIndividual",
      content = lit"$answer",
      href    = routes.EmailAddressForIndividualController.onPageLoad(CheckMode).url
    )
  }

  //12
  def individualWhichCountryTax: Option[Row] = userAnswers.get(IndividualWhichCountryTaxPage) map { answer =>
      toRow(
        msgKey  = "whichCountryTaxForIndividual",
        content = yesOrNo(answer),
        href    = routes.IndividualWhichCountryTaxController.onPageLoad(CheckMode).url
      )
    }

  // 13
  def individulaResidentCountryTinQuestion: Option[Row] = userAnswers.get(IndividulaResidentCountryTinQuestionPage) map { answer =>
      toRow(
        msgKey  = "individulaResidentCountryTinQuestion",
        content = yesOrNo(answer),
        href    = routes.IndividulaResidentCountryTinQuestionController.onPageLoad(CheckMode).url
      )
    }

  // 14
  def individualNonUkTaxNumbers: Option[Row] = userAnswers.get(IndividualNonUkTaxNumbersPage) map { answer =>
    toRow(
      msgKey  = "individualNonUkTaxNumbers",
      content = lit"$answer",
      href    = routes.IndividualNonUkTaxNumbersController.onPageLoad(CheckMode).url
    )
  }

  // 15
  def individualUkTinQuestion: Option[Row] = userAnswers.get(IndividualUkTinQuestionPage) map { answer =>
      toRow(
        msgKey  = "individualUkTinQuestion",
        content = yesOrNo(answer),
        href    = routes.IndividualUkTinQuestionController.onPageLoad(CheckMode).url
      )
    }

  // 16
  def individualUkTaxNumbers: Option[Row] = userAnswers.get(IndividualUkTaxNumbersPage) map { answer =>
    toRow(
      msgKey  = "individualUkTaxNumbers",
      content = lit"$answer",
      href    = routes.IndividualUkTaxNumbersController.onPageLoad(CheckMode).url
    )
  }

  // 17
  def individualTaxOtherCountries: Option[Row] = userAnswers.get(IndividualTaxOtherCountriesQuestionPage) map { answer =>
      toRow(
        msgKey  = "individualTaxOtherCountries",
        content = yesOrNo(answer),
        href    = routes.IndividualTaxOtherCountriesController.onPageLoad(CheckMode).url
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