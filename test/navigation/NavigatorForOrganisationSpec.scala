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

      navigator.routeMap(UnknownPage)(DefaultRouting(NormalMode))(None)(0)
        .mustBe(controllers.routes.IndexController.onPageLoad())
    }

    "Default routing" - {

      "in Normal mode" - {

        s"must go from $D1 to $D2" in {

          navigator
            .routeMap(OrganisationNamePage)(DefaultRouting(NormalMode))(Some("name"))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(NormalMode))
        }

        s"must go from $D2 to $D7 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(NormalMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(NormalMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(DefaultRouting(NormalMode))(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))

        }

        s"must go from $D5 to $D7 " in {

          navigator
            .routeMap(SelectAddressPage)(DefaultRouting(NormalMode))(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }

        // manual?

        s"must go from $D6 to $D7 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(DefaultRouting(NormalMode))(Some(address))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(NormalMode))
        }

        s"must go from $D7 to $D9 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D8 to $D9 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(DefaultRouting(NormalMode))(Some("email@email.com"))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }

        s"must go from $D14 to $D15 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }
      }

      "in Check mode" - {

        val defaultRoutingInCheckMode = DefaultRouting(CheckMode)

        s"must go from $D1 to $D15" in {

          navigator
            .routeMap(OrganisationNamePage)(defaultRoutingInCheckMode)(Some("name"))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(CheckMode))
        }

        s"must go from $D2 to $D15 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(defaultRoutingInCheckMode)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

        }

        s"must go from $D5 to $D7 " in {

          navigator
            .routeMap(SelectAddressPage)(defaultRoutingInCheckMode)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
        }

        // manual?

        s"must go from $D6 to $D7 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(defaultRoutingInCheckMode)(Some(address))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $D15 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D8 to $D15 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(defaultRoutingInCheckMode)(Some("email@email.com"))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(defaultRoutingInCheckMode)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(defaultRoutingInCheckMode)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(defaultRoutingInCheckMode)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(defaultRoutingInCheckMode)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(defaultRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D14 to $D15 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(defaultRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }
      }
    }

    "Associated enterprise routing" - {

      "in Normal mode" - {

        s"must go from $D14 to $E10 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(AssociatedEnterprisesRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode))
        }
      }

      "in Check mode" - {

        val associatedEnterprisesRoutingInCheckMode = AssociatedEnterprisesRouting(CheckMode)

        s"must go from $D1 to $E11" in {

          navigator
            .routeMap(OrganisationNamePage)(associatedEnterprisesRoutingInCheckMode)(Some("name"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(associatedEnterprisesRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(CheckMode))
        }

        s"must go from $D2 to $E11 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(associatedEnterprisesRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(associatedEnterprisesRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(associatedEnterprisesRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(associatedEnterprisesRoutingInCheckMode)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

        }

        s"must go from $D5 to $D7 " in {

          navigator
            .routeMap(SelectAddressPage)(associatedEnterprisesRoutingInCheckMode)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
        }

        // manual?

        s"must go from $D6 to $D7 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(associatedEnterprisesRoutingInCheckMode)(Some(address))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $E11 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D8 to $E11 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some("email@email.com"))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(associatedEnterprisesRoutingInCheckMode)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(associatedEnterprisesRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D14 to $E11 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(associatedEnterprisesRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }
      }

    }

    "Relevant taxpayers routing" - {

      "in Normal mode" - {

        s"must go from $D14 to $T9 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(TaxpayersRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.MarketableArrangementGatewayController.onRouting(NormalMode))
        }
      }

      "in Check mode" - {

        val taxpayersRoutingInCheckMode = TaxpayersRouting(CheckMode)

        s"must go from $D1 to $T11" in {

          navigator
            .routeMap(OrganisationNamePage)(taxpayersRoutingInCheckMode)(Some("name"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D2 to $D3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(taxpayersRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(CheckMode))
        }

        s"must go from $D2 to $T11 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(taxpayersRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D3 to $D4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(taxpayersRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(CheckMode))
        }

        s"must go from $D3 to $D6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(taxpayersRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(CheckMode))
        }

        s"must go from $D4 to $D5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(taxpayersRoutingInCheckMode)(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(CheckMode))

        }

        s"must go from $D5 to $D7 " in {

          navigator
            .routeMap(SelectAddressPage)(taxpayersRoutingInCheckMode)(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
        }

        // manual?

        s"must go from $D6 to $D7 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(taxpayersRoutingInCheckMode)(Some(address))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $D8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(taxpayersRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(CheckMode))
        }

        s"must go from $D7 to $T11 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(taxpayersRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D8 to $T11 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(taxpayersRoutingInCheckMode)(Some("email@email.com"))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }

        s"must go from $D9 to $D10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(taxpayersRoutingInCheckMode)(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D9 to $D11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(taxpayersRoutingInCheckMode)(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D10 to $D12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(taxpayersRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D10 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(taxpayersRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D11 to $D13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(taxpayersRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D11 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(taxpayersRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D12 to $D14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(taxpayersRoutingInCheckMode)(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D13 to $D14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(taxpayersRoutingInCheckMode)(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode, index + 1))
        }

        s"must go from $D14 to $D9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(taxpayersRoutingInCheckMode)(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode, index))
        }

        s"must go from $D14 to $T11 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(taxpayersRoutingInCheckMode)(Some(false))(0)
            .mustBe(controllers.taxpayer.routes.TaxpayersCheckYourAnswersController.onPageLoad())
        }
      }

    }

    "Add intermediaries routing" - {

      "in Normal mode" - {

        s"must go from $D14 to $I9 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(IntermediariesRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(NormalMode))
        }
      }
    }

  }
}


