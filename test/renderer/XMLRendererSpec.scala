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

package renderer

import base.SpecBase
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.disclosure.DisclosureType
import models.hallmarks.{HallmarkD, HallmarkD1}
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{Address, Country, LoopDetails, TaxReferenceNumbers, UserAnswers}
import pages.arrangement._
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import pages.reporter.RoleInArrangementPage
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementPage
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}

import java.time.LocalDate

class XMLRendererSpec extends SpecBase {

  val xmlRenderer: XMLRenderer = injector.instanceOf[XMLRenderer]

  //      val selectedCountry: Country = Country("valid", "GB", "United Kingdom")
  val address: Address =
    Address(
      Some("value 1"),
      Some("value 2"),
      Some("value 3"),
      "value 4",
      Some("XX9 9XX"),
      Country("valid","FR","France")
    )
  val organisationLoopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")), Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("AnotherTIN"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  "XMLRenderer" - {

    "buildIDForOrganisation must render an Elem" ignore {
      val userAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "Name Here").success.value
        .set(OrganisationLoopPage, organisationLoopDetails).success.value
        .set(OrganisationAddressPage, address).success.value
        .set(EmailAddressForOrganisationPage, "email@email.com").success.value

      val result = xmlRenderer.buildIDForOrganisation(userAnswers)

      println(s"\n\n$result\n\n")

      result mustBe "<ID></ID>"

    }

    "buildLiability must render an Elem" ignore {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value

      val result = xmlRenderer.buildLiability(userAnswers)

      println(s"\n\n$result\n\n")

      result mustBe "<Liability></Liability>"

    }

    "buildRelevantTaxPayers must render an Elem" ignore {
      val userAnswers = UserAnswers(userAnswersId)
        .set(OrganisationNamePage, "Name Here").success.value
        .set(OrganisationLoopPage, organisationLoopDetails).success.value
        .set(OrganisationAddressPage, address).success.value
        .set(EmailAddressForOrganisationPage, "email@email.com").success.value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, LocalDate.now.minusDays(10)).success.value

      val result = xmlRenderer.buildRelevantTaxPayers(userAnswers)

      println(s"\n\n$result\n\n")

      result mustBe "<RelevantTaxPayers></RelevantTaxPayers>"

    }

    "buildDisclosureInformation must render an Elem" ignore {
      val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
        Seq(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom, WhichExpectedInvolvedCountriesArrangement.Austria).toSet

      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTheImplementationDatePage, LocalDate.now()).success.value
        .set(DoYouKnowTheReasonToReportArrangementNowPage, true).success.value
        .set(WhyAreYouReportingThisArrangementNowPage, WhyAreYouReportingThisArrangementNow.Dac6703).success.value
        .set(WhatIsThisArrangementCalledPage, "Name name").success.value
        .set(GiveDetailsOfThisArrangementPage, "Should be some long string but it will be short for now").success.value
        .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, "Another section that will have long strings").success.value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangement("GBP", 1000)).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries).success.value
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("D1a") ++
          HallmarkD1.enumerable.withName("D1other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallllllllllmark D1 oooooother desciption").success.value

      val result = xmlRenderer.buildDisclosureInformation(userAnswers)

      println(s"\n\n$result\n\n")

      result mustBe "<DisclosureInformation></DisclosureInformation>"

    }

    "renderXML must render an Elem" in {
      val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
        Seq(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom, WhichExpectedInvolvedCountriesArrangement.Austria).toSet

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureTypePage, DisclosureType.Dac6new).success.value
        .set(DisclosureMarketablePage, true).success.value
        .set(DisclosureNamePage, "DisclosureNomme").success.value
        .set(OrganisationNamePage, "Name Here").success.value
        .set(OrganisationLoopPage, organisationLoopDetails).success.value
        .set(OrganisationAddressPage, address).success.value
        .set(EmailAddressForOrganisationPage, "email@email.com").success.value
        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, LocalDate.now.minusDays(10)).success.value
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value
        .set(WhatIsTheImplementationDatePage, LocalDate.now()).success.value
        .set(DoYouKnowTheReasonToReportArrangementNowPage, true).success.value
        .set(WhyAreYouReportingThisArrangementNowPage, WhyAreYouReportingThisArrangementNow.Dac6703).success.value
        .set(WhatIsThisArrangementCalledPage, "Name name").success.value
        .set(GiveDetailsOfThisArrangementPage, "Should be some long string but it will be short for now").success.value
        .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, "Another section that will have long strings").success.value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangement("GBP", 1000)).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries).success.value
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("D1a") ++
          HallmarkD1.enumerable.withName("D1other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallllllllllmark D1 oooooother desciption").success.value

      val result = xmlRenderer.renderXML(userAnswers)

      println(s"\n\n$result\n\n")

      result mustBe "<DAC6_Arrangement></DAC6_Arrangement>"

    }
  }

}
