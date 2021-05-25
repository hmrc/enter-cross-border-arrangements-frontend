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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.arrangement._
import pages.disclosure._
import pages.enterprises.{IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.intermediaries._
import pages.organisation._
import pages.reporter._
import pages.reporter.individual.{ReporterIndividualEmailAddressPage, ReporterIndividualEmailAddressQuestionPage, _}
import pages.reporter.intermediary._
import pages.reporter.organisation.{ReporterOrganisationEmailAddressPage, ReporterOrganisationEmailAddressQuestionPage, ReporterOrganisationPostcodePage}
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import pages.taxpayer._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(RemoveTaxpayerPage.type, JsValue)] ::
    arbitrary[(RemoveDisclosurePage.type, JsValue)] ::
    arbitrary[(ReplaceOrDeleteADisclosurePage.type, JsValue)] ::
    arbitrary[(ReporterOtherTaxResidentQuestionPage.type, JsValue)] ::
    arbitrary[(ReporterNonUKTaxNumbersPage.type, JsValue)] ::
    arbitrary[(ReporterUKTaxNumbersPage.type, JsValue)] ::
    arbitrary[(ReporterTinNonUKQuestionPage.type, JsValue)] ::
    arbitrary[(ReporterTinUKQuestionPage.type, JsValue)] ::
    arbitrary[(ReporterTaxResidentCountryPage.type, JsValue)] ::
    arbitrary[(ReporterOrganisationOrIndividualPage.type, JsValue)] ::
    arbitrary[(ReporterOrganisationEmailAddressPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualEmailAddressPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualEmailAddressQuestionPage.type, JsValue)] ::
    arbitrary[(ReporterOrganisationEmailAddressQuestionPage.type, JsValue)] ::
    arbitrary[(ReporterOrganisationEmailAddressQuestionPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualEmailAddressPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualSelectAddressPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualAddressPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualPostcodePage.type, JsValue)] ::
    arbitrary[(ReporterIsIndividualAddressUKPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualPlaceOfBirthPage.type, JsValue)] ::
    arbitrary[(ReporterIndividualDateOfBirthPage.type, JsValue)] ::
    arbitrary[(DisclosureIdentifyArrangementPage.type, JsValue)] ::
    arbitrary[(ReporterOrganisationPostcodePage.type, JsValue)] ::
    arbitrary[(ReporterIndividualNamePage.type, JsValue)] ::
    arbitrary[(WhatTypeofIntermediaryPage.type, JsValue)] ::
    arbitrary[(YouHaveNotAddedAnyIntermediariesPage.type, JsValue)] ::
    arbitrary[(IsExemptionKnownPage.type, JsValue)] ::
    arbitrary[(IsExemptionCountryKnownPage.type, JsValue)] ::
    arbitrary[(ExemptCountriesPage.type, JsValue)] ::
    arbitrary[(TaxpayerWhyReportArrangementPage.type, JsValue)] ::
    arbitrary[(TaxpayerWhyReportInUKPage.type, JsValue)] ::
    arbitrary[(IntermediaryWhichCountriesExemptPage.type, JsValue)] ::
    arbitrary[(IntermediaryDoYouKnowExemptionsPage.type, JsValue)] ::
    arbitrary[(IntermediaryExemptionInEUPage.type, JsValue)] ::
    arbitrary[(IntermediaryRolePage.type, JsValue)] ::
    arbitrary[(DisclosureMarketablePage.type, JsValue)] ::
    arbitrary[(DisclosureTypePage.type, JsValue)] ::
    arbitrary[(DisclosureNamePage.type, JsValue)] ::
    arbitrary[(IntermediaryWhyReportInUKPage.type, JsValue)] ::
    arbitrary[(RoleInArrangementPage.type, JsValue)] ::
    arbitrary[(IsIndividualDateOfBirthKnownPage.type, JsValue)] ::
    arbitrary[(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage.type, JsValue)] ::
    arbitrary[(WhatIsTaxpayersStartDateForImplementingArrangementPage.type, JsValue)] ::
    arbitrary[(TaxpayerSelectTypePage.type, JsValue)] ::
    arbitrary[(UpdateTaxpayerPage.type, JsValue)] ::
    arbitrary[(IsAssociatedEnterpriseAffectedPage.type, JsValue)] ::
    arbitrary[(YouHaveNotAddedAnyAssociatedEnterprisesPage.type, JsValue)] ::
    arbitrary[(GiveDetailsOfThisArrangementPage.type, JsValue)] ::
    arbitrary[(WhichNationalProvisionsIsThisArrangementBasedOnPage.type, JsValue)] ::
    arbitrary[(WhatIsTheExpectedValueOfThisArrangementPage.type, JsValue)] ::
    arbitrary[(WhichExpectedInvolvedCountriesArrangementPage.type, JsValue)] ::
    arbitrary[(WhyAreYouReportingThisArrangementNowPage.type, JsValue)] ::
    arbitrary[(WhatIsTheImplementationDatePage.type, JsValue)] ::
    arbitrary[(WhatIsThisArrangementCalledPage.type, JsValue)] ::
    arbitrary[(DoYouKnowTINForNonUKIndividualPage.type, JsValue)] ::
    arbitrary[(EmailAddressQuestionForIndividualPage.type, JsValue)] ::
    arbitrary[(EmailAddressForIndividualPage.type, JsValue)] ::
    arbitrary[(WhatAreTheTaxNumbersForNonUKOrganisationPage.type, JsValue)] ::
    arbitrary[(DoYouKnowTINForNonUKOrganisationPage.type, JsValue)] ::
    arbitrary[(WhichCountryTaxForIndividualPage.type, JsValue)] ::
    arbitrary[(WhatAreTheTaxNumbersForUKIndividualPage.type, JsValue)] ::
    arbitrary[(IsIndividualResidentForTaxOtherCountriesPage.type, JsValue)] ::
    arbitrary[(DoYouKnowAnyTINForUKIndividualPage.type, JsValue)] ::
    arbitrary[(IsOrganisationResidentForTaxOtherCountriesPage.type, JsValue)] ::
    arbitrary[(WhatAreTheTaxNumbersForUKOrganisationPage.type, JsValue)] ::
    arbitrary[(DoYouKnowAnyTINForUKOrganisationPage.type, JsValue)] ::
    arbitrary[(WhichCountryTaxForOrganisationPage.type, JsValue)] ::
    arbitrary[(EmailAddressForOrganisationPage.type, JsValue)] ::
    arbitrary[(EmailAddressQuestionForOrganisationPage.type, JsValue)] ::
    arbitrary[(IsIndividualPlaceOfBirthKnownPage.type, JsValue)] ::
    arbitrary[(IsIndividualAddressKnownPage.type, JsValue)] ::
    arbitrary[(IndividualPlaceOfBirthPage.type, JsValue)] ::
    arbitrary[(IndividualNamePage.type, JsValue)] ::
    arbitrary[(IndividualDateOfBirthPage.type, JsValue)] ::
    arbitrary[(OrganisationAddressPage.type, JsValue)] ::
    arbitrary[(OrganisationNamePage.type, JsValue)] ::
    arbitrary[(IsOrganisationAddressKnownPage.type, JsValue)] ::
    arbitrary[(IsOrganisationAddressUkPage.type, JsValue)] ::
    arbitrary[(PostcodePage.type, JsValue)] ::
    arbitrary[(HallmarkD1OtherPage.type, JsValue)] ::
    arbitrary[(HallmarkD1Page.type, JsValue)] ::
    arbitrary[(HallmarkDPage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id      <- nonEmptyString
        data    <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers (
        id = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
