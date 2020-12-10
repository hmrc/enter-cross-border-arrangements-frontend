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

package controllers.organisation

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages.enterprises.AssociatedEnterpriseTypePage
import pages.taxpayer.TaxpayerSelectTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersOrganisationHelper

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersOrganisationController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val associatedEnterpriseJourney: Boolean = request.userAnswers.get(AssociatedEnterpriseTypePage) match {
        case Some(_) => true
        case None => false
      }

      val relevantTaxpayerJourney: Boolean = request.userAnswers.get(TaxpayerSelectTypePage) match {
        case Some(_) => true
        case None => false
      }

      //TODO Below redirect is temporary until a solution about change routing is found
      if (associatedEnterpriseJourney) {
        Future.successful(Redirect(controllers.enterprises.routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()))
      } else if (relevantTaxpayerJourney) {
        Future.successful(Redirect(controllers.taxpayer.routes.CheckYourAnswersTaxpayersController.onPageLoad()))
      } else {
        val helper = new CheckYourAnswersOrganisationHelper(request.userAnswers)
        val organisationDetails: Seq[SummaryList.Row] = helper.buildOrganisationDetails
        val countryDetails: Seq[SummaryList.Row] = helper.buildTaxResidencySummary

        renderer.render(
          "organisation/check-your-answers-organisation.njk",
          Json.obj("organisationSummary" -> organisationDetails,
            "countrySummary" -> countryDetails
          )
        ).map(Ok(_))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      //TODO - build full Taxpayer Details to submit
      val name = request.userAnswers.get(OrganisationNamePage).get
      val taxpayerLoopList = request.userAnswers.get(TaxpayerLoopPage) match {
        case Some(list) => // append to existing list
          list :+ Taxpayer.apply(Organisation(name))
        case None => // start new list
          IndexedSeq[Taxpayer](Taxpayer.apply(Organisation(name)))
      }

      for {
        userAnswersWithTaxpayerLoop <- Future.fromTry(request.userAnswers.set(TaxpayerLoopPage, taxpayerLoopList))
        _ <- sessionRepository.set(userAnswersWithTaxpayerLoop)
      } yield
        Redirect(controllers.taxpayer.routes.UpdateTaxpayerController.onPageLoad())

  }
}
