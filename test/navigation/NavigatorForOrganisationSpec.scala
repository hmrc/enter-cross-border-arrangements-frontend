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
import controllers.mixins.DefaultRouting
import controllers.routes
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.organisation._
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest

class NavigatorForOrganisationSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForOrganisation
  val country: Country = Country("valid", "GB", "United Kingdom")
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        navigator.routeMap(UnknownPage)(DefaultRouting(NormalMode))(None)(0)
          .mustBe(routes.IndexController.onPageLoad())
      }

      "must go from What is the name of the organisation? page to Do you know {0}’s Address page" in {

        navigator
          .routeMap(OrganisationNamePage)(DefaultRouting(NormalMode))(Some("name"))(0)
          .mustBe(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
      }

      "must go from the Do you know {0}’s Address page to the Is {0}’s main address in the United Kingdom? when answer is 'Yes' " in {

        navigator
          .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(NormalMode))
      }

      "must go from the Do you know {0}’s Address page to " +
        "Do you know the email address for a main contact at the organisation? page when answer is 'No' " in {

        navigator
          .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
      }

      "must go from the Is {0}’s main address in the United Kingdom? page to the Index page when answer is 'Yes' " in {

        navigator
          .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(NormalMode))
      }

      "must go from the Is {0}’s main address in the United Kingdom? page to What is {0}’s main address? " in {

        navigator
          .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(NormalMode))
      }

      "must go from Postcode page to What is your main address page" in {

        navigator
          .routeMap(PostcodePage)(DefaultRouting(NormalMode))(Some("ZZ1 ZZ4"))(0)
          .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))

      }

      "must go from What is the organisation's main address page (/select-address) to " +
        "Do you know the email address for a main contact at the organisation? page" in {

        navigator
          .routeMap(SelectAddressPage)(DefaultRouting(NormalMode))(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
          .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
      }

      "must go from What is the organisation's main address page (/address) to " +
        "Do you know the email address for a main contact at the organisation? page" in {

        val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
          Country("valid", "FR", "France"))

        navigator
          .routeMap(OrganisationAddressPage)(DefaultRouting(NormalMode))(Some(address))(0)
          .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
      }

      "must go from Do you know the email address for a main contact at the organisation? page to " +
        "What is the email address for a main contact at the organisation? page if the answer is true" in {

        navigator
          .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(NormalMode))
      }

      "must go from Do you know the email address for a main contact at the organisation? page to " +
        "Which country is the organisation resident in for tax purposes? page if the answer is false" in {

        navigator
          .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from What is the email address for a main contact at the organisation? page to " +
        "Which country is the organisation resident in for tax purposes? page" in {

        navigator
          .routeMap(EmailAddressForOrganisationPage)(DefaultRouting(NormalMode))(Some("email@email.com"))(0)
          .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from Which country is the organisation resident in for tax purposes? page to " +
        "Do you know any of the organisation’s tax reference numbers for the United Kingdom? page if the answer is GB" in {

        navigator
          .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(Some(country))(0)
          .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from Which country is the organisation resident in for tax purposes? page to " +
        "Do you know the organisation’s tax identification numbers for the country? page if the answer is not GB" in {

        navigator
          .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(Some(Country("valid", "FR", "France")))(0)
          .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from Do you know any of the organisation’s tax reference numbers for the United Kingdom? page " +
        "to What are the organisation’s tax reference numbers for the United Kingdom? page if the answer is true" in {

        navigator
          .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from Do you know any of the organisation’s tax reference numbers for the United Kingdom? page " +
        "to Is the organisation resident for tax purposes in any other countries? page if the answer is false" in {

        navigator
          .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
      }

      "must go from Do you know the organisation’s tax identification numbers for the country? page " +
        "to What are the organisation’s tax identification numbers for the country? page if the answer is true" in {

        navigator
          .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from Do you know the organisation’s tax identification numbers for the country? page " +
        "to Is the organisation resident for tax purposes in any other countries? page if the answer is false" in {

        navigator
          .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
          .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
      }

      "must go from What are the organisation’s tax reference numbers for the United Kingdom? " +
        "to Is the organisation resident for tax purposes in any other countries? page" in {

        navigator
          .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
          .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
      }

      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
        "Which country is the organisation resident in for tax purposes? page if the answer is true" in {

        navigator
          .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(true))(0)
          .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
      }

      "must go from What are the organisation’s tax identification numbers for the country? page to " +
        "Is the organisation resident for tax purposes in any other countries? page if the answer is false" in {

        navigator
          .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
          .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
      }

//      "must go from Is the organisation resident for tax purposes in any other countries? page to " +
//        "'What is [name]'s implementation date?' for organisation page if the answer is false and in relevant taxpayer journey" in {
//
//        navigator
//          .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(false))
//          .mustBe(controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(NormalMode))
//      }
    }
  }
}

