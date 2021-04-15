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

import helpers.data.ValidUserAnswersForSubmission.{validIndividual, validTaxResidencies}
import models.Country
import pages.behaviours.PageBehaviours


class WhichCountryTaxForIndividualPageSpec extends PageBehaviours {

  "WhichCountryTaxForIndividualPage" - {

    beRetrievable[Country](WhichCountryTaxForIndividualPage)

    beSettable[Country](WhichCountryTaxForIndividualPage)

    beRemovable[Country](WhichCountryTaxForIndividualPage)
  }

  "can restore from model " - {

    "- when first detail in loop is the UK " in {

      WhichCountryTaxForIndividualPage.getFromModel(validIndividual) mustBe(Some(Country.UK))
    }

    "- when first detail in loop is not from the UK " in {

      WhichCountryTaxForIndividualPage.getFromModel(validIndividual.copy(taxResidencies = validTaxResidencies.reverse)) mustBe(Some(Country("valid", "FR", "France")))
    }

    "- when details are empty " in {

      WhichCountryTaxForIndividualPage.getFromModel(validIndividual.copy(taxResidencies = IndexedSeq.empty)) mustBe(None)
    }
  }
}
