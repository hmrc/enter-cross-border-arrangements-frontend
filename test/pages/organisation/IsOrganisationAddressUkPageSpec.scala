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

import helpers.data.ValidUserAnswersForSubmission.{validAddress, validOrganisation}
import models.Country
import pages.behaviours.PageBehaviours

class IsOrganisationAddressUkPageSpec extends PageBehaviours {

  "isOrganisationAddressUkPage" - {

    beRetrievable[Boolean](IsOrganisationAddressUkPage)

    beSettable[Boolean](IsOrganisationAddressUkPage)

    beRemovable[Boolean](IsOrganisationAddressUkPage)
  }

  "can restore from model " - {

    "- when address is in the UK " in {

      IsOrganisationAddressUkPage.getFromModel(validOrganisation.copy(address = Some(validAddress.copy(country = Country.UK)))) mustBe (Some(true))
    }

    "- when address is not in the UK " in {

      IsOrganisationAddressUkPage.getFromModel(validOrganisation) mustBe (Some(false))
    }

    "- when address is empty " in {

      IsOrganisationAddressUkPage.getFromModel(validOrganisation.copy(address = None)) mustBe (Some(false))
    }
  }
}
