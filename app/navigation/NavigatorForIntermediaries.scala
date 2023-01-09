/*
 * Copyright 2023 HM Revenue & Customs
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
class NavigatorForIntermediaries @Inject() () extends AbstractNavigator {

  override val routeMap: Page => CheckRoute => Int => Option[Any] => Int => Call = {

    case YouHaveNotAddedAnyIntermediariesPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(YesAddNow) => controllers.intermediaries.routes.IntermediariesTypeController.onPageLoad(id, checkRoute.mode)
                case _               => controllers.routes.DisclosureDetailsController.onPageLoad(id)
              }

    case IntermediariesTypePage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(SelectType.Organisation) =>
                  jumpOrCheckYourAnswers(id, controllers.organisation.routes.OrganisationNameController.onPageLoad(id, checkRoute.mode), checkRoute)
                case Some(SelectType.Individual) =>
                  jumpOrCheckYourAnswers(id, controllers.individual.routes.IndividualNameController.onPageLoad(id, checkRoute.mode), checkRoute)
              }

    case WhatTypeofIntermediaryPage =>
      checkRoute => id => _ => _ => jumpOrCheckYourAnswers(id, routes.IsExemptionKnownController.onPageLoad(id, checkRoute.mode), checkRoute)

    case IsExemptionKnownPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(Yes) => controllers.intermediaries.routes.IsExemptionCountryKnownController.onPageLoad(id, checkRoute.mode)
                case _         => jumpOrCheckYourAnswers(id, routes.IntermediariesCheckYourAnswersController.onPageLoad(id, None), checkRoute)
              }

    case IsExemptionCountryKnownPage =>
      checkRoute =>
        id =>
          value =>
            _ =>
              value match {
                case Some(true) => controllers.intermediaries.routes.ExemptCountriesController.onPageLoad(id, checkRoute.mode)
                case _          => jumpOrCheckYourAnswers(id, routes.IntermediariesCheckYourAnswersController.onPageLoad(id, None), checkRoute)
              }

    case ExemptCountriesPage =>
      _ => id => _ => _ => routes.IntermediariesCheckYourAnswersController.onPageLoad(id, None)

    case IntermediariesCheckYourAnswersPage =>
      _ => id => _ => _ => routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(id)

    case _ =>
      checkRoute =>
        id =>
          _ =>
            _ =>
              checkRoute.mode match {
                case NormalMode => indexRoute
                case CheckMode  => controllers.routes.IndexController.onPageLoad
              }
  }

  override val routeAltMap: Page => CheckRoute => Int => Option[Any] => Int => Call = _ =>
    _ => id => _ => _ => routes.IntermediariesCheckYourAnswersController.onPageLoad(id, None)

  override private[navigation] def jumpOrCheckYourAnswers(id: Int, jumpTo: Call, checkRoute: CheckRoute): Call =
    checkRoute match {
      case IntermediariesRouting(CheckMode) => routes.IntermediariesCheckYourAnswersController.onPageLoad(id, None)
      case _                                => jumpTo
    }
}
