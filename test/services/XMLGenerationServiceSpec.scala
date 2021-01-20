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

package services

import java.time.LocalDate

import base.SpecBase
import helpers.xml.GeneratedXMLExamples
import models.arrangement.{WhatIsTheExpectedValueOfThisArrangement, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.disclosure.DisclosureType
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import models.hallmarks.{HallmarkD, HallmarkD1}
import models.intermediaries.{ExemptCountries, Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.requests.DataRequest
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, Country, IsExemptionKnown, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UserAnswers}
import org.joda.time.DateTime
import pages.arrangement._
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureNamePage, DisclosureTypePage}
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.reporter.individual._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}
import play.api.mvc.AnyContent

class XMLGenerationServiceSpec extends SpecBase {

  val xmlGenerationService: XMLGenerationService = injector.instanceOf[XMLGenerationService]

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  val address: Address =
    Address(
      Some("value 1"),
      Some("value 2"),
      Some("value 3"),
      "value 4",
      Some("XX9 9XX"),
      Country("valid","FR","France")
    )

  val loopDetails = IndexedSeq(
    LoopDetails(Some(true), Some(Country("valid", "GB", "United Kingdom")),
      Some(true), None, None, Some(TaxReferenceNumbers("1234567890", Some("0987654321"), None))),
    LoopDetails(None, Some(Country("valid", "FR", "France")), None, None, None, None))

  val email = "email@email.com"

  val taxResidencies = IndexedSeq(
    TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))),
    TaxResidency(Some(Country("", "FR", "France")), Some(TaxReferenceNumbers("CS700100A", Some("UTR5678"), None)))
  )

  val organisation: Organisation = Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)

  def today: LocalDate = LocalDate.now
  val todayMinusOneMonth: LocalDate = LocalDate.now.minusMonths(1)
  val todayMinusTwoMonths: LocalDate = LocalDate.now.minusMonths(2)
  val taxpayers = IndexedSeq(
    Taxpayer("123", None, Some(organisation), Some(todayMinusOneMonth)),
    Taxpayer("Another ID", None, Some(organisation.copy(organisationName = "Other Taxpayers Ltd")), Some(todayMinusTwoMonths)))

  val exemptCountries: Set[ExemptCountries] = Seq(ExemptCountries.UnitedKingdom, ExemptCountries.France).toSet

  val intermediaries = IndexedSeq(
    Intermediary("123", None, Some(organisation), WhatTypeofIntermediary.Promoter, IsExemptionKnown.Yes, Some(true), Some(exemptCountries)),
    Intermediary("Another ID", None, Some(organisation.copy(organisationName = "Other Taxpayers Ltd")),
      WhatTypeofIntermediary.Promoter, IsExemptionKnown.No, None, None))

  val countries: Set[WhichExpectedInvolvedCountriesArrangement] =
    Seq(WhichExpectedInvolvedCountriesArrangement.UnitedKingdom, WhichExpectedInvolvedCountriesArrangement.France).toSet


  "XMLGenerationService" - {

    "buildHeader must build the XML header" in {
      val mandatoryTimestamp: String = DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ss")

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureNamePage, "DisclosureName").success.value

      implicit val request: DataRequest[AnyContent] =
        DataRequest[AnyContent](fakeRequest, "internalID", "XADAC0001122345", userAnswers)

      val result = xmlGenerationService.buildHeader(userAnswers)

      val expected =
      s"""<Header>
        |    <MessageRefId>GBXADAC0001122345DisclosureName</MessageRefId>
        |    <Timestamp>$mandatoryTimestamp</Timestamp>
        |</Header>""".stripMargin

      prettyPrinter.format(result) mustBe expected
    }

    "buildHeader must throw an exception if disclosure name is missing" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(HallmarkDPage, HallmarkD.enumerable.withName("D1").toSet).success.value

      implicit val request: DataRequest[AnyContent] =
        DataRequest[AnyContent](fakeRequest, "internalID", "XADAC0001122345", userAnswers)

      assertThrows[Exception] {
        xmlGenerationService.buildHeader(userAnswers)
      }
    }

    "buildDisclosureImportInstruction must build the import instruction section if additional arrangement" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureTypePage, DisclosureType.Dac6add).success.value

      val result = xmlGenerationService.buildDisclosureImportInstruction(userAnswers)

      val expected = "<DisclosureImportInstruction>DAC6ADD</DisclosureImportInstruction>"

      prettyPrinter.format(result) mustBe expected
    }

    "buildDisclosureImportInstruction must build the import instruction section if new arrangement" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureTypePage, DisclosureType.Dac6new).success.value

      val result = xmlGenerationService.buildDisclosureImportInstruction(userAnswers)

      val expected = "<DisclosureImportInstruction>DAC6NEW</DisclosureImportInstruction>"

      prettyPrinter.format(result) mustBe expected
    }

    "buildDisclosureImportInstruction must throw an exception if import instruction is missing" in {
      assertThrows[Exception] {
        xmlGenerationService.buildDisclosureImportInstruction(UserAnswers(userAnswersId))
      }
    }

    "buildInitialDisclosureMA must build the disclosure MA section when arrangement is marketable" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureMarketablePage, true).success.value

      val result = xmlGenerationService.buildInitialDisclosureMA(userAnswers)

      val expected = "<InitialDisclosureMA>true</InitialDisclosureMA>"

      prettyPrinter.format(result) mustBe expected
    }

    "buildInitialDisclosureMA must build the disclosure MA section when arrangement is not marketable" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureMarketablePage, false).success.value

      val result = xmlGenerationService.buildInitialDisclosureMA(userAnswers)

      val expected = "<InitialDisclosureMA>false</InitialDisclosureMA>"

      prettyPrinter.format(result) mustBe expected
    }

    "buildInitialDisclosureMA must throw an exception if disclosure MA is missing" in {
      assertThrows[Exception] {
        xmlGenerationService.buildInitialDisclosureMA(UserAnswers(userAnswersId))
      }
    }

    "buildArrangementID" - {

      "must build arrangement ID when user Selects DAC6ADD & enters a valid arrangementID" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6add).success.value
          .set(DisclosureIdentifyArrangementPage, "GBA20210120FOK5BT").success.value

        val result = xmlGenerationService.buildArrangementID(userAnswers)

        val expected = "<ArrangementID>GBA20210120FOK5BT</ArrangementID>"

        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must NOT build arrangement ID when user Selects DAC6NEW" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(DisclosureTypePage, Dac6new).success.value

        val result = xmlGenerationService.buildArrangementID(userAnswers)

        val expected = ""

        prettyPrinter.formatNodes(result) mustBe expected
      }

    }

    "must build the full XML for a reporter that is an ORGANISTION" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureNamePage, "DisclosureName").success.value
        .set(DisclosureTypePage, DisclosureType.Dac6new).success.value
        .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Organisation).success.value
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(ReporterOrganisationNamePage, "Reporter name").success.value
        .set(ReporterOrganisationAddressPage, address).success.value
        .set(ReporterOrganisationEmailAddressPage, "email@email.co.uk").success.value
        .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
        .set(DisclosureMarketablePage, true).success.value
        .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
        .set(TaxpayerLoopPage, taxpayers).success.value
        .set(WhatIsTheImplementationDatePage, today).success.value
        .set(DoYouKnowTheReasonToReportArrangementNowPage, true).success.value
        .set(WhyAreYouReportingThisArrangementNowPage, WhyAreYouReportingThisArrangementNow.Dac6703).success.value
        .set(WhatIsThisArrangementCalledPage, "Arrangement name").success.value
        .set(GiveDetailsOfThisArrangementPage, "Some description").success.value
        .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, "National provisions description").success.value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangement("GBP", 1000)).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries).success.value
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallmark D1 other description").success.value

      implicit val request: DataRequest[AnyContent] =
        DataRequest[AnyContent](fakeRequest, "internalID", "XADAC0001122345", userAnswers)

      val result = xmlGenerationService.createXmlSubmission(userAnswers)

      prettyPrinter.format(result) mustBe GeneratedXMLExamples.xmlForOrganisation
    }

    "must build the full XML for a reporter that is an INDIVIDUAL" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(DisclosureNamePage, "DisclosureName").success.value
        .set(DisclosureTypePage, DisclosureType.Dac6new).success.value
        .set(DisclosureMarketablePage, true).success.value
        .set(ReporterOrganisationOrIndividualPage, ReporterOrganisationOrIndividual.Individual).success.value
        .set(ReporterIndividualNamePage, Name("Reporter", "Name")).success.value
        .set(ReporterIndividualDateOfBirthPage, LocalDate.of(1990,1,1)).success.value
        .set(ReporterIndividualPlaceOfBirthPage, "SomePlace").success.value
        .set(ReporterIndividualAddressPage, address).success.value
        .set(ReporterIndividualEmailAddressPage, "email@email.com").success.value
        .set(ReporterTaxResidencyLoopPage, loopDetails).success.value
        .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
        .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
        .set(ReporterTaxpayersStartDateForImplementingArrangementPage, today).success.value
        .set(TaxpayerLoopPage, taxpayers).success.value
        .set(WhatIsTheImplementationDatePage, today).success.value
        .set(DoYouKnowTheReasonToReportArrangementNowPage, true).success.value
        .set(WhyAreYouReportingThisArrangementNowPage, WhyAreYouReportingThisArrangementNow.Dac6703).success.value
        .set(WhatIsThisArrangementCalledPage, "Arrangement name").success.value
        .set(GiveDetailsOfThisArrangementPage, "Some description").success.value
        .set(WhichNationalProvisionsIsThisArrangementBasedOnPage, "National provisions description").success.value
        .set(WhatIsTheExpectedValueOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangement("GBP", 1000)).success.value
        .set(WhichExpectedInvolvedCountriesArrangementPage, countries).success.value
        .set(HallmarkDPage, HallmarkD.values.toSet).success.value
        .set(HallmarkD1Page, (HallmarkD1.enumerable.withName("DAC6D1a") ++
          HallmarkD1.enumerable.withName("DAC6D1Other")).toSet).success.value
        .set(HallmarkD1OtherPage, "Hallmark D1 other description").success.value

      implicit val request: DataRequest[AnyContent] =
        DataRequest[AnyContent](fakeRequest, "internalID", "XADAC0001122345", userAnswers)

      val result = xmlGenerationService.createXmlSubmission(userAnswers)

      prettyPrinter.format(result) mustBe GeneratedXMLExamples.xmlForIndividual

    }
  }

}
