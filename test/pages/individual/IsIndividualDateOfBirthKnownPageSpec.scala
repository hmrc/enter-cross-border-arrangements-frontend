/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.LocalDate

class IsIndividualDateOfBirthKnownPageSpec extends PageBehaviours {

  "IsIndividualDateOfBirthKnownPage" - {

    beRetrievable[Boolean](IsIndividualDateOfBirthKnownPage)

    beSettable[Boolean](IsIndividualDateOfBirthKnownPage)

    beRemovable[Boolean](IsIndividualDateOfBirthKnownPage)
  }

  "can restore from model " - {

    "- when dob exists " in {

      IsIndividualDateOfBirthKnownPage.getFromModel(validIndividual) mustBe (Some(true))
    }

    "- when dob is empty (before 1900-01-02) " in {

      IsIndividualDateOfBirthKnownPage.getFromModel(validIndividual.copy(birthDate = Some(LocalDate.of(1900, 1, 1)))) mustBe (Some(false))
    }
  }
}
