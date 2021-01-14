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

package helpers.xml

import base.SpecBase
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.disclosure.DisclosureType
import models.hallmarks.{HallmarkD, HallmarkD1}
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.requests.DataRequest
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, Country, LoopDetails, TaxReferenceNumbers, UserAnswers}
import pages.arrangement._
import pages.disclosure.{DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.organisation.{EmailAddressForOrganisationPage, OrganisationAddressPage, OrganisationLoopPage, OrganisationNamePage}
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.{TaxpayerLoopPage, WhatIsTaxpayersStartDateForImplementingArrangementPage}
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}
import play.api.mvc.AnyContent
import services.XMLGenerationService

import java.time.LocalDate

class XMLRendererSpec extends SpecBase {

  val xmlRenderer: XMLGenerationService = injector.instanceOf[XMLGenerationService]

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  //TODO Need to clean up the tests

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

  val email = "email@email.com"
  val taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))
  val organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)
  val taxpayers = IndexedSeq(Taxpayer("123", None, Some(organisation), Some(LocalDate.now)),
    Taxpayer("Another ID", None, Some(organisation), Some(LocalDate.now.minusMonths(1))))

  "XMLRenderer" - {

    "buildIDForOrganisation must render an Elem" ignore {
//      val userAnswers = UserAnswers(userAnswersId)
//        .set(OrganisationNamePage, "Name Here").success.value
//        .set(OrganisationLoopPage, organisationLoopDetails).success.value
//        .set(OrganisationAddressPage, address).success.value
//        .set(EmailAddressForOrganisationPage, "email@email.com").success.value

      val result = RelevantTaxPayersXMLSection.buildIDForOrganisation(organisation)

      println(s"\n\n$result\n\n")

      result mustBe "<ID></ID>"

    }

    "buildLiability must render an Elem" ignore {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value

      val result = DisclosingXMLSection.buildLiability(userAnswers)

      println(s"\n\n$result\n\n")

      result mustBe "<Liability></Liability>"

    }

    "buildDisclosingSection must render an Elem" ignore {
      val userAnswers = UserAnswers(userAnswersId)
        .set(ReporterOrganisationNamePage, "Reporter name").success.value
        .set(ReporterOrganisationAddressPage, address).success.value
        .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, organisationLoopDetails).success.value
        .set(TaxpayerLoopPage, taxpayers).success.value
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkTaxResident).success.value
        .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value

      val result = DisclosingXMLSection.toXml(userAnswers)

      println(s"\n\n${prettyPrinter.format(result)}\n\n")

      result mustBe "<Disclosing></Disclosing>"
    }

    "buildRelevantTaxPayers must render an Elem" ignore {
      val userAnswers = UserAnswers(userAnswersId)
//        .set(OrganisationNamePage, "Name Here").success.value
//        .set(OrganisationLoopPage, organisationLoopDetails).success.value
//        .set(OrganisationAddressPage, address).success.value
//        .set(EmailAddressForOrganisationPage, "email@email.com").success.value
//        .set(WhatIsTaxpayersStartDateForImplementingArrangementPage, LocalDate.now.minusDays(10)).success.value
        .set(TaxpayerLoopPage, taxpayers).success.value

      val result = RelevantTaxPayersXMLSection.toXml(userAnswers)

      println(s"\n\n${prettyPrinter.format(result)}\n\n")

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
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallllllllllmark D1 oooooother desciption").success.value

      val result = DisclosureInformationXMLSection.toXml(userAnswers)

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
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallllllllllmark D1 oooooother desciption").success.value
        .set(TaxpayerLoopPage, taxpayers).success.value
        .set(ReporterOrganisationNamePage, "Reporter name").success.value
        .set(ReporterOrganisationAddressPage, address).success.value
        .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, organisationLoopDetails).success.value

      implicit val request: DataRequest[AnyContent] = DataRequest[AnyContent](fakeRequest, "XADAC0001122345", "enrolmentID", userAnswers)

      val result = xmlRenderer.renderXML(userAnswers)

      println(s"\n\n${prettyPrinter.format(result)}\n\n")

      result mustBe "<DAC6_Arrangement></DAC6_Arrangement>"

    }
  }

}
