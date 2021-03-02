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
import helpers.data.ValidUserAnswersForSubmission.{userAnswersForIndividual, userAnswersForOrganisation}
import helpers.xml.GeneratedXMLExamples
import models.Submission
import helpers.xml.GeneratedXMLExamples
import models.arrangement.{ExpectedArrangementValue, WhichExpectedInvolvedCountriesArrangement, WhyAreYouReportingThisArrangementNow}
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.enterprises.AssociatedEnterprise
import models.hallmarks.{HallmarkD, HallmarkD1}
import models.individual.Individual
import models.intermediaries.{ExemptCountries, Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.reporter.taxpayer.TaxpayerWhyReportInUK
import models.requests.DataRequestWithContacts
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, Country, IsExemptionKnown, LoopDetails, Name, ReporterOrganisationOrIndividual, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.joda.time.DateTime
import pages.arrangement._
import pages.disclosure.{DisclosureDetailsPage, DisclosureMarketablePage, FirstInitialDisclosureMAPage}
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.reporter.individual._
import pages.reporter.organisation.{ReporterOrganisationAddressPage, ReporterOrganisationEmailAddressPage, ReporterOrganisationNamePage}
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportInUKPage}
import pages.reporter.{ReporterOrganisationOrIndividualPage, ReporterTaxResidencyLoopPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import pages.{GiveDetailsOfThisArrangementPage, WhatIsTheExpectedValueOfThisArrangementPage}

class XMLGenerationServiceSpec extends SpecBase {

  val xmlGenerationService: XMLGenerationService = injector.instanceOf[XMLGenerationService]

  val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)

  "XMLGenerationService" - {

    "must build the full XML for a reporter that is an ORGANISATION" in {

      xmlGenerationService.createXmlSubmission(Submission(userAnswersForOrganisation, 0, "XADAC0001122345")) map { result =>

        prettyPrinter.format(result) mustBe GeneratedXMLExamples.xmlForOrganisation
      }
    }

    "must build the full XML for a reporter that is an INDIVIDUAL" in {

      xmlGenerationService.createXmlSubmission(Submission(userAnswersForIndividual, 0, "XADAC0001122345")) map { result =>

        prettyPrinter.format(result) mustBe GeneratedXMLExamples.xmlForIndividual
      }
    }

  }
}
