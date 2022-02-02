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

package pages.organisation

import helpers.data.ValidUserAnswersForSubmission.validOrganisation
import pages.behaviours.PageBehaviours

class OrganisationNamePageSpec extends PageBehaviours {

  "OrganisationNamePage" - {

    beRetrievable[String](OrganisationNamePage)

    beSettable[String](OrganisationNamePage)

    beRemovable[String](OrganisationNamePage)
  }

  "can restore from model " - {

    "- when name exists " in {

      OrganisationNamePage.getFromModel(validOrganisation) mustBe (Some("Taxpayers Ltd"))
    }
  }
}
