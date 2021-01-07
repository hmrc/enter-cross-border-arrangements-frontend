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

import controllers.enterprises._
import controllers.mixins.{AssociatedEnterprisesRouting, CheckRoute, DefaultRouting}
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.{CheckMode, SelectType}
import pages.Page
import pages.enterprises.{AssociatedEnterpriseTypePage, IsAssociatedEnterpriseAffectedPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForEnterprises @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case YouHaveNotAddedAnyAssociatedEnterprisesPage =>
      checkRoute => value => _ => value match {
        case Some(YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)  =>
          routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(checkRoute.mode)
        case _ =>
          routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(checkRoute.mode)
      }

    case SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage =>
      checkRoute => _ => _ => routes.AssociatedEnterpriseTypeController.onPageLoad(checkRoute.mode) // TODO redirect

    case AssociatedEnterpriseTypePage =>
      checkRoute => value => _ => value match {
        case Some(SelectType.Organisation)  =>
          jumpOrCheckYourAnswers(controllers.organisation.routes.OrganisationNameController.onPageLoad(checkRoute.mode), checkRoute)
        case Some(SelectType.Individual)    =>
          jumpOrCheckYourAnswers(controllers.individual.routes.IndividualNameController.onPageLoad(checkRoute.mode), checkRoute)
      }

    case IsAssociatedEnterpriseAffectedPage =>
      _ => _ => _ => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()
  }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode) => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()
      case DefaultRouting(CheckMode)               => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()
      case _                                       => jumpTo
    }
  }

}
