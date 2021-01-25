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
import controllers.mixins.{AssociatedEnterprisesRouting, DefaultRouting, IntermediariesRouting, TaxpayersRouting}
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.organisation._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForOrganisationSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForOrganisation
  val country: Country = Country("valid", "GB", "United Kingdom")
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  val D1 = "What is the name of the organisation?"
  val D2 = "Do you know {0}’s Address?"
  val D3 = "Is {0}’s main address in the United Kingdom?"
  val D4 = "What is the {0}'s postcode?"
  val D5 = "What is {0}'s main address? (/select-address)"
  val D6 = "What is {0}'s main address? (/address)"
  val D7 = "Do you know the email address for a main contact at {0}?"
  val D8 = "What is the email address for a main contact at {0}?"
  val D9 = "Which country is {0} resident in for tax purposes?"
  val D10 = "Do you know any of {0}’s tax reference numbers for the United Kingdom?"
  val D11 = "Do you know {0}’s tax identification numbers for the country?"
  val D12 = "What are {0}’s tax reference numbers for the United Kingdom?"
  val D13 = "What are {0}’s tax identification numbers for {1}?"
  val D14 = "Is {0} resident for tax purposes in any other countries?"
  val D15 = "[Organisation] Check your answers"
  // In the associated enterprise journey
  val E10 = "Is {0} affected by the arrangement?"
  val E11 = "[Associated Enterprises] Check your answers?"
  // In the relevant taxpayers journey
  val T9 = "Is this a marketable arrangement - gateway controller"
  val T11 = "[Relevant Taxpayers] Check your answers?"
  // In the add intermediaries journey
  val I9 = "What type of intermediary is {0}?"
  val I13 = "[Add Intermediaries] Check your answers?"

  "Organisation Navigator" - {

    "must go from a page that doesn't exist in the route map to Index" ignore {

//  TODO Test is correct but sometimes fails with
//  [info]     - must go from a page that doesn't exist in the route map to Index *** FAILED ***
//  [info]       / was not equal to /enter-cross-border-arrangements (NavigatorForOrganisationSpec.scala:64)

      case object UnknownPage extends Page

      navigator.routeMap(UnknownPage)(DefaultRouting(NormalMode))(0)(None)(0)
        .mustBe(controllers.routes.IndexController.onPageLoad())
    }

    "Default routing" - {

      "in Normal mode" - {

        s"must go from $D1 to $D2" in {

          navigator
            .routeMap(OrganisationNamePage)(DefaultRouting(NormalMode))(0)(Some("name"))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(0, NormalMode))
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(0, NormalMode))
        }

        s"must go from $D2 to $D7 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(0, NormalMode))
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(0, NormalMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, NormalMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(DefaultRouting(NormalMode))(0)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(0, NormalMode))

        }

        s"must go from $D5 to $D7 " in {

          navigator
            .routeMap(SelectAddressPage)(DefaultRouting(NormalMode))(0)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(0, NormalMode))
        }

        // manual?

        s"must go from $D6 to $D7 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(DefaultRouting(NormalMode))(0)(Some(address))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(0, NormalMode))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(0, NormalMode))
        }

        s"must go from $D7 to $D9 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D8 to $D9 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(DefaultRouting(NormalMode))(0)(Some("email@email.com"))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(DefaultRouting(NormalMode))(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, NormalMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, NormalMode, index))
        }

        s"must go from $D14 to $D15 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }
      }

      "in Check mode" - {

        val defaultRoutingInCheckMode = DefaultRouting(CheckMode)

        s"must go from $D1 to $D15" in {

          navigator
            .routeMap(OrganisationNamePage)(defaultRoutingInCheckMode)(0)(Some("name"))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(defaultRoutingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $D15 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(defaultRoutingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(defaultRoutingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(defaultRoutingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(defaultRoutingInCheckMode)(0)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(0, CheckMode))

        }

        s"must go from $D5 to $D15 " in {

          navigator
            .routeMap(SelectAddressPage)(defaultRoutingInCheckMode)(0)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }

        // manual?

        s"must go from $D6 to $D15 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(defaultRoutingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $D15 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D8 to $D15 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(defaultRoutingInCheckMode)(0)(Some("email@email.com"))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(defaultRoutingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(defaultRoutingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D14 to $D15 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(defaultRoutingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad(0))
        }
      }
    }

    "Associated enterprise routing" - {

      "in Normal mode" - {

        s"must go from $D14 to $E10 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(AssociatedEnterprisesRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(0, NormalMode))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = AssociatedEnterprisesRouting(CheckMode)

        s"must go from $D1 to $E11" in {

          navigator
            .routeMap(OrganisationNamePage)(routingInCheckMode)(0)(Some("name"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $E11 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(routingInCheckMode)(0)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(0, CheckMode))

        }

        s"must go from $D5 to $E11 " in {

          navigator
            .routeMap(SelectAddressPage)(routingInCheckMode)(0)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }

        // manual?

        s"must go from $D6 to $E11 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $E11 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D8 to $E11 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(routingInCheckMode)(0)(Some("email@email.com"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(routingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D14 to $E11 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(0))
        }
      }

    }

    "Relevant taxpayers routing" - {

      "in Normal mode" - {

        s"must go from $D14 to $T9 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(TaxpayersRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersMarketableArrangementGatewayController.onRouting(0, NormalMode))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = TaxpayersRouting(CheckMode)

        s"must go from $D1 to $T11" in {

          navigator
            .routeMap(OrganisationNamePage)(routingInCheckMode)(0)(Some("name"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $T11 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(routingInCheckMode)(0)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(0, CheckMode))

        }

        s"must go from $D5 to $T11 " in {

          navigator
            .routeMap(SelectAddressPage)(routingInCheckMode)(0)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }

        // manual?

        s"must go from $D6 to $T11 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $T11 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D8 to $T11 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(routingInCheckMode)(0)(Some("email@email.com"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(routingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D14 to $T11 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad(0))
        }
      }

    }

    "Add intermediaries routing" - {

      "in Normal mode" - {

        s"must go from $D14 to $I9 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(IntermediariesRouting(NormalMode))(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(0, NormalMode))
        }
      }

      "in Check mode" - {

        val routingInCheckMode = IntermediariesRouting(CheckMode)

        s"must go from $D1 to $I13" in {

          navigator
            .routeMap(OrganisationNamePage)(routingInCheckMode)(0)(Some("name"))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(0, CheckMode))
        }

        s"must go from $D2 to $I13 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(0, CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(0, CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(routingInCheckMode)(0)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(0, CheckMode))

        }

        s"must go from $D5 to $I13 " in {

          navigator
            .routeMap(SelectAddressPage)(routingInCheckMode)(0)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }

        // manual?

        s"must go from $D6 to $I13 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(routingInCheckMode)(0)(Some(address))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(0, CheckMode))
        }

        s"must go from $D7 to $I13 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D8 to $I13 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(routingInCheckMode)(0)(Some("email@email.com"))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(routingInCheckMode)(0)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(routingInCheckMode)(0)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(routingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(routingInCheckMode)(0)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(0, CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(0, CheckMode, index))
        }

        s"must go from $D14 to $I13 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(routingInCheckMode)(0)(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad(0))
        }
      }
    }

  }
}


