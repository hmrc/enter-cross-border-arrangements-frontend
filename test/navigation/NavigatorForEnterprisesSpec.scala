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
import controllers.enterprises._
import controllers.mixins.{AssociatedEnterprisesRouting, DefaultRouting}
import generators.Generators
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.{CheckMode, NormalMode, SelectType}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.enterprises.{AssociatedEnterpriseCheckYourAnswersPage, AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class NavigatorForEnterprisesSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new NavigatorForEnterprises
  val index: Int = 0
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", s"/uri/$index")

  val E2 = "You have not added any taxpayers"
  val E4 = "Select any taxpayers this enterprise is associated with"
  val E7 = "Is this an organisation or an individual?"
  val E10 = "Is {0}  affected by this arrangement?"
  val E11 = "Check your answers"
  // organizations
  val O1 = "What is the name of the organisation? in the organisation journey"
  // individuals
  val I1 = "What is their name? in the individual journey"

  "NavigatorForEnterprises" - {

    "in Normal Mode" - {

      s"must go from $E2 to $E4 when answer is 'Yes, add now'" in {

        navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(DefaultRouting(NormalMode))(Some(YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow))(0)
          .mustBe(routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(NormalMode))
      }

      s"must go from $E2 to the same page when answer is 'Yes, add later'" in {

        navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(DefaultRouting(NormalMode))(Some(YouHaveNotAddedAnyAssociatedEnterprises.YesAddLater))(0)
          .mustBe(routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(NormalMode))
      }

      s"must go from $E2 page to the same page when answer is 'No'" in {

        navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(DefaultRouting(NormalMode))(Some(YouHaveNotAddedAnyAssociatedEnterprises.No))(0)
          .mustBe(routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(NormalMode))
      }

      s"must go from $E7 to $O1 when answer is Organisation" in {

        navigator.routeMap(AssociatedEnterpriseTypePage)(DefaultRouting(NormalMode))(Some(SelectType.Organisation))(0)
          .mustBe(controllers.organisation.routes.OrganisationNameController.onPageLoad(NormalMode))
      }

      s"must go from $E7 to $I1 when answer is Individual" in {

        navigator.routeMap(AssociatedEnterpriseTypePage)(DefaultRouting(NormalMode))(Some(SelectType.Individual))(0)
          .mustBe(controllers.individual.routes.IndividualNameController.onPageLoad(NormalMode))
      }

      s"must go from $E10 to $E11 in the associated enterprise journey" in {

        forAll(Gen.oneOf(Seq(true, false))) {
          affectedPageAnswer =>

            navigator.routeMap(IsAssociatedEnterpriseAffectedPage)(DefaultRouting(NormalMode))(Some(affectedPageAnswer))(0)
              .mustBe(routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }
      }

      s"must go from $E11 to $E2 in the associated enterprise journey" in {

        navigator.routeMap(AssociatedEnterpriseCheckYourAnswersPage)(DefaultRouting(NormalMode))(None)(0)
          .mustBe(routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(NormalMode))
      }
    }

    "in Check Mode" - {

      val routingInCheckMode = AssociatedEnterprisesRouting(CheckMode)

      s"must go from $E2 to $E4 when answer is 'Yes, add now'" in {

        navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(routingInCheckMode)(Some(YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow))(0)
          .mustBe(routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(CheckMode))
      }

      s"must go from $E2 to the same page when answer is 'Yes, add later'" in {

        navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(routingInCheckMode)(Some(YouHaveNotAddedAnyAssociatedEnterprises.YesAddLater))(0)
          .mustBe(routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(CheckMode))
      }

      s"must go from $E2 page the same page when answer is 'No'" in {

        navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(routingInCheckMode)(Some(YouHaveNotAddedAnyAssociatedEnterprises.No))(0)
          .mustBe(routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(CheckMode))
      }

      "assuming the value has not changed (otherwise the controller would have forced the NormalMode" - {

        s"must go from $E7 to $E11 when answer is Organisation" in {

          navigator.routeMap(AssociatedEnterpriseTypePage)(routingInCheckMode)(Some(SelectType.Organisation))(0)
            .mustBe(routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }

        s"must go from $E7 to $E11 when answer is Individual" in {

          navigator.routeMap(AssociatedEnterpriseTypePage)(routingInCheckMode)(Some(SelectType.Individual))(0)
            .mustBe(routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }
      }

      s"must go from $E10 to $E11 in the associated enterprise journey" in {

        forAll(Gen.oneOf(Seq(true, false))) {
          affectedPageAnswer =>

            navigator.routeMap(IsAssociatedEnterpriseAffectedPage)(routingInCheckMode)(Some(affectedPageAnswer))(0)
              .mustBe(routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad())
        }
      }
    }

  }
}
