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

      navigator.routeMap(UnknownPage)(DefaultRouting(NormalMode))(Some(true))(0)
        .mustBe(controllers.routes.IndexController.onPageLoad())
    }

    "Default routing" - {

      "in Normal mode" - {

        s"must go from $D1 page to $D2" in {

          navigator
            .routeMap(IndividualNamePage)(DefaultRouting(NormalMode))(Some(Name("first", "last")))(0)
            .mustBe(routes.IsIndividualDateOfBirthKnownController.onPageLoad(NormalMode))
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(NormalMode))
        }

        s"must go from $D2 to $D4 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(NormalMode))
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(NormalMode))
        }

        s"must go from $D4 to the $D6 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }

        s"must go from $D5 to $D6" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(DefaultRouting(NormalMode))(Some("address"))(0)
            .mustBe(routes.IsIndividualAddressKnownController.onPageLoad(NormalMode))
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(NormalMode))
        }

        s"must go from $D6 to $D11 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(NormalMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(NormalMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(DefaultRouting(NormalMode))(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(NormalMode))
        }

        s"must go from $D9 to $D11" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(DefaultRouting(NormalMode))(Some("A99 AA9"))(0)
            .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
        }

        s"must go from $D10 to $D11" in {

          navigator
            .routeMap(IndividualAddressPage)(DefaultRouting(NormalMode))(Some(address))(0)
            .mustBe(routes.EmailAddressQuestionForIndividualController.onPageLoad(NormalMode))
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(NormalMode))
        }

        s"must go from $D11 to $D13 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, 0))
        }

        s"must go from $D12 to $D13" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(DefaultRouting(NormalMode))(Some("test@email.com"))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, 1))
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(DefaultRouting(NormalMode))(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(NormalMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(NormalMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(DefaultRouting(NormalMode))(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(DefaultRouting(NormalMode))(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(NormalMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(NormalMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(DefaultRouting(NormalMode))(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(NormalMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(NormalMode, 0))
        }

        s"must go from $D18 to $D19 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }
      }

      "in Check mode" - {

        val defaultRoutingInCheckMode = DefaultRouting(CheckMode)

        s"must go from $D1 page to $D19" in {

          navigator
            .routeMap(IndividualNamePage)(defaultRoutingInCheckMode)(Some(Name("first", "last")))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D2 page to $D3 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.IndividualDateOfBirthController.onPageLoad(CheckMode))
        }

        s"must go from $D2 to $D19 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualDateOfBirthKnownPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D4 to the $D5 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode))
        }

        s"must go from $D4 to the $D19 when answer is 'No' " in {

          navigator
            .routeMap(IsIndividualPlaceOfBirthKnownPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D5 to $D19" in {

          navigator
            .routeMap(IndividualPlaceOfBirthPage)(defaultRoutingInCheckMode)(Some("address"))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D6 to $D7 when answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.IsIndividualAddressUkController.onPageLoad(CheckMode))
        }

        s"must go from $D6 to $D19 when answer is 'No'" in {

          navigator
            .routeMap(IsIndividualAddressKnownPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D7 to $D10 when the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IndividualAddressController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $D8 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualAddressUkPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.IndividualPostcodeController.onPageLoad(CheckMode))
        }

        s"must go from $D8 to $D9 when the answer has multiple entries " in {

          navigator
            .routeMap(IndividualUkPostcodePage)(defaultRoutingInCheckMode)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualSelectAddressController.onPageLoad(CheckMode))
        }

        s"must go from $D9 to $D19" in {

          navigator
            .routeMap(IndividualSelectAddressPage)(defaultRoutingInCheckMode)(Some("A99 AA9"))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D10 to $D19" in {

          navigator
            .routeMap(IndividualAddressPage)(defaultRoutingInCheckMode)(Some(address))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D11 to $D12 when the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.EmailAddressForIndividualController.onPageLoad(CheckMode))
        }

        s"must go from $D11 to $D19 when the answer is 'No' " in {

          navigator
            .routeMap(EmailAddressQuestionForIndividualPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D12 to $D19" in {

          navigator
            .routeMap(EmailAddressForIndividualPage)(defaultRoutingInCheckMode)(Some("test@email.com"))(0)
            .mustBe(routes.IndividualCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D13 to $D14 when the country is GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(defaultRoutingInCheckMode)(Some(country))(0)
            .mustBe(routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode, 0))
        }

        s"must go from $D14 to $D15 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode, 0))
        }

        s"must go from $D14 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKIndividualPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1))
        }

        s"must go from $D15 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKIndividualPage)(defaultRoutingInCheckMode)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1))
        }

        s"must go from $D13 to $D16 when the country is non GB" in {

          navigator
            .routeMap(WhichCountryTaxForIndividualPage)(defaultRoutingInCheckMode)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(CheckMode, 0))
        }

        s"must go from $D16 to $D17 when the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.WhatAreTheTaxNumbersForNonUKIndividualController.onPageLoad(CheckMode, 0))
        }

        s"must go from $D16 to $D18 when the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKIndividualPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1))
        }

        s"must go from $D17 to $D18" in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKIndividualPage)(defaultRoutingInCheckMode)(Some(tin))(0)
            .mustBe(routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode, 1))
        }

        s"must go from $D18 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode, 0))
        }
      }
    }

    "Associated enterprise routing" - {

      "in Normal mode" - {

        s"must go from $D18 to $E10 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(AssociatedEnterprisesRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode))
        }
      }
    }

    "Relevant taxpayers routing" - {

      "in Normal mode" - {

        s"must go from $D18 to $T9 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(TaxpayersRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.MarketableArrangementGatewayController.onRouting(NormalMode))
        }
      }
    }

    "Add intermediaries routing" - {

      "in Normal mode" - {

        s"must go from $D18 to $I9 if the answer is 'No' " in {

          navigator
            .routeMap(IsIndividualResidentForTaxOtherCountriesPage)(IntermediariesRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(NormalMode))
        }
      }
    }

  }
}

