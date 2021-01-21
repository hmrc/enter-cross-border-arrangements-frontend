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

package helpers.xml.disclosing

import helpers.xml.XmlBase
import models.UserAnswers
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary.{IntermediaryRolePage, IntermediaryWhyReportInUKPage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}

class DiscloseDetailsLiabilityXMLSectionSpec extends XmlBase {

  "DiscloseDetailsLiability" - {

    "buildReporterCapacity" - {

      "must build optional reporter capacity for intermediary promoter" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(IntermediaryRolePage, IntermediaryRole.Promoter)
          .success
          .value

        val result = DiscloseDetailsLiability.buildReporterCapacity(userAnswers)
        val expected = "<Capacity>DAC61101</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must build optional reporter capacity for intermediary service provider" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(IntermediaryRolePage, IntermediaryRole.ServiceProvider)
          .success
          .value

        val result = DiscloseDetailsLiability.buildReporterCapacity(userAnswers)
        val expected = "<Capacity>DAC61102</Capacity>"
        prettyPrinter.formatNodes(result) mustBe expected
      }

      "must not build the optional reporter capacity if answer is 'doNotKnow' in intermediary/why-report-in-uk" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(IntermediaryRolePage, IntermediaryRole.Unknown)
          .success
          .value

        val result = DiscloseDetailsLiability.buildReporterCapacity(userAnswers)
        val expected = ""
        prettyPrinter.formatNodes(result) mustBe expected

      }

    }

    "buildLiability" - {

      def assert(userAnswers: UserAnswers, expected: String) =
        DiscloseDetailsLiability.build(userAnswers) map { result =>

          prettyPrinter.formatNodes(result) mustBe expected
        }

      "must build the optional liability section for TAXPAYER" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
          .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.ProfessionalPrivilege).success.value

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |        <Capacity>DAC61104</Capacity>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        assert(userAnswers, expected)
      }

      "must build the optional liability section for INTERMEDIARY" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(IntermediaryRolePage, IntermediaryRole.Promoter).success.value

        val expected =
          """<Liability>
            |    <IntermediaryDiscloser>
            |        <IntermediaryNexus>INEXa</IntermediaryNexus>
            |        <Capacity>DAC61101</Capacity>
            |    </IntermediaryDiscloser>
            |</Liability>""".stripMargin

        assert(userAnswers, expected)
      }

      "must not build the optional liability section if data is missing" in {

        assert(UserAnswers(userAnswersId), "")
      }

      "must not build the optional liability section if answer is 'doNotKnow' in intermediary/why-report-in-uk" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.DoNotKnow).success.value

        assert(userAnswers, "")
      }

      "must not build the optional liability section if answer is 'doNotKnow' in taxpayer/why-report-in-uk" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.DoNotKnow).success.value

        assert(userAnswers, "")
      }

      "must not include the optional capacity section if answer is missing in taxpayer/why-reporting" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        assert(userAnswers, expected)
      }

      "must not include the optional capacity section if answer is 'Unknown' in intermediary/role" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Intermediary).success.value
          .set(IntermediaryWhyReportInUKPage, IntermediaryWhyReportInUK.TaxResidentUK).success.value
          .set(IntermediaryRolePage, IntermediaryRole.Unknown).success.value

        val expected =
          """<Liability>
            |    <IntermediaryDiscloser>
            |        <IntermediaryNexus>INEXa</IntermediaryNexus>
            |    </IntermediaryDiscloser>
            |</Liability>""".stripMargin

        assert(userAnswers, expected)
      }

      "must not include the optional capacity section if answer is 'doNotKnow' in taxpayer/why-reporting" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(RoleInArrangementPage, RoleInArrangement.Taxpayer).success.value
          .set(TaxpayerWhyReportInUKPage, TaxpayerWhyReportInUK.UkPermanentEstablishment).success.value
          .set(TaxpayerWhyReportArrangementPage, TaxpayerWhyReportArrangement.DoNotKnow).success.value

        val expected =
          """<Liability>
            |    <RelevantTaxpayerDiscloser>
            |        <RelevantTaxpayerNexus>RTNEXb</RelevantTaxpayerNexus>
            |    </RelevantTaxpayerDiscloser>
            |</Liability>""".stripMargin

        assert(userAnswers, expected)
      }
    }

  }

}
