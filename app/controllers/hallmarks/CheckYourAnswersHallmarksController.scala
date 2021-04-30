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

package controllers.hallmarks

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{NormalMode, UserAnswers}
import models.hallmarks.{HallmarkDetails, JourneyStatus}
import navigation.Navigator
import pages.hallmarks.{HallmarkDetailsPage, HallmarkStatusPage, HallmarksCheckYourAnswersPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersHallmarksController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    navigator: Navigator,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val hallmarks = helper.buildHallmarksRow(id)
      val answers: Seq[SummaryList.Row] = Seq(Some(hallmarks), helper.mainBenefitTest(id), helper.hallmarkD1Other(id)).flatten

      renderer.render(
        "hallmarks/check-your-answers-hallmarks.njk",
        Json.obj("list" -> answers)
      ).map(Ok(_))
  }

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        hallmarksModel <- Future.fromTry(request.userAnswers.set(HallmarkDetailsPage, id,
          HallmarkDetails.buildHallmarkDetails(request.userAnswers, id)))
        userAnswers: UserAnswers <- Future.fromTry(hallmarksModel.set(HallmarkStatusPage, id, JourneyStatus.Completed))
        _ <- sessionRepository.set(userAnswers)
      } yield
        Redirect(navigator.nextPage(HallmarksCheckYourAnswersPage, id, NormalMode, request.userAnswers))
  }
}
