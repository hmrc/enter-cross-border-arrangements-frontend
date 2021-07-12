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

package pages.individual

import helpers.data.ValidUserAnswersForSubmission.validIndividual
import pages.behaviours.PageBehaviours

class IndividualPlaceOfBirthPageSpec extends PageBehaviours {

  "IndividualPlaceOfBirthPage" - {

    beRetrievable[String](IndividualPlaceOfBirthPage)

    beSettable[String](IndividualPlaceOfBirthPage)

    beRemovable[String](IndividualPlaceOfBirthPage)
  }

  "can restore from model " - {

    "- when place of birth exists " in {

      IndividualPlaceOfBirthPage.getFromModel(validIndividual) mustBe (Some("SomePlace"))
    }

    "- when place of birth is empty " in {

      IndividualPlaceOfBirthPage.getFromModel(validIndividual.copy(birthPlace = Some(""))) mustBe None
    }

    "- when place of birth is not defined " in {

      IndividualPlaceOfBirthPage.getFromModel(validIndividual.copy(birthPlace = None)) mustBe None
    }
  }
}
