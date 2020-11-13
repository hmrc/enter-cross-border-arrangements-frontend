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

import controllers.routes
import models.CheckMode
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.Row
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

  // 6 /individual/live-in-uk
  def isIndividualAddressUk: Option[Row] = userAnswers.get(IsIndividualAddressUkPage) map { answer =>
      toRow(
        msgKey  = "isIndividualAddressUk",
        content = yesOrNo(answer),
        href    = routes.IsIndividualAddressUkController.onPageLoad(CheckMode).url
      )
    }

  // 7 /individual/postcode
  def individualUkPostcode: Option[Row] = userAnswers.get(IndividualUkPostcodePage) map { answer =>
      toRow(
        msgKey  = "individualUkPostcode",
        content = lit"$answer",
        href    = routes.IndividualPostcodeController.onPageLoad(CheckMode).url
      )
    }

  // 8 /individual/select-address
  def individualSelectAddress: Option[Row] = userAnswers.get(IndividualSelectAddressPage) map { answer =>
      toRow(
        msgKey  = "selectAddress",
        content = lit"$answer",
        href    = routes.IndividualSelectAddressController.onPageLoad(CheckMode).url
      )
    }

  // 9 /individual/address
  def individualAddress: Option[Row] = userAnswers.get(IndividualAddressPage) map { answer =>
      toRow(
        msgKey  = "individualAddress",
        content = lit"$answer",
        href    = routes.IndividualAddressController.onPageLoad(CheckMode).url
      )
    }

  // 10 /individual/what-is-email-address
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

  //12 /individual/which-country-tax
  def whichCountryTaxForIndividual: Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage) map { answer =>
    toRow(
      msgKey  = "whichCountryTaxForIndividual",
      content = lit"$answer",
      href    = routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode).url
    )
  }

  // 13 //individual/uk-tin-known
  def doYouKnowAnyTINForUKIndividual: Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage) map { answer =>
    toRow(
      msgKey  = "doYouKnowAnyTINForUKIndividual",
      content = yesOrNo(answer),
      href    = routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode).url
    )
  }

  // 14 /individual/uk-tax-numbers
  def whatAreTheTaxNumbersForUKIndividual: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage) map { answer =>
    toRow(
      msgKey  = "whatAreTheTaxNumbersForUKIndividual",
      content = lit"$answer",
      href    = routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode).url
    )
  }

  // 15 TODO
//  def individulaResidentCountryTinQuestion: Option[Row] = userAnswers.get(IndividulaResidentCountryTinQuestionPage) map { answer =>
//      toRow(
//        msgKey  = "individulaResidentCountryTinQuestion",
//        content = yesOrNo(answer),
//        href    = routes.IndividulaResidentCountryTinQuestionController.onPageLoad(CheckMode).url
//      )
//    }

  // 16 TODO
//  def individualNonUkTaxNumbers: Option[Row] = userAnswers.get(IndividualNonUkTaxNumbersPage) map { answer =>
//    toRow(
//      msgKey  = "individualNonUkTaxNumbers",
//      content = lit"$answer",
//      href    = routes.IndividualNonUkTaxNumbersController.onPageLoad(CheckMode).url
//    )
//  }

  // 17 /individual/tax-resident-countries
  def isIndividualResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsIndividualResidentForTaxOtherCountriesPage) map { answer =>
      toRow(
        msgKey  = "isIndividualResidentForTaxOtherCountries",
        content = yesOrNo(answer),
        href    = routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode).url
      )
    }
}
