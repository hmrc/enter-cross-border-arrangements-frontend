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

package navigation

import base.SpecBase
import controllers.individual._
import controllers.mixins.{AssociatedEnterprisesRouting, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.individual._

class NavigatorForIndividualSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForIndividual
  val country: Country = Country("valid", "GB", "United Kingdom")
  val address: Address = Address(None, None, None, "", None, country)
  val tin: TaxReferenceNumbers = TaxReferenceNumbers("1234567890", None, None)

  val D1 = "What is their name?"
  val D2 = "Do you know {0}'s date of birth?"
  val D3 = "What is {0}'s date of birth?"
  val D4 = "Do you know where {0} was born?"
  val D5 = "Where was {0} born?"
  val D6 = "Do you know {0}’s Address?"
  val D7 = "Does {0}’s live in the United Kingdom?"
  val D8 = "What is the {0}'s postcode?"
  val D9 = "What is {0}'s address? (/select-address)"
  val D10 = "What is {0}'s address? (/address)"
  val D11 = "Do you know the email address for a main contact at {0}?"
  val D12 = "What is the email address for a main contact at {0}?"
  val D13 = "Which country is {0} resident in for tax purposes?"
  val D14 = "Do you know any of {0}’s tax reference numbers for the United Kingdom?"
  val D15 = "Do you know {0}’s tax identification numbers for the country?"
  val D16 = "What are {0}’s tax reference numbers for the United Kingdom?"
  val D17 = "What are {0}’s tax identification numbers for {1}?"
  val D18 = "Is {0} resident for tax purposes in any other countries?"
  val D19 = "[Organisation] Check your answers"
  // In the associated enterprise journey
  val E10 = "Is {0} affected by the arrangement?"
  val E11 = "[Associated Enterprises] Check your answers?"
  // In the relevant taxpayers journey
  val T9 = "Is this a marketable arrangement - gateway controller"
  val T11 = "[Relevant Taxpayers] Check your answers?"
  // In the add intermediaries journey
  val I9 = "What type of intermediary is {0}?"
  val I13 = "[Add Intermediaries] Check your answers?"

  "Individual Navigator" - {

    "must go from a page that doesn't exist in the route map to Index" ignore {

      case object UnknownPage extends Page

      navigator.routeMap(UnknownPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
        .mustBe(controllers.routes.IndexController.onPageLoad())
    }

    "Default routing" - {

      "in Normal mode" - {

        s"must go from $D1 page to $D2" in {

          navigator
            .routeMap(IndividualNamePage)(DefaultRouting(NormalMode))(0)(Some(Name("first", "last")))(0)
            .mustBe(routes.IsIndividualDateOfBirthKnownController.onPageLoad(0, NormalMode))
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(0, NormalMode))
        }

        s"must go from $D2 to $D4 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(0, NormalMode))
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(0, NormalMode))
        }

        s"must go from $D4 to the $D6 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(0, NormalMode))
        }

        s"must go from $D5 to $D6" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(DefaultRouting(NormalMode))(0)(Some("address"))(0)
            .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(0, NormalMode))
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(0, NormalMode))
        }

        s"must go from $D6 to $D11 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(0, NormalMode))
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(0, NormalMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(0, NormalMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(DefaultRouting(NormalMode))(0)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(0, NormalMode))
        }

        s"must go from $D9 to $D11" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(DefaultRouting(NormalMode))(0)(Some("A99 AA9"))(0)
            .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(0, NormalMode))
        }

        s"must go from $D10 to $D11" in {

          navigator
            .routeMap(IndividualAddressPage)(DefaultRouting(NormalMode))(0)(Some(address))(0)
            .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(0, NormalMode))
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(0, NormalMode))
        }

        s"must go from $D11 to $D13 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D12 to $D13" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(DefaultRouting(NormalMode))(0)(Some("test@email.com"))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(DefaultRouting(NormalMode))(0)(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(DefaultRouting(NormalMode))(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(DefaultRouting(NormalMode))(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(DefaultRouting(NormalMode))(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, NormalMode, 0))
        }

        s"must go from $D18 to $D19 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = DefaultRouting(CheckMode)

        s"must go from $D1 page to $D19" in {

          navigator
            .routeMap(IndividualNamePage)(routingInCheckMode)(0)(Some(Name("first", "last")))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $D19 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to the $D19 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D5 to $D19" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(routingInCheckMode)(0)(Some("address"))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D6 to $D19 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D9 to $D19" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D10 to $D19" in {

          navigator
            .routeMap(IndividualAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(0, CheckMode))
        }

        s"must go from $D11 to $D19 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D12 to $D19" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(routingInCheckMode)(0)(Some("test@email.com"))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D18 to $D19 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad(0))
        }

      }
    }

    "Associated enterprise routing" - {

      "in Normal mode" - {

        s"must go from $D18 to $E10 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(AssociatedEnterprisesRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(0, NormalMode))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = AssociatedEnterprisesRouting(CheckMode)

        s"must go from $D1 page to $E11" in {

          navigator
            .routeMap(IndividualNamePage)(routingInCheckMode)(0)(Some(Name("first", "last")))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $E11 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to the $E11 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D5 to $E11" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(routingInCheckMode)(0)(Some("address"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D6 to $E11 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D9 to $E11" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D10 to $E11" in {

          navigator
            .routeMap(IndividualAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(0, CheckMode))
        }

        s"must go from $D11 to $E11 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D12 to $E11" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(routingInCheckMode)(0)(Some("test@email.com"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D18 to $E11 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0, None))
        }
      }

    }

    "Relevant taxpayers routing" - {

      "in Normal mode" - {

        s"must go from $D18 to $T9 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(TaxpayersRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = TaxpayersRouting(CheckMode)

        s"must go from $D1 page to $T11" in {

          navigator
            .routeMap(IndividualNamePage)(routingInCheckMode)(0)(Some(Name("first", "last")))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $T11 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to the $T11 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D5 to $T11" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(routingInCheckMode)(0)(Some("address"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D6 to $T11 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D9 to $T11" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D10 to $T11" in {

          navigator
            .routeMap(IndividualAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(0, CheckMode))
        }

        s"must go from $D11 to $T11 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D12 to $T11" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(routingInCheckMode)(0)(Some("test@email.com"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D18 to $T11 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0, None))
        }
      }
    }

    "Add intermediaries routing" - {

      "in Normal mode" - {

        s"must go from $D18 to $I9 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(IntermediariesRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(0, NormalMode))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = IntermediariesRouting(CheckMode)

        s"must go from $D1 page to $I13" in {

          navigator
            .routeMap(IndividualNamePage)(routingInCheckMode)(0)(Some(Name("first", "last")))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $I13 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to the $I13 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D5 to $I13" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(routingInCheckMode)(0)(Some("address"))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D6 to $I13 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D9 to $I13" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(routingInCheckMode)(0)(Some("A99 AA9"))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D10 to $I13" in {

          navigator
            .routeMap(IndividualAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(0, CheckMode))
        }

        s"must go from $D11 to $I13 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D12 to $I13" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(routingInCheckMode)(0)(Some("test@email.com"))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(routingInCheckMode)(0)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(0, CheckMode, 0))
        }

        s"must go from $D18 to $I13 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0, None))
        }
      }
    }

  }
}

