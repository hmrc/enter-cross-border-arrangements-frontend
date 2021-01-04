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

package navigation

import base.SpecBase
import controllers.mixins._
import generators.Generators
import models.intermediaries.{ExemptCountries, WhatTypeofIntermediary, YouHaveNotAddedAnyIntermediaries}
import models.{IsExemptionKnown, NormalMode, SelectType}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individual.WhatAreTheTaxNumbersForNonUKIndividualPage
import pages.intermediaries._

class NavigatorForIntermediariesSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForIntermediaries

  "NavigatorForIntermediaries" - {

    "must go from 'You have not added any intermediaries' page to " +
      "'Is this an organisation or an individual?' if answer is yes" in {
          navigator
            .routeMap(YouHaveNotAddedAnyIntermediariesPage)(IntermediariesRouting(NormalMode))(Some(YouHaveNotAddedAnyIntermediaries.YesAddNow))(0)
            .mustBe(controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(NormalMode))
      }

    //TODO: Link to task list page when ready
    "must go from 'You have not added any intermediaries' page to " +
      "'You have not added any intermediaries' if answer is 'No'" in {
      navigator
        .routeMap(YouHaveNotAddedAnyIntermediariesPage)(IntermediariesRouting(NormalMode))(Some(YouHaveNotAddedAnyIntermediaries.No))(0)
        .mustBe(controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(NormalMode))
    }

    //TODO: Link to task list page when ready
    "must go from 'You have not added any intermediaries' page to " +
      "'You have not added any intermediaries' if answer is 'YesAddLater'" in {
      navigator
        .routeMap(YouHaveNotAddedAnyIntermediariesPage)(IntermediariesRouting(NormalMode))(Some(YouHaveNotAddedAnyIntermediaries.YesAddLater))(0)
        .mustBe(controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(NormalMode))
    }

    "must go from 'Is this an organisation or an individual?' intermediaries page to " +
      "'What is the name of the organisation?' if answer is Organisation" in {
      navigator
        .routeMap(IntermediariesTypePage)(IntermediariesRouting(NormalMode))(Some(SelectType.Organisation))(0)
        .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(NormalMode))
    }

    "must go from 'Is this an organisation or an individual?' intermediaries page to " +
      "'What is their name?' if answer is Individual" in {
      navigator
        .routeMap(IntermediariesTypePage)(IntermediariesRouting(NormalMode))(Some(SelectType.Individual))(0)
        .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(NormalMode))
    }

    "must go from 'What type of intermediary is name?' page to " +
      "'Is *name* exempt from reporting in an EU member state, or the UK?' if answer is Promoter" in {
      navigator
        .routeMap(WhatTypeofIntermediaryPage)(IntermediariesRouting(NormalMode))(Some(WhatTypeofIntermediary.Promoter))(0)
        .mustBe(controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(NormalMode))
    }

    "must go from 'What type of intermediary is name?' page to " +
      "'Is *name* exempt from reporting in an EU member state, or the UK?' if answer is Service provider" in {
      navigator
        .routeMap(WhatTypeofIntermediaryPage)(IntermediariesRouting(NormalMode))(Some(WhatTypeofIntermediary.Serviceprovider))(0)
        .mustBe(controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(NormalMode))
    }

    "must go from 'What type of intermediary is name?' page to " +
      "'Is *name* exempt from reporting in an EU member state, or the UK?' if answer is I Do Not Know" in {
      navigator
        .routeMap(WhatTypeofIntermediaryPage)(IntermediariesRouting(NormalMode))(Some(WhatTypeofIntermediary.IDoNotKnow))(0)
        .mustBe(controllers.intermediaries.routes.IsExemptionKnownController.onPageLoad(NormalMode))
    }

    "must go from ' Is *name* exempt from reporting in an EU member state, or the UK?' intermediaries page to " +
      "'Do you know which countries *name* is exempt from reporting in?' if answer is Yes" in {
      navigator
        .routeMap(IsExemptionKnownPage)(IntermediariesRouting(NormalMode))(Some(IsExemptionKnown.Yes))(0)
        .mustBe(controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(NormalMode))
    }

    "Is *name* exempt from reporting in an EU member state, or the UK?' intermediaries page to " +
      "Intermediaries Check Your Answers Page if answer is No" in {
      navigator
        .routeMap(IsExemptionKnownPage)(IntermediariesRouting(NormalMode))(Some(IsExemptionKnown.No))(0)
        .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad())
    }

    "Is *name* exempt from reporting in an EU member state, or the UK?' intermediaries page to " +
      "Intermediaries Check Your Answers Page if answer is I Do Not Know" in {
      navigator
        .routeMap(IsExemptionKnownPage)(IntermediariesRouting(NormalMode))(Some(IsExemptionKnown.Unknown))(0)
        .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad())
    }

    "Do you know which countries *name* exempt from reporting in?' page to " +
      "'Which countries is *name* exempt from reporting in?' when answer is true" in {
      navigator
        .routeMap(IsExemptionCountryKnownPage)(IntermediariesRouting(NormalMode))(Some(true))(0)
        .mustBe(controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(NormalMode))
    }

    "Do you know which countries *name* exempt from reporting in?' page to " +
      "'Check your answers' page in the intermediaries journey when answer is false" in {
      navigator
        .routeMap(IsExemptionCountryKnownPage)(IntermediariesRouting(NormalMode))(Some(false))(0)
        .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad())
    }

    "'Which countries is *name* exempt from reporting in?' page to " +
      "'Check your answers' page in the intermediaries journey when valid answer provided" in {
      navigator
        .routeMap(ExemptCountriesPage)(IntermediariesRouting(NormalMode))(Some(ExemptCountries.UnitedKingdom))(0)
        .mustBe(controllers.intermediaries.routes.IntermediariesCheckYourAnswersController.onPageLoad())
    }
  }
}
