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

package controllers.taxpayer

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.exceptions.UnsupportedRouteException
import models.taxpayer.Taxpayer
import models.{Mode, NormalMode, SelectType, UserAnswers}
import navigation.Navigator
import pages.taxpayer.{TaxpayerCheckYourAnswersPage, TaxpayerLoopPage, TaxpayerSelectTypePage, UpdateTaxpayerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class TaxpayersCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val (taxpayerSummary: Seq[SummaryList.Row], countrySummary: Seq[SummaryList.Row]) = request.userAnswers.get(TaxpayerSelectTypePage, id) match {
        case Some(SelectType.Organisation) =>
          (helper.taxpayerSelectType(id).toSeq ++ helper.organisationName(id).toSeq ++
            helper.buildOrganisationAddressGroup(id) ++ helper.buildOrganisationEmailAddressGroup(id),
            helper.buildTaxResidencySummaryForOrganisation(id))

        case Some(SelectType.Individual) =>

          (Seq(helper.taxpayerSelectType(id), helper.individualName(id)).flatten ++
            helper.buildIndividualDateOfBirthGroup(id) ++
            helper.buildIndividualPlaceOfBirthGroup(id) ++
            helper.buildIndividualAddressGroup(id) ++
            helper.buildIndividualEmailAddressGroup(id),
            helper.buildTaxResidencySummaryForIndividuals(id))

        case _ => throw new UnsupportedRouteException(id)
      }

      val implementingDateSummary = helper.whatIsTaxpayersStartDateForImplementingArrangement(id).toSeq

      renderer.render(
        "taxpayer/check-your-answers-taxpayers.njk",
        Json.obj(
          "taxpayersSummary" -> taxpayerSummary,
          "countrySummary" -> countrySummary,
          "implementingDateSummary" -> implementingDateSummary,
          "id" -> id,
          "mode" -> NormalMode
        )).map(Ok(_))
  }

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        userAnswers                 <- Future.fromTry(request.userAnswers.remove(UpdateTaxpayerPage, id))
        userAnswersWithTaxpayerLoop <- Future.fromTry(userAnswers.set(TaxpayerLoopPage, id, updatedLoopList(request.userAnswers, id)))
        _                           <- sessionRepository.set(userAnswersWithTaxpayerLoop)
      } yield {
        Redirect(navigator.nextPage(TaxpayerCheckYourAnswersPage, id, mode, userAnswersWithTaxpayerLoop))
      }
  }

  private[taxpayer] def updatedLoopList(userAnswers: UserAnswers, id: Int): IndexedSeq[Taxpayer] = {
    val taxpayer: Taxpayer = Taxpayer.buildTaxpayerDetails(userAnswers, id)
    userAnswers.get(TaxpayerLoopPage, id) match {
      case Some(list) => // append to existing list without duplication
        list.filterNot(_.nameAsString == taxpayer.nameAsString) :+ taxpayer
      case None =>       // start new list
        IndexedSeq[Taxpayer](taxpayer)
    }
  }
}
