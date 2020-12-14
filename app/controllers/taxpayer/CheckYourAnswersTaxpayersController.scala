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

package controllers.taxpayer

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import handlers.ErrorHandler
import models.SelectType.{Individual, Organisation}
import models.{SelectType, UserAnswers}
import pages.organisation.OrganisationLoopPage
import pages.taxpayer.TaxpayerSelectTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.{CheckYourAnswersHelper, CheckYourAnswersOrganisationHelper}

import scala.concurrent.ExecutionContext

class CheckYourAnswersTaxpayersController @Inject()(
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
          val helper = new CheckYourAnswersHelper(request.userAnswers)
          val orgHelper = new CheckYourAnswersOrganisationHelper(request.userAnswers)

          request.userAnswers.get(TaxpayerSelectTypePage) match {
            case Some(selectType) =>
              val taxpayerDetails: Seq[SummaryList.Row] =
                helper.buildTaxpayerDetails( detailsSummary(selectType)(helper, orgHelper),
                  countryDetails(selectType, request.userAnswers)(helper, orgHelper))
                renderer.render (
                "taxpayer/check-your-answers-taxpayers.njk",
                Json.obj ("taxpayersSummary" -> taxpayerDetails
                )
                ).map (Ok (_) )
            case _ => errorHandler.onServerError(request, throw new Exception("Relevant taxpayer type is missing"))
          }
      }

    def detailsSummary(selectType :SelectType)
                      (helper: CheckYourAnswersHelper, orgHelper: CheckYourAnswersOrganisationHelper): Seq[SummaryList.Row] = {
     selectType match {
        case Organisation =>
          orgHelper.buildOrganisationDetails
        case Individual => Seq(helper.individualName, helper.individualDateOfBirth).flatten ++
            helper.buildIndividualPlaceOfBirthGroup ++
            helper.buildIndividualAddressGroup ++
            helper.buildIndividualEmailAddressGroup
      }
    }

    def countryDetails(selectType :SelectType, userAnswers: UserAnswers)
                      (helper: CheckYourAnswersHelper, orgHelper: CheckYourAnswersOrganisationHelper): Seq[SummaryList.Row] = {
      selectType match {
        case Organisation =>
          userAnswers.get(OrganisationLoopPage) match {
            case Some(taxResidentCountriesLoop) =>
              orgHelper.buildTaxResidencySummary
            case _ => Nil
          }
        case Individual => helper.buildTaxResidencySummaryForIndividuals
      }
    }
  }

