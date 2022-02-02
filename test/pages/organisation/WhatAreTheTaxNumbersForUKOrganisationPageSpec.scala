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

import helpers.data.ValidUserAnswersForSubmission.{validOrganisation, validTaxResidencies}
import models.TaxReferenceNumbers
import pages.behaviours.PageBehaviours

class WhatAreTheTaxNumbersForUKOrganisationPageSpec extends PageBehaviours {

  "WhatAreTheTaxNumbersForUKOrganisationPage" - {

    beRetrievable[TaxReferenceNumbers](WhatAreTheTaxNumbersForUKOrganisationPage)

    beSettable[TaxReferenceNumbers](WhatAreTheTaxNumbersForUKOrganisationPage)

    beRemovable[TaxReferenceNumbers](WhatAreTheTaxNumbersForUKOrganisationPage)
  }

  "can restore from model " - {

    "- when first detail in loop is from the UK " in {

      WhatAreTheTaxNumbersForUKOrganisationPage.getFromModel(validOrganisation) mustBe (validTaxResidencies.head.taxReferenceNumbers)
    }

    "- when first detail in loop is not from the UK " in {

      WhatAreTheTaxNumbersForUKOrganisationPage.getFromModel(validOrganisation.copy(taxResidencies = validTaxResidencies.reverse)) mustBe None
    }

    "- when details are empty " in {

      WhatAreTheTaxNumbersForUKOrganisationPage.getFromModel(validOrganisation.copy(taxResidencies = IndexedSeq.empty)) mustBe None
    }
  }
}
