/*
 * Copyright 2020 HM Revenue & Customs
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

import models.YesNoDoNotKnowRadios.Yes
import models._
import models.reporter.RoleInArrangement.Intermediary
import models.reporter.taxpayer.TaxpayerWhyReportInUK.DoNotKnow
import pages._
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary._
import pages.reporter.taxpayer.{TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.api.mvc.Call


object NavigatorForReporter extends AbstractNavigatorOld {

  override val checkYourAnswersRoute: Call = controllers.routes.IndexController.onPageLoad() //TODO - change when CYA page built

  private[navigation] val routeMap: Page => Mode => Option[Any] => Int => Call = {

    case RoleInArrangementPage => mode => value => _ => value match {
      case Some(Intermediary) => controllers.reporter.intermediary.routes.IntermediaryWhyReportInUKController.onPageLoad(mode)
      case _ => controllers.reporter.taxpayer.routes.TaxpayerWhyReportInUKController.onPageLoad(mode)
    }

    // Reporter - Intermediary Journey Navigation

    case IntermediaryWhyReportInUKPage => mode => _ => _ =>
      controllers.reporter.intermediary.routes.IntermediaryRoleController.onPageLoad(mode)

    case IntermediaryRolePage => mode =>_ =>_ =>
      controllers.reporter.intermediary.routes.IntermediaryExemptionInEUController.onPageLoad(mode)

    case IntermediaryExemptionInEUPage => mode => value => _ => value match {
      case Some(Yes) =>      controllers.reporter.intermediary.routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(mode)
      case _ => controllers.reporter.routes.RoleInArrangementController.onPageLoad(mode) //TODO - Change redirect to CYA when built

    }

    case IntermediaryDoYouKnowExemptionsPage => mode => value =>_ => value match {
      case Some(true) => controllers.reporter.intermediary.routes.IntermediaryWhichCountriesExemptController.onPageLoad(mode)
      case _ => controllers.reporter.routes.RoleInArrangementController.onPageLoad(mode) // TODO - Change redirect to CYA when built
    }

    case IntermediaryWhichCountriesExemptPage => mode => _ =>_ =>
      controllers.reporter.routes.RoleInArrangementController.onPageLoad(mode) //TODO - Change redirect to CYA when built


    // Reporter - Taxpayer Journey Navigation

    case TaxpayerWhyReportInUKPage => mode => value =>_ => value match {
      case Some(DoNotKnow) => controllers.reporter.routes.RoleInArrangementController.onPageLoad(mode) // TODO - Change redirect to date implementing page when built
      case _ => controllers.reporter.taxpayer.routes.TaxpayerWhyReportArrangementController.onPageLoad(mode)
    }

    case TaxpayerWhyReportArrangementPage => mode => _ =>_ =>
      controllers.reporter.routes.RoleInArrangementController.onPageLoad(mode) // TODO - Change redirect to date implementing page when built


    case _ => mode => _ => _ => mode match {
        case NormalMode => indexRoute
        case CheckMode  => checkYourAnswersRoute
      }

  }

  private[navigation] val alternativeRouteMap: Page => Call = {

    case _ => checkYourAnswersRoute

  }

}
