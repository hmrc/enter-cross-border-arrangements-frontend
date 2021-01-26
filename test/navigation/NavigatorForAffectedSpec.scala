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
import models.affected.YouHaveNotAddedAnyAffected
import models.{NormalMode, SelectType}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.affected.{AffectedCheckYourAnswersPage, AffectedTypePage, YouHaveNotAddedAnyAffectedPage}

class NavigatorForAffectedSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForAffected

  val A1 = "You have not added any other parties affected by this arrangement"
  val A2 = "Is this an organisation or an individual?"
  val A3 = "What is {0}'s date of birth?"
  val A7 = "Check your answers"
  val TL = "Your DAC6 Disclosure Details"

  "NavigatorForAffected" - {

    s"must go from $A1 $A2 when answer is yes" in {
          navigator
            .routeMap(YouHaveNotAddedAnyAffectedPage)(AffectedRouting(NormalMode))(0)(Some(YouHaveNotAddedAnyAffected.YesAddNow))(0)
            .mustBe(controllers.affected.routes.AffectedTypeController.onPageLoad(0, NormalMode))
      }

    s"must go from $A1 $TL when answer is 'No'" in {
      navigator
        .routeMap(YouHaveNotAddedAnyAffectedPage)(AffectedRouting(NormalMode))(0)(Some(YouHaveNotAddedAnyAffected.No))(0)
        .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad(0))
    }

    s"must go from $A1 $TL when answer is 'YesAddLater'" in {
      navigator
        .routeMap(YouHaveNotAddedAnyAffectedPage)(AffectedRouting(NormalMode))(0)(Some(YouHaveNotAddedAnyAffected.YesAddLater))(0)
        .mustBe(controllers.routes.DisclosureDetailsController.onPageLoad(0))
    }

    s"must go from $A2 to 'What is the name of the organisation?' if answer is Organisation" in {
      navigator
        .routeMap(AffectedTypePage)(AffectedRouting(NormalMode))(0)(Some(SelectType.Organisation))(0)
        .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(0, NormalMode))
    }

    s"must go from $A2 to 'What is their name?' if answer is Individual" in {
      navigator
        .routeMap(AffectedTypePage)(AffectedRouting(NormalMode))(0)(Some(SelectType.Individual))(0)
        .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(0, NormalMode))
    }

    s"must go from $A7 to $A1" in {
      navigator
        .routeMap(AffectedCheckYourAnswersPage)(AffectedRouting(NormalMode))(0)(None)(0)
        .mustBe(controllers.affected.routes.YouHaveNotAddedAnyAffectedController.onPageLoad(0))
    }
  }
}
