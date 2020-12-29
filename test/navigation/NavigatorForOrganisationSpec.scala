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
import controllers.mixins.{AssociatedEnterprisesRouting, DefaultRouting}
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

  info("Page index for the Organisation Navigator test")
  info(" --- In the organisation sub-journey")
  info(" 1 - What is the name of the organisation?")
  info(" 2 - Do you know {0}’s Address?")
  info(" 3 - Is {0}’s main address in the United Kingdom?")
  info(" 4 - What is the {0}'s postcode?")
  info(" 5 - What is {0}'s main address? (/select-address)")
  info(" 6 - What is {0}'s main address? (/address)")
  info(" 7 - Do you know the email address for a main contact at {0}?")
  info(" 8 - What is the email address for a main contact at {0}?")
  info(" 9 - Which country is {0} resident in for tax purposes?")
  info("10 - Do you know any of {0}’s tax reference numbers for the United Kingdom?")
  info("11 - Do you know {0}’s tax identification numbers for the country?")
  info("12 - What are {0}’s tax reference numbers for the United Kingdom?")
  info("13 - What are {0}’s tax identification numbers for {1}?")
  info("14 - Is {0} resident for tax purposes in any other countries?")
  info("15 - Check your answers")
  info(" --- In the associated enterprise journey")
  info("E10 - Is {0} affected by the arrangement?")
  info("E11 - Check your answers?")

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

        "must go from 1 to 2" in {

          navigator
            .routeMap(OrganisationNamePage)(DefaultRouting(NormalMode))(Some("name"))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressKnownController.onPageLoad(NormalMode))
        }

        "must go from 2 to 3 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationAddressUkController.onPageLoad(NormalMode))
        }

        "must go from 2 to 7 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressKnownPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }

        "must go from 3 to 4 when the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.OrganisationPostcodeController.onPageLoad(NormalMode))
        }

        "must go from 3 to 6 when the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationAddressUkPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationAddressController.onPageLoad(NormalMode))
        }

        "must go from 4 to 5 when the answer has multiple entries " in {

          navigator
            .routeMap(PostcodePage)(DefaultRouting(NormalMode))(Some("ZZ1 ZZ4"))(0)
            .mustBe(controllers.organisation.routes.OrganisationSelectAddressController.onPageLoad(NormalMode))

        }

        "must go from 5 to 7 " in {

          navigator
            .routeMap(SelectAddressPage)(DefaultRouting(NormalMode))(Some("25 Testing Close, Othertown, Z9 3WW"))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }

        // manual?

        "must go from 6 to 7 " in {

          val address: Address = Address(Some("value 1"), Some("value 2"), Some("value 3"), "value 4", Some("XX9 9XX"),
            Country("valid", "FR", "France"))

          navigator
            .routeMap(OrganisationAddressPage)(DefaultRouting(NormalMode))(Some(address))(0)
            .mustBe(controllers.organisation.routes.EmailAddressQuestionForOrganisationController.onPageLoad(NormalMode))
        }

        "must go from 7 to 8 if the answer is 'Yes' " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.EmailAddressForOrganisationController.onPageLoad(NormalMode))
        }

        "must go from 7 to 9 if the answer is false " in {

          navigator
            .routeMap(EmailAddressQuestionForOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 8 to 9 " in {

          navigator
            .routeMap(EmailAddressForOrganisationPage)(DefaultRouting(NormalMode))(Some("email@email.com"))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 9 to 10 if the answer is GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(Some(country))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowAnyTINForUKOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 9 to 11 if the answer is not GB" in {

          navigator
            .routeMap(WhichCountryTaxForOrganisationPage)(DefaultRouting(NormalMode))(Some(Country("valid", "FR", "France")))(0)
            .mustBe(controllers.organisation.routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 10 to 12 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 10 to 14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowAnyTINForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        "must go from 11 to 13 if the answer is 'Yes' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhatAreTheTaxNumbersForNonUKOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 11 to 14 if the answer is 'No' " in {

          navigator
            .routeMap(DoYouKnowTINForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        "must go from 12 to 14 " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForUKOrganisationPage)(DefaultRouting(NormalMode))(Some(TaxReferenceNumbers("1234567890", None, None)))(index)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        "must go from 13 to 14 if the answer is 'No' " in {

          navigator
            .routeMap(WhatAreTheTaxNumbersForNonUKOrganisationPage)(DefaultRouting(NormalMode))(Some(TaxReferenceNumbers("1234567890", None, None)))(0)
            .mustBe(controllers.organisation.routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(NormalMode, index + 1))
        }

        "must go from 14 to 9 if the answer is 'Yes' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(true))(0)
            .mustBe(controllers.organisation.routes.WhichCountryTaxForOrganisationController.onPageLoad(NormalMode, index))
        }

        "must go from 14 to 15 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(DefaultRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.organisation.routes.OrganisationCheckYourAnswersController.onPageLoad())
        }
      }
    }

    "Associated enterprise routing" - {

      "in Normal mode" - {

        "must go from 14 to E10 if the answer is 'No' " in {

          navigator
            .routeMap(IsOrganisationResidentForTaxOtherCountriesPage)(AssociatedEnterprisesRouting(NormalMode))(Some(false))(0)
            .mustBe(controllers.enterprises.routes.IsAssociatedEnterpriseAffectedController.onPageLoad(NormalMode))
        }
      }
    }

  }
}

