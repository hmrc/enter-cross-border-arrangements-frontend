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

package controllers.arrangement

import controllers.actions._
import controllers.mixins.{DefaultRouting, RoutingSupport}
import models.arrangement.ArrangementDetails
import models.hallmarks.JourneyStatus
import models.{NormalMode, UserAnswers}
import navigation.NavigatorForArrangement
import pages.arrangement.{ArrangementCheckYourAnswersPage, ArrangementDetailsPage, ArrangementStatusPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.SummaryList
import utils.CheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ArrangementCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  navigator: NavigatorForArrangement,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with RoutingSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      ArrangementDetails.buildArrangementDetails(request.userAnswers, id)
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val list: Seq[SummaryList.Row] =
        Seq(
          helper.whatIsThisArrangementCalledPage(id),
          helper.whatIsTheImplementationDatePage(id),
          helper.buildWhyAreYouReportingThisArrangementNow(id),
          helper.whichExpectedInvolvedCountriesArrangement(id),
          helper.whatIsTheExpectedValueOfThisArrangement(id),
          helper.whichNationalProvisionsIsThisArrangementBasedOn(id),
          helper.giveDetailsOfThisArrangement(id)
        ).flatten

      renderer.render("arrangement/check-your-answers-arrangement.njk", Json.obj("list" -> list)).map(Ok(_))
  }

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      for {
        arrangementModel <- Future.fromTry(
          request.userAnswers.set(ArrangementDetailsPage, id, ArrangementDetails.buildArrangementDetails(request.userAnswers, id))
        )
        userAnswers: UserAnswers <- Future.fromTry(arrangementModel.set(ArrangementStatusPage, id, JourneyStatus.Completed))
        _                        <- sessionRepository.set(userAnswers)
      } yield Redirect(navigator.routeMap(ArrangementCheckYourAnswersPage)(DefaultRouting(NormalMode))(id)(None)(0))
  }
}
