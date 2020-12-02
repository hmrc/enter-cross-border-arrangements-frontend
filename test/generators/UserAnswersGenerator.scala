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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.arrangement._
import pages.enterprises.{IsAssociatedEnterpriseAffectedPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.organisation._
import pages.arrangement._
import pages.enterprises.{SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import pages.hallmarks._
import pages.individual._
import pages.organisation._
import pages.taxpayer.UpdateTaxpayerPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(SelectTypePage.type, JsValue)] ::
    arbitrary[(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage.type, JsValue)] ::
    arbitrary[(UpdateTaxpayerPage.type, JsValue)] ::
    arbitrary[(IsAssociatedEnterpriseAffectedPage.type, JsValue)] ::
    arbitrary[(YouHaveNotAddedAnyAssociatedEnterprisesPage.type, JsValue)] ::
    arbitrary[(GiveDetailsOfThisArrangementPage.type, JsValue)] ::
    arbitrary[(WhichNationalProvisionsIsThisArrangementBasedOnPage.type, JsValue)] ::
    arbitrary[(WhatIsTheExpectedValueOfThisArrangementPage.type, JsValue)] ::
    arbitrary[(WhichExpectedInvolvedCountriesArrangementPage.type, JsValue)] ::
    arbitrary[(WhyAreYouReportingThisArrangementNowPage.type, JsValue)] ::
    arbitrary[(DoYouKnowTheReasonToReportArrangementNowPage.type, JsValue)] ::
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
    arbitrary[(HallmarkEPage.type, JsValue)] ::
    arbitrary[(HallmarkC1Page.type, JsValue)] ::
    arbitrary[(HallmarkCPage.type, JsValue)] ::
    arbitrary[(HallmarkD1OtherPage.type, JsValue)] ::
    arbitrary[(HallmarkD1Page.type, JsValue)] ::
    arbitrary[(HallmarkDPage.type, JsValue)] ::
    arbitrary[(HallmarkBPage.type, JsValue)] ::
    arbitrary[(MainBenefitTestPage.type, JsValue)] ::
    arbitrary[(HallmarkAPage.type, JsValue)] ::
    arbitrary[(HallmarkCategoriesPage.type, JsValue)] ::
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
