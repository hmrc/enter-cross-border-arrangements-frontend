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

package controllers

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import handlers.ErrorHandler
import pages.OrganisationLoopPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersOrganisationHelper

import scala.concurrent.ExecutionContext

class CheckYourAnswersOrganisationController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    errorHandler: ErrorHandler,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.get(OrganisationLoopPage) match {
        case Some(taxResidentCountriesLoop) =>
          val helper = new CheckYourAnswersOrganisationHelper(request.userAnswers)
          val orgDetails: Seq[SummaryList.Row] = helper.buildOrganisationDetails
          val countryDetails: Seq[SummaryList.Row] = helper.buildCountryWithReferenceSummary(taxResidentCountriesLoop)

          renderer.render(
            "check-your-answers-organisation.njk",
            Json.obj("orgSummary" -> orgDetails,
              "countrySummary" -> countryDetails)
          ).map(Ok(_))

        case _ => errorHandler.onServerError(request, throw new Exception("OrganisationLoop is missing"))

      }
  }


//  private def buildDetails(helper: CheckYourAnswersOrganisationHelper): Seq[SummaryList.Row] = {
//
//    val pagesToCheck = helper.displayAddressQuestionWithAddress
//
////    val pagesToCheck = Tuple5(
////      helper.organisationName,
////      helper.displayAddressQuestionWithAddress,
////      helper.emailAddressQuestionForOrganisation,
////      helper.emailAddressForOrganisation,
////      helper.isOrganisationResidentForTaxOtherCountries
////    )
//
////    val questionAndAddress =
////      helper.isOrganisationAddressKnown
//
////    questionAndAddress match {
////      case Some(yesOrNo) if yesOrNo.value.equals(true) =>
////        Seq(helper.isOrganisationAddressKnown, helper.organisationAddress).flatten
////      case _ => Seq()
////    }
//
//  }
//
}
