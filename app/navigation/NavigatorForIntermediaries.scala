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

import controllers.intermediaries._
import controllers.mixins.{CheckRoute, IntermediariesRouting}
import models.IsExemptionKnown.Yes
import models.intermediaries.YouHaveNotAddedAnyIntermediaries.YesAddNow
import models.{CheckMode, NormalMode, SelectType}
import pages.Page
import pages.intermediaries._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class NavigatorForIntermediaries @Inject()() extends AbstractNavigator {

  override val routeMap:  Page => CheckRoute => Option[Any] => Int => Call = {

    case YouHaveNotAddedAnyIntermediariesPage =>
      checkRoute => value => _ =>
        value match {
          case Some(YesAddNow) => controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(checkRoute.mode)
            //TODO: Send to task list page when created
          case _ => controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad()
        }

    case IntermediariesTypePage =>
      checkRoute => value => _ =>
        value match {
          case Some(SelectType.Organisation) =>
            jumpOrCheckYourAnswers(controllers.organisation.routes.OrganisationNameController.onPageLoad(checkRoute.mode), checkRoute)
          case Some(SelectType.Individual)   =>
            jumpOrCheckYourAnswers(controllers.individual.routes.IndividualNameController.onPageLoad(checkRoute.mode), checkRoute)
        }

    case WhatTypeofIntermediaryPage =>
      checkRoute => _ => _ => jumpOrCheckYourAnswers(routes.IsExemptionKnownController.onPageLoad(checkRoute.mode), checkRoute)

    case IsExemptionKnownPage =>
      checkRoute => value => _ =>
        value match {
          case Some(Yes) => controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(checkRoute.mode)
          case _ => jumpOrCheckYourAnswers(routes.IntermediariesCheckYourAnswersController.onPageLoad(), checkRoute)
        }

    case IsExemptionCountryKnownPage =>
      checkRoute => value => _ =>
        value match {
          case Some(true) => controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(checkRoute.mode)
          case _ => jumpOrCheckYourAnswers(routes.IntermediariesCheckYourAnswersController.onPageLoad(), checkRoute)
        }

    case ExemptCountriesPage =>
      _ => _ => _ => routes.IntermediariesCheckYourAnswersController.onPageLoad()

    case _ =>
      checkRoute => _ => _ => checkRoute.mode match {
        case NormalMode => indexRoute
        case CheckMode  => controllers.routes.IndexController.onPageLoad()
      }
  }

  override val routeAltMap: Page => CheckRoute => Option[Any] => Int => Call = _ =>
    _ => _ => _ => routes.IntermediariesCheckYourAnswersController.onPageLoad()

  private[navigation] def jumpOrCheckYourAnswers(jumpTo: Call, checkRoute: CheckRoute): Call = {
    checkRoute match {
      case IntermediariesRouting(CheckMode)        => routes.IntermediariesCheckYourAnswersController.onPageLoad()
      case _                                       => jumpTo
    }
  }
}
