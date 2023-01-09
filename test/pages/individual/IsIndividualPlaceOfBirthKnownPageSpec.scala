/*
 * Copyright 2023 HM Revenue & Customs
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

package pages.individual

import helpers.data.ValidUserAnswersForSubmission.validIndividual
import pages.behaviours.PageBehaviours

class IsIndividualPlaceOfBirthKnownPageSpec extends PageBehaviours {

  "IsIndividualPlaceOfBirthKnownPage" - {

    beRetrievable[Boolean](IsIndividualPlaceOfBirthKnownPage)

    beSettable[Boolean](IsIndividualPlaceOfBirthKnownPage)

    beRemovable[Boolean](IsIndividualPlaceOfBirthKnownPage)
  }

  "can restore from model " - {

    "- when place of birth exists " in {

      IsIndividualPlaceOfBirthKnownPage.getFromModel(validIndividual) mustBe (Some(true))
    }

    "- when place of birth is empty " in {

      IsIndividualPlaceOfBirthKnownPage.getFromModel(validIndividual.copy(birthPlace = Some(""))) mustBe (Some(false))
    }

    "- when place of birth is not defined " in {

      IsIndividualPlaceOfBirthKnownPage.getFromModel(validIndividual.copy(birthPlace = None)) mustBe (Some(false))
    }
  }
}
