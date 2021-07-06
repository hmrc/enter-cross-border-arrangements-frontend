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

package pages.organisation

import helpers.data.ValidUserAnswersForSubmission.{validOrganisation, validTaxResidencies}
import pages.behaviours.PageBehaviours

class DoYouKnowTINForNonUKOrganisationPageSpec extends PageBehaviours {

  "DoYouKnowTINForNonUKOrganisationPage" - {

    beRetrievable[Boolean](DoYouKnowTINForNonUKOrganisationPage)

    beSettable[Boolean](DoYouKnowTINForNonUKOrganisationPage)

    beRemovable[Boolean](DoYouKnowTINForNonUKOrganisationPage)
  }

  "can restore from model " - {

    "- when first detail in loop is from the UK " in {

      DoYouKnowTINForNonUKOrganisationPage.getFromModel(validOrganisation) mustBe (Some(false))
    }

    "- when first detail in loop is not from the UK " in {

      DoYouKnowTINForNonUKOrganisationPage.getFromModel(validOrganisation.copy(taxResidencies = validTaxResidencies.reverse)) mustBe (Some(true))
    }

    "- when details are empty " in {

      DoYouKnowTINForNonUKOrganisationPage.getFromModel(validOrganisation.copy(taxResidencies = IndexedSeq.empty)) mustBe (Some(false))
    }
  }
}
