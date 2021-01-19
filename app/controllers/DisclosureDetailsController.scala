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

package controllers

import config.FrontendAppConfig
import controllers.actions._

import javax.inject.Inject
import pages.disclosure.DisclosureIdentifyArrangementPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.Radios.MessageInterpolators
import models.ReporterOrganisationOrIndividual.{Individual, Organisation}
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import models.hallmarks.HallmarkD.D1
import models.hallmarks.HallmarkD1.D1other
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.taxpayer.UpdateTaxpayer.{Later, No}
import pages.arrangement._
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureMarketablePage, DisclosureTypePage}
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.individual._
import pages.reporter.organisation._
import pages.reporter.{ReporterOrganisationOrIndividualPage, RoleInArrangementPage}
import pages.taxpayer.{UpdateTaxpayerPage, _}

import scala.concurrent.ExecutionContext

class DisclosureDetailsController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    frontendAppConfig: FrontendAppConfig,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val arrangementMessage: String = request.userAnswers.fold("") {
        value => value.get(DisclosureIdentifyArrangementPage)
          .map(msg"disclosureDetails.heading.forArrangement".withArgs(_).resolve)
          .getOrElse("")
      }


      val diclosureTypeStatus = if (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
        && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(true)

        || request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add)
        && request.userAnswers.flatMap(_.get(DisclosureIdentifyArrangementPage)).isDefined

        || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add) || request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add))
        && request.userAnswers.flatMap(_.get(DisclosureIdentifyArrangementPage)).isDefined
        && request.userAnswers.flatMap(_.get(DisclosureIdentifyArrangementPage)).isDefined)
      {
        "complete"
      } else {
        "in progress"
      }




      val hallmarkStatus =  if(request.userAnswers.flatMap(_.get(HallmarkDPage)).isEmpty){
        "not started"
      } else if ((request.userAnswers.flatMap(_.get(HallmarkDPage)).contains(Set(D1))
        && request.userAnswers.flatMap(_.get(HallmarkD1Page)).isEmpty)

        || (request.userAnswers.flatMap(_.get(HallmarkD1Page)).contains(Set(D1other))
        && request.userAnswers.flatMap(_.get(HallmarkD1OtherPage)).isEmpty)
      ){
        "in progress"
      } else {
        "complete"
      }


      val arrangementDetailsStatus =
        if (request.userAnswers.flatMap(_.get(WhatIsThisArrangementCalledPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhatIsTheImplementationDatePage)).isDefined
          && request.userAnswers.flatMap(_.get(DoYouKnowTheReasonToReportArrangementNowPage)).contains(false)
          && request.userAnswers.flatMap(_.get(WhichExpectedInvolvedCountriesArrangementPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhatIsTheExpectedValueOfThisArrangementPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhichNationalProvisionsIsThisArrangementBasedOnPage)).isDefined
          && request.userAnswers.flatMap(_.get(GiveDetailsOfThisArrangementPage)).isDefined
        ){
          "complete"
        } else if(request.userAnswers.flatMap(_.get(WhatIsThisArrangementCalledPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhatIsTheImplementationDatePage)).isDefined
          && request.userAnswers.flatMap(_.get(DoYouKnowTheReasonToReportArrangementNowPage)).contains(true)
          && request.userAnswers.flatMap(_.get(WhyAreYouReportingThisArrangementNowPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhichExpectedInvolvedCountriesArrangementPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhatIsTheExpectedValueOfThisArrangementPage)).isDefined
          && request.userAnswers.flatMap(_.get(WhichNationalProvisionsIsThisArrangementBasedOnPage)).isDefined
          && request.userAnswers.flatMap(_.get(GiveDetailsOfThisArrangementPage)).isDefined) {
          "complete"
        } else if(request.userAnswers.flatMap(_.get(WhatIsThisArrangementCalledPage)).isEmpty
          && request.userAnswers.flatMap(_.get(WhatIsTheImplementationDatePage)).isEmpty
          && request.userAnswers.flatMap(_.get(DoYouKnowTheReasonToReportArrangementNowPage)).isEmpty
          && request.userAnswers.flatMap(_.get(WhyAreYouReportingThisArrangementNowPage)).isEmpty
          && request.userAnswers.flatMap(_.get(WhichExpectedInvolvedCountriesArrangementPage)).isEmpty
          && request.userAnswers.flatMap(_.get(WhatIsTheExpectedValueOfThisArrangementPage)).isEmpty
          && request.userAnswers.flatMap(_.get(WhichNationalProvisionsIsThisArrangementBasedOnPage)).isEmpty
          && request.userAnswers.flatMap(_.get(GiveDetailsOfThisArrangementPage)).isEmpty){
          "not started"
        } else {
          "in progress"
        }

      //      val reporterDetailsStatus =
      //        if (((
      //
      //          // Org without tins
      //          request.userAnswers.flatMap(_.get(ReporterOrganisationOrIndividualPage)).contains(Organisation)
      //          && request.userAnswers.flatMap(_.get(ReporterOrganisationNamePage)).isDefined
      //          && (request.userAnswers.flatMap(_.get(ReporterOrganisationSelectAddressPage)).isDefined
      //          || request.userAnswers.flatMap(_.get(ReporterOrganisationAddressPage)).isDefined)
      //          && request.userAnswers.flatMap(_.get(ReporterOrganisationEmailAddressQuestionPage)).isDefined)
      //
      //          ||
      //        // Ind without tins
      //          (request.userAnswers.flatMap(_.get(ReporterOrganisationOrIndividualPage)).contains(Individual)
      //          && request.userAnswers.flatMap(_.get(ReporterIndividualNamePage)).isDefined
      //          && request.userAnswers.flatMap(_.get(ReporterIndividualDateOfBirthPage)).isDefined
      //          && (request.userAnswers.flatMap(_.get(ReporterIndividualSelectAddressPage)).isDefined
      //            || request.userAnswers.flatMap(_.get(ReporterIndividualAddressPage)).isDefined)
      //          && request.userAnswers.flatMap(_.get(ReporterIndividualDateOfBirthPage)).isDefined
      //          && request.userAnswers.flatMap(_.get(ReporterIndividualEmailAddressQuestionPage)).isDefined))
      //
      //
      //        //  &&
      //
      //          // tins
      //
      //          //
      //
      //        ) {
      //          "complete"
      //        } else if (???){
      //
      //          // if the above is empty
      //          "not started"
      //        } else {
      //          "in progress"
      //        }

      


      val taxpayersExist= request.userAnswers.flatMap(_.get(TaxpayerLoopPage)) match {
        case Some(_) => true
        case None => false
      }

      val relevantTaxPayerStatus =

        if ((request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)).contains(No)

          && ((request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
          && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(true))

          || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
          && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(false)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Taxpayer))

          || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Intermediary))))


          // or

          || (request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)).contains(No)

          && ((request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
          && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(false)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Intermediary))

          || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Intermediary)))

          && taxpayersExist)


          || taxpayersExist)
        {
          "complete"
        }
        else if (request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)).contains(Later)) {
          "in progress"
        } else {
          "not started"
        }







      val intermediariesExist= request.userAnswers.flatMap(_.get(IntermediaryLoopPage)) match {
        case Some(_) => true
        case None => false
      }

      val intermediariesStatus =

        if ((request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)).contains(No)

          && ((request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
          && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(true))

          || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
          && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(false)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Taxpayer))

          || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Intermediary))))


          // or

          || (request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)).contains(No)

          && ((request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6new)
          && request.userAnswers.flatMap(_.get(DisclosureMarketablePage)).contains(false)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Intermediary))

          || (request.userAnswers.flatMap(_.get(DisclosureTypePage)).contains(Dac6add)
          && request.userAnswers.flatMap(_.get(RoleInArrangementPage)).contains(Intermediary)))

          && intermediariesExist)


          || intermediariesExist)
        {
          "complete"
        }
        else if (request.userAnswers.flatMap(_.get(UpdateTaxpayerPage)).contains(Later)) {
          "in progress"
        } else {
          "not started"
        }

      val json = Json.obj(
        "arrangementID" -> arrangementMessage,

        "diclosureTypeStatus" -> diclosureTypeStatus,
        "hallmarkStatus"-> hallmarkStatus,
        "arrangementDetailsStatus"-> arrangementDetailsStatus,
        //        "reporterDetailsStatus"-> reporterDetailsStatus,
        "relevantTaxPayerStatus"-> relevantTaxPayerStatus,
        "intermediariesStatus"-> intermediariesStatus,

        "hallmarksUrl" -> frontendAppConfig.hallmarksUrl,
        "arrangementsUrl" -> frontendAppConfig.arrangementsUrl,
        "reportersUrl" -> frontendAppConfig.reportersUrl,
        "taxpayersUrl" -> frontendAppConfig.taxpayersUrl,
        "intermediariesUrl" -> frontendAppConfig.intermediariesUrl,
        "disclosureUrl" -> frontendAppConfig.disclosureUrl
      )


      renderer.render("disclosureDetails.njk", json).map(Ok(_))
  }

}
