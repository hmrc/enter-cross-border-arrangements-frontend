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

import java.time.LocalDate
import scala.xml.PrettyPrinter

class DisclosingXMLSectionSpec extends SpecBase {

  val prettyPrinter: PrettyPrinter = new scala.xml.PrettyPrinter(80, 4)

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
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val email = "email@email.com"

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  val today: LocalDate = LocalDate.now
  val todayMinusOneMonth: LocalDate = LocalDate.now.minusMonths(1)
  val todayMinusTwoMonths: LocalDate = LocalDate.now.minusMonths(2)
  val taxpayers = IndexedSeq(
    Taxpayer("123", None, Some(organisation), Some(todayMinusOneMonth)),
    Taxpayer("Another ID", None, Some(organisation.copy(organisationName = "Other Taxpayers Ltd")), Some(todayMinusTwoMonths)))


  "DisclosingXMLSection" - {

    "buildLiability must build the optional liability section" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value

      val result = DisclosingXMLSection.buildLiability(userAnswers)

      val expected =
        """<Liability>
          |    <RelevantTaxpayerDiscloser>
          |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
          |        <Capacity>DAC61104</Capacity>
          |    </RelevantTaxpayerDiscloser>
          |</Liability>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildLiability must not build the optional liability section if /why-report-in-uk data is missing" in {
      val result = DisclosingXMLSection.buildLiability(UserAnswers(userAnswersId))

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildLiability must not build the optional liability section if answer is 'doNotKnow' to /why-report-in-uk" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.DoNotKnow).success.value

      val result = DisclosingXMLSection.buildLiability(userAnswers)

      prettyPrinter.formatNodes(result) mustBe ""
    }

    "buildLiability must not include the optional capacity section if answer is missing to /why-reporting" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value

      val result = DisclosingXMLSection.buildLiability(userAnswers)

      val expected =
        """<Liability>
          |    <RelevantTaxpayerDiscloser>
          |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
          |    </RelevantTaxpayerDiscloser>
          |</Liability>""".stripMargin

      prettyPrinter.formatNodes(result) mustBe expected
    }

    "buildLiability must not include the optional capacity section if answer is 'doNotKnow' to /why-reporting" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.DoNotKnow).success.value

      val result = DisclosingXMLSection.buildLiability(userAnswers)

      val expected =
        """<Liability>
          |    <RelevantTaxpayerDiscloser>
          |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
          |    </RelevantTaxpayerDiscloser>
          |</Liability>""".stripMargin

      println(s"\n\n${prettyPrinter.formatNodes(result)}\n\n")

      prettyPrinter.formatNodes(result) mustBe expected
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

    "renderXML must render an Elem" ignore {
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

//      implicit val request: DataRequest[AnyContent] = DataRequest[AnyContent](fakeRequest, "XADAC0001122345", "enrolmentID", userAnswers)

//      val result = xmlRenderer.renderXML(userAnswers)
//
//      println(s"\n\n${prettyPrinter.format(result)}\n\n")
//
//      result mustBe "<DAC6_Arrangement></DAC6_Arrangement>"

    }
  }

}
