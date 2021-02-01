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
import pages.enterprises._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForEnterprises @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case YouHaveNotAddedAnyAssociatedEnterprisesPage =>
      checkRoute => id => value => _ => value match {
        case Some(YouHaveNotAddedAnyAssociatedEnterprises.YesAddNow)  =>
          routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(id, checkRoute.mode)
        case _ =>
          routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(id, checkRoute.mode)
      }

    case SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.AssociatedEnterpriseTypeController.onPageLoad(id, checkRoute.mode), checkRoute)

    case AssociatedEnterpriseTypePage =>
      checkRoute => id => value => _ => value match {
        case Some(SelectType.Organisation)  =>
          jumpOrCheckYourAnswers(id, controllers.organisation.routes.OrganisationNameController.onPageLoad(id, checkRoute.mode), checkRoute)
        case Some(SelectType.Individual)    =>
          jumpOrCheckYourAnswers(id, controllers.individual.routes.IndividualNameController.onPageLoad(id, checkRoute.mode), checkRoute)
      }

    case IsAssociatedEnterpriseAffectedPage =>
      _ => id => _ => _ => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id)

    case AssociatedEnterpriseCheckYourAnswersPage =>
      checkRoute => id => _ => _ => routes.YouHaveNotAddedAnyAssociatedEnterprisesController.onPageLoad(id, checkRoute.mode)
  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call = _ =>
    _ => id => _ => _ => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id)

  private[navigation] def jumpOrCheckYourAnswers(id:Int, jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case AssociatedEnterprisesRouting(CheckMode) => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id)
      case DefaultRouting(CheckMode)               => routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id)
      case _                                       => jumpTo
    }
  }

}
